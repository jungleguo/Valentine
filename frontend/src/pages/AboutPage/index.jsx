// src/pages/AboutPage/index.jsx
import './about.css';

export default function AboutPage() {
  return (
    <div className="about-container">
      <h1>About Valentine Platform</h1>
      <div className="content-section">
        <p>A dedicated platform for interactive experiences and social connections.</p>
        <div className="features">
          <div className="feature-item">
            <h3>Multiplayer Games</h3>
            <p>Real-time gaming experience</p>
          </div>
          <div className="feature-item">
            <h3>Social Features</h3>
            <p>Chat, groups and events</p>
          </div>
        </div>
      </div>
    </div>
  );
}