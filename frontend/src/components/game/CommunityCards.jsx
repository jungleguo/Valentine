export default function CommunityCards({ cards }) {
  const [card1, card2, card3, card4, card5] = [...cards];
  return (
    <div className="community-cards">
      <div key={card1?.id} className={`poker-card ${!!card1 || 'back'}`}>
        <span className={`rank ${card1?.suit}`}>{card1?.rank}</span>
        <span className={`suit ${card1?.suit}`}>{card1?.suit}</span>
      </div>
      <div key={card2?.id} className={`poker-card ${!!card2 || 'back'}`}>
        <span className={`rank ${card2?.suit}`}>{card2?.rank}</span>
        <span className={`suit ${card2?.suit}`}>{card2?.suit}</span>
      </div>
      <div key={card3?.id} className={`poker-card ${!!card3 || 'back'}`}>
        <span className={`rank ${card3?.suit}`}>{card3?.rank}</span>
        <span className={`suit ${card3?.suit}`}>{card3?.suit}</span>
      </div>
      <div key={card4?.id} className={`poker-card ${!!card4 || 'back'}`}>
        <span className={`rank ${card4?.suit}`}>{card4?.rank}</span>
        <span className={`suit ${card4?.suit}`}>{card4?.suit}</span>
      </div>
      <div key={card5?.id} className={`poker-card ${!!card5 || 'back'}`}>
        <span className={`rank ${card5?.suit}`}>{card5?.rank}</span>
        <span className={`suit ${card5?.suit}`}>{card5?.suit}</span>
      </div>
    </div>
  );
}