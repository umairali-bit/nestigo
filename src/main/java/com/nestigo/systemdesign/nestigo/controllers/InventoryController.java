package com.nestigo.systemdesign.nestigo.controllers;


import com.nestigo.systemdesign.nestigo.dtos.InventoryDTO;
import com.nestigo.systemdesign.nestigo.dtos.InventoryUpdateRequestDTO;
import com.nestigo.systemdesign.nestigo.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));

    }

    @PatchMapping("rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId,
                                                @RequestBody InventoryUpdateRequestDTO inventoryUpdateRequestDTO) {

        inventoryService.updateInventory(roomId, inventoryUpdateRequestDTO);
        return ResponseEntity.noContent().build();

    }












}
