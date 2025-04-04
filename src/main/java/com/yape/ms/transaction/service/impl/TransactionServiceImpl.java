package com.yape.ms.transaction.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.ms.transaction.exceptions.KafkaSendException;
import com.yape.ms.transaction.exceptions.Resilence4jError;
import com.yape.ms.transaction.exceptions.TransactionBadRequestException;
import com.yape.ms.transaction.exceptions.TransactionNotFoundException;
import com.yape.ms.transaction.model.events.SendTransactionEvent;
import com.yape.ms.transaction.model.request.TransactionRequest;
import com.yape.ms.transaction.model.response.TransactionResponse;
import com.yape.ms.transaction.repository.TransactionRepository;
import com.yape.ms.transaction.service.TransactionService;
import com.yape.ms.transaction.util.TransactionAdapter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String,String>kafkaTemplate;
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  KafkaTemplate<String,
                                          String> kafkaTemplate) {
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    @CircuitBreaker(name = "transaction-service",fallbackMethod = "fallbackCreateTransaction")
    @TimeLimiter(name = "transaction-service")
    @Retry(name = "transaction-service")
    @Override
    public Mono<TransactionResponse> createTransaction(TransactionRequest transactionRequest ,String typeTransaction) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SendTransactionEvent sendTransactionEvent = TransactionAdapter.toTransactionSendEvent(transactionRequest,typeTransaction);
        String jsonStringSendTransactionEvent = objectMapper.writeValueAsString(sendTransactionEvent);
        log.info("Start Microservice Transaction and use create Transaction");
        return Mono.just(transactionRequest)
                .switchIfEmpty(Mono.error(new TransactionBadRequestException("Bad request is empity")))
                .map(TransactionAdapter::toTransaction)
                .flatMap(transactionRepository::save)
                .switchIfEmpty(Mono.error(new RuntimeException("No se pudo guardar la transacción")))
                .map(TransactionAdapter::toTransactionResponse)
                .flatMap(response ->
                        Mono.fromFuture(() -> kafkaTemplate.send("send-Mesage-state-topic", jsonStringSendTransactionEvent))
                                .thenReturn(response)
                                .doOnError(kafkaError -> log.error("Error sending message to Kafka: {}", kafkaError.getMessage(), kafkaError))
                                .onErrorResume(kafkaError -> Mono.error(new KafkaSendException("Error sending message to Kafka", kafkaError))));

    }
    @CircuitBreaker(name = "transaction-service",fallbackMethod = "getAllTransactionFallback")
    @TimeLimiter(name = "transaction-service")
    @Retry(name = "transaction-service")
    @Override
    public Flux<TransactionResponse> getAllTransaction() {
        return transactionRepository.findAll()
                .map(TransactionAdapter::toTransactionResponse)
                .switchIfEmpty(Flux.empty())
                .doOnError(throwable ->
                    log.error("Error en buscar la transaction:{}",throwable.getMessage(),throwable))
                .onErrorResume(throwable -> Mono.error(new TransactionNotFoundException("Error to obtain Transaction" + throwable.getMessage())));
    }

    @Cacheable(value = "Transaction", key = "#idTransaction")
    @CircuitBreaker(name = "transaction-service",fallbackMethod = "getTransactionByIdFallback")
    @TimeLimiter(name = "transaction-service")
    @Retry(name = "transaction-service")
    @Override
    public Mono<TransactionResponse> getTransactionById(String idTransaction) {
        return transactionRepository.findById(idTransaction).map(TransactionAdapter::toTransactionResponse)
                .switchIfEmpty(Mono.empty())
                    .doOnError(throwable -> log.error("Error en buscar la transaction:{}",throwable.getMessage(),throwable))
                .onErrorResume(throwable -> Mono.error(new TransactionNotFoundException("Error to obtain Transaction" + throwable.getMessage())));

    }
    @CircuitBreaker(name = "transaction-service",fallbackMethod = "deleteTransactionByIdFallback")
    @TimeLimiter(name = "transaction-service")
    @Retry(name = "transaction-service")
    @Override
    public Mono<Void> deleteTransactionById(String idTransaction) {
        return transactionRepository.deleteById(idTransaction)
                .switchIfEmpty(Mono.empty())
                .doOnError(throwable -> log.error("Error en borrar la transaction:{}",throwable.getMessage(),throwable))
                .onErrorResume(throwable -> Mono.error(new TransactionNotFoundException("Error to delete Transaction" + throwable.getMessage())));

    }
    @KafkaListener(id = "myConsumer",topics = "read-Mesage-state-topic", groupId = "test1",autoStartup = "true")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message in group foo: " + message);
    }

    public Mono<TransactionResponse> fallbackCreateTransaction(Resilence4jError exception) {
        log.error("Fallback method for Transaction", exception.getMessage());
        return Mono.error(new Resilence4jError("Message Exception: " + exception.getMessage()));
    }
    public Flux<TransactionResponse> getAllTransactionFallback(Throwable exception) {
        log.error("Fallback for getAllTransaction: {}", exception.getMessage(), exception);
        return Flux.error(new Resilence4jError("Service unavailable: " + exception.getMessage()));
    }

    public Mono<TransactionResponse> getTransactionByIdFallback(String idTransaction, Throwable exception) {
        log.error("Fallback for getTransactionById: {}", exception.getMessage(), exception);
        return Mono.error(new Resilence4jError("Service unavailable: " + exception.getMessage()));
    }

    public Mono<Void> deleteTransactionByIdFallback(String idTransaction, Throwable exception) {
        log.error("Fallback for deleteTransactionById: {}", exception.getMessage(), exception);
        return Mono.error(new Resilence4jError("Service unavailable: " + exception.getMessage()));
    }

}
