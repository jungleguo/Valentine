import Player from "./Player";

export default interface Pool {
    id: number;
    pot: number;
    players: Player[];
    winners: Player[];
}