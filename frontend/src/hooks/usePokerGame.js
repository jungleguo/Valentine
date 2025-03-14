import { useState, useEffect } from 'react';
import { createDeck, shuffle } from '../utils/deck';

const INITIAL_STACK = 1000;
const BLINDS = { small: 10, big: 20 };
const PHASES = ['preflop', 'flop', 'turn', 'river', 'showdown'];

export default function usePokerGame(playersCount = 2, currrentUserId = 0) {
  const [deck, setDeck] = useState([]);
  const [players, setPlayers] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [communityCards, setCommunityCards] = useState([]);
  const [pot, setPot] = useState(0);
  const [currentPlayer, setCurrentPlayer] = useState(0);
  const [gamePhase, setGamePhase] = useState(PHASES[0]);
  // 初始化游戏
  useEffect(() => initializeGame(), []);

  const initializeGame = () => {
    const newDeck = shuffle(createDeck());
    setDeck([...newDeck]);
    
    const newPlayers = Array.from({ length: playersCount }, (_, i) => ({
      id: i,
      hand: [newDeck.pop(), newDeck.pop()],
      stack: INITIAL_STACK,
      bet: i === 0 ? BLINDS.small : BLINDS.big,
      isFolded: false
    }));
    
    const currentUser = newPlayers.find(i => i.id === currrentUserId);
    const otherPlayers = newPlayers.filter(i => i.id !== currrentUserId);
    setPlayers(otherPlayers);
    setCurrentUser(currentUser);
    setPot(BLINDS.small + BLINDS.big);
    setGamePhase(PHASES[0]);
  };

  // 下注处理
  const handleBet = (amount) => {
    setPlayers(prev => prev.map((p, i) => 
      i === currentPlayer ? { 
        ...p, 
        stack: p.stack - amount,
        bet: p.bet + amount 
      } : p
    ));
    setPot(prev => prev + amount);
    nextPlayer();
  };

  // 弃牌处理
  const handleFold = () => {
    setPlayers(prev => prev.map((p, i) => 
      i === currentPlayer ? { ...p, isFolded: true } : p
    ));
    nextPlayer();
  };

  // 阶段推进
  const progressPhase = () => {
    const currentIndex = PHASES.indexOf(gamePhase);
    if (currentIndex < PHASES.length - 1) {
      const newPhase = PHASES[currentIndex + 1];
      setGamePhase(newPhase);
      
      // 发公共牌
      if (newPhase === 'flop') dealCommunityCards(3);
      if (newPhase === 'turn') dealCommunityCards(1);
      if (newPhase === 'river') dealCommunityCards(1);
    }
  };

  // 辅助方法
  const nextPlayer = () => 
    setCurrentPlayer(prev => (prev + 1) % players.length);

  const dealCommunityCards = (count) => {
    const newCards = deck.splice(-count);
    setCommunityCards(prev => [...prev, ...newCards]);
  };

  return { 
    players, 
    currentUser,
    communityCards, 
    pot, 
    currentPlayer, 
    gamePhase,
    handleBet,
    handleFold,
    progressPhase 
  };
}