import Card from './Card';
import User from './User';

export default interface Player extends User {
    userName: string;
    isActive: boolean;
    actedThisRound: boolean;
    chips: number;
    currentBets: number;
}