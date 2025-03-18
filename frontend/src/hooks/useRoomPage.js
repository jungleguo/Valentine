import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function useRoomPage() {
    const navigate = useNavigate();
    const [rooms, setRooms] = useState([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(null);
    const [password, setPassword] = useState();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState();
    const [players, setPlayers] = useState([]);

    // 新建房间表单状态
    const [newRoom, setNewRoom] = useState({
        name: '',
        maxPlayers: 6,
        password: ''
    });

    const fetchRooms = async () => {
        try {
            setIsLoading(true);
            const response = await axios.get('http://localhost:8080/api/rooms');
            setRooms(response.data);
        } catch (err) {
            setError('Failed to load rooms');
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        fetchRooms();
        const interval = setInterval(fetchRooms, 60000);
        return () => clearInterval(interval);
    }, []);

    const handleCreateRoom = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/rooms/create', {
                roomName: newRoom.roomName,
                creatorId: "player2",
                maxPlayers: newRoom.maxPlayers,
                roomPassword: newRoom.password
            });
            navigate(`/room/${response.data.roomId}/poker`);
        } catch (err) {
            setError("Failed to create room");
        }
    }

    const handleJoinRoom = async (roomId) => {
        try {
            if (password) {
                await axios.post(`http://localhost:8080/api/rooms/${roomId}/join`, { password });
            }
            navigate(`/room/${roomId}/poker`);
        } catch (err) {
            setError('Failed to join room');
        }
    }

    const handleShowCreateModal = (isShowModal) => {
        setShowCreateModal(isShowModal);
    }

    const handleShowJoinModal = (isShowModal) => {
        setShowJoinModal(isShowModal);
    }

    const handleSetNewRoom = ({ newRoom, roomName, maxPlayers, password }) => {
        setNewRoom({ ...newRoom, roomName, maxPlayers, password });
    }

    const handleSetPassword = (password) => {
        setPassword(password);
    }

    const handleStartGame = async (roomId) => {

        setIsLoading(true);
        try {
            var response = await axios.get(`http://localhost:8080/api/room/${roomId}/startpoker`);

            console.log("Room Page", response.data);
        } catch (err) {
            setError("开始游戏失败");
        } finally {
            setIsLoading(false);
        }
    }

    if (isLoading) return <div>Loading rooms...</div>;
    if (error) return <div className="error">{error}</div>;

    return {
        rooms,
        newRoom,
        password,
        isLoading,
        showCreateModal,
        showJoinModal,
        handleShowCreateModal,
        handleShowJoinModal,
        handleSetNewRoom,
        handleCreateRoom,
        handleJoinRoom,
        handleSetPassword
    };
}