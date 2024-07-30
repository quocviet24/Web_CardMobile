package com.nishikatakagi.ProductDigital.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PublisherDto {

    @NotEmpty(message = "Tên không được để trống")
    private String name;

    private String active;

    private MultipartFile image;

}
