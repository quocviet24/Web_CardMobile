package com.nishikatakagi.ProductDigital.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.Publisher;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import com.nishikatakagi.ProductDigital.service.PublisherService;

import jakarta.servlet.http.HttpSession;

@RequestMapping("")
@Controller
public class HomeController {

	@Autowired
	private HttpSession session;
	@Autowired
    CardTypeService cardTypeService;
    @Autowired
    PublisherService publisherService;
	
	public HomeController() {
	}
		
	@GetMapping("logout")
    public String showRegisterPage() {
       session.removeAttribute("user_sess");
		return "redirect:/oauth2/logout";
    }

    @GetMapping("/")
    public String showPage(Model model, HttpSession session) {
        List<Publisher> publishers = publisherService.getAllPublisherActive();
        List<CardType> cardTypes = cardTypeService.findAllCardTypes();
        model.addAttribute("publishers", publishers);
        model.addAttribute("cardTypes", cardTypes);
        if (session.getAttribute("errorCheckoutHome") != null) {
            model.addAttribute("errorCheckoutHome", session.getAttribute("errorCheckoutHome"));
            session.removeAttribute("errorCheckoutHome");
        }
        return "index"; // Tên của view template, không cần đuôi .html
    }


    @PostMapping
    @ResponseBody // ResponseBody: response data in JSON format (this case) in body.
    public List<CardType> getFilteredCardTypes(@RequestParam("publisherId") int publisherId) {
        List<CardType> cardTypes = cardTypeService.findAllCardTypes();
        List<CardType> filtered = cardTypes.stream()
                .filter(cardType -> cardType.getPublisher().getId() == publisherId&&cardType.getIsDeleted()==false&&cardType.getPublisher().getIsDeleted()==false)
                .collect(Collectors.toList());
        return filtered;
    }
}
