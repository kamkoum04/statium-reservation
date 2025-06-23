package com.example.demo.User;

import com.example.demo.service.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public UserDto register(UserDto registrationRequest){
        UserDto resp = new UserDto();

        try {
            Optional<Utilisateur> existingUser = utilisateurRepository.findByEmail(registrationRequest.getEmail());

            if (existingUser.isPresent()) {
                // If user already exists, set error message and status code
                resp.setStatusCode(400);
                resp.setMessage("User with this email already exists");
                return resp;
            }
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(registrationRequest.getEmail());
            utilisateur.setCin(Integer.parseInt(registrationRequest.getCin()));  // Changed to `cin` to match your `Utilisateur` class
            utilisateur.setRole(registrationRequest.getRole());
            utilisateur.setNom(registrationRequest.getNom());  // Changed to `nom` for name (from 'name')
            utilisateur.setPrenom(registrationRequest.getPrenom());  // Changed to `prenom` for first name
            utilisateur.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            Utilisateur savedUser = utilisateurRepository.save(utilisateur);  // Renamed to match repo name

            if (savedUser.getId() > 0) {
                resp.setUtilisateurs(savedUser);  // Changed to match your class (`setUtilisateur`)
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public UserDto login(UserDto loginRequest){
        UserDto response = new UserDto();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = utilisateurRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public UserDto refreshToken(UserDto refreshTokenRequest){
        UserDto response = new UserDto();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            Utilisateur users = utilisateurRepository.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public UserDto getAllUsers() {
        UserDto userDto = new UserDto();

        try {
            List<Utilisateur> result = utilisateurRepository.findAll();
            if (!result.isEmpty()) {
                userDto.setUtilisateursList(result);
                userDto.setStatusCode(200);
                userDto.setMessage("Successful");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("No users found");
            }
            return userDto;
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred: " + e.getMessage());
            return userDto;
        }
    }

    public UserDto getUsersById(Integer id) {
        UserDto userDto = new UserDto();
        try {
            Utilisateur usersById = utilisateurRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("User Not found"));
            userDto.setUtilisateurs(usersById);
            userDto.setStatusCode(200);
            userDto.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred: " + e.getMessage());
        }
        return userDto;
    }

    public UserDto deleteUser(Integer userId) {
        UserDto userDto = new UserDto();
        try {
            Optional<Utilisateur> userOptional = utilisateurRepository.findById(Long.valueOf(userId));
            if (userOptional.isPresent()) {
                utilisateurRepository.deleteById(Long.valueOf(userId));
                userDto.setStatusCode(200);
                userDto.setMessage("User deleted successfully");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return userDto;
    }
    public UserDto updateUser(Integer userId, Utilisateur updatedUser) {
        UserDto userDto = new UserDto();
        try {
            Optional<Utilisateur> userOptional = utilisateurRepository.findById(Long.valueOf(userId));
            if (userOptional.isPresent()) {
                Utilisateur existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setNom(updatedUser.getNom());  // Renamed to match `nom`
                existingUser.setPrenom(updatedUser.getPrenom());  // Renamed to match `prenom`
                existingUser.setCin(updatedUser.getCin());  // Renamed to match `cin`
                existingUser.setRole(updatedUser.getRole());

                // If password is provided, encode and update
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                Utilisateur savedUser = utilisateurRepository.save(existingUser);
                userDto.setUtilisateurs(savedUser);  // Renamed to match method `setUtilisateur`
                userDto.setStatusCode(200);
                userDto.setMessage("User updated successfully");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("User not found for update");
            }
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return userDto;
    }
    public UserDto getMyInfo(String email) {
        UserDto userDto = new UserDto();
        try {
            Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                userDto.setUtilisateurs(userOptional.get());  // Renamed to match method `setUtilisateur`
                userDto.setStatusCode(200);
                userDto.setMessage("Successful");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("User not found");
            }
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return userDto;
    }






}