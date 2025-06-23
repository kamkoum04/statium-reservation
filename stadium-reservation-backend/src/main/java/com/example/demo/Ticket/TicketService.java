package com.example.demo.Ticket;

import com.example.demo.Bloc.Bloc;
import com.example.demo.Bloc.BlocRepository;
import com.example.demo.FootballMatch.FootballMatch;
import com.example.demo.Matchseat.MatchSeat;
import com.example.demo.Matchseat.MatchSeatRepository;
import com.example.demo.User.Utilisateur;
import com.example.demo.User.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MatchSeatRepository matchSeatRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BlocRepository blocRepository; // Add this repository

    @Autowired
    private EmailService emailService;

    public List<TicketDto> createTickets(TicketRequest ticketRequest) throws Exception {
        List<TicketDto> response = new ArrayList<>();
        try {
            Utilisateur user = utilisateurRepository.findById(ticketRequest.getUserId())
                    .orElseThrow(() -> new Exception("User not found"));

            FootballMatch footballMatch = matchSeatRepository.findByFootballMatchId(ticketRequest.getFootballMatchId())
                    .orElseThrow(() -> new Exception("Match not found"));

            for (SeatRequest seatRequest : ticketRequest.getSeatRequests()) {
                Long blocId = seatRequest.getBlocId();
                int seatNumber = seatRequest.getSeatNumber();

                // Fetch the Bloc entity
                Bloc bloc = blocRepository.findById(blocId)
                        .orElseThrow(() -> new Exception("Bloc not found"));

                MatchSeat matchSeat = matchSeatRepository.findByFootballMatchIdAndPlaceNuméroAndBlocId(
                                ticketRequest.getFootballMatchId(), seatNumber, blocId)
                        .orElseThrow(() -> new Exception("Seat number " + seatNumber + " in block ID " + blocId + " not found"));

                if (matchSeat.isReserved()) {
                    throw new Exception("Seat number " + seatNumber + " in block " + bloc.getName() + " is already reserved");
                }

                matchSeat.setReserved(true);
                matchSeatRepository.save(matchSeat);

                // Create a new ticket for this match seat
                Ticket ticket = new Ticket();
                ticket.setUtilisateur(user);
                ticket.setFootballMatch(footballMatch);
                ticket.setTotalTicketPrice(matchSeat.getSeatPrice());
                ticket.setBookedSeat("Seat No: " + seatNumber + " (Block: " + bloc.getName() + ")"); // Use bloc name instead of bloc ID
                ticket.setMatchSeat(matchSeat);
                ticketRepository.save(ticket);

                // Generate PDF ticket
                String pdfFilePath = PdfTicketGenerator.generatePdfTicket(
                        footballMatch,
                        ticket.getTicketId().toString(),
                        ticket.getBookedSeat()
                );

                // Send email with PDF ticket
                emailService.sendEmailWithAttachment(
                        ticketRequest.getUserEmail(), // User's email
                        "Your Ticket Reservation",
                        "Please find your ticket attached.",
                        pdfFilePath
                );

                TicketDto ticketDto = new TicketDto();
                ticketDto.setTicketId(ticket.getTicketId());
                ticketDto.setTotalTicketPrice(matchSeat.getSeatPrice());
                ticketDto.setBookedSeats("Seat No: " + seatNumber + " (Block: " + bloc.getName() + ")"); // Use bloc name instead of bloc ID
                ticketDto.setStatusCode(200);
                ticketDto.setMessage("Ticket created and sent to your email");

                response.add(ticketDto);
            }

        } catch (Exception e) {
            TicketDto errorResponse = new TicketDto();
            errorResponse.setStatusCode(400);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setError(e.toString());
            response.add(errorResponse);
        }

        return response;
    }



    public List<TicketDto> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        List<TicketDto> ticketDtos = new ArrayList<>();

        for (Ticket ticket : tickets) {
            TicketDto ticketDto = new TicketDto();
            ticketDto.setTicketId(ticket.getTicketId());
            ticketDto.setDateDeReservation(ticket.getCreatedAt());
            ticketDto.setTotalTicketPrice(ticket.getTotalTicketPrice());
            ticketDto.setBookedSeats(ticket.getBookedSeat());
            ticketDto.setUtilisateurId(ticket.getUtilisateur().getId());
            ticketDto.setFootballMatch(ticket.getFootballMatch());
            ticketDto.setMatchSeat(ticket.getMatchSeat()); // Only one match seat per ticket
            ticketDto.setStatusCode(200);
            ticketDto.setMessage("Success");
            ticketDtos.add(ticketDto);
        }

        return ticketDtos;
    }

    public List<TicketDto> getTicketsByUserId(Long userId) {
        List<TicketDto> response = new ArrayList<>();
        try {
            Utilisateur user = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));
            List<Ticket> tickets = ticketRepository.findByUtilisateur(user);

            for (Ticket ticket : tickets) {
                TicketDto ticketDto = new TicketDto();
                ticketDto.setTicketId(ticket.getTicketId());
                ticketDto.setDateDeReservation(ticket.getCreatedAt());
                ticketDto.setTotalTicketPrice(ticket.getTotalTicketPrice());
                ticketDto.setBookedSeats(ticket.getBookedSeat());
                ticketDto.setUtilisateurId(userId);
                ticketDto.setFootballMatch(ticket.getFootballMatch());
                ticketDto.setMatchSeat(ticket.getMatchSeat());
                ticketDto.setStatusCode(200);
                ticketDto.setMessage("Success");
                response.add(ticketDto);
            }

        } catch (Exception e) {
            TicketDto errorResponse = new TicketDto();
            errorResponse.setStatusCode(400);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setError(e.toString());
            response.add(errorResponse);
        }

        return response;
    }

    public TicketDto cancelTicket(Long ticketId) {
        TicketDto response = new TicketDto();
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new Exception("Ticket not found"));

            MatchSeat matchSeat = ticket.getMatchSeat();
            matchSeat.setReserved(false);
            matchSeatRepository.save(matchSeat);

            ticketRepository.delete(ticket);
            response.setStatusCode(200);
            response.setMessage("Ticket canceled successfully");

        } catch (Exception e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            response.setError(e.toString());
        }

        return response;
    }
}
