package com.example.demo.Matchseat;

import com.example.demo.service.BaseResponseDto;
import lombok.Data;

@Data
public class MatchSeatDto extends BaseResponseDto {
    private Long id;
    private Double seatPrice;
    private boolean isReserved;
    private int placeNuméro;
    private Long placeId; // ID of the place (seat) in the stadium
    private String blocName;
    private Long blocId;
}
