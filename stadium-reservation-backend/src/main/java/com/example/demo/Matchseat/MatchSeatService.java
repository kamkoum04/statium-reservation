package com.example.demo.Matchseat;

import com.example.demo.FootballMatch.FootballMatch;
import com.example.demo.FootballMatch.FootballMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchSeatService {

    @Autowired
    private MatchSeatRepository matchSeatRepository;

    @Autowired
    private FootballMatchRepository footballMatchRepository;

    public List<MatchSeatDto> getSeatsByMatchId(Long matchId) {
        List<MatchSeatDto> seatDtos = new ArrayList<>();

        try {
            FootballMatch footballMatch = footballMatchRepository.findById(matchId)
                    .orElseThrow(() -> new Exception("Football match not found with ID: " + matchId));

            List<MatchSeat> matchSeats = matchSeatRepository.findByFootballMatch(footballMatch);

            if (!matchSeats.isEmpty()) {
                for (MatchSeat seat : matchSeats) {
                    MatchSeatDto seatDto = mapToDto(seat);
                    seatDto.setStatusCode(200);
                    seatDto.setMessage("Seat data retrieved successfully.");
                    seatDtos.add(seatDto);
                }
            } else {
                MatchSeatDto emptyResponse = new MatchSeatDto();
                emptyResponse.setStatusCode(404);
                emptyResponse.setMessage("No seats found for the given match.");
                seatDtos.add(emptyResponse);
            }

        } catch (Exception e) {
            MatchSeatDto errorResponse = new MatchSeatDto();
            errorResponse.setStatusCode(500);
            errorResponse.setMessage("Error occurred while fetching seat data: " + e.getMessage());
            errorResponse.setError(e.getMessage());
            seatDtos.add(errorResponse);
        }

        return seatDtos;
    }


    /**
     * Map a MatchSeat entity to a MatchSeatDto.
     *
     * @param matchSeat The MatchSeat entity.
     * @return The corresponding MatchSeatDto.
     */
    private MatchSeatDto mapToDto(MatchSeat matchSeat) {
        MatchSeatDto seatDto = new MatchSeatDto();
        seatDto.setId(matchSeat.getId());
        seatDto.setSeatPrice(matchSeat.getSeatPrice());
        seatDto.setReserved(matchSeat.isReserved());
        seatDto.setPlaceNuméro(matchSeat.getPlaceNuméro());
        seatDto.setPlaceId(matchSeat.getPlace().getPlace_id());

        // Set the bloc name instead of bloc ID
        if (matchSeat.getBloc() != null) {
            seatDto.setBlocName(matchSeat.getBloc().getName());
            seatDto.setBlocId(matchSeat.getBloc().getId());
        }

        return seatDto;
    }

}
