package com.nishikatakagi.ProductDigital.service_impl;

import com.nishikatakagi.ProductDigital.dto.CardDTO;
import com.nishikatakagi.ProductDigital.dto.CardUpdateDTO;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.Card;
import com.nishikatakagi.ProductDigital.model.CardType;
import com.nishikatakagi.ProductDigital.model.Publisher;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.CardRepository;
import com.nishikatakagi.ProductDigital.repository.CardTypeRepository;
import com.nishikatakagi.ProductDigital.repository.UserRepository;
import com.nishikatakagi.ProductDigital.service.CardService;
import com.nishikatakagi.ProductDigital.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties.Server.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private CardTypeRepository cardTypeRepository;

    @Override
    public List<Card> findAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public Card findById(int id) {
        Optional<Card> optionalCardType = cardRepository.findById(id);
        return optionalCardType.orElseThrow();
    }

    @Override
    public void saveCard(CardDTO cardDTO, Integer createdBy) {
        Card card = new Card();
        card.setCardNumber(cardDTO.getCardNumber());
        card.setSeriNumber(cardDTO.getSeriNumber());
        card.setExpiryDate(cardDTO.getExpiryDate());
        card.setCardType(cardDTO.getCardType());
        card.setCreatedBy(createdBy);
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        card.setCreatedDate(currentTime);
        card.setIsDeleted(false);
        cardRepository.save(card);
        // update quantity
        CardType cardType = cardDTO.getCardType();
        cardType.setInStock(cardType.getInStock() + 1);
        cardTypeRepository.save(cardType);
    }

    @Override
    public void updateCard(CardUpdateDTO cardDTO, Integer updatedBy) {
        Card card = findById(cardDTO.getId());
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        card.setCardNumber(cardDTO.getCardNumber());
        card.setSeriNumber(cardDTO.getSeriNumber());
        card.setExpiryDate(cardDTO.getExpiryDate());
        CardType cardType = cardTypeService.findById(cardDTO.getCardTypeId());
        card.setCardType(cardType);
        card.setLastUpdated(currentTime);
        card.setUpdatedBy(updatedBy);
        cardRepository.save(card);
    }

    @Override
    public Page<Card> findAllCards(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return cardRepository.findAll(pageable);
    }

    @Override
    public List<Card> getCardFollowQuantityAndCardID(int quantity, CardType cardType) {
        List<Card> listCard = cardRepository.findByCardTypeAndIsDeletedOrderByExpiryDateAsc(cardType, false);
        List<Card> listCardCustomerOrder = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            listCardCustomerOrder.add(listCard.get(i));
            listCard.get(i).setIsDeleted(true);
            cardRepository.save(listCard.get(i));
        }
        return listCardCustomerOrder;
    }

    @Override
    public void setActiveById(int id, UserSessionDto userDTO, boolean toDelete) {
        Card c = findById(id);
        User user = userRepository.findUserByUsername(userDTO.getUsername());
        c.setIsDeleted(toDelete);
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        if (toDelete) {
            c.setDeletedDate(currentTime);
            c.setDeletedBy(user.getId());
            cardRepository.save(c);
            // change number of cardtype
            CardType cardType = c.getCardType();
            cardType.setInStock(cardType.getInStock() - 1);
            cardTypeRepository.save(cardType);
        }

    }

    public List<Card> cardList() {
        return cardRepository.getAllCart();
    }

    public void addCards(InputStream inputStream, Integer createdByDefault, List<String> messages) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        int successCount = 0;
        int failCount = 0;

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            // Kiểm tra header
            Row headerRow = sheet.getRow(0);
            // check number of heard is 4
            if (headerRow == null || headerRow.getPhysicalNumberOfCells() != 4) {
                messages.add("Lỗi: sai định dạng file quy định, không thể xử lý file.");
                return;
            }
            String[] expectedHeaders = { "Card Type ID", "Serial Number", "Card Number", "Expiry Date" };
            boolean headerValid = true;
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null || !expectedHeaders[i].equals(formatter.formatCellValue(cell))) {
                    messages.add("Tên cột không đúng tại vị trí: " + (i + 1) + ". Mong đợi: " + expectedHeaders[i]);
                    headerValid = false;
                }
            }

            if (!headerValid) {
                messages.add("Lỗi: Tên cột không hợp lệ, không thể xử lý file.");
                return; // Dừng lại nếu header không đúng
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Skip header row

                // Validate columns
                String cardTypeIdStr = formatter.formatCellValue(row.getCell(0));
                String seriNumber = formatter.formatCellValue(row.getCell(1));
                String cardNumber = formatter.formatCellValue(row.getCell(2));
                String expiryDateStr = formatter.formatCellValue(row.getCell(3));

                if (cardTypeIdStr == null || cardTypeIdStr.trim().isEmpty() ||
                        seriNumber == null || seriNumber.trim().isEmpty() ||
                        cardNumber == null || cardNumber.trim().isEmpty() ||
                        expiryDateStr == null || expiryDateStr.trim().isEmpty()) {
                    failCount++;
                    continue;
                }

                Integer cardTypeId;
                try {
                    cardTypeId = Integer.parseInt(cardTypeIdStr);
                } catch (NumberFormatException e) {
                    failCount++;
                    continue;
                }

                CardType cardType = cardTypeRepository.findById(cardTypeId).orElse(null);
                if (cardType == null) {
                    failCount++;
                    continue;
                }

                java.util.Date utilDate;
                try {
                    utilDate = dateFormat.parse(expiryDateStr);
                } catch (ParseException e) {
                    failCount++;
                    continue;
                }

                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                // Check for duplicate (cardTypeId + seriNumber)
                boolean isDuplicate = cardRepository.existsByCardTypeAndSeriNumber(cardType, seriNumber);
                if (isDuplicate) {
                    failCount++;
                    continue;
                }

                // Save card
                Card newCard = new Card();
                newCard.setCardType(cardType);
                newCard.setSeriNumber(seriNumber);
                newCard.setCardNumber(cardNumber);
                newCard.setExpiryDate(sqlDate);
                newCard.setIsDeleted(false);
                newCard.setCreatedBy(createdByDefault);
                newCard.setCreatedDate(new java.sql.Date(new java.util.Date().getTime()));
                cardRepository.save(newCard);
                // update cardtype
                cardType.setInStock(cardType.getInStock() + 1);
                cardTypeRepository.save(cardType);
                successCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            messages.add("Lỗi xử lý tệp Excel: " + e.getMessage());
            failCount++;
        }

        // Display success and fail messages
        messages.add("Nhập thành công " + successCount + " thẻ.");
        messages.add("Nhập thất bại " + failCount + " thẻ");
    }

    public void exportCardsToExcel(String fileName) {
        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(fileName)) {
            Sheet sheet = workbook.createSheet("Cards");
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));

            // Define header titles - excluding "ID"
            String[] headers = { "Card Type ID", "Serial Number", "Card Number",
                    "Expiry Date", "Is Deleted", "Deleted Date", "Deleted By",
                    "Created Date", "Created By", "Last Updated", "Updated By" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Fetching card data from database
            List<Card> cards = cardRepository.findAll();
            int rowIdx = 1;
            for (Card card : cards) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue((double) card.getCardType().getId()); // Card Type ID
                row.createCell(1).setCellValue(card.getSeriNumber()); // Serial Number
                row.createCell(2).setCellValue(card.getCardNumber()); // Card Number
                Cell expiryDateCell = row.createCell(3);
                expiryDateCell.setCellValue(card.getExpiryDate());
                expiryDateCell.setCellStyle(dateStyle); // Expiry Date
                row.createCell(4).setCellValue(card.getIsDeleted()); // Is Deleted
                Cell deletedDateCell = row.createCell(5);
                if (card.getDeletedDate() != null) {
                    deletedDateCell.setCellValue(card.getDeletedDate());
                    deletedDateCell.setCellStyle(dateStyle); // Deleted Date
                }
                Cell deletedByCell = row.createCell(6);
                if (card.getDeletedBy() != null) {
                    deletedByCell.setCellValue((double) card.getDeletedBy());
                } else {
                    deletedByCell.setCellValue("N/A"); // Deleted By
                }
                Cell createdDateCell = row.createCell(7);
                createdDateCell.setCellValue(card.getCreatedDate());
                createdDateCell.setCellStyle(dateStyle); // Created Date
                row.createCell(8).setCellValue((double) card.getCreatedBy()); // Created By
                Cell lastUpdatedCell = row.createCell(9);
                if (card.getLastUpdated() != null) {
                    lastUpdatedCell.setCellValue(card.getLastUpdated());
                    lastUpdatedCell.setCellStyle(dateStyle); // Last Updated
                }
                Cell updatedByCell = row.createCell(10);
                if (card.getUpdatedBy() != null) {
                    updatedByCell.setCellValue((double) card.getUpdatedBy());
                } else {
                    updatedByCell.setCellValue("N/A"); // Updated By
                }
            }

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Workbook exportCardsToExcel() {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Cards");
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));

            String[] headers = { "Card Type ID", "Serial Number", "Card Number", "Expiry Date", "Is Deleted",
                    "Deleted Date", "Deleted By", "Created Date", "Created By", "Last Updated", "Updated By" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            List<Card> cards = cardRepository.findAll();
            int rowIdx = 1;
            for (Card card : cards) {
                Row row = sheet.createRow(rowIdx++);
                fillRowWithData(row, card, dateStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally rethrow as a custom exception or handle as needed
        }
        return workbook;
    }

    private void fillRowWithData(Row row, Card card, CellStyle dateStyle) {
        // Method to fill row data, reduces code duplication
        row.createCell(0).setCellValue((double) card.getCardType().getId()); // Card Type ID
        row.createCell(1).setCellValue(card.getSeriNumber()); // Serial Number
        row.createCell(2).setCellValue(card.getCardNumber()); // Card Number

        Cell expiryDateCell = row.createCell(3);
        if (card.getExpiryDate() != null) {
            expiryDateCell.setCellValue(card.getExpiryDate());
            expiryDateCell.setCellStyle(dateStyle); // Expiry Date
        }

        row.createCell(4).setCellValue(card.getIsDeleted()); // Is Deleted

        Cell deletedDateCell = row.createCell(5);
        if (card.getDeletedDate() != null) {
            deletedDateCell.setCellValue(card.getDeletedDate());
            deletedDateCell.setCellStyle(dateStyle); // Deleted Date
        } else {
            deletedDateCell.setCellValue("N/A");
        }

        Cell deletedByCell = row.createCell(6);
        if (card.getDeletedBy() != null) {
            deletedByCell.setCellValue((double) card.getDeletedBy()); // Deleted By
        } else {
            deletedByCell.setCellValue("N/A");
        }

        Cell createdDateCell = row.createCell(7);
        createdDateCell.setCellValue(card.getCreatedDate());
        createdDateCell.setCellStyle(dateStyle); // Created Date

        row.createCell(8).setCellValue((double) card.getCreatedBy()); // Created By

        Cell lastUpdatedCell = row.createCell(9);
        if (card.getLastUpdated() != null) {
            lastUpdatedCell.setCellValue(card.getLastUpdated());
            lastUpdatedCell.setCellStyle(dateStyle); // Last Updated
        } else {
            lastUpdatedCell.setCellValue("N/A");
        }

        Cell updatedByCell = row.createCell(10);
        if (card.getUpdatedBy() != null) {
            updatedByCell.setCellValue((double) card.getUpdatedBy()); // Updated By
        } else {
            updatedByCell.setCellValue("N/A");
        }
    }

    @Override
    public Page<Card> searchCard(Integer pageNo, Integer pageSize, Integer publisherId, Integer unitPrice,
            Integer status) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<CardType> cardTypes = new ArrayList<>();
        if (publisherId != null) {
            cardTypes = cardTypeService.getCardTypeByPublisher(publisherId);
            if (unitPrice != null) {
                cardTypes.removeIf(cardType -> cardType.getUnitPrice() != unitPrice);
            }
        } else {
            if (unitPrice != null) {
                cardTypes = cardTypeService.getCardTypeByUnitPrice(unitPrice);
            } else {
                cardTypes = cardTypeService.findAllCardTypes();
            }
        }
        // find by list of cardtype and isdeleted
        List<Boolean> isDeleted = new ArrayList<>();
        if (status == null) {
            isDeleted.add(false);
            isDeleted.add(true);
        } else if (status == 0) {
            isDeleted.add(false);
        } else if (status == 1) {
            isDeleted.add(true);
        }
        Specification<Card> spec = Specification.where(hasCardType(cardTypes))
                .and(hasIsDeleted(isDeleted));
        return cardRepository.findAll(spec, pageable);
    }

    Specification<Card> hasCardType(List<CardType> cardTypes) {
        return (root, query, cb) -> root.get("cardType").in(cardTypes);
    }

    Specification<Card> hasIsDeleted(List<Boolean> isDeleted) {
        return (root, query, cb) -> root.get("isDeleted").in(isDeleted);
    }
}
