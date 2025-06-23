package com.example.demo.Ticket;

import com.example.demo.FootballMatch.FootballMatch;
import com.example.demo.Matchseat.MatchSeat;
import com.example.demo.service.BaseResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto extends BaseResponseDto {
    private Long ticketId;
    private Date dateDeReservation;
    private double totalTicketPrice;
    private String bookedSeats; // A single reserved seat description
    private Long utilisateurId; // User who made the reservation
    private FootballMatch footballMatch; // The match for which the reservation is made
    private MatchSeat matchSeat; // Each ticket corresponds to one reserved seat
}
