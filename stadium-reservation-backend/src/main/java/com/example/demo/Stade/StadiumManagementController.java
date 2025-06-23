package com.example.demo.Stade;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class StadiumManagementController {
    @Autowired
    private StadiumManagementService stadiumManagementService;

    @PostMapping("/public/stadium/addStadium")
    public ResponseEntity<StadiumDto> addStadium(
            @RequestPart("stadiumDto") StadiumDto stadiumDto, // Remove @RequestBody
            @RequestPart("file") MultipartFile file) {
        StadiumDto response = stadiumManagementService.addStade(stadiumDto, file);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/public/stadiums")
    public ResponseEntity<StadiumDto> getAllStadiums() {
        return ResponseEntity.ok(stadiumManagementService.getAllStadiums());
    }

    @PutMapping("/admin/stadium/update/{id}")
    public ResponseEntity<StadiumDto> updateStadium(@PathVariable Long id, @RequestBody StadiumDto stadiumDto) {
        StadiumDto updatedStadium = stadiumManagementService.updateStadium(id, stadiumDto);
        return ResponseEntity.ok(updatedStadium);
    }

    @DeleteMapping("/admin/stadium/delete/{id}")
    public ResponseEntity<StadiumDto> deleteStadium(@PathVariable Long id) {
        return ResponseEntity.ok(stadiumManagementService.deleteStadium(id));
    }
}
