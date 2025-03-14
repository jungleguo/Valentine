import Card from './Card';
import User from './User';

export default interface Player extends User {
    name: string;
    stack: number;
    action: "call" | "fold" | "check";
    status: "active" | "inactive";
    decks: Card[];
}