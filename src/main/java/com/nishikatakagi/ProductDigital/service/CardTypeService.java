package com.nishikatakagi.ProductDigital.service;

import com.nishikatakagi.ProductDigital.dto.CardTypeDTO;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CardTypeService {
    public List<CardType> findAllCardTypes();

    public CardType findById(int id);

    public List<CardType> getCardTypeByPublisher(int publisherId);

    public CardType findCardTypeByPublisherActive(Integer publisherId);

    public void setActiveById(int id, UserSessionDto user, boolean toDeleted);

    public void saveCardType(CardTypeDTO cardDTO, User createdBy);

    Page<CardType> findAllCardTypes(Integer pageNo, Integer pageSize);

    CardType findCarfTypeByPublisherNameAndUnitPrice(String publisherName, Integer unitPrice);


    Page<CardType> filterCardType(Integer pageNo, Integer pageSize, String publisher, String status);

    public List<CardType> getCardTypeByUnitPrice(Integer unitPrice);
}
