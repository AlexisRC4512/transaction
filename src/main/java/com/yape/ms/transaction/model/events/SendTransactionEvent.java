package com.yape.ms.transaction.model.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendTransactionEvent {
    private String transactionExternalId;
    private String transactionType;
    private String transactionStatus;
    private double value;
    private String createAdd;
}
