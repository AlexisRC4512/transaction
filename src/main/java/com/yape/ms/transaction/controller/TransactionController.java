package com.yape.ms.transaction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yape.ms.transaction.model.request.TransactionRequest;
import com.yape.ms.transaction.model.response.TransactionResponse;
import com.yape.ms.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CacheConfig(cacheNames = "Transaction")
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TransactionResponse>getAllTransactions() {
        return transactionService.getAllTransaction();
    }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TransactionResponse>createTransaction(@Valid @RequestBody TransactionRequest transactionRequest
    , @RequestParam String typeTransaction) throws JsonProcessingException {
        return transactionService.createTransaction(transactionRequest,typeTransaction);
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TransactionResponse>getTransactionById(@PathVariable("id")String idTransaction) {
        return transactionService.getTransactionById(idTransaction);
    }
    @DeleteMapping("/{id}")
    public Mono<Void>deleteTransactionById(@PathVariable("id")String idTransaction) {
        return transactionService.deleteTransactionById(idTransaction);
    }
}
