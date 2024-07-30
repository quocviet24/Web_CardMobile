package com.nishikatakagi.ProductDigital.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nishikatakagi.ProductDigital.dto.UserRegisterRequestDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;
    Logger logger = LoggerFactory.getLogger(AccountController.class);

    @GetMapping("")
    public String showPagePublisher(Model model, @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        Page<User> listAccount = userService.findAllUser(pageNo, 7);
        if (listAccount.isEmpty()) {
            model.addAttribute("error", "Không có tài khoản nào");
        } else {
            List<User> listAccount1 = listAccount.getContent();
            model.addAttribute("listAccount", listAccount1);
        }
		model.addAttribute("totalPages", listAccount.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("action", "/account?");
        return "pages/account/view-account.html";
    }

    @GetMapping("/delete")
    public String deleteAccount(@RequestParam("username") String username, RedirectAttributes model) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user == null) {
            return "redirect:/login";
        }
        User userDelete = userService.findByUsername(username);
        if (userDelete.isDeleted()) {
            model.addFlashAttribute("error", "Tài khoản đã bị khóa");
            return "redirect:/account";
        }
        if (userDelete.getRoleId() == 1) {
            model.addFlashAttribute("error", "Bạn không thể khóa tài khoản này");
            return "redirect:/account";
        } else {
            userService.deleteUserByAdmin(username, user);
            return "redirect:/account";
        }
    }

    @GetMapping("/create")
    public String showCreateAccountPage(Model model) {
        UserRegisterRequestDto user = new UserRegisterRequestDto();
        model.addAttribute("user", user);
        return "pages/account/create-account.html";
    }

    @PostMapping("/create")
    public String createAccount(Model model, @Valid @ModelAttribute("user") UserRegisterRequestDto user,
            BindingResult result) {
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null) {
            return "redirect:/login";
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            result.addError(new FieldError("user", "username", "Tên đăng nhập đã tồn tại"));
        }
        if (userService.checkEmailExist(user.getEmail())) {
            result.addError(new FieldError("user", "email", "Email đã tồn tại"));
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "pages/account/create-account.html";
        }
        User createdBy = userService.findUserDBByUserSession(userSession);
        userService.saveUser(user, createdBy);
        return "redirect:/account";
    }

    @GetMapping("/accountdetail")
    public String showDetailAccount(Model model, @RequestParam("username") String username,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại");
            return "redirect:/account";
        }
        model.addAttribute("user", user);
        return "pages/account/detailed-account.html";
    }

    @GetMapping("/changepassword")
    public String showChangePasswordPage(Model model, @RequestParam("username") String username,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(username);
        if (user.getRoleId() == 1) {
            redirectAttributes.addFlashAttribute("error", "Bạn không thể thay đổi mật khẩu của tài khoản này");
            return "redirect:/account";
        }
        model.addAttribute("user", user);
        return "pages/account/change-password.html";
    }

    @PostMapping("/changepassword")
    public String changePassword(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.findByUsername(username);
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if(user.getRoleId()==1){
            return "redirect:/account";
        }
        if (userSession == null) {
            return "redirect:/login";
        }
        User userSessionData = userService.findByUsername(userSession.getUsername());
        userService.changePassword(user, password, userSessionData);
        return "redirect:/account";
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("username") String username, RedirectAttributes model) {
        User user = userService.findByUsername(username);
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null) {
            return "redirect:/login";
        }
        if (user.getRoleId() == 1) {
            model.addFlashAttribute("error", "Bạn không thể kích hoạt tài khoản này");
            return "redirect:/account";
        }
        User userSessionData = userService.findByUsername(userSession.getUsername());
        userService.activateAccount(user, userSessionData);
        return "redirect:/account";
    }

    @GetMapping("/filter")
    public String filterAccount(@RequestParam(value = "role", required = false) List<Integer> roleId,
            @RequestParam(value = "is-deleted", required = false) List<Integer> isDeleted,
            @RequestParam(value = "username", required = false) String username,@RequestParam(defaultValue = "0") Integer pageNo, Model model) {
        Page<User> listAccount = userService.filterAccount(roleId, isDeleted, username,pageNo,1);
        List<User> listAccount1 = listAccount.getContent();
        if(listAccount1.isEmpty()){
            model.addAttribute("error", "Không có tài khoản nào");
        }
        String link = "/account/filter?";
        if(roleId != null){
            for(Integer role : roleId){
                link += "role=" + role + "&";
            }
        }
        if(isDeleted != null){
            for(Integer delete : isDeleted){
                link += "is-deleted=" + delete + "&";
            }
        }
        link += "username=";
        if(username != null){
            link += username;
        }
        link += "&";
        model.addAttribute("action", link);
        model.addAttribute("totalPages", listAccount.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("listAccount", listAccount1);
        model.addAttribute("role", roleId);
        model.addAttribute("isDeleted", isDeleted);
        model.addAttribute("username", username);
        return "pages/account/view-account.html";
    }
}
