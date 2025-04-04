package com.yape.ms.transaction.util;

import com.yape.ms.transaction.model.domain.Transaction;
import com.yape.ms.transaction.model.events.SendTransactionEvent;
import com.yape.ms.transaction.model.request.TransactionRequest;
import com.yape.ms.transaction.model.response.TransactionResponse;
import java.util.UUID;

public class TransactionAdapter {
   public static Transaction toTransaction (TransactionRequest transactionRequest) {
      return Transaction.builder()
              .idTransaction(UUID.randomUUID().toString())
              .value(transactionRequest.getValue())
              .date(transactionRequest.getDate())
              .accountExternalIdCredit(transactionRequest.getAccountExternalIdCredit())
              .accountExternalIdDebit(transactionRequest.getAccountExternalIdDebit())
              .build();
   }
   public static TransactionResponse toTransactionResponse(Transaction transaction) {
      return TransactionResponse.builder()
              .idTransaction(transaction.getIdTransaction())
              .status(transaction.getStatus())
              .date(transaction.getDate())
              .accountExternalIdCredit(transaction.getAccountExternalIdCredit())
              .accountExternalIdDebit(transaction.getAccountExternalIdDebit())
              .value(transaction.getValue())
              .build();
   }
   public static SendTransactionEvent toTransactionSendEvent(TransactionRequest transactionRequest,String typeTransaction) {
      return SendTransactionEvent.builder()
              .transactionExternalId(transactionRequest.getAccountExternalIdCredit())
              .transactionType(typeTransaction)
              .transactionStatus(transactionRequest.getStatus())
              .value(transactionRequest.getValue())
              .createAdd(transactionRequest.getDate()).build();
   }
}
