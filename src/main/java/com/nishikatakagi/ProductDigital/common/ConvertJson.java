package com.nishikatakagi.ProductDigital.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.model.Transaction;
import com.nishikatakagi.ProductDigital.model.User;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertJson {
    public static class JsonToTransactionConverter {

        public static Transaction convertJsonToTransaction(JsonNode rootNode, User user, Order order) throws IOException, ParseException {

            Transaction transaction = new Transaction();
            transaction.setId(rootNode.has("id") ? rootNode.get("id").asText() : null);
            transaction.setUser(user);
            transaction.setOrder(order);
            if (rootNode.has("transactions")) {
                JsonNode transactionNode = rootNode.get("transactions").get(0); // Chỉ lấy phần tử đầu tiên trong mảng transactions

                transaction.setAmount(transactionNode.has("amount") ? transactionNode.get("amount").asDouble() : 0.0);
                transaction.setAccountName(transactionNode.has("counterAccountName") ? transactionNode.get("counterAccountName").asText() : null);
                transaction.setAccountNumber(transactionNode.has("counterAccountNumber") ? transactionNode.get("counterAccountNumber").asText() : null);
                transaction.setDescription(transactionNode.has("description") ? transactionNode.get("description").asText() : null);
                transaction.setBankId(transactionNode.has("counterAccountBankId") ? transactionNode.get("counterAccountBankId").asText() : null);

                if (transactionNode.has("transactionDateTime")) {
                    String dateString = transactionNode.get("transactionDateTime").asText();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    Date transactionDate = sdf.parse(dateString);
                    transaction.setTransactionDate(transactionDate);
                }
                transaction.setStatus(rootNode.has("status") ? rootNode.get("status").asText() : null);
            }

            return transaction;
        }

        public static String getStatusTransaction(JsonNode rootNode) throws IOException, ParseException {
                return rootNode.has("status") ? rootNode.get("status").asText() : null;
        }
    }
}
