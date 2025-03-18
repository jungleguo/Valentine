
import './BlindTag.css'
export default function BlindTag({ tag }) {
  const tagSize = '20px';
  const background = tag == "D" ? '#3b8bc4' : tag == 'B' ? '#968d3c' : '#e7e4db';

  return (
    <div
      className="Blind-Tag"
      style={{
        background: background,
        color: '#2a2a2a',
        width: tagSize,
        height: tagSize,
        fontSize: '0.8rem'
      }}>
      {tag}
    </div>
  );

}