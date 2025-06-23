package com.example.demo.service;


import lombok.Data;

@Data

public class BaseResponseDto {
    private int statusCode;
    private String message;
    private String error;
}
