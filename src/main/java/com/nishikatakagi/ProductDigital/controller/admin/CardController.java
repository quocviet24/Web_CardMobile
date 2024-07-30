package com.nishikatakagi.ProductDigital.controller.admin;

import com.nishikatakagi.ProductDigital.dto.CardDTO;
import com.nishikatakagi.ProductDigital.dto.CardUpdateDTO;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.*;
import com.nishikatakagi.ProductDigital.service.CardService;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import com.nishikatakagi.ProductDigital.service.PublisherService;
import com.nishikatakagi.ProductDigital.service.UserService;
import com.nishikatakagi.ProductDigital.service_impl.CardServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cardAdmin")
public class CardController {
    @Autowired
    CardTypeService cardTypeService;
    @Autowired
    UserService userService;
    @Autowired
    CardService cardService;
    @Autowired
    HttpSession session;
    @Autowired
    PublisherService publisherService;

    Logger logger = LoggerFactory.getLogger(CardController.class);

    @GetMapping("")
    public String displayCard(Model model, @RequestParam(defaultValue = "0") Integer pageNo) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        Page<Card> pageList = cardService.findAllCards(pageNo, 7);
        if (pageList.isEmpty()) {
            model.addAttribute("error", "Không có thẻ nào");
        } else {
            List<Card> listCard = pageList.getContent();
            model.addAttribute("listCard", listCard);
        }
        List<Publisher> listPublisher = publisherService.getAllPublisher();
        model.addAttribute("listPublisher", listPublisher);
        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("action", "/cardAdmin?");

