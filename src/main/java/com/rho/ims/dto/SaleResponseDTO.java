package com.rho.ims.dto;

import com.rho.ims.enums.PaymentMethod;
import com.rho.ims.model.Sale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class SaleResponseDTO {
    private Long saleId;
    private Long customerId;
    private BigDecimal totalAmount;
    private LocalDate saleDate;
    private PaymentMethod paymentMethod;
    private BigDecimal discountAmount;


    public SaleResponseDTO(Sale sale) {
        this.saleId = sale.getId();
        this.customerId = sale.getCustomer().getId();
        this.totalAmount = sale.getTotalAmount();
        this.saleDate = sale.getSaleDate();
        this.paymentMethod = sale.getPaymentMethod();
        this.discountAmount = sale.getDiscountAmount();
    }

}
