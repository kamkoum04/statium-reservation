package com.example.demo.Ticket;

import lombok.Data;
import java.util.List;

@Data
public class TicketRequest {
    private Long userId;
    private Long footballMatchId;
    private String userEmail; // Add this field

    private List<SeatRequest> seatRequests;
}
