import Card from "./Card";
import { GameState } from "./GameState";
import Player from "./Player";
import Pool from "./Pool";

export default interface GameContext {
    state: GameState;
    currentBetLevel: number;
    requiredCallAmount: number;
    actionPlayer: Player;
    lastRaiser?: Player;
    dealer: Player;
    smallBlind: Player;
    bigBlind: Player;
    players: Player[];
    communityCards: Card[];
    pools: { };
}