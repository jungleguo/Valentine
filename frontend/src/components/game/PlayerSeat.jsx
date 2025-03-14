import Chip from './Chip';

export default function PlayerSeat({ player, isActive, isFolded }) {
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
        {player.hand.map(card => (
          <div key={card.id} className="poker-card">
            <span className={`rank ${card.suit}`}>{card.rank}</span>
            <span className={`suit ${card.suit}`}>{card.suit}</span>
          </div>
        ))}
      </div>
    </div>
  );
}