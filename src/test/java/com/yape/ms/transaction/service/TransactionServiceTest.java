package com.yape.ms.transaction.service;

import com.yape.ms.transaction.model.domain.Transaction;
import com.yape.ms.transaction.model.request.TransactionRequest;
import com.yape.ms.transaction.model.response.TransactionResponse;
import com.yape.ms.transaction.repository.TransactionRepository;
import com.yape.ms.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    KafkaTemplate<String, String> kafkaTemplate;
    @InjectMocks
    TransactionServiceImpl transactionService;



    private TransactionRequest transactionRequest;
    private Transaction transaction;
    private TransactionResponse transactionResponse;
    @BeforeEach
    void setUp() {
        transactionRequest = TransactionRequest.builder()
                .accountExternalIdDebit("123456789")
                .accountExternalIdCredit("987654321")
                .status("PENDING")
                .value(100.50)
                .date("15032025")
                .build();

        transaction = Transaction.builder()
                .idTransaction("txn-001")
                .accountExternalIdDebit("123456789")
                .accountExternalIdCredit("987654321")
                .status("PENDING")
                .value(100.50)
                .date("15032025")
                .build();

        transactionResponse = TransactionResponse.builder()
                .accountExternalIdDebit("txn-001")
                .accountExternalIdCredit("txn-002")
                .status("PENDING")
                .value(100.50)
                .date("15032025")
                .build();
    }


    @Test
    void createAndSentTransactionSuccess() throws Exception {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        doReturn(future).when(kafkaTemplate).send(anyString(), anyString());
        StepVerifier.create(transactionService.createTransaction(transactionRequest, "PENDING"))
                .assertNext(objTransactio -> {
                    assertEquals(objTransactio.getIdTransaction(),transaction.getIdTransaction());
                })
                .verifyComplete();
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
    }
    @Test
    void findAllTransactionSuccess() {

        when(transactionRepository.findAll()).thenReturn(Flux.just(transaction));
        StepVerifier.create(transactionService.getAllTransaction())
                .expectNextMatches(transactionResponse1 -> transactionResponse1 != null)
                .verifyComplete();
        verify(transactionRepository,times(1)).findAll();

    }

    @Test
    void findByTransactionIdSuccess() {
        String idTransaction = "txn-001";
        when(transactionRepository.findById(idTransaction)).thenReturn(Mono.just(transaction));
        StepVerifier.create(transactionService.getTransactionById(idTransaction))
                        .assertNext(transactionResponse1 -> {
                            assertNotNull(transactionResponse1);
                            assertEquals(transactionResponse1.getIdTransaction(),idTransaction);
                        }).verifyComplete();
        verify(transactionRepository,times(1)).findById(idTransaction);
    }
    @Test
    void deleteByTransactionIdSuccess() {
        String idTransactionDelete = "txn-001";
        when(transactionRepository.deleteById(idTransactionDelete)).thenReturn(Mono.empty());
        StepVerifier.create(transactionService.deleteTransactionById(idTransactionDelete))
                .verifyComplete();
        verify(transactionRepository,times(1)).deleteById(idTransactionDelete);
    }
}
