import usePokerEngine from '../../hooks/texasPokerEngine';
import CommunityCards from '../../components/game/CommunityCards';
import PlayerSeat from '../../components/game/PlayerSeat';
import ActionControls from '../../components/game/ActionControls';
import PotDisplay from '../../components/game/PotDisplay'
import GameStartButton from '../../components/game/GameStartButton';
import GameActionButton from '../../components/game/GameActionButton';
import { useParams } from 'react-router-dom';
import "./poker.css"
import PotPool from '../../components/game/PotPool';

export default function PokerTable() {
  const { roomId } = useParams();
  const {
    actionUser,
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
    handleStart,
    handleNextGame
  } = usePokerEngine(roomId);

  const getBlindTag = (player) => {
    if (!player)
      return null;

    if (!!smallBlind && player.userId == smallBlind.userId)
      return "S";

    if (!!bigBlind && player.userId == bigBlind.userId)
      return "B";

    if (!!dealer && player.userId == dealer.userId)
      return "D";

    return null;
  }

  console.log("RequireCallAmount", requiredCallAmount);
  console.log("Poker table", players)
  return (
    <div className="poker-table">

      <div className="players-container">
        {players.map((player, index) => (
          <PlayerSeat
            key={index}
            player={player}
            handCards={player?.cards}
            tagDescription={getBlindTag(player)}
            isActive={!!actionUser && !player?.isAllin && player.userId === actionUser.userId}
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
        {
          <PotPool PotPools={pools} />
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
        <GameActionButton
          roomId={roomId}
          buttonText={'下一局'}
          action={handleNextGame}
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