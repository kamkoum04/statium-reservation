package com.example.demo.Stade;

import com.example.demo.FootballMatch.FootballMatch;
import com.example.demo.Bloc.Bloc;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Stadium {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String location;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String image;

    @JsonIgnore
    @OneToMany(mappedBy = "stadium",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bloc> blocs;

    @JsonIgnore
    @OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL)
    private List<FootballMatch> footballMatches;
}
