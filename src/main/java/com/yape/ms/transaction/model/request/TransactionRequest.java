package com.yape.ms.transaction.model.request;

import com.yape.ms.transaction.model.validator.ValidDateFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionRequest {
    @NotNull(message = "AccountExternal of Debit Id is required")
    @NotBlank
    @Size(min = 9,max = 9)
    private String accountExternalIdDebit;
    @NotNull(message = "AccountExternal of Credit Id is required")
    @NotBlank
    @Size(min = 9,max = 9)
    private String accountExternalIdCredit;
    @NotNull(message = "status of is required")
    @NotBlank
    private String status;
    @Min(1)
    private double value;
    @NotNull(message = "The Date is required")
    @ValidDateFormat
    private String date;
}
