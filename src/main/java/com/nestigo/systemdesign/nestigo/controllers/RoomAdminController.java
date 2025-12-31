package com.nestigo.systemdesign.nestigo.controllers;


import com.nestigo.systemdesign.nestigo.dtos.RoomDTO;
import com.nestigo.systemdesign.nestigo.services.HotelService;
import com.nestigo.systemdesign.nestigo.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomAdminController {

    private final RoomService roomService;
    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom (@RequestBody RoomDTO roomDTO,
                                               @PathVariable Long hotelId) {
        log.info("Creating a new room with id: " + roomDTO.getId());

        RoomDTO room = roomService.createRoom(hotelId, roomDTO);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRoomsInHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getAllRoomsByHotelId(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById (@PathVariable Long roomId,
                                                @PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RoomDTO> deleteRoomById (@PathVariable Long id,
                                                   @PathVariable Long hotelId){
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }


}

