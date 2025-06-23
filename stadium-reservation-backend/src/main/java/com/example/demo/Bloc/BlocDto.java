package com.example.demo.Bloc;


import com.example.demo.service.BaseResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlocDto extends BaseResponseDto {

    private Long id;
    private String name;
    private int totalPlace;
    private double defaultPrice;

}
