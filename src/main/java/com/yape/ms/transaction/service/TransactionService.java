package com.yape.ms.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yape.ms.transaction.model.request.TransactionRequest;
import com.yape.ms.transaction.model.response.TransactionResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TransactionService {
    Mono<TransactionResponse>createTransaction(TransactionRequest transactionRequest,String typeTransaction) throws JsonProcessingException;
    Flux<TransactionResponse>getAllTransaction();
    Mono<TransactionResponse>getTransactionById(String idTransaction);
    Mono<Void>deleteTransactionById(String idTransaction);
}
