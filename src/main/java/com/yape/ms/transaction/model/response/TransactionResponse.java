package com.yape.ms.transaction.model.response;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String idTransaction;
    private String accountExternalIdDebit;
    private String accountExternalIdCredit;
    private String status;
    private double value;
    private String date;
}
