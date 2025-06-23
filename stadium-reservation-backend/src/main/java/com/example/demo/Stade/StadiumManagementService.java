package com.example.demo.Stade;


import com.example.demo.Bloc.Bloc;
import com.example.demo.Bloc.BlocDto;
import com.example.demo.Bloc.BlocRepository;
import com.example.demo.Place.Place;
import com.example.demo.Place.PlaceRepository;
import com.example.demo.service.ImageService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class StadiumManagementService {
    @Autowired
    private StadeRepository stadeRepository;
    @Autowired
    private BlocRepository blocRepository;

    @Autowired
    private PlaceRepository placeRepository;

    public StadiumDto addStade(StadiumDto stadiumDto, MultipartFile file) {
        StadiumDto response = new StadiumDto();
        try {
            System.out.println("Received stadiumDto: " + stadiumDto);
            System.out.println("Received file: " + file.getOriginalFilename());

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (fileName.contains("..")) {
                response.setMessage("Invalid file name: " + fileName);
                response.setStatusCode(400);
                return response;
            }

            try {
                stadiumDto.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
                response.setMessage("Error encoding image: " + e.getMessage());
                response.setStatusCode(500);
                return response;
            }

            // Log the stadium creation
            System.out.println("Creating stadium: " + stadiumDto.getName());

            // Create the stadium
            Stadium stadium = new Stadium();
            stadium.setName(stadiumDto.getName());
            stadium.setLocation(stadiumDto.getLocation());
            stadium.setImage(stadiumDto.getImage());

            // Save the stadium
            Stadium savedStadium = stadeRepository.save(stadium);
            System.out.println("Stadium saved with ID: " + savedStadium.getId());

            // Create blocks and places (seats)
            for (BlocDto blocDto : stadiumDto.getBlocks()) {
                System.out.println("Creating bloc: " + blocDto.getName());

                Bloc bloc = new Bloc();
                bloc.setName(blocDto.getName());
                bloc.setTotalPlace(blocDto.getTotalPlace());
                bloc.setStadium(savedStadium);
                bloc.setDefaultPrice(blocDto.getDefaultPrice());

                Bloc savedBloc = blocRepository.save(bloc);
                System.out.println("Bloc saved with ID: " + savedBloc.getId());

                List<Place> places = new ArrayList<>();
                for (int i = 1; i <= blocDto.getTotalPlace(); i++) {
                    Place place = new Place();
                    place.setPlaceNuméro(i);
                    place.setPrice(bloc.getDefaultPrice());
                    place.setBloc(savedBloc);
                    places.add(place);
                }

                placeRepository.saveAll(places);
                System.out.println("Places saved for bloc: " + savedBloc.getName());
            }

            response.setStadium(savedStadium);
            response.setMessage("Stadium, blocks, and places added successfully.");
            response.setStatusCode(200);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            response.setStatusCode(500);
            response.setMessage("Error occurred while adding stadium: " + e.getMessage());
        }
        return response;
    }
    public StadiumDto getAllStadiums() {
        StadiumDto stadiumDto = new StadiumDto();

        try {
            List<Stadium> result = stadeRepository.findAll();
            if (!result.isEmpty()) {
                stadiumDto.setStadiumList(result); // Set the list of stadiums
                stadiumDto.setStatusCode(200);
                stadiumDto.setMessage("Successful");
            } else {
                stadiumDto.setStatusCode(404);
                stadiumDto.setMessage("No stadiums found");
            }
            return stadiumDto;
        } catch (Exception e) {
            stadiumDto.setStatusCode(500);
            stadiumDto.setMessage("Error occurred: " + e.getMessage());
            return stadiumDto;
        }
    }

    public StadiumDto updateStadium(Long stadiumId, StadiumDto stadiumDto) {
        StadiumDto response = new StadiumDto();
        try {
            // Find the stadium by ID
            Stadium stadium = stadeRepository.findById(stadiumId)
                    .orElseThrow(() -> new Exception("Stadium not found"));

            // Update the stadium fields
            stadium.setName(stadiumDto.getName());
            stadium.setLocation(stadiumDto.getLocation());
            // Save the updated stadium
            stadeRepository.save(stadium);
            response.setStadium(stadium);
            response.setMessage("Stadium updated successfully");
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating stadium: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    public StadiumDto deleteStadium(Long id) {
        StadiumDto stadiumDto = new StadiumDto();
        try {
            // Find the stadium to delete
            Optional<Stadium> stadiumOptional = stadeRepository.findById(id);
            if (stadiumOptional.isPresent()) {
                Stadium stadium = stadiumOptional.get();
                stadeRepository.delete(stadium); // This will also delete associated blocs due to cascading

                // Set response message after deletion
                stadiumDto.setStatusCode(200);
                stadiumDto.setMessage("Stadium and associated blocks deleted successfully");
            } else {
                stadiumDto.setStatusCode(404);
                stadiumDto.setMessage("Stadium not found");
            }
        } catch (Exception e) {
            stadiumDto.setStatusCode(500);
            stadiumDto.setMessage("Error occurred: " + e.getMessage());
        }
        return stadiumDto;
    }








}
