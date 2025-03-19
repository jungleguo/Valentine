package com.valentine.DTO;

import java.util.List;

public class RoomDTO {
    public  String roomId;
    public  String roomName;
    public int maxPlayers;
    public RoomState roomState;
    public List<PlayerDTO> players;
}
