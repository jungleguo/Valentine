import './GameStartButton.css';


export default function GameStartButton({
    canStart, isLoading, error, onStart, playersCount
}) {
    const getButtonText = () => {
        if (isLoading) return '游戏准备中...';
        if (playersCount < 2) return "至少需要2名玩家";
        if (!canStart) return "等待玩家准备...";
        return "开始游戏";
    };

    return (
        <div className="start-game-section">
            <button
                className={`start-button ${canStart ? 'active' : ''}`}
                onClick={onStart}
                disabled={!canStart || isLoading}
            >
                {isLoading ? (
                    <div className="loader" />
                ) : (
                    <span>{getButtonText()}</span>
                )}
            </button>
            {error && <div className="error-message">{error}</div>}
            <div className="start-hint">
                {canStart && "所有玩家已准备，点击开始发牌"}
                {playersCount < 2 && `当前玩家:${playersCount}/2`}
            </div>
        </div>
    )
}