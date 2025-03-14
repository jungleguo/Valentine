package com.valentine.service;

import com.valentine.model.Player;
import com.valentine.model.PokerRoom;
import com.valentine.model.PokerTable;
import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RoomManager {
    private static final ConcurrentMap<String, PokerRoom> rooms = new ConcurrentHashMap<String, PokerRoom>();
    private static final int ROOM_ID_LENGTH = 6;
    private static final ConcurrentMap<String, Set<Session>> roomSessions = new ConcurrentHashMap<>();
    protected static final ConcurrentMap<String, UserInfo> userSessions = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(RoomManager.class);

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String roomId;
    }

    @Value("${room.cleanup.waiting-hours:2}")
    private int maxWaitingHours;

    @Value("${room.cleanup.inactive-minutes:30}")
    private int maxInactiveMinutes;

    public PokerRoom createRoom(Player creator) {
        String roomId = generateRoomId();
        PokerRoom room = new PokerRoom();
        room.setRoomId(roomId);
        room.setCreatorId(creator.getId());
        room.getPlayers().put(room.getPlayers().size(), creator);
        rooms.put(roomId, room);

        return room;
    }

    public PokerRoom joinRoom(String roomId, Player player) throws IllegalStateException {
        PokerRoom room = rooms.get(roomId);
        if (room == null) throw new IllegalStateException("Room doesn't exists");
        if (!room.canJoin()) throw new IllegalStateException("Room is not ready.");

        room.getPlayers().put(room.getPlayers().size(), player);
        return room;
    }

    public Optional<PokerRoom> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public PokerRoom startPoker(String roomId) {
        PokerRoom room = rooms.get(roomId);
        room.start();
        return room;
    }

    public List<PokerRoom> getRooms() {
        return rooms.values().stream().toList();
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanRooms() {
        Instant now = Instant.now();

        rooms.values().removeIf(room -> shouldCleanRoom(room, now));

        log.info("Room was cleaned: {}", rooms.size());
    }

    private boolean shouldCleanRoom(PokerRoom room, Instant now) {
        if (room.getStatus() == PokerRoom.RoomStatus.CLOSED) {
            return true;
        }

        if (room.getStatus() == PokerRoom.RoomStatus.WAITING) {
            Duration waittingDuration = Duration.between(
                    room.getCreatedTime(), now
            );

            return waittingDuration.toHours() == maxWaitingHours;
        }

        Duration inactiveDuration = Duration.between(
                room.getLastActiveTime(), now
        );

        return inactiveDuration.toMinutes() == maxInactiveMinutes;
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString().substring(0, ROOM_ID_LENGTH).toUpperCase();
    }

    public void addUserToRoom(String roomId, String userId, Session session) {
        PokerRoom room = rooms.getOrDefault(roomId, null);

        if (room == null){
            log.warn("Room doesn't exists. Please confirm.");
            return;
        }

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        userSessions.put(session.getId(), new UserInfo(userId, roomId));
    }

    public void removeUser(Session session) {
        UserInfo userInfo = userSessions.get(session.getId());
        if (userInfo != null) {
            Set<Session> rSessions = roomSessions.get(userInfo.getRoomId());
            if (rSessions != null) {
                rSessions.remove(session);
                if (rSessions.isEmpty()) {
                    roomSessions.remove(userInfo.getRoomId());
                }
            }
            userSessions.remove(session.getId());
        }
    }

    public Set<Session> getRoomSessions(String roomId) {
        return roomSessions.getOrDefault(roomId, Collections.emptySet());
    }
}
