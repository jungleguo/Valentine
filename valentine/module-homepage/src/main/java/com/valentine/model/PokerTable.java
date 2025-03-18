package com.valentine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.valentine.service.TexasHoldemEngine;
import jakarta.ws.rs.NotFoundException;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
public class PokerTable {
    private int round = 0;
    private int dealerIndex = 0;
    private int currentActiveIndex;
    @JsonIgnore
    private List<Poker> deck;
    @JsonIgnore
    private Queue<Poker> shuffledDeck;
    private final Map<Integer, Player> players = new HashMap<>();
    private final Map<Integer, ChipPool> chipPools = new HashMap<>();
    private final List<Poker> communityPokers = new ArrayList<>();
    private TexasHoldemEngine holdemEngine = new TexasHoldemEngine();
    public GameContext gameContext;

    PokerTable() {
        this.deck = Poker.createDeck();
    }

    PokerTable(Map<Integer, Player> players) {
        this.players.putAll(players);
    }

    public GameContext getGameContext() {
        if (this.holdemEngine == null)
            throw new NotFoundException("未找到游戏");

        return this.holdemEngine.gameContext;
    }

    public void startGame() {
        if (deck == null)
            deck = Poker.createDeck();

        var gamePlayers = initialUsers();
        this.holdemEngine.initilizeGame(gamePlayers);
        this.holdemEngine.startGame();

    }

    public GameContext processAction(GameUserAction action) {
        this.holdemEngine.onUserBetting(action);

        return this.holdemEngine.gameContext;
    }

    private void initialGameContext() {
        this.gameContext = new GameContext();
    }

    public void onPlayerAction() {

    }

    public void nextRound() {
        // Next dealer
//        licenseCommunity(this.shuffledDeck);

    }

    private int getNextDealerIndex() {
        int nextDealerIndex = dealerIndex + 1;
        // If index > size() means complete a cycle
        // dealer should back to the first user.
        if (nextDealerIndex >= players.size())
            nextDealerIndex = 0;

        return nextDealerIndex;
    }

    public void initialUserRole(int dealerIndex) {
        Player dealer = this.players.get(dealerIndex);
        dealer.setRole(Player.UserRole.DEALER);

        var smallBlind = this.players.get(dealerIndex + 1);
        smallBlind.setRole(Player.UserRole.SMALL_BLIND);
        smallBlind.setBet(5);

        var bigBlind = this.players.get(dealerIndex + 2);
        bigBlind.setRole(Player.UserRole.BIG_BLIND);
        bigBlind.setBet(10);

        var gun = this.players.get(dealerIndex + 3);
        gun.setRole(Player.UserRole.GUN);
        gun.setIsActive(true);

        shuffledDeck = shuffle(dealer, deck);

        license(shuffledDeck, this.players);

        onInitChipPool(smallBlind);
        onInitChipPool(bigBlind);
    }

    public void handleAction(
            int index,
            UserAction action) {

        if (action == null)
            return;

        if (action.getUserId() == null)
            return;

        if (index != currentActiveIndex)
            return;

        Player player = players.get(index);
        if (player == null || !player.getId().equals(action.getUserId()))
            return;

        if (action.action.equals(UserAction.Action.CHECK)) {
            onPlayerCheck(player);
        } else if (action.action.equals(UserAction.Action.CALL)) {
            onPlayerCall(player, action.getChips());
        } else if (action.action.equals(UserAction.Action.RAISE)) {
            onPlayerRaise(player, action.getChips());
        } else if (action.action.equals(UserAction.Action.FOLD)) {
            onUserFold(player);
        }

        if (isPoolBalanced()) {
            if (player.getRole().equals(Player.UserRole.SMALL_BLIND) && this.round == 0) {
                licenseCommunity(shuffledDeck, 3);
                this.round = this.round + 1;
            }
            this.currentActiveIndex = getNextActiveIndex(this.currentActiveIndex);
        }
    }

    private int getNextActiveIndex(int index) {
        var preNextIndex = index + 1;
        var preNextPlayer = players.get(preNextIndex);
        if (preNextPlayer.status.equals(Player.PlayerStatus.FOLD)) {
            return getNextActiveIndex(preNextIndex);
        } else {
            preNextPlayer.setIsActive(true);
            return preNextIndex;
        }
    }

    public void onUserFold(Player player) {
        player.fold();
    }

    public void onPlayerCheck(Player player) {
        player.check();
        for (ChipPool pool : chipPools.values()) {
            pool.onPlayerBet(player);
        }
    }

    public boolean isPoolBalanced() {
        for (ChipPool pool : chipPools.values()) {
            if (!pool.isBalanced)
                return false;
        }
        return true;
    }

    public void onPlayerCall(Player player, int bet) {
        player.call(bet);
        for (ChipPool pool : chipPools.values()) {
            pool.onPlayerBet(player);
        }
    }

    public void onPlayerRaise(Player player, int bet) {
        player.raise(bet);
        for (ChipPool pool : chipPools.values()) {
            pool.onPlayerBet(player);
        }
    }

    public void onInitChipPool(Player player) {
        for (ChipPool pool : chipPools.values()) {
            pool.onPlayerBet(player);
        }
    }

    private List<GamePlayer> initialUsers() {
        List<GamePlayer> gamePlayers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GamePlayer user = new GamePlayer(
                    "player" + i,
                    "player" + i,
                    1000
            );
            gamePlayers.add(user);
        }

        return gamePlayers;
    }

    private int chooseDealer() {
        int seed = getASeed();
        return seed % 6;
    }

    private int getASeed() {
        Instant now = Instant.now();
        int seed = (int) now.toEpochMilli() % 100;
        Random random = new Random(seed);
        return random.nextInt();
    }

    private Queue<Poker> shuffle(Player player, List<Poker> deck) {
        player.shuffle(deck);
        return new LinkedList<>(deck);
    }

    private void licenseCommunity(Queue<Poker> shuffledPokers, int count) {
        communityPokers.addAll(poll(count, shuffledPokers));
    }

    private void license(Queue<Poker> shuffledDeck, Map<Integer, Player> players) {
        for (Player player : players.values()) {
            player.license(poll(2, shuffledDeck));
        }
    }

    private List<Poker> poll(int pollCount, Queue<Poker> shuffledDeck) {
        List<Poker> pokers = new ArrayList<>();
        for (int i = 0; i < pollCount; i++) {
            pokers.add(shuffledDeck.poll());
        }
        return pokers;
    }
}
