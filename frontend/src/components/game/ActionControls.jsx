import { useState, useEffect } from 'react';

export default function ActionControls({
  currentUser,
  roomId,
  requiredCallAmount,
  onBet,
  onFold,
  phase
}) {
  const [betAmount, setBetAmount] = useState(requiredCallAmount);
  const [isFirstInput, setIsFirstInput] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setBetAmount(requiredCallAmount);
    setIsFirstInput(true);
    setError('');
  }, [requiredCallAmount]);

  const validate = (amount) => {
    const errors = [];

    if (isFirstInput && amount < 2 * requiredCallAmount) {
      errors.push(`首次加注至少需要${2 * requiredCallAmount}`);
    }

    if (amount < requiredCallAmount) {
      errors.push(`金额不能小于${requiredCallAmount}`);
    }

    if (amount % 5 !== 0) {
      errors.push(`每次涨幅不能小于 $5.`)
    }
    return errors;
  }

  const handleChange = (e) => {
    const value = e.target.value;

    if (value === '') {
      setBetAmount(requiredCallAmount);
      setError('');
      return;
    }

    const num = Number(value);

    if (isFirstInput) {
      if (num < 2 * requiredCallAmount) {
        setError(`首次加注至少需要 ${2 * requiredCallAmount}`);
      } else {
        setError('');
        setIsFirstInput(false);
      }
    }

    setBetAmount(num);
  }

  const isDisabled = () => {
    return error || 
      betAmount % 5 !== 0 || 
      betAmount < requiredCallAmount ||
      (isFirstInput && betAmount < 2 * requiredCallAmount)
  };

  return (
    <div className="controls">
      {(phase !== 'SHOWDOWN' || phase !== 'GAME_OVER') && (
        <>
          <input
            type="number"
            value={betAmount === requiredCallAmount ? '' : betAmount}
            onChange={handleChange}
            placeholder={`${requiredCallAmount}`}
            min={isFirstInput ? 2 * requiredCallAmount : 0}
            step="5"
          />
          {/* {error && <div className="error-message">{error}</div>} */}
          <button onClick={() => onBet({ roomId, player: currentUser, amount: betAmount })}>Bet</button>
          <button onClick={() => onFold({ roomId, player: currentUser })}>Fold</button>
        </>
      )}
    </div>
  );
}