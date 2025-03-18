import { useState } from 'react';

export default function ActionControls({
  currentUser,
  roomId,
  requiredCallAmount,
  onBet,
  onFold,
  phase
}) {
  const [betAmount, setBetAmount] = useState(requiredCallAmount);

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
          <button onClick={() => onBet({ roomId, player: currentUser, amount: betAmount })}>Bet</button>
          <button onClick={() => onFold({ roomId, player: currentUser })}>Fold</button>
        </>
      )}
    </div>
  );
}