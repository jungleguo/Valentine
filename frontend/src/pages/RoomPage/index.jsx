import useRoomPage from '../../hooks/useRoomPage';
import './room.css';

export default function GameRoom() {

    const {
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
    } = useRoomPage();

    return (
        <div className="room-list-container">
            {/* 头部操作栏 */}
            <div className="room-header">
                <h1>Poker Rooms</h1>
                <button onClick={() => handleShowCreateModal(true)}>Create New Room</button>
            </div>

            {/* 房间列表表格 */}
            <table className="room-table">
                <thead>
                    <tr>
                        <th>Room Name</th>
                        <th>Players</th>
                        <th>Status</th>
                        <th>Password</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {!!rooms && rooms.map(room => (
                        <tr key={room.roomId}>
                            <td>{room.roomName}</td>
                            <td>{room.currentPlayers}/{room.maxPlayers}</td>
                            <td>{room.status}</td>
                            <td>{room.hasPassword ? '🔒' : '❌'}</td>
                            <td>
                                <button
                                    onClick={() => room.hasPassword
                                        ? handleShowJoinModal(room.roomId)
                                        : handleJoinRoom(room.roomId)
                                    }
                                >
                                    Join
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* 创建房间模态框 */}
            {showCreateModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Create New Room</h2>
                        <form onSubmit={handleCreateRoom}>
                            <label>
                                Room Name:
                                <input
                                    type="text"
                                    required
                                    value={newRoom.roomName}
                                    onChange={e => handleSetNewRoom({ ...newRoom, roomName: e.target.value })}
                                />
                            </label>

                            <label>
                                Max Players:
                                <select
                                    value={newRoom.maxPlayers}
                                    onChange={e => handleSetNewRoom({ ...newRoom, maxPlayers: Number(e.target.value) })}
                                >
                                    {[2, 4, 6, 8].map(num => (
                                        <option key={num} value={num}>{num}</option>
                                    ))}
                                </select>
                            </label>

                            <label>
                                Password (optional):
                                <input
                                    type="password"
                                    value={newRoom.password}
                                    onChange={e => handleSetNewRoom({ ...newRoom, password: e.target.value })}
                                />
                            </label>

                            <div className="modal-actions">
                                <button type="submit">Create</button>
                                <button type="button" onClick={() => handleShowCreateModal(false)}>
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* 加入房间密码模态框 */}
            {showJoinModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Enter Password</h2>
                        <input
                            type="password"
                            placeholder="Room password"
                            value={password}
                            onChange={e => handleSetPassword(e.target.value)}
                        />
                        <div className="modal-actions">
                            <button onClick={() => handleJoinRoom(showJoinModal)}>Join</button>
                            <button onClick={() => handleShowJoinModal(null)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}