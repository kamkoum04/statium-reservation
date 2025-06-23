package com.example.demo.FootballMatch;

import com.example.demo.Matchseat.MatchSeatDto;
import com.example.demo.service.BaseResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class FootballMatchDto extends BaseResponseDto {
    private Long id;
    private Date date;
    private LocalTime time;

    private String equipe1;
    private String equipe2;
    private String equipe1Logo;
    private String equipe2Logo;


    private Long stadiumId;
    private String stadiumName;

    // New field for the price modifier
    private double matchPriceModifier;  // This will be added to the price of the seats

    // Optional: A list of match seats, if you want to return them in the DTO
    private List<MatchSeatDto> matchSeats; // Include match seats with each match
}
