package com.example.demo.Stade;


import com.example.demo.Bloc.BlocDto;
import com.example.demo.service.BaseResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class StadiumDto extends BaseResponseDto {
    private Long id;
    private String name;
    private String location;
    private String image;
    private List<BlocDto> blocks;
    private Stadium stadium;
    private List<Stadium> stadiumList  ;// Add the Stadium object here


}
