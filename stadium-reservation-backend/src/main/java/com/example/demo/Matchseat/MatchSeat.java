package com.example.demo.Matchseat;

import com.example.demo.Bloc.Bloc;
import com.example.demo.FootballMatch.FootballMatch;
import com.example.demo.Place.Place;
import com.example.demo.Ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double seatPrice; // Final price for the seat in this match
    private int placeNuméro;  // Add this field to store the place number for the match seat
    private boolean isReserved;
    private boolean temporarilyReserved = false;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private FootballMatch footballMatch; // The match this seat belongs to

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place; // The specific place (seat)

    @ManyToOne
    @JoinColumn(name = "bloc_id")
    private Bloc bloc;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket; // Change from Many-to-One to One-to-One

    // Method to set the seat price by adding the match price modifier to the place price
    public void setSeatPrice() {
        if (footballMatch != null && place != null) {
            double finalPrice = place.getPrice() + footballMatch.getMatchPriceModifier();
            this.seatPrice = finalPrice;
        }
    }
}
