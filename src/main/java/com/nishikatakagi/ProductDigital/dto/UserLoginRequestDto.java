package com.nishikatakagi.ProductDigital.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {
	@NotEmpty(message = "Tên đăng nhập không được để trống")
    private String userName;
	@NotEmpty(message = "Mật khẩu không được để trống")
    private String password;
	@NotEmpty(message = "Captcha không được để trống")
	private String captcha;

	public @NotEmpty(message = "Tên đăng nhập không được để trống") String getUserName() {
		return userName;
	}

	public void setUserName(@NotEmpty(message = "Tên đăng nhập không được để trống") String userName) {
		this.userName = userName;
	}

	public @NotEmpty(message = "Mật khẩu không được để trống") String getPassword() {
		return password;
	}

	public void setPassword(@NotEmpty(message = "Mật khẩu không được để trống") String password) {
		this.password = password;
	}

	public @NotEmpty(message = "Captcha không được để trống") String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(@NotEmpty(message = "Captcha không được để trống") String captcha) {
		this.captcha = captcha;
	}
}
