package com.example.demo.User;

import com.example.demo.service.BaseResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseResponseDto {

    private String token;
    private String refreshToken;
    private String expirationTime;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String cin;
    private Utilisateur utilisateurs;
    private List<Utilisateur> utilisateursList;

}
