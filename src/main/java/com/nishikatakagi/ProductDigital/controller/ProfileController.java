package com.nishikatakagi.ProductDigital.controller;

import com.nishikatakagi.ProductDigital.service.EmailService;
import com.nishikatakagi.ProductDigital.service.TokenService;
import com.nishikatakagi.ProductDigital.service_impl.SecurityServiceImpl;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.TokenType;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.TokenRepository;
import com.nishikatakagi.ProductDigital.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RequestMapping("/profile")
@Controller
public class ProfileController {
	@Autowired
	private UserService userService;
	@Autowired
	SecurityServiceImpl security;

	@Autowired
	EmailService emailService;

	@Autowired
	HttpSession session;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	TokenService tokenService;

	public ProfileController() {

	}

	@GetMapping("")
	public String showProfilePage(Model model, HttpSession session) {
		// có thể findById trả về null nên phải cho điều kiện orElse để xử lý
		// Get session user từ login
		UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
		if (user != null) {
			// Proceed with user data
			return "publics/profile.html";
		} else {
			// Handle the case where the user is not found
			return "publics/index.html";
		}
	}

	@GetMapping("/update")
	public String showProfileUpdatePage(HttpSession session, Model model) {
		UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
		if (user != null) {
			model.addAttribute("User", user);
			return "publics/profileUpdate";
		} else {
			return "publics/index.html"; // Redirect to home page if user is not in session
		}
	}

	@PostMapping("/update")
	public String updateProfilePage(@Valid @ModelAttribute("User") UserSessionDto userDto, BindingResult result,
			Model model) {
		// Kiểm tra các dữ liệu nhập đầu vào có valid không
		if (userDto.getPhone().length() != 10 && userDto.getPhone().length() > 0) {
			result.addError(
					new FieldError("UserSessionDto", "phone", "Số điện thoại phải có 10 chữ số"));
		}
		if (result.hasErrors()) {
			model.addAttribute("User", userDto);
			return "publics/profileUpdate";
		} else {
			// Lấy đối tượng userSession trên phiên làm việc hiện tại
			UserSessionDto userDtoSession = (UserSessionDto) session.getAttribute("user_sess");
			// Lấy userName của user đó ( gần như là lấy khóa chính )
			// Tìm đối tượng user ( với đầy đủ data ) ở trong database theo userName
			User user = userService.findUserDBByUserSession(userDtoSession);
			// Kiểm tra có tìm được user không
			if (user == null) {
				return "publics/index.html";
			} else {
				// Nếu tìm thấy user tương ứng trong database thì tiến hành update use đó theo
				// thông tin được lưu tại userDto lấy từ trang html
				userService.updateUser(userDto, user);
				userDto.setEmail(user.getEmail());

				// update session with new userDto
				session.setAttribute("user_sess", userDto);
			}
			return "redirect:/profile";
		}

	}

	@GetMapping("confirmPass")
	public String showPageConfirmPassword() {
		return "publics/ConfirmPass.html";
	}

	@PostMapping("confirmPass")
	public String ConfirmPassword(Model model, @RequestParam("Password") String password,
			RedirectAttributes redirectAttributes) {
		UserSessionDto userDtoSession = (UserSessionDto) session.getAttribute("user_sess");
		User user = userService.findUserDBByUserSession(userDtoSession);

		if (!user.getPassword().equals(security.encode(password))) {
			model.addAttribute("error", "password không đúng");
			return "publics/ConfirmPass.html";
		}

		model.addAttribute("user", new UserSessionDto());
		return "publics/EnterNewEmail.html"; // No token or email sending yet
	}

	@GetMapping("/change-email")
	public String showPageEnterEmail(Model model, RedirectAttributes redirectAttributes) {
		UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
		if (userSessionDto == null) {
			return "redirect:/";
		}

		User user = userService.findUserDBByUserSession(userSessionDto);
		String token = (String) session.getAttribute("emailChangeToken_" + user.getEmail());

		if (token == null || !tokenService.validateToken(token, TokenType.EMAIL_CHANGE)) {
			redirectAttributes.addFlashAttribute("resetTokenExpired", true);
			return "redirect:/profile/confirmPass";
		}

		// Create a new userDTO and add it to the model
		UserSessionDto newUserDto = new UserSessionDto(); // Create new UserSessionDto
		newUserDto.setEmail(user.getEmail()); // Set initial value to the current email
		model.addAttribute("user", newUserDto);
		return "publics/EnterNewEmail.html";
	}

	@PostMapping("/change-email") // No path variable anymore
	public String showPageEnterEmail(@Valid @ModelAttribute("user") UserSessionDto userDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
		if (userSessionDto == null) {
			return "redirect:/";
		}

		// Check if the entered email is valid
		// if (bindingResult.hasErrors()) {
		// return "publics/EnterNewEmail.html"; // Return to the form with errors
		// }

		String newEmail = userDto.getEmail();

		if (userService.checkEmailExist(newEmail)) {
			bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
			return "publics/EnterNewEmail.html";
		}

		User user = userService.findUserDBByUserSession(userSessionDto);

		// Store new email in session to be used later
		session.setAttribute("newEmail_" + user.getEmail(), newEmail);

		// Generate email change token
		String token = tokenService.generateToken(user, TokenType.EMAIL_CHANGE);
		session.setAttribute("emailChangeToken_" + user.getEmail(), token);

		try {
			emailService.sendEmailChangeConfirmationEmail(newEmail, token); // Send to new email
		} catch (MessagingException e) {
			// Log or handle the email sending error
			e.printStackTrace();
		}

		redirectAttributes.addFlashAttribute("emailChangeRequested", true);
		return "redirect:/profile";
	}


	@GetMapping("/change-email/{token}")
public String verifyEmailChange(@PathVariable String token, RedirectAttributes redirectAttributes) {
    UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
    if (userSessionDto == null) {
        return "redirect:/";
    }

    User user = userService.findUserDBByUserSession(userSessionDto);
    String sessionToken = (String) session.getAttribute("emailChangeToken_" + user.getEmail());

    if (tokenService.validateToken(token, TokenType.EMAIL_CHANGE) && token.equals(sessionToken)) {
        // Retrieve new email from session
        String newEmail = (String) session.getAttribute("newEmail_" + user.getEmail()); 

        userService.updateUserEmail(user, newEmail);
        userSessionDto.setEmail(newEmail);
        session.setAttribute("user_sess", userSessionDto);
        
        // Remove token and newEmail from session after successful change
        session.removeAttribute("emailChangeToken_" + user.getEmail());
        session.removeAttribute("newEmail_" + user.getEmail()); 

        redirectAttributes.addFlashAttribute("emailChanged", true);
    } else {
        redirectAttributes.addFlashAttribute("error", "Invalid or expired token");
    }

    return "redirect:/profile";
}

}
