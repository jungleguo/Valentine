export const evaluateHand = (hand, community) => {
    const allCards = [...hand, ...community];
    
    // 实现牌型判断逻辑
    const isFlush = () => {
      const suitCount = {};
      allCards.forEach(c => suitCount[c.suit] = (suitCount[c.suit] || 0) + 1);
      return Object.values(suitCount).some(count => count >= 5);
    };
  
    // 返回示例结构
    return {
      rank: 'Flush',
      strength: 5,
      kickers: ['A', 'K']
    };
  };