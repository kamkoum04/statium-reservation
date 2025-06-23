package com.example.demo.FootballMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FootballMatchController {

   @Autowired
   private FootballMatchService footballMatchService;

   // Add a new football match
   @PostMapping("/public/footballMatch/addFootball")
   public ResponseEntity<FootballMatchDto> addFootball(
           @RequestPart("footballMatchDto") FootballMatchDto footballMatchDto,
           @RequestPart("equipe1Logo") MultipartFile file1,
           @RequestPart("equipe2Logo") MultipartFile file2) {

      FootballMatchDto createdMatch = footballMatchService.addFootballMatch(footballMatchDto, file1, file2);
      if (createdMatch.getStatusCode() == 200) {
         return ResponseEntity.status(201).body(createdMatch); // Return 201 Created
      } else {
         return ResponseEntity.status(createdMatch.getStatusCode()).body(createdMatch);
      }
   }


   // Get all football matches
   @GetMapping("/public/FootballMatch")
   public ResponseEntity<List<FootballMatchDto>> getAllFootballMatch() { // Return List properly
      List<FootballMatchDto> matches = footballMatchService.getAllFootballMatches();
      return ResponseEntity.ok(matches); // 200 OK with a list
   }

   // Update a football match
   @PutMapping("/admin/footballMatch/update/{id}")
   public ResponseEntity<FootballMatchDto> updateFootballMatch(@PathVariable Long id, @RequestBody FootballMatchDto footballMatchDto) {
      FootballMatchDto updatedMatch = footballMatchService.updateFootballMatch(id, footballMatchDto);
      return ResponseEntity.ok(updatedMatch); // 200 OK with the updated match
   }

   // Delete a football match
   @DeleteMapping("/admin/footballMatch/delete/{id}")
   public ResponseEntity<FootballMatchDto> deleteFootballMatch(@PathVariable Long id) {
     return  ResponseEntity.ok(footballMatchService.deleteFootballMatch(id));
   }
}
