package com.valentine.service;

import com.valentine.model.*;
import jakarta.ws.rs.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TexasHoldemEngine {
    private final Deck deck = new Deck();
    private final List<GamePlayer> players = new ArrayList<>();
    private final List<Poker> commmunityCards = new ArrayList<>();
    private final Map<Integer, PotPool> pools = new HashMap<>();
    private GameState currentState = GameState.INIT;
    private int pot = 0;
    private int currentBetLevel = 0;
    private int requiredCallAmount = 0;
    private GamePlayer dealer;
    private int smallBlind = 5;
    private int bigBlind = 10;
    private GamePlayer currentActionPlayer;
    private GamePlayer lastRaiser;
    public GameContext gameContext;
    private List<GamePlayer> winners;

    public void initilizeGame(List<GamePlayer> players) {
        this.players.addAll(players);
        var initialFirstDealerIndex = (int) (Math.random() * 100) % this.players.size();
        this.dealer = players.get(initialFirstDealerIndex);
        deck.shuffle();

        // 初始化游戏上下文
        this.gameContext = new GameContext();
        this.gameContext.initialPlayers(players);
        this.gameContext.setDealer(dealer.toPlayerDTO());
        this.gameContext.setDealerIndex(initialFirstDealerIndex);
        this.gameContext.setState(this.currentState);
    }

    public void startGame() {
        // 初始化 user 手牌,公共牌, 然后洗牌发牌
        prepareNewHand();
        // 设置大小盲
        postBlinds();
    }

    public GameContext nextGame() {
        if (this.gameContext.state == GameState.SHOWDOWN || this.gameContext.state == GameState.GAME_OVER){
            this.gameContext.nextGame();
            rotateDealer();
            prepareNewHand();
            postBlinds();
            return this.gameContext;
        }else {
            throw new IllegalStateException("当前游戏还在进行中.");
        }
    }


    public GameContext onUserBetting(GameUserAction action) {
        var player = getPlayer(action.userId);
        if (player.isEmpty())
            throw new IllegalArgumentException("Betting user is not current active user.");

        var currentPlayer = player.get();
        if (!currentPlayer.isActive)
            throw new IllegalStateException("Current user is inactive.");

        if (!currentPlayer.id.equals(this.currentActionPlayer.id))
            throw new IllegalArgumentException("现在应该由" + this.currentActionPlayer.username + "话事.");

        if (action.action.equals(PlayerAction.CHECK)) {
            if (this.requiredCallAmount != 0) {
                throw new IllegalArgumentException("现在底池还未配平");
            }
        }

        if (action.action.equals(PlayerAction.CALL) && !currentPlayer.id.equals(this.gameContext.smallBlind.userId)) {
            if (action.bet < requiredCallAmount && currentPlayer.chips >= requiredCallAmount)
                throw new IllegalArgumentException("Call amount should equal required call amount: " + requiredCallAmount);
        }

        if (action.action.equals(PlayerAction.RAISE)) {
            if (action.bet < requiredCallAmount * 2 && currentPlayer.chips >= requiredCallAmount * 2)
                throw new IllegalArgumentException("Raise amount should at least equal or greater than : " + requiredCallAmount * 2);
        }

        var opool = getPotPool(action.poolId);
        if (opool.isEmpty())
            throw new NotFoundException("筹码池不存在");

        if (currentPlayer.chips < action.bet && action.action != PlayerAction.ALL_IN) {
            action.action = PlayerAction.ALL_IN;
        }

        var currentPool = opool.get();
        //处理下注
        process(currentPlayer, currentPool, action.action, action.bet);

        // 更新下注后当前 User 的状态
        this.gameContext.updatePlayer(currentPlayer);
        this.gameContext.updatePool(currentPool);

        var activePlayers = getActivePlayers();
        // Active user 只有一个人，则 Active User 自动成为 Winner
        // 自动成为 winner 的情况不需要显示手牌，所以不需要show hands
        if (activePlayers.size() == 1) {
            // 下一个 Active 的 User 就是winner
            var winner = getNextActivePlayer(currentPlayer);
            // 结算 winner 的筹码
            settlement();
            // 更新 context
            this.gameContext.setActionPlayer(winner.toPlayerDTO());
            var winners = new ArrayList<GamePlayer>();
            winners.add(winner);
            this.gameContext.setWinners(winners);
            // 更新游戏状态
            processGameState(GameState.RIVER);

            return this.gameContext;
        }

        // 处理 1 人 All in, 其余人还有筹码的情况
        // 如果剩下的 Active user 大于 2 人，并且有 all in 的 user, 那就要为没有 all in 的 player 开边池
        if (activePlayers.size() > 2 && activePlayers.stream().anyMatch(i -> i.chips == 0)) {
            var sidePoolId = this.pools.size();
            var sidePotPool = new PotPool(sidePoolId);
            this.pools.put(sidePoolId, sidePotPool);
            this.gameContext.updatePool(sidePotPool);
        }

        // 配平之后，处理是否需要推进游戏
        if (isBalanced()) {
            processGameState(this.currentState);
        }

        if (this.currentState == GameState.SHOWDOWN) {
            playerShowHands();
            settlement();
        } else if (this.currentState == GameState.GAME_OVER) {
            // Game Over 以后需要开启下一局游戏
            return nextGame();
        } else {
            // 需要将 Context 中的行动 User移动到下一位
            this.currentActionPlayer = getNextActivePlayer(this.currentActionPlayer);
            requiredCallAmount = currentBetLevel - this.currentActionPlayer.currentBet;
            this.gameContext.setActionPlayer(this.currentActionPlayer.toPlayerDTO());
            this.gameContext.requiredCallAmount = requiredCallAmount;
        }
        return this.gameContext;
    }

    private boolean isBalanced() {
        boolean allActed = players.stream()
                .filter(i -> i.isActive)
                .allMatch(i -> i.actedThisRound);

        boolean betsBalanced = players.stream()
                .filter(i -> i.isActive)
                .mapToInt(i -> i.currentBet)
                .allMatch(bet -> bet == currentBetLevel);

        boolean noPendingRaise = (lastRaiser == null) ||
                players.stream()
                        .filter(i -> i != lastRaiser)
                        .filter(i -> i.isActive)
                        .allMatch(p -> p.currentBet == currentBetLevel);

        // 当前 Active 并且还有筹码的 Player 下注相同了，就说明已经配平了
        return allActed && betsBalanced && noPendingRaise;
    }

    public void process(GamePlayer player, PotPool pool, PlayerAction action, int bet) {
        switch (action) {
            case FOLD:
                player.isActive = false;
                player.actedThisRound = true;
                break;
            case CHECK:
                player.currentBet = 0;
                player.actedThisRound = true;
                break;
            case CALL:
                player.chips -= bet;
                pool.pot += bet;
                player.currentBet += bet;
                currentBetLevel = player.currentBet;
                pool.onBetting(player);
                player.actedThisRound = true;
                break;
            case RAISE:
                player.chips -= bet;
                pool.pot += bet;
                player.currentBet += bet;
                currentBetLevel = player.currentBet;
                pool.onBetting(player);
                lastRaiser = player;
                player.actedThisRound = true;
                break;
            case ALL_IN:
                pool.pot += player.chips;
                player.currentBet += player.chips;
                player.chips = 0;
                if (player.currentBet > currentBetLevel) {
                    currentBetLevel = player.currentBet;
                }
                pool.onBetting(player);
                player.actedThisRound = true;
                break;
            default:
                break;
        }
    }

    private Optional<GamePlayer> getPlayer(String userId) {
        return this.players.stream().filter(i -> i.id.equals(userId)).findFirst();
    }

    private Optional<PotPool> getPotPool(int poolId) {
        return this.pools.values().stream().filter(i -> i.id == poolId).findFirst();
    }

    private void prepareNewHand() {
        pools.clear();
        commmunityCards.clear();
        players.forEach(p -> {
            p.resetRoundState();
            p.isActive = true;
        });
        deck.shuffle();
        dealHoleCards();
    }

    private void dealHoleCards() {
        for (GamePlayer p : players) {
            p.holeCards.add(deck.dealCard());
            p.holeCards.add(deck.dealCard());
        }
    }

    private void dealCommunityCards(int count) {
        for (int i = 0; i < count; i++) {
            deck.burnCard();
            commmunityCards.add(deck.dealCard());
        }
    }

    private void postBlinds() {
        GamePlayer smallBlindPlayer = getNextActivePlayer(dealer);
        GamePlayer bigBlindPlayer = getNextActivePlayer(smallBlindPlayer);
        int pot = 0;

        smallBlindPlayer.chips -= smallBlind;
        smallBlindPlayer.currentBet = smallBlind;
        pot += smallBlind;

        bigBlindPlayer.chips -= bigBlind;
        bigBlindPlayer.currentBet = bigBlind;
        pot += bigBlind;

        currentBetLevel = bigBlind;

        var poolIndex = this.pools.size();
        var pool = new PotPool(poolIndex);
        pool.pot = pot;
        pool.onBetting(smallBlindPlayer);
        pool.onBetting(bigBlindPlayer);
        this.pools.put(poolIndex, pool);

        this.currentState = GameState.PRE_FLOP;
        currentActionPlayer = getNextActivePlayer(bigBlindPlayer);
        this.gameContext.setSmallBlind(smallBlindPlayer.toPlayerDTO());
        this.gameContext.setBigBlind(bigBlindPlayer.toPlayerDTO());
        this.gameContext.updatePlayer(smallBlindPlayer);
        this.gameContext.updatePlayer(bigBlindPlayer);
        this.gameContext.setCurrentBetLevel(currentBetLevel);
        this.gameContext.setRequiredCallAmount(currentBetLevel);
        this.gameContext.setActionPlayer(currentActionPlayer.toPlayerDTO());
        this.gameContext.setState(this.currentState);
        this.gameContext.initialPotPool(poolIndex, pool);
    }

    private void determineWinner() {
        List<GamePlayer> activePlayers = players.stream()
                .filter(p -> p.isActive)
                .collect(Collectors.toList());

        Map<GamePlayer, EvaluatedHand> playerHands = new HashMap<>();
        for (GamePlayer p : activePlayers) {
            List<Poker> allCards = new ArrayList<>(p.holeCards);
            allCards.addAll(commmunityCards);
            List<List<Poker>> combinations = PokerHandEvaluator.generateCombinations(allCards);
            EvaluatedHand best = combinations.stream()
                    .map(PokerHandEvaluator::evaluate)
                    .max(Comparator.naturalOrder())
                    .get();
            playerHands.put(p, best);
        }

        var winner = Collections.max(playerHands.entrySet(),
                Comparator.comparing(e -> e.getValue())).getKey();

        winner.chips += pot;
        pot = 0;
    }

    private void bettingRound() {
        int startIndex = getNextActivePlayerIndex(dealer);
        int currentPlayerIndex = startIndex;
        int remainingPlayers = activePlayers();
        int raises = 0;
        int maxRaises = 3;

        do {
            GamePlayer currentPlayer = players.get(currentPlayerIndex);
            if (currentPlayer.isActive) {
                var action = getPlayerAction(currentPlayer);
                processAction(currentPlayer, action);

                if (action == PlayerAction.RAISE) {
                    raises++;
                    if (raises >= maxRaises) {
                        break;
                    }
                }
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (currentPlayerIndex != startIndex && remainingPlayers > 1);
    }

    private GamePlayer getNextActivePlayer(GamePlayer player) {
        var nextActivePlayerIndex = getNextActivePlayerIndex(player);
        return this.players.get(nextActivePlayerIndex);
    }

    private PlayerAction getPlayerAction(GamePlayer player) {
        Scanner scanner = new Scanner(System.in);

        return PlayerAction.valueOf(scanner.nextLine().toUpperCase());
    }

    private int getNextActivePlayerIndex(GamePlayer player) {
        var playerIndex = this.players.indexOf(player);
        var nextIndex = getNextIndex(playerIndex);
        var nextPlayer = this.players.get(nextIndex);
        if (!nextPlayer.isActive) {
            return getNextActivePlayerIndex(nextPlayer);
        }
        return nextIndex;
    }

    private int getNextIndex(int currentIndex) {
        return (currentIndex + 1) % players.size();
    }

    private void processAction(GamePlayer player, PlayerAction action) {
        switch (action) {
            case FOLD:
                player.isActive = false;
                break;
            case CALL:
                int amountToCall = currentBetLevel - player.currentBet;
                int actualCall = Math.min(amountToCall, player.chips);
                player.chips -= actualCall;
                pot += actualCall;
                player.currentBet += actualCall;
                break;
            case RAISE:
                int raiseAmount = currentBetLevel * 2;
                int actualRaise = Math.min(raiseAmount, player.chips);
                player.chips -= actualRaise;
                pot += actualRaise;
                currentBetLevel = player.currentBet + actualRaise;
                break;
            case ALL_IN:
                pot += player.chips;
                player.currentBet += player.chips;
                player.chips = 0;
                break;
        }
    }

    private int activePlayers() {
        return (int) players.stream().filter(p -> p.isActive).count();
    }

    private List<GamePlayer> getActivePlayers() {
        return players.stream().filter(p -> p.isActive).toList();
    }

    private void processGameState(GameState state) {
        switch (state) {
            case PRE_FLOP:
                this.currentState = GameState.FLOP;
                // 翻牌
                dealCommunityCards(3);
                this.gameContext.updateCommunityCards(this.commmunityCards);
                this.gameContext.setState(this.currentState);
                resetBettingState();
                break;
            case FLOP:
                this.currentState = GameState.TURN;
                // 转牌
                dealCommunityCards(1);
                this.gameContext.updateCommunityCards(this.commmunityCards);
                // 下一轮投注
                this.gameContext.setState(this.currentState);
                resetBettingState();
                break;
            case TURN:
                this.currentState = GameState.RIVER;
                // 河牌
                dealCommunityCards(1);
                this.gameContext.updateCommunityCards(this.commmunityCards);
                // 下一轮投注
                this.gameContext.setState(this.currentState);
                resetBettingState();
                break;
            case RIVER:
                this.currentState = GameState.SHOWDOWN;
                // 停止下注，并结算比较大小
                this.gameContext.setState(this.currentState);
                break;
            case SHOWDOWN:
                this.currentState = GameState.GAME_OVER;
                this.gameContext.setState(this.currentState);
                break;
            case GAME_OVER:
                this.currentState = GameState.INIT;
                this.gameContext.setState(this.currentState);
                break;
            default:
                break;
        }
    }

    private void playerShowHands() {
        var activatedPlayers = this.players.stream().filter(i -> i.isActive).toList();
        if (activatedPlayers.size() > 1) {
            for (GamePlayer p : activatedPlayers) {
                this.gameContext.playersShowHands(p);
            }
        }
    }

    // 结算需要两个步骤
    // 1. 如果是只剩下最后一个 Player 自动获胜的情况，则不需要比较大小，直接结算即可
    // 2. 如果只有一个底池，且是河牌之后的比牌，需要比较大小
    // 3. 如果是两个及以上的底池，需要分别按不同底池比较大小
    private void settlement() {
        var activatedPlayers = this.players.stream().filter(i -> i.isActive).toList();

        if (activatedPlayers.size() == 1) {
            poolSettlement();
        } else {
          buildPlayerBest(activatedPlayers);
          poolSettlement();
        }
    }

    private void buildPlayerBest(List<GamePlayer> activatedPlayers) {
        for (GamePlayer player : activatedPlayers) {
            var allCards = new ArrayList<Poker>(player.holeCards);
            allCards.addAll(this.commmunityCards);
            player.best = PokerHandEvaluator.generateCombinations(allCards)
                    .stream().map(PokerHandEvaluator::evaluate)
                    .max(Comparator.naturalOrder())
                    .get();
            this.gameContext.updatePlayer(player);
        }
    }

    private void poolSettlement() {
        for (PotPool pool: this.pools.values()) {
            pool.onSettlement();
            this.gameContext.updatePool(pool);
        }
    }

    private void resetBettingState() {
        currentBetLevel = 0;
        requiredCallAmount = 0;
        lastRaiser = null;
        this.players.forEach(GamePlayer::resetRoundState);
        this.gameContext.resetState();
    }

    private void rotateDealer() {
        int currentIndex = players.indexOf(dealer);
        var dealerIndex = (currentIndex + 1) % players.size();
        dealer = players.get(dealerIndex);
        this.gameContext.setDealer(dealer.toPlayerDTO());
        this.gameContext.setDealerIndex(dealerIndex);
    }
}
