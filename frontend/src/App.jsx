// src/App.jsx
import { BrowserRouter, Routes, Route, useParams } from 'react-router-dom';
import HomePage from './pages/HomePage';
import AboutPage from './pages/AboutPage';
// import Navigation  from './components/navigation'
import PokerTable from './pages/Poker';
import GameRoom from './pages/RoomPage';

// 临时显式导入（如果配置未生效）
// import React from 'react'; 

function App() {
  return (
    <BrowserRouter>
      {/* <Navigation /> */}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/about" element={<AboutPage />} />
        <Route path="*" element={<NotFound />} />
        <Route path="/rooms" element={<GameRoom />} />
        <Route path="/poker" element={<PokerTable />} />
        <Route path="/room/:roomId/poker" element={<PokerTable />} />
        {/* <Route path="/game/poker" element={<PokerTable />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

function NotFound() {
  return <h1>404 - Page Not Found</h1>;
}

export default App;