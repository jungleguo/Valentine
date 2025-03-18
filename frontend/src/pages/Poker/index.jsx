import usePokerEngine from '../../hooks/texasPokerEngine';
import CommunityCards from '../../components/game/CommunityCards';
import PlayerSeat from '../../components/game/PlayerSeat';
import ActionControls from '../../components/game/ActionControls';
import PotDisplay from '../../components/game/PotDisplay'
import GameStartButton from '../../components/game/GameStartButton';
import { useParams } from 'react-router-dom';
import "./poker.css"

export default function PokerTable() {
  const { roomId } = useParams();
console.log("Current roomId", roomId)
  const {
    requiredCallAmount,
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
    handleStart
  } = usePokerEngine(roomId);

  const getBlindTag = (player) => {
    if (!player)
      return null;

    if (player.userId == smallBlind.userId)
      return "S";

    if (player.userId == bigBlind.userId)
      return "B";

    if (player.userId == dealer.userId)
      return "D";

    return null;
  }

  return (
    <div className="poker-table">

      <div className="players-container">
        {players.map((player, index) => (
          <PlayerSeat
            key={index}
            player={player}
            tagDescription={getBlindTag(player)}
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
      {
        <GameStartButton
          canStart={true}
          isLoading={false}
          error={''}
          onStart={() => handleStart(roomId)}
          playersCount={2}
        />
      }
      {
        <ActionControls
          roomId={roomId}
          requiredCallAmount={requiredCallAmount}
          currentUser={currentUser}
          onBet={handleBet}
          onFold={handleFold}
          phase={gameState}
        />
      }

    </div>
  );
}