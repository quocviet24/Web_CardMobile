package com.nishikatakagi.ProductDigital.service_impl;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.nishikatakagi.ProductDigital.dto.CardTypeDTO;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.Publisher;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.UserRepository;
import com.nishikatakagi.ProductDigital.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.repository.CardTypeRepository;
import com.nishikatakagi.ProductDigital.service.CardTypeService;

@Service
public class CardTypeServiceImpl implements CardTypeService {

    @Autowired
    PublisherService publisherService;

    @Autowired
    CardTypeRepository cardTypeRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<CardType> findAllCardTypes() {
        return cardTypeRepository.findAll();
    }

    @Override
    public CardType findById(int id) {
        Optional<CardType> optionalCardType = cardTypeRepository.findById(id);
        return optionalCardType.orElseThrow(); // Corrected
    }

    @Override
    public List<CardType> getCardTypeByPublisher(int publisherId) {
        return cardTypeRepository.findByPublisherId(publisherId);
    }

    @Override
    public CardType findCardTypeByPublisherActive(Integer publisherId) {
        List<CardType> cardTypes = cardTypeRepository.findAll();
        for (CardType cardType : cardTypes) {
            if (cardType.getPublisher().getId() == publisherId && !cardType.getPublisher().getIsDeleted()) {
                return cardType;
            }
        }
        return null;
    }

    @Override
    public void setActiveById(int id, UserSessionDto userDTO, boolean toDeleted) {
        CardType ct = findById(id);
        User user = userRepository.findUserByUsername(userDTO.getUsername());
        ct.setIsDeleted(toDeleted);
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        if (toDeleted) {
            ct.setDeletedDate(currentTime);
            ct.setDeletedBy(user.getId());
        } else {
            ct.setDeletedBy(null);
        }
        cardTypeRepository.save(ct);
    }
    @Override
    public void saveCardType(CardTypeDTO cardDTO, User createdBy) {
        CardType cardType = new CardType();
        cardType.setId(0);
        cardType.setCreatedBy(createdBy.getId());
        cardType.setUnitPrice(cardDTO.getUnitPrice());
        cardType.setPublisher(publisherService.findPublisherById(cardDTO.getPublisher_id()));
        cardType.setInStock(0);
        cardType.setSoldQuantity(0);
        cardType.setIsDeleted(false);
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        cardType.setCreatedDate(currentTime);
        cardTypeRepository.save(cardType);
    }

    @Override
    public Page<CardType> findAllCardTypes(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return cardTypeRepository.findAll(pageable);
    }

    @Override
    public Page<CardType> filterCardType(Integer pageNo, Integer pageSize, String publisher, String status) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Boolean isDeleted = null;  // Use Boolean instead of boolean for nullable field

        // Determine isDeleted based on status
        if (status != null) {
            if (status.equals("active")) {
                isDeleted = false;
            } else if (status.equals("deactive")) {
                isDeleted = true;
            }
        }

        int publisherId = -1;
        if (publisher != null) {
            switch (publisher) {
                case "viettel":
                    publisherId = 1;
                    break;
                case "mobifone":
                    publisherId = 2;
                    break;
                case "vinaphone":
                    publisherId = 3;
                    break;
                default:
                    publisherId = -1;
            }
        }

        Specification<CardType> spec = Specification.where((root, query, cb) -> cb.conjunction());

        // Add publisherId condition if applicable
        if (publisherId != -1) {
            spec = spec.and(hasPublisherId(publisherId));
        }

        // Add isDeleted condition if applicable
        if (isDeleted != null) {
            spec = spec.and(isDeleted(isDeleted));
        }

        return cardTypeRepository.findAll(spec, pageable);
    }

    private Specification<CardType> hasPublisherId(int publisherId) {
        return (root, query, cb) -> cb.equal(root.get("publisher").get("id"), publisherId);
    }

    private Specification<CardType> isDeleted(boolean isDeleted) {
        return (root, query, cb) -> cb.equal(root.get("isDeleted"), isDeleted);
    }
    @Override
    public CardType findCarfTypeByPublisherNameAndUnitPrice(String publisherName, Integer unitPrice) {
        Publisher publisher = publisherService.findPublisherByName(publisherName);
        CardType cardType = cardTypeRepository.findByPublisherIdAndUnitPrice(publisher.getId(), unitPrice);
        return cardType;
    }

    @Override
    public List<CardType> getCardTypeByUnitPrice(Integer unitPrice) {
        return cardTypeRepository.findByUnitPrice(unitPrice);
    }

}
