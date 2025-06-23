package com.example.demo.FootballMatch;

import com.example.demo.Matchseat.MatchSeat;
import com.example.demo.Stade.Stadium;
import com.example.demo.Ticket.Ticket;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FootballMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;
    private LocalTime time;
    private String equipe1;
    private String equipe2;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String equipe1Logo;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String equipe2Logo;


    // Price modifier for this specific match
    private double matchPriceModifier;  // This will be added to the place price

    @ManyToOne
    @JoinColumn(name = "stade_id")
    private Stadium stadium;

    @OneToMany(mappedBy = "footballMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MatchSeat> matchSeats;

    @OneToMany(mappedBy = "footballMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ticket> tickets;

    public double calculateSeatPrice(double placePrice) {
        return placePrice + this.matchPriceModifier;
    }
}
