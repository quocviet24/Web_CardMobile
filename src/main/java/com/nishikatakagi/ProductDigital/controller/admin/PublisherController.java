package com.nishikatakagi.ProductDigital.controller.admin;

import com.nishikatakagi.ProductDigital.dto.PublisherDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.Publisher;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.PublisherService;
import com.nishikatakagi.ProductDigital.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@RequestMapping("/publisher")
@Controller
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @Autowired
    UserService userService;

    @Autowired
    HttpSession session;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("")
    public String ShowPagePublisher(Model model) {
        List<Publisher> listPublisher = publisherService.getAllPublisher();
        model.addAttribute("listPublisher", listPublisher);
        return "pages/publisher/publisher.html";
    }

    @GetMapping("/update")
    public String ShowPageUpdatePublisher(Model model, @RequestParam int id) {
        Publisher publisher = publisherService.findPublisherById(id);
        model.addAttribute("publisher", publisher);

        PublisherDto publisherDto = new PublisherDto();
        if(publisher.getIsDeleted()){
            publisherDto.setActive("inactive");
        }
        if(!publisher.getIsDeleted()){
            publisherDto.setActive("active");
        }

        publisherDto.setName(publisher.getName());
        model.addAttribute("publisherDto", publisherDto);
        return "pages/publisher/updatePublisher.html";
    }

    @PostMapping("/update")
    public String updatePublisher(Model model, @RequestParam int id,
                                  @Valid @ModelAttribute PublisherDto publisherDto,
                                  BindingResult result) {
        try {
            Publisher publisher = publisherService.findPublisherById(id);
            model.addAttribute("publisher", publisher);

            if (result.hasErrors()) {
                return "pages/publisher/updatePublisher.html";
            }


            if (!publisherDto.getImage().isEmpty()) {
                //delete old image
                String uploadDir = "src/main/resources/static/assets/images/publisher/";
                Path oldImagePath = Paths.get(uploadDir + publisher.getImage());
                System.out.println(oldImagePath);
                if (Files.exists(oldImagePath)) {
                    try {
                        Files.delete(oldImagePath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // save new image file
                MultipartFile image = publisherDto.getImage();
                Date createAt = new Date();
                String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                publisher.setImage(storageFileName);
            }

            publisher.setName(publisherDto.getName());

            if(publisherDto.getActive().equals("editInActive")){
                publisher.setIsDeleted(true);
            }
            if(publisherDto.getActive().equals("active")){
                publisherService.activePublisher(publisher);
            }
            
            publisherService.savePublisher(publisher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/publisher";
    }

    @GetMapping("/add")
    public String showPageAddPublisher(Model model){
        PublisherDto publisherDto = new PublisherDto();
        model.addAttribute("publisherDto",publisherDto);
        return "pages/publisher/addPublisher.html";
    }

    @PostMapping("/add")
    public String addPublisher(@Valid @ModelAttribute PublisherDto publisherDto,
                               BindingResult result){
        if(publisherDto.getImage().isEmpty()){
            result.addError(new FieldError("publisherDto","image","Thiếu hình ảnh"));
        }

        if(result.hasErrors()) {
            return "pages/publisher/addPublisher.html";
        }

        MultipartFile image = publisherDto.getImage();
        Date createAt = new Date();
        String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();
        try {
            String uploadDir = "src/main/resources/static/assets/images/publisher/";

            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
        User user = userService.findUserDBByUserSession(userSessionDto);

        Publisher publisher = new Publisher();
        publisher.setName(publisherDto.getName());
        publisher.setImage(storageFileName);
        publisher.setCreatedDate(createAt);
        publisher.setCreatedBy(user.getId());
        publisher.setIsDeleted(false);

        publisherService.savePublisher(publisher);

        return "redirect:/publisher";
    }

    @GetMapping("/delete")
    public String inactivePublisher(Model model, @RequestParam int id){
        UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("user_sess");
        User user = userService.findUserDBByUserSession(userSessionDto);
        Publisher publisher = publisherService.findPublisherById(id);
        publisherService.inactivePublisher(publisher,user);
        return "redirect:/publisher";
    }

    @GetMapping("/active")
    public String activePublisher(Model model, @RequestParam int id){
        Publisher publisher = publisherService.findPublisherById(id);
        publisherService.activePublisher(publisher);
        return "redirect:/publisher";
    }

    @GetMapping("/filter")
    public String ShowPagePublisherFilter(Model model, @RequestParam(value = "status", required = false) String status) {

        List<Publisher> listPublisher;
        switch (status){
            case "default":
                listPublisher = publisherService.getAllPublisher();
                break;
            case "active":
                listPublisher = publisherService.getAllPublisherActive();
                break;
            case "inactive":
                listPublisher = publisherService.getAllPublisherDeactive();
                break;
            default:
                System.out.println("Have error");
                listPublisher = publisherService.getAllPublisher();
                break;
        }
        model.addAttribute("status", status);
        model.addAttribute("listPublisher", listPublisher);
        return "pages/publisher/publisher.html";

    }
}
