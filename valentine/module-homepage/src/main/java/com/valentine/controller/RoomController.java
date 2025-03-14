package com.valentine.controller;

import com.valentine.model.GameUserAction;
import com.valentine.model.Player;
import com.valentine.model.PokerRoom;
import com.valentine.model.UserAction;
import com.valentine.service.RoomManager;
import jakarta.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomManager roomManager;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody Player creator) {
        System.out.println("Create one room");
        PokerRoom room = roomManager.createRoom(creator);
        return ResponseEntity.ok(Map.of(
                "roomId", room.getRoomId(),
                "players", room.getPlayers()
        ));
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> joinRoom(
            @PathVariable String roomId,
            @RequestBody Player player
    ) {
        try {
            PokerRoom room = roomManager.joinRoom(roomId, player);
            return ResponseEntity.ok(room);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomInfo(
            @PathVariable String roomId
    ) {
        return roomManager.getRoom(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{roomId}/startpoker")
    public ResponseEntity<?> startPoker(
            @PathVariable String roomId
    ) {
        PokerRoom room = roomManager.startPoker(roomId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}/pokertable")
    public ResponseEntity<?> getPokerTable(
            @PathVariable String roomId
    ) {
        var room = roomManager.getRoom(roomId);
        return room.map(r -> ResponseEntity.ok(r.getGameContext()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roomId}/pokertable")
    public ResponseEntity<?> pokerAction(
            @PathVariable String roomId,
            @QueryParam("index") int index,
            @RequestBody UserAction action
    ) {
        var room = roomManager.getRoom(roomId);
        return room.map(r -> ResponseEntity.ok(r.handleUserAction(index, action)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roomId}/process")
    public ResponseEntity<?> processAction(
            @PathVariable String roomId,
            @RequestBody GameUserAction action
    ){
        var room = roomManager.getRoom(roomId);
        return room.map(r -> ResponseEntity.ok(r.processAction(action)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<?> getRooms() {
        List<PokerRoom> rooms = roomManager.getRooms();
        System.out.println("get Rooms");
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/test-connect")
    public ResponseEntity<String> testWebSocketConnection() {
        return ResponseEntity.ok("WebSocket endpoint available at: ws://localhost:8080/poker/{roomId}?userId=YOUR_ID");
    }
}
