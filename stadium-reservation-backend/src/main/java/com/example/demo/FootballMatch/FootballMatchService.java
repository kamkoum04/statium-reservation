package com.example.demo.FootballMatch;

import com.example.demo.Bloc.Bloc;
import com.example.demo.Bloc.BlocRepository;
import com.example.demo.Matchseat.MatchSeat;
import com.example.demo.Matchseat.MatchSeatRepository;
import com.example.demo.Place.Place;
import com.example.demo.Stade.StadeRepository;
import com.example.demo.Stade.Stadium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class FootballMatchService {

    @Autowired
    private FootballMatchRepository footballMatchRepository;

    @Autowired
    private StadeRepository stadeRepository;
    @Autowired
    private BlocRepository blocRepository;
    @Autowired
    private MatchSeatRepository matchSeatRepository;

    public FootballMatchDto addFootballMatch(FootballMatchDto footballMatchDto , MultipartFile file1 , MultipartFile file2 ) {
        FootballMatchDto response = new FootballMatchDto();
        try {
            Optional<Stadium> stadium = stadeRepository.findById(footballMatchDto.getStadiumId());
            if (!stadium.isPresent()) {
                response.setStatusCode(404);
                response.setMessage("Stadium not found");
                return response;
            }
            String fileName1 = StringUtils.cleanPath(file1.getOriginalFilename());
            if (fileName1.contains("..")){
                System.out.println("not a valid file ");
            }
            try {
                footballMatchDto.setEquipe1Logo(Base64.getEncoder().encodeToString(file1.getBytes()));
            }catch (IOException e){
                e.printStackTrace();
            }
            String fileName2 = StringUtils.cleanPath(file2.getOriginalFilename());
            if (fileName2.contains("..")){
                System.out.println("not a valid file ");
            }
            try {
                footballMatchDto.setEquipe2Logo(Base64.getEncoder().encodeToString(file2.getBytes()));
            }catch (IOException e){
                e.printStackTrace();
            }

            // Create the football match
            FootballMatch footballMatch = new FootballMatch();
            footballMatch.setDate(footballMatchDto.getDate());
            footballMatch.setTime(footballMatchDto.getTime());
            footballMatch.setEquipe1(footballMatchDto.getEquipe1());
            footballMatch.setEquipe2(footballMatchDto.getEquipe2());
            footballMatch.setStadium(stadium.get());
            footballMatch.setEquipe1Logo(footballMatchDto.getEquipe1Logo());
            footballMatch.setEquipe2Logo(footballMatchDto.getEquipe2Logo());
            footballMatch.setMatchPriceModifier(footballMatchDto.getMatchPriceModifier());

            // Save the football match first
            FootballMatch savedMatch = footballMatchRepository.save(footballMatch);

            // Create MatchSeat for each Place in the stadium
            for (Bloc bloc : stadium.get().getBlocs()) {
                for (Place place : bloc.getPlaces()) {
                    MatchSeat matchSeat = new MatchSeat();
                    matchSeat.setFootballMatch(savedMatch);
                    matchSeat.setPlace(place);
                    matchSeat.setBloc(bloc);
                    matchSeat.setSeatPrice(savedMatch.calculateSeatPrice(place.getPrice()));
                    matchSeat.setReserved(false);

                    matchSeat.setPlaceNuméro(place.getPlaceNuméro());
                    matchSeatRepository.save(matchSeat);
                }
            }

            response.setId(savedMatch.getId());
            response.setMessage("Football Match added successfully with seats");
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while adding the football match: " + e.getMessage());
        }
        return response;
    }




    public List<FootballMatchDto> getAllFootballMatches() {
        return footballMatchRepository.findAll().stream().map(match -> {
            FootballMatchDto dto = new FootballMatchDto();
            dto.setId(match.getId());
            dto.setDate(match.getDate());
            dto.setEquipe1(match.getEquipe1());
            dto.setEquipe2(match.getEquipe2());
            dto.setStadiumId(match.getStadium().getId());
            dto.setStadiumName(match.getStadium().getName());
            dto.setEquipe1Logo(match.getEquipe1Logo());
            dto.setEquipe2Logo(match.getEquipe2Logo());

            return dto;
        }).collect(Collectors.toList());
    }

    public FootballMatchDto updateFootballMatch(Long id, FootballMatchDto footballMatchDto) {
        FootballMatchDto response = new FootballMatchDto();
        try {
            Optional<FootballMatch> existingMatch = footballMatchRepository.findById(id);
            if (!existingMatch.isPresent()) {
                response.setStatusCode(404);
                response.setMessage("Football match not found");
                return response;
            }

            Optional<Stadium> stadium = stadeRepository.findById(footballMatchDto.getStadiumId());
            if (!stadium.isPresent()) {
                response.setStatusCode(404);
                response.setMessage("Stadium not found");
                return response;
            }

            // Check for scheduling conflicts, excluding the current match being updated
            boolean conflictExists = footballMatchRepository.existsByStadiumAndDateAndTimeAndIdNot(
                    stadium.get(),
                    footballMatchDto.getDate(),
                    footballMatchDto.getTime(),
                    id
            );
            if (conflictExists) {
                response.setStatusCode(409);
                response.setMessage("A match is already scheduled at this time in the same stadium");
                return response;
            }

            // Update the match details
            FootballMatch match = existingMatch.get();
            match.setDate(footballMatchDto.getDate());
            match.setTime(footballMatchDto.getTime());
            match.setEquipe1(footballMatchDto.getEquipe1());
            match.setEquipe2(footballMatchDto.getEquipe2());
            match.setStadium(stadium.get());

            // Update match price modifier
            double updatedPriceModifier = footballMatchDto.getMatchPriceModifier();
            match.setMatchPriceModifier(updatedPriceModifier);

            // Update seat prices for this match
            for (MatchSeat matchSeat : match.getMatchSeats()) {
                // Calculate the new seat price by adding the match price modifier to the place price
                double newSeatPrice = match.calculateSeatPrice(matchSeat.getPlace().getPrice()); // assuming place has a base price
                matchSeat.setSeatPrice(newSeatPrice); // Update seat price
                matchSeatRepository.save(matchSeat); // Save the updated seat
            }

            // Save the updated match
            footballMatchRepository.save(match);

            response.setId(match.getId());
            response.setMessage("Football Match updated successfully");
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating the football match: " + e.getMessage());
        }
        return response;
    }


    public FootballMatchDto deleteFootballMatch(Long id) {
        FootballMatchDto response = new FootballMatchDto();
        try {
            Optional<FootballMatch> existingMatch = footballMatchRepository.findById(id);
            if (!existingMatch.isPresent()) {
                response.setStatusCode(404);
                response.setMessage("Football match not found");
                return response;
            }

            footballMatchRepository.deleteById(id);
            response.setMessage("Football Match deleted successfully");
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting the football match: " + e.getMessage());
        }
        return response;
    }
}
