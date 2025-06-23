package com.example.demo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {

    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto registrationRequest){
        return ResponseEntity.ok(usersManagementService.register(registrationRequest));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto loginRequest){
        return ResponseEntity.ok(usersManagementService.login(loginRequest));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<UserDto> refreshToken(@RequestBody UserDto refreshTokenRequest){
        return ResponseEntity.ok(usersManagementService.refreshToken(refreshTokenRequest));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<UserDto> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));
    }

    @PutMapping("/adminuser/update/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer userId, @RequestBody Utilisateur updatedUser){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, updatedUser));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<UserDto> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDto response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

}
