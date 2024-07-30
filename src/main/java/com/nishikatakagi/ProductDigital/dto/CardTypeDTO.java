package com.nishikatakagi.ProductDigital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTypeDTO {
    @NotNull(message = "Nhà phát hành không được để trống")
    private Integer publisher_id;
    @NotNull(message = "Mệnh giá không được để trống")
    @Positive(message = "Số tiền phải là số dương")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    @Digits(integer = 9, fraction = 2, message = "Số tiền không đúng định dạng hoặc quá lớn")
    private Double unitPrice;

    public @NotNull(message = "Nhà phát hành không được để trống") Integer getPublisher_id() {
        return publisher_id;
    }

    public void setPublisherId(@NotNull(message = "Nhà phát hành không được để trống") Integer publisher_id) {
        this.publisher_id = publisher_id;
    }
    public @NotNull(message = "Mệnh giá không được để trống") Double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(@NotNull(message = "Mệnh giá không được để trống") Double unitPrice) {
        this.unitPrice = unitPrice;
    }


}
