import { useEffect, useState } from 'react';
import axios from 'axios';

const initState = {
    "currentBetLevel": 0,
    "requiredCallAmount": 0,
    "actionPlayer": {
        "userId": "player5",
        "userName": "player5",
        "isActive": true,
        "actedThisRound": false,
        "chips": 955,
        "currentBets": 0
    },
    "dealer": {
        "userId": "player6",
        "userName": "player6",
        "isActive": true,
        "actedThisRound": false,
        "chips": 1000,
        "currentBets": 0
    },
    "smallBlind": {
        "userId": "player7",
        "userName": "player7",
        "isActive": true,
        "actedThisRound": false,
        "chips": 995,
        "currentBets": 5
    },
    "bigBlind": {
        "userId": "player0",
        "userName": "player0",
        "isActive": true,
        "actedThisRound": false,
        "chips": 990,
        "currentBets": 10
    },
    "players": [
        {
            "userId": "player0",
            "userName": "player0",
            "isActive": false,
            "actedThisRound": false,
            "chips": 990,
            "currentBets": 0
        },
        {
            "userId": "player1",
            "userName": "player1",
            "isActive": false,
            "actedThisRound": false,
            "chips": 1000,
            "currentBets": 0
        },
        {
            "userId": "player2",
            "userName": "player2",
            "isActive": false,
            "actedThisRound": false,
            "chips": 1000,
            "currentBets": 0
        },
        {
            "userId": "player3",
            "userName": "player3",
            "isActive": false,
            "actedThisRound": false,
            "chips": 990,
            "currentBets": 0
        },
        {
            "userId": "player4",
            "userName": "player4",
            "isActive": true,
            "actedThisRound": true,
            "chips": 955,
            "currentBets": 0
        },
        {
            "userId": "player5",
            "userName": "player5",
            "isActive": true,
            "actedThisRound": true,
            "chips": 955,
            "currentBets": 0
        },
        {
            "userId": "player6",
            "userName": "player6",
            "isActive": false,
            "actedThisRound": false,
            "chips": 1000,
            "currentBets": 0
        },
        {
            "userId": "player7",
            "userName": "player7",
            "isActive": false,
            "actedThisRound": false,
            "chips": 995,
            "currentBets": 0
        }
    ],
    "state": "PRE_FLOP",
    "communityCards": [
        {
            "id": "4♠",
            "rank": "4",
            "suit": "♠",
            "value": 4
        },
        {
            "id": "8♦",
            "rank": "8",
            "suit": "♦",
            "value": 8
        },
        {
            "id": "8♥",
            "rank": "8",
            "suit": "♥",
            "value": 8
        },
        {
            "id": "4♥",
            "rank": "4",
            "suit": "♥",
            "value": 4
        },
        {
            "id": "9♦",
            "rank": "9",
            "suit": "♦",
            "value": 9
        }
    ],
    "pools": {
        "0": {
            "id": 0,
            "pot": 115,
            "players": [
                {
                    "userId": "player7",
                    "userName": "player7",
                    "isActive": false,
                    "actedThisRound": false,
                    "chips": 995,
                    "currentBets": 0
                },
                {
                    "userId": "player0",
                    "userName": "player0",
                    "isActive": false,
                    "actedThisRound": false,
                    "chips": 990,
                    "currentBets": 0
                },
                {
                    "userId": "player3",
                    "userName": "player3",
                    "isActive": false,
                    "actedThisRound": false,
                    "chips": 990,
                    "currentBets": 0
                },
                {
                    "userId": "player4",
                    "userName": "player4",
                    "isActive": true,
                    "actedThisRound": true,
                    "chips": 1070,
                    "currentBets": 0
                },
                {
                    "userId": "player5",
                    "userName": "player5",
                    "isActive": true,
                    "actedThisRound": true,
                    "chips": 955,
                    "currentBets": 0
                }
            ],
            "winners": [
                {
                    "userId": "player4",
                    "userName": "player4",
                    "isActive": true,
                    "actedThisRound": true,
                    "chips": 1070,
                    "currentBets": 0
                }
            ]
        }
    }
};

export default function usePokerEngine() {
    const [dealer, setDealer] = useState();
    const [actionUser, setActionUser] = useState();
    const [smallBlind, setSmallBlind] = useState();
    const [bigBlind, setBigBlind] = useState();
    const [players, setPalyers] = useState([]);
    const [userCards, setUserCards] = useState([]);
    const [communityCards, setCommunityCards] = useState([]);
    const [pools, setPools] = useState([]);
    const [gameState, setGameState] = useState();
    const [currentHand, setCurrentHand] = useState([]);
    const [error, setError] = useState();

    useEffect(() => initializeGame(initState), []);

    const initializeGame = (state) => {
        setActionUser(state.actionPlayer);
        setPalyers(state.players)
        setDealer(state.dealer);
        setSmallBlind(state.smallBlind);
        setBigBlind(state.bigBlind);
        setCommunityCards(state.communityCards);
        setPools(state.pools);
        setGameState(state.state);
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
            initializeGame(response.data);
        }catch(err) {
            setError("Failed to start game"),
            console.log(err);
        }
    }

    const handleUserAction = (action) => {

    };

    const handleBet = (amount) => {

    };

    // 弃牌处理
    const handleFold = () => {

    };

    // 阶段推进
    const progressPhase = () => {

    };

    return {
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
        handleStart,
        handleBet,
        handleFold,
        progressPhase
    }
}