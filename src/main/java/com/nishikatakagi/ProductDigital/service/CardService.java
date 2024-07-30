package com.nishikatakagi.ProductDigital.service;

import com.nishikatakagi.ProductDigital.dto.CardDTO;
import com.nishikatakagi.ProductDigital.dto.CardUpdateDTO;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.Card;
import com.nishikatakagi.ProductDigital.model.CardType;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

public interface CardService {
    public List<Card> findAllCards();

    public void setActiveById(int id, UserSessionDto user, boolean toDelete);

    public Card findById(int id);

    public void saveCard(CardDTO cardDTO, Integer createdBy);

    public void updateCard(CardUpdateDTO cardDTO, Integer updatedBy);

    Page<Card> findAllCards(Integer pageNo, Integer pageSize);

    public List<Card> getCardFollowQuantityAndCardID(int quantity, CardType cardType);

    //huy
    void addCards(InputStream inputStream, Integer createdByDefault, List<String> messages);
    //ngoc
    Workbook exportCardsToExcel();

    public Page<Card> searchCard(Integer pageNo, Integer pageSize, Integer publisherId, Integer unitPrice, Integer status);
}
