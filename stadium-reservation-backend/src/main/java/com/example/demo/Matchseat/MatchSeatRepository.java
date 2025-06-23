package com.example.demo.Matchseat;

import com.example.demo.FootballMatch.FootballMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchSeatRepository extends JpaRepository<MatchSeat, Long> {

    @Query("SELECT ms FROM MatchSeat ms WHERE ms.footballMatch.id = :footballMatchId AND ms.place.placeNuméro = :placeNumero AND ms.bloc.id = :blocId")
    Optional<MatchSeat> findByFootballMatchIdAndPlaceNuméroAndBlocId(
            @Param("footballMatchId") Long footballMatchId,
            @Param("placeNumero") int placeNumero,
            @Param("blocId") Long blocId
    );


    @Query("SELECT fm FROM FootballMatch fm WHERE fm.id = :footballMatchId")
    Optional<FootballMatch> findByFootballMatchId(@Param("footballMatchId") Long footballMatchId);

    List<MatchSeat> findByFootballMatch(FootballMatch footballMatch);
}

