export default function Chip({ amount, large = false }) {
    const chipSize = large ? '80px' : '50px';
    
    return (
      <div 
        className="chip"
        style={{
          color: '#2a2a2a',
          width: chipSize,
          height: chipSize,
          fontSize: large ? '1.2rem' : '0.8rem'
        }}
      >
        ${amount}
      </div>
    );
  }