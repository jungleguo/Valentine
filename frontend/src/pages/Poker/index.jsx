import usePokerEngine from '../../hooks/texasPokerEngine';
import CommunityCards from '../../components/game/CommunityCards';
import PlayerSeat from '../../components/game/PlayerSeat';
import ActionControls from '../../components/game/ActionControls';
import PotDisplay from '../../components/game/PotDisplay'
import "./poker.css"

export default function PokerTable() {
  const {
    currentUser,
    currentHand,
    dealer,
    smallBlind,
    bigBlind,
    players,
    userCards,
    communityCards,
    pools,
    gameState,
    handleBet,
    handleFold,
    progressPhase
  } = usePokerEngine();

  return (
    <div className="poker-table">

      <div className="players-container">
        {players.map((player, index) => (
          <PlayerSeat
            key={index}
            player={player}
            isActive={player.userId === currentUser.userId}
            isFolded={player.isFolded}
          />
        ))}
      </div>

      <div className="game-phase">
        {
          <div>
            Current Phase: {gameState}
          </div>
        }
      </div>
      <CommunityCards cards={communityCards} phase={gameState} />

      <div className="current-user">
        {!!currentUser && <PlayerSeat
          key={currentUser.userId}
          player={currentUser}
          handCards={currentHand}
          isActive={false}
          isFolded={false} />
        }
      </div>

      {/* <PotDisplay amount={pot} /> */}
      <ActionControls
        onBet={handleBet}
        onFold={handleFold}
        onNextPhase={progressPhase}
        phase={gameState}
      />
    </div>
  );
}