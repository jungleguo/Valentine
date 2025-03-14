package com.valentine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valentine.config.CustomConfigurator;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ServerEndpoint(value = "/poker/{roomId}/{userId}")
@Component
public class PokerWebSocket {

    private static RoomManager roomManager;
    private static final Logger logger = LoggerFactory.getLogger(PokerWebSocket.class);

    @Autowired
    public void setRoomManager(RoomManager roomManager) {
        PokerWebSocket.roomManager = roomManager;
        logger.info("Poker roomManager initialized");
    }

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("roomId") String roomId,
            @PathParam("userId") String userId
    ) {
        logger.info("New connection attempt:");
        logger.info(" - Session ID: " + session.getId());
        logger.info(" - Room ID: " + roomId);
        logger.info(" - User ID: " + userId);

        if (userId == null || userId.isEmpty()) {
            System.err.println("Rejecting connection: Missing userId");
            closeSession(session, "Missing userId");
            return;
        }

        try {
            roomManager.addUserToRoom(roomId, userId, session);
            System.out.println("User added to room: " + roomId);
            broadcastSystemMessage(roomId, userId + " joined room");
        } catch (Exception e) {
            System.err.println("Error adding user to room: " + e.getMessage());
            closeSession(session, "Server error");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        RoomManager.UserInfo userInfo = roomManager.userSessions.get(session.getId());
        if (userInfo == null) return;

        try {
            JsonNode json = new ObjectMapper().readTree(message);
            String type = json.get("type").asText();
            String content = json.get("content").asText();

            switch (type) {
                case "chat":
                    broadcastChatMessage(userInfo.getRoomId(), userInfo.getUserId(), content);
                    break;
                case "action":
//                    handleGameAction(userInfo.getRoomId(), userInfo.getUserId(), content);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            sendError(session, e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        RoomManager.UserInfo userInfo = roomManager.userSessions.get(session.getId());
        if (userInfo != null) {
            broadcastSystemMessage(userInfo.getRoomId(), userInfo.getUserId() + "Left room");
            roomManager.removeUser(session);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sendError(session, throwable.getMessage());
    }

    private void sendError(Session session, String message) {
        if (session.isOpen()) {
            session.getAsyncRemote().sendText("SystemError:"+message);
        }
    }

    private void broadcastSystemMessage(String roomId, String message) {
        broadcast(roomId, "System:"+message);
    }

    private void broadcastChatMessage(String roomId, String userId, String message) {
        String jsonMsg = String.format(
                "{\"type\":\"chat\",\"user\":\"%s\",\"content\":\"%s\"}",
                userId, message
        );

        broadcast(roomId, jsonMsg);
    }

    private void broadcast(String roomId, String message) {
        roomManager.getRoomSessions(roomId)
                .forEach(session -> {
                    if (session.isOpen()) {
                        session.getAsyncRemote().sendText(message);
                    }
                });
    }

    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }
}
