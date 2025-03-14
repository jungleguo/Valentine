import { useState, useEffect } from 'react';

const SERVER_ENPOINT = "localhost:8080";

export default function SocketService(roomId, userId) {

    const [pokerSocket, setWebsocket] = useState();
    const [boardCast, setBoardCast] = useState([]);

    useEffect(() => initialzie, []);

    const initialzie = () => {

    };

    const setWebSocket = ()=> {
        pokerSocket = new WebSocket(`${SERVER_ENPOINT}/${roomId}/${userId}`);
    };
    
    const connectToServer = () => {
        pokerSocket.onOpen(() => {
            console.log("Connected to server.")
        });
    }

    const sendMessage = (message) => {
        pokerSocket.send(message);
        console.log($`Send message:${message}`);
    }

    const closeConnection = () => {
        pokerSocket.close(1, 'Close by client');
        console.log(`Server connection has been closed.`)
    }

    return {
        pokerSocket,
        setWebSocket,
        connectToServer,
        sendMessage,
        closeConnection
    }
}