import User from "./User";

export default interface Room {
    id:string;
    status: string;
    members: User[];
    memberCount: number
}