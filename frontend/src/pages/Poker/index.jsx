import usePokerGame from '../../hooks/usePokerGame';
import CommunityCards from '../../components/game/CommunityCards';
import PlayerSeat from '../../components/game/PlayerSeat';
import ActionControls from '../../components/game/ActionControls';
import PotDisplay from '../../components/game/PotDisplay'
import "./poker.css"

export default function PokerTable() {
  const {
    players,
    currentUser,
    communityCards,
    pot,
    currentPlayer,
    gamePhase,
    handleBet,
    handleFold,
    progressPhase
  } = usePokerGame(6);

  return (
    <div className="poker-table">
      
      <div className="players-container">
        {players.map((player, index) => (
          <PlayerSeat
            key={index}
            player={player}
            isActive={index === currentPlayer}
            isFolded={player.isFolded}
          />
        ))}
      </div>

      <div className="game-phase">
        {
          <div>
            Current Phase: {gamePhase}
          </div>
        }
      </div>
      <CommunityCards cards={communityCards} phase={gamePhase} />

      <div className="current-user">
        {!!currentUser && <PlayerSeat
          key={0}
          player={currentUser}
          isActive={false}
          isFolded={false} />
        }
      </div>
      
      {/* <PotDisplay amount={pot} /> */}
      <ActionControls
        onBet={handleBet}
        onFold={handleFold}
        onNextPhase={progressPhase}
        phase={gamePhase}
      />
    </div>
  );
}