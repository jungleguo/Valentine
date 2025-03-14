// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';  // 自动识别 .jsx 扩展名

// 获取挂载节点
const rootElement = document.getElementById('root');

// 创建根节点并渲染应用
const root = ReactDOM.createRoot(rootElement);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);