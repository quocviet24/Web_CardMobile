package com.nishikatakagi.ProductDigital.controller.admin;

import com.nishikatakagi.ProductDigital.dto.CardTypeDTO;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.*;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import com.nishikatakagi.ProductDigital.service.PublisherService;
import com.nishikatakagi.ProductDigital.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cardType")
public class CardTypeController {
    @Autowired
    CardTypeService cardTypeService;
    @Autowired
    PublisherService publisherService;
    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;
    Logger logger = LoggerFactory.getLogger(CardTypeController.class);

    @GetMapping("")
    public String displayCardType(Model model, @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        Page<CardType> pageList = cardTypeService.findAllCardTypes(pageNo, 7);
        if (pageList.isEmpty()){
            model.addAttribute("error", "Không có thẻ nào");
        } else {
            List<CardType> listCT = pageList.getContent();
            model.addAttribute("listCT", listCT);
        }
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("action", "/cardType?");

        return "pages/cardType/cardType.html";
    }

    @GetMapping("/setActive")
    public String setActiveCardType(@RequestParam("id") int id, @RequestParam("isDeleted") boolean toDelete,RedirectAttributes model) {
        //Get user for delete
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user == null) {
            return "redirect:/login";
        }
        cardTypeService.setActiveById(id, user, toDelete);
        return "redirect:/cardType";
    }
    //create is error because lack of money
    @GetMapping("/create")
    public String showCreateCardTypePage(Model model) {
        CardTypeDTO card = new CardTypeDTO();
        List<Publisher> listPublisher = publisherService.getAllPublisher();
        // List<Money> listMoney = moneyService.getAll();
        model.addAttribute("card", card);
        // model.addAttribute("listMoney", listMoney);
        model.addAttribute("listPublisher", listPublisher);
        return "pages/cardType/create-cardType.html";
    }
    //postmapping is error because lack of money
    @PostMapping("/create")
    public String createCardType(Model model, @Valid @ModelAttribute("card") CardTypeDTO cardDTO,
                                 BindingResult result) {
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            List<Publisher> listPublisher = publisherService.getAllPublisher();
            model.addAttribute("listPublisher", listPublisher);
            return "pages/cardType/create-cardType.html";
        }

        User createdBy = userService.findUserDBByUserSession(userSession);
        List<CardType> listCT = cardTypeService.findAllCardTypes();
        for (CardType cardType : listCT) {
            if (cardType.getPublisher().getId() == cardDTO.getPublisher_id() && Double.compare(cardType.getUnitPrice(), cardDTO.getUnitPrice()) == 0) {
                String error = "Card type already exists";
                model.addAttribute("error", error);
                return showCreateCardTypePage(model);
            }
        }
        cardTypeService.saveCardType(cardDTO, createdBy);
        return "redirect:/cardType";
    }

    @GetMapping("/viewDetail")
    public String showDetailCardTypePage(@RequestParam("cardTypeId") int cardTypeId, Model model) {
        CardType cardType = cardTypeService.findById(cardTypeId);
        model.addAttribute("cardType", cardType);

        return "pages/cardType/viewDetail.html";
    }

    @GetMapping("/filter")
    public String ShowPageCardTypeFilter(Model model, @RequestParam(value = "publisher", required = false) String publisher,
                                         @RequestParam(value = "status", required = false) String status,
                                         @RequestParam(defaultValue = "0") Integer pageNo) {

        if (pageNo < 0) {
            pageNo = 0;
        }
        Page<CardType> pageList = cardTypeService.filterCardType(pageNo, 4, publisher, status);
        if (pageList.isEmpty()) {
            model.addAttribute("error", "Không có thẻ nào");
        } else {
            List<CardType> listCT = pageList.getContent();
            model.addAttribute("listCT", listCT);
        }
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("pageNo", pageNo);

        // Construct action with filter parameters
        String action = String.format("filter?publisher=%s&status=%s&",
                publisher != null ? publisher : "default",
                status != null ? status : "default");
        model.addAttribute("action", action);
        model.addAttribute("status", status);
        model.addAttribute("publisher", publisher);

        return "pages/cardType/cardType.html";
    }
}
