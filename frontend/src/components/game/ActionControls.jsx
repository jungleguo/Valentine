import { useState } from 'react';

export default function ActionControls({ 
    onBet, 
    onFold, 
    onNextPhase,
    phase 
  }) {
    const [betAmount, setBetAmount] = useState(0);
  
    return (
      <div className="controls">
        {phase !== 'showdown' && (
          <>
            <input
              type="number"
              value={betAmount}
              onChange={(e) => setBetAmount(Number(e.target.value))}
              placeholder="Enter bet amount"
            />
            <button onClick={() => onBet(betAmount)}>Bet</button>
            <button onClick={onFold}>Fold</button>
          </>
        )}
        
        {['flop', 'turn', 'river'].includes(phase) && (
          <button onClick={onNextPhase}>
            Next Phase ({phase.toUpperCase()})
          </button>
        )}
      </div>
    );
  }