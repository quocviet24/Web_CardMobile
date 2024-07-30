package com.nishikatakagi.ProductDigital.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalMoneyByMonthDTO {
    private int month;
    private double totalMoney;
}
