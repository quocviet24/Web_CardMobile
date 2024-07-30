package com.nishikatakagi.ProductDigital.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nishikatakagi.ProductDigital.dto.UserChangpasswordDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.SecurityService;
import com.nishikatakagi.ProductDigital.service.UserService;

@RequestMapping("/changepassword")
@Controller
public class ChangPasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession session;
    @Autowired
    SecurityService security;

    @GetMapping("")
    public String ShowPage(Model model) {
        UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
        if (userSessionDto == null) {
            return "redirect:/";
        } else {
            UserChangpasswordDto userChangpasswordDto = new UserChangpasswordDto();
            model.addAttribute("user", userChangpasswordDto);
            return "publics/changepassword";
        }


    }

    @PostMapping("")
    public String changePassword(@Valid @ModelAttribute("user") UserChangpasswordDto userChangpasswordDto, BindingResult bindingResult, Model model) {
        //kiem tra mat khau cu

        //kiem tra mat khau moi mat khau moi bang nhau chua

        UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
        // Lấy username từ user session
        if(userSessionDto == null){
            return "redirect:/";
        }
        String username = userSessionDto.getUsername();
        // Tìm user từ cơ sở dữ liệu theo username
        User user = userService.findByUsername(username);
        // Kiểm tra nếu mật khẩu cũ không đúng, thêm lỗi vào BindingResult và trả về trang thay đổi mật khẩu
        String encodedOldPassword = security.encode(userChangpasswordDto.getOldPassword());
        if (!user.getPassword().equals(encodedOldPassword)) {
            bindingResult.addError(new FieldError("user", "oldPassword", "Mật khẩu không đúng!"));
        }

        // Kiểm tra nếu mật khẩu mới và xác nhận mật khẩu không khớp, thêm lỗi vào BindingResult và trả về trang thay đổi mật khẩu
        if (!userChangpasswordDto.getNewPassword().equals(userChangpasswordDto.getConfirmPassword())) {
            bindingResult.addError(new FieldError("user", "confirmPassword", "Mật khẩu mới và xác nhận mật khẩu không khớp!"));

        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userChangpasswordDto);
            return "publics/changepassword";
        }
        // Thay đổi mật khẩu của user trong cơ sở dữ liệu
        userService.changePassword(user.getId(), userChangpasswordDto.getNewPassword());
        //update tren backend
        // Chuyển hướng đến trang thay đổi mật khẩu thành công
        return "publics/profile.html";
    }


}
