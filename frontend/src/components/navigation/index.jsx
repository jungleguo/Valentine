import { Link } from 'react-router-dom';

export default function Navigation() {
  return (
    <nav className="main-nav">
      <Link to="/">Home</Link>
      <Link to="/about">About</Link>
    </nav>
  );
}