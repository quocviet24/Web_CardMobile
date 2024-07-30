package com.nishikatakagi.ProductDigital.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChangpasswordDto {
   @NotNull
   @Size(min = 8,message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String oldPassword;
   @NotNull
   @Size(min = 8,message = "Mật khẩu phải có ít nhất 8 ký tựs")
    private String newPassword;
    private String confirmPassword;
}
