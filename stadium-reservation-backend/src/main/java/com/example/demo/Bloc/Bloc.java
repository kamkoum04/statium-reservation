package com.example.demo.Bloc;


import com.example.demo.Matchseat.MatchSeat;
import com.example.demo.Place.Place;
import com.example.demo.Stade.Stadium;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

public class Bloc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int totalPlace;
    private double defaultPrice;




    @OneToMany(mappedBy = "Bloc",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Place> Places;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "Stade_id")
    private Stadium stadium;


    @OneToMany(mappedBy = "bloc", cascade = CascadeType.ALL)
    private List<MatchSeat> matchSeats; // One place can be linked to many MatchSeats



}
