export const createDeck = () => {
    const suits = ["♠", "♥", "♦", "♣"];
    const ranks = ["2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"];
    return suits.flatMap(suit => 
      ranks.map(rank => ({
        suit,
        rank,
        id: `${rank}${suit}`,
        value: ranks.indexOf(rank) + 2
      }))
    );
  };
  
  export const shuffle = (deck) => {
    for (let i = deck.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [deck[i], deck[j]] = [deck[j], deck[i]];
    }
    return deck;
  };