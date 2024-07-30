package com.nishikatakagi.ProductDigital.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatDTO {
    int id;
    String username;
    double totalMoney;
}
