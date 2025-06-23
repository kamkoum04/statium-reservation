package com.example.demo.FootballMatch;

import com.example.demo.Stade.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.Date;

public interface FootballMatchRepository extends JpaRepository<FootballMatch, Long> {
    boolean existsByStadiumAndDateAndTimeAndIdNot(Stadium stadium, Date date, LocalTime time, Long id);
}
