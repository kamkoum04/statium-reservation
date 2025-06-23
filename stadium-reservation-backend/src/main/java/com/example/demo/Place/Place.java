package com.example.demo.Place;

import com.example.demo.Matchseat.MatchSeat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long place_id;
    private int placeNuméro;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "Bloc_id")
    private com.example.demo.Bloc.Bloc Bloc;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MatchSeat> matchSeats; // One place can be linked to many MatchSeats

}
