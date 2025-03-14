// src/pages/HomePage/index.jsx
import './home.css';

export default function HomePage() {
  return (
    <div className="home-container">
      <h1>Welcome to Valentine Platform</h1>
      <div className="feature-cards">
        <div className="card">
          <h2>Game Center</h2>
          <p>Discover our featured games</p>
          <button>Explore</button>
        </div>
        <div className="card">
          <h2>Social Hub</h2>
          <p>Connect with other players</p>
          <button>Join Now</button>
        </div>
      </div>
    </div>
  );
}