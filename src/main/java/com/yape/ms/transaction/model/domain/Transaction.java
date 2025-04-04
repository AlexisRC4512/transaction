package com.yape.ms.transaction.model.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@Document(collection = "Transaction")
public class Transaction  {
    @Id
    private String idTransaction;
    private String accountExternalIdDebit;
    private String accountExternalIdCredit;
    private String status;
    private double value;
    private String date;
}
