import './GameActionButton.css';

export default function GameActionButton({
    isVisiable, 
    isLoading, 
    error, 
    roomId,
    buttonText,
    action
}) {
    return (
        <div className="game-section">
            <button
                className={`action-button`}
                onClick={() => action({ roomId })}
                disabled={isLoading}
            >
                {isLoading ? (
                    <div className="loader" />
                ) : (
                    <span>{buttonText}</span>
                )}
            </button>
            {error && <div className="error-message">{error}</div>}
        </div>
    )
}