        return "pages/card/card.html";
    }

    @GetMapping("/search")
    public String searchCard(Model model, @RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(value="publisherId",required=false) Integer publisherId,
                             @RequestParam(value = "unitPrice",required=false) Integer unitPrice,
                             @RequestParam(value="status",required=false) Integer status){
        if (pageNo < 0) {
            pageNo = 0;
        }
        List<Publisher> listPublisher = publisherService.getAllPublisher();
        model.addAttribute("listPublisher", listPublisher);
        String link="";
        if(publisherId != null){
            link += "publisherId=" + publisherId;
        }
        if(unitPrice != null){
            link += "&unitPrice=" + unitPrice;
        }
        if(status != null){
            link += "&status=" + status;
        }
        
        Page<Card> pageList = cardService.searchCard(pageNo, 7, publisherId, unitPrice, status);
        if (pageList.isEmpty()) {
            model.addAttribute("error", "Không có thẻ nào");
        } else {
            List<Card> listCard = pageList.getContent();
            model.addAttribute("listCard", listCard);
        }
        

        model.addAttribute("totalPages", pageList.getTotalPages());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("action", "/cardAdmin/search?" + link + "&");
        model.addAttribute("publisherId", publisherId);
        model.addAttribute("status", status);
        model.addAttribute("unitPrice", unitPrice);
        return "pages/card/card.html";
    }

    @GetMapping("/setActive")
    public String setActiveCard(@RequestParam("id") int id, @RequestParam("isDeleted") boolean toDelete,
                                RedirectAttributes model) {
        // Get user for delete
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user == null) {
            return "redirect:/login";
        }
        if(!toDelete){
            model.addFlashAttribute("error", "Không thể khôi phục thẻ đã xóa");
            return "redirect:/cardAdmin";
        }
        cardService.setActiveById(id, user, toDelete);
        return "redirect:/cardAdmin";
    }

    @GetMapping("/create")
    public String showCreateCardPage(Model model) {
        CardDTO card;
        if(model.getAttribute("card")==null){
            card = new CardDTO();
        }
        else{
            card = (CardDTO) model.getAttribute("card");
        }
        List<CardType> listCT = cardTypeService.findAllCardTypes();
        model.addAttribute("card", card);
        model.addAttribute("listCT", listCT);
        return "pages/card/createCard.html";
    }

    @PostMapping("/create")
    public String createCard(Model model, @Valid @ModelAttribute("cardDTO") CardDTO cardDTO, RedirectAttributes redirectAttributes) {
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null) {
            return "redirect:/login";
        }
        //check expiry date
        Date currentDate = Date.valueOf(LocalDate.now());
        if (cardDTO.getExpiryDate().before(currentDate)) {
            redirectAttributes.addFlashAttribute("error", "Expiry date cannot be before current date");
            redirectAttributes.addFlashAttribute("card", cardDTO);
            return "redirect:/cardAdmin/create";
        }
        User createdBy = userService.findUserDBByUserSession(userSession);
        List<Card> listCard = cardService.findAllCards();
        for (Card card : listCard) {
            // check the card number is exist with the same publisher
            if (card.getSeriNumber().equals(cardDTO.getSeriNumber())) {
                if (card.getCardType().getPublisher().getId() == cardDTO.getCardType().getPublisher().getId()) {
                    redirectAttributes.addFlashAttribute("error", "Thẻ đã tồn tại");
                    redirectAttributes.addFlashAttribute("card", cardDTO);
                    return "redirect:/cardAdmin/create";
                }
            }
        }
        cardService.saveCard(cardDTO, createdBy.getId());
        return "redirect:/cardAdmin";
    }

    @GetMapping("/detail")
    public String showDetailCardPage(@RequestParam("cardId") int cardId, Model model) {
        Card card = cardService.findById(cardId);
        CardUpdateDTO cardDto = ConvertToCardUpdateDTO(card);
        List<CardType> listCT = cardTypeService.findAllCardTypes();

        model.addAttribute("cardDto", cardDto);
        model.addAttribute("listCT", listCT);
        model.addAttribute("CTId", cardDto.getCardTypeId());
        return "pages/card/viewDetail.html";
    }

    private CardUpdateDTO ConvertToCardUpdateDTO(Card card) {
        CardUpdateDTO cardDto = new CardUpdateDTO();

        cardDto.setId(card.getId());
        cardDto.setCardTypeId(card.getCardType().getId());
        cardDto.setSeriNumber(card.getSeriNumber());
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setExpiryDate(card.getExpiryDate());
        cardDto.setDeletedBy(card.getDeletedBy());
        cardDto.setIsDeleted(card.getIsDeleted());
        cardDto.setDeletedDate(card.getDeletedDate());
        cardDto.setCreatedDate(card.getCreatedDate());
        cardDto.setCreatedBy(card.getCreatedBy());
        cardDto.setLastUpdated(card.getLastUpdated());
        cardDto.setUpdatedBy(card.getUpdatedBy());
        return cardDto;
    }

    @PostMapping("/update")
    public String updateCard(Model model, @Valid @ModelAttribute("cardDTO") CardUpdateDTO cardDTO,
                             BindingResult result) {
        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null) {
            return "redirect:/login";
        }
        User updatedBy = userService.findUserDBByUserSession(userSession);
        if(cardDTO.getIsDeleted()){
            result.rejectValue("isDeleted", "isDeleted.error", "Không thể cập nhật thẻ đã xóa");
            return "/cardAdmin/detail?cardId=" + cardDTO.getId();
        }
        Date currentDate = Date.valueOf(LocalDate.now());
        if (cardDTO.getExpiryDate().before(currentDate)) {
            result.rejectValue("expiryDate", "expiryDate.error", "Expiry date cannot be before current date");
            return "/cardAdmin/detail?cardId=" + cardDTO.getId();
        } else {
            cardService.updateCard(cardDTO, updatedBy.getId());
        }

        return "redirect:/cardAdmin";
    }
    @PostMapping("/addCards")
    public String addCards(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", List.of("Không tìm thấy file!"));
            return "redirect:/cardAdmin";   
        }

        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user_sess");
        if (userSession == null || userSession.getId() == null) {
            redirectAttributes.addFlashAttribute("messages", List.of("id người dùng không tồn tại!"));
            return "redirect:/login";
        }

        Integer createdByDefault = userSession.getId();
        List<String> messages = new ArrayList<>();
        try {
            cardService.addCards(file.getInputStream(), createdByDefault, messages);
        } catch (Exception e) {
            messages.add("Lỗi khi xử lý tệp: " + e.getMessage());
        }
        System.out.println(messages);
        redirectAttributes.addFlashAttribute("messages", messages);

        return "redirect:/cardAdmin";
    }


    @GetMapping("/exportCards")
    public void exportCardsToExcel(HttpServletResponse response) {

        String fileName = "ExportedCards_" + UUID.randomUUID().toString() + ".xlsx";

        try {
            Workbook workbook = cardService.exportCardsToExcel();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
    @GetMapping("/test")
    public String testMessages(RedirectAttributes redirectAttributes) {
        List<String> messages = new ArrayList<>();
        messages.add("Đây là thông báo lỗi thử nghiệm.");
        messages.add("Đây là thông báo thành công thử nghiệm.");
        redirectAttributes.addFlashAttribute("messages", messages);
        return "redirect:/cardAdmin";
    }
}
