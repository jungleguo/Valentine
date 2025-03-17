import User from "./User";

export default interface Room {
    roomId:string;
    roomName: string;
    status: string;
    members: User[];
    memberCount: number
}