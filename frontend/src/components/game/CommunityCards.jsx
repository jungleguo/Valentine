export default function CommunityCards({ cards }) {
  return (
    <div className="community-cards">
      <div className={`poker-card back`}>
        <span className="rank"></span>
        <span className="suit"></span>
      </div>
      <div className={`poker-card back`}>
        <span className="rank"></span>
        <span className="suit"></span>
      </div>
      <div className={`poker-card back`}>
        <span className="rank"></span>
        <span className="suit"></span>
      </div>
      <div className={`poker-card back`}>
        <span className="rank"></span>
        <span className="suit"></span>
      </div>
      <div className={`poker-card back`}>
          <span className="rank"></span>
          <span className="suit"></span>
        </div>
    </div>
  );
}