import Chip from './Chip';

export default function PlayerSeat({ player, handCards, isActive, isFolded }) {
  const [handCard1, handCard2] = handCards || [];
  console.log(handCard1)
  return (
    <div className={`player-seat ${isActive ? 'active' : ''} ${isFolded ? 'folded' : ''}`}>
      <div className="player-info">
        <div className='player-score'>
          <span>Stack:</span>
          <Chip amount={player.stack} />
        </div>
        <div className='player-score'>
          <span>Bet: </span>
          <Chip amount={player.bet} />
        </div>
      </div>
      <div className="player-hand">
        <div key={handCard1?.id} className={`poker-card ${!!handCard1 || "back"}`}>
          <span className={`rank ${handCard1?.suit}`}>{handCard1?.rank}</span>
          <span className={`suit ${handCard1?.suit}`}>{handCard1?.suit}</span>
        </div>
        <div key={handCard2?.id} className={`poker-card ${!!handCard2 || "back"}`}>
          <span className={`rank ${handCard2?.suit}`}>{handCard2?.rank}</span>
          <span className={`suit ${handCard2?.suit}`}>{handCard2?.suit}</span>
        </div>
      </div>
    </div>
  );
}