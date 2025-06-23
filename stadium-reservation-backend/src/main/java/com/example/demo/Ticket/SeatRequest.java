package com.example.demo.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatRequest {
    private Integer seatNumber;
    private String blocName; // If you're still using block names in some cases
    private Long blocId;     // Add this field to pass block IDs
}
