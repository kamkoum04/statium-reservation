package com.example.demo.Ticket;

import com.example.demo.FootballMatch.FootballMatch;
import com.example.demo.Matchseat.MatchSeat;
import com.example.demo.User.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketId;
    private double totalTicketPrice;
    private String BookedSeat;

    @CreationTimestamp
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "footballMatch_id")
    private FootballMatch footballMatch;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL)
    @JsonIgnore
    private MatchSeat matchSeat; // Change from List to a single MatchSeat
}
