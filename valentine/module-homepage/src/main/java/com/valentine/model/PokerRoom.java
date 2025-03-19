package com.valentine.model;

import com.valentine.DTO.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PokerRoom {
    private String roomId;
    private String roomName;
    private String creatorId;
    private Instant createdTime = Instant.now();
    private Instant lastActiveTime = Instant.now();
    private Map<Integer, Player> players = new ConcurrentHashMap<>();
    private int maxPlayers = 8;
    private RoomStatus status = RoomStatus.WAITING;
    private List<GamePlayer> gamePlayers = new ArrayList<>();

    private PokerTable pokerTable = new PokerTable();

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public void updateActiveTime() {
        this.lastActiveTime = Instant.now();
    }

    public RoomStatus getStatus() {
        return this.status;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Instant getLastActiveTime() {
        return lastActiveTime;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void start() {
        if (pokerTable == null)
            pokerTable = new PokerTable(this.players, this.gamePlayers);

        pokerTable.startGame();
    }

    public PokerTable handleUserAction(int index, UserAction action) {
        pokerTable.handleAction(index, action);
        return pokerTable;
    }

    public GameContext processAction(GameUserAction action) {
        return pokerTable.processAction(action);
    }

    public PokerTable getPokerTable() {
        return pokerTable;
    }

    public GameContext getGameContext() {
        return pokerTable.getGameContext();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public enum RoomStatus {WAITING, IN_GAME, CLOSED}

    // 构造器/getters/setters
    public boolean canJoin() {
        return status == RoomStatus.WAITING && players.size() < maxPlayers;
    }

    public RoomDTO ToDTO() {
        var dto = new RoomDTO();
        dto.roomId = this.roomId;
        dto.roomName = this.roomName;
        dto.maxPlayers = this.maxPlayers;
        dto.players = this.gamePlayers.stream().map(GamePlayer::toPlayerDTO).toList();
        return dto;
    }
}
