import Chip from './Chip';

export default function PotDisplay({ amount }) {
  return (
    <div className="pot-display">
      <h3>Total Pot:</h3>
      <Chip amount={amount} large />
    </div>
  );
}