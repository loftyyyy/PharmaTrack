package com.rho.ims.dto;

import com.rho.ims.enums.PaymentMethod;
import com.rho.ims.enums.SaleStatus;
import com.rho.ims.model.Sale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SaleResponseDTO {

    private Long saleId;
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal grandTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String customerName;
    private LocalDate saleDate;
    private PaymentMethod paymentMethod;
    private SaleStatus saleStatus;
    private List<SaleItemResponseDTO> saleItems;


    public SaleResponseDTO(Sale sale) {
        this.saleId = sale.getId();
        this.customerId = sale.getCustomer().getId();
        this.totalAmount = sale.getTotalAmount();
        this.taxAmount = sale.getTaxAmount();
        this.grandTotal = sale.getGrandTotal();
        this.saleDate = sale.getSaleDate();
        this.paymentMethod = sale.getPaymentMethod();
        this.customerName = sale.getCustomer().getName();
        this.discountAmount = sale.getDiscountAmount();
        this.saleStatus = sale.getStatus();
        this.saleItems = sale.getSaleItems().stream().map(saleItem -> new SaleItemResponseDTO(saleItem)).toList();
    }

}
