package com.example.demo.Ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/public/reserve")
    public ResponseEntity<?> reserveTickets(@RequestBody TicketRequest ticketRequest) {
        List<TicketDto> response = null;
        try {
            response = ticketService.createTickets(ticketRequest);
            if (response != null && !response.isEmpty()) {
                return ResponseEntity.status(response.get(0).getStatusCode()).body(response);
            } else {
                return ResponseEntity.status(400).body("Error: Empty response");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/admin/ticket")
    public ResponseEntity<?> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/public/ticket/{userId}")
    public ResponseEntity<?> getTicketsByUserId(@PathVariable Long userId) {
        List<TicketDto> response = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/public/delete/{ticketId}")
    public ResponseEntity<?> cancelTicket(@PathVariable Long ticketId) {
        TicketDto response = ticketService.cancelTicket(ticketId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
