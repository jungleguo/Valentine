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

    public void start() {
        if (pokerTable == null)
            pokerTable = new PokerTable(this.players);

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
        return dto;
    }
}
