import { useEffect, useState } from 'react';
import axios from 'axios';

const initState = {
    "currentBetLevel": 0,
    "requiredCallAmount": 0,
    "actionPlayer": null,
    "dealer": null,
    "smallBlind": null,
    "bigBlind": null,
    "players": [],
    "state": "PRE_FLOP",
    "communityCards": [],
    "pools": {}
};

export default function usePokerEngine(roomId) {
    const [currentBetLevel, setCurrentBetLevel] = useState(0);
    const [requiredCallAmount, setRequiredCallAmount] = useState(0);
    const [dealer, setDealer] = useState();
    const [actionUser, setActionUser] = useState();
    const [smallBlind, setSmallBlind] = useState();
    const [bigBlind, setBigBlind] = useState();
    const [players, setPalyers] = useState([]);
    const [userCards, setUserCards] = useState([]);
    const [communityCards, setCommunityCards] = useState([]);
    const [pools, setPools] = useState({});
    const [gameState, setGameState] = useState();
    const [currentHand, setCurrentHand] = useState([]);
    const [error, setError] = useState();

    useEffect(() => {
        refreshRoom(roomId);
        refreshTable(roomId);
        const interval = setInterval(() => refreshTable(roomId), 10000);
        return () => clearInterval(interval);
    }, []);

    const initializeGame = (state) => {
        setActionUser(state.actionPlayer);
        setPalyers(state.players)
        setDealer(state.dealer);
        setSmallBlind(state.smallBlind);
        setBigBlind(state.bigBlind);
        setCommunityCards(state.communityCards);
        setPools(state.pools);
        setGameState(state.state);
        setRequiredCallAmount(state.requiredCallAmount);
        setCurrentBetLevel(state.currentBetLevel);
        setCurrentHand([
            {
                "id": "10♠",
                "rank": "10",
                "suit": "♠",
                "value": 10
            }, {
                "id": "6♣",
                "rank": "6",
                "suit": "♣",
                "value": 6
            }
        ])
    };

    const handleStart = async (roomId) => {
        try {
            var response = await axios.get(`http://localhost:8080/api/rooms/${roomId}/startpoker`)
            console.log("Start game response", response.data);
            initializeGame(response.data);
        } catch (err) {
            setError("Failed to start game");
        }
    }

    const refreshTable = async (roomId) => {
        try {
            var response = await axios.get(`http://localhost:8080/api/rooms/${roomId}/pokertable`)
            if (response.data)
                initializeGame(response.data);
        } catch (err) {
            setError(err);
        }
    }

    const refreshRoom = async (roomId) => {
        try {
            var response = await axios.get(`http://localhost:8080/api/rooms/${roomId}`);
            if (response.data) {
                console.log("Current room", response);
                setPalyers(response.data.gamePlayers);
            }
        } catch (err) {
            setError(err);
        }
    }

    const handleUserAction = async ({ roomId, action }) => {
        try {
            var response = await axios.post(`http://localhost:8080/api/rooms/${roomId}/process`, action);
            console.log("Handle process response", response.data);
            initializeGame(response.data);
        } catch (err) {
            setError(err);
        }
    };

    const handleBet = ({ roomId, player, amount }) => {

        let userAction = "CALL";

        if (requiredCallAmount == 0 && amount == 0)
            userAction = "CHECK";
        else if (amount >= player.chips) {
            amount = player.chips;
            userAction = "ALL_IN";
        } else if (amount == requiredCallAmount && amount < player.chips) {
            userAction = "CALL";
        } else if (amount >= requiredCallAmount * 2 && amount < player.chips) {
            userAction = "RAISE";
        }

        let poolId = getLargestPoolId();

        const action = {
            userId: player.userId,
            action: userAction,
            bet: amount,
            poolId: poolId
        };
        handleUserAction({ roomId, action });
    };

    const getLargestPoolId = () => {
        let poolId = 0;
        for (let p in Object.getOwnPropertyNames(pools)) {
            if (poolId < pools[p].id)
                poolId = pools[p].id;
        }
        return poolId;
    }

    // 弃牌处理
    const handleFold = ({ roomId, player }) => {
        const action = {
            userId: player.userId,
            action: "FOLD",
            bet: 0,
            poolId: 0
        };
        handleUserAction({ roomId, action });
    };

    const handleNextGame = async ({ roomId }) => {
        try {
            var response = await axios.get(`http://localhost:8080/api/rooms/${roomId}/pokertable/nextgame`);
            initializeGame(response.data);
        } catch (err) {
            setError(err);
        }
    }

    return {
        actionUser,
        requiredCallAmount,
        currentUser: actionUser,
        currentHand,
        dealer,
        smallBlind,
        bigBlind,
        players,
        userCards,
        communityCards,
        pools,
        gameState,
        handleBet,
        handleFold,
        handleStart,
        handleNextGame
    }
}