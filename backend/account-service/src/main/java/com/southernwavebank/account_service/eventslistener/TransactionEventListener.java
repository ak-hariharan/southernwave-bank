package com.southernwavebank.account_service.eventslistener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.southernwavebank.account_service.events.TransactionEvent;
import com.southernwavebank.account_service.kafka.TransactionEventProducer;
import com.southernwavebank.account_service.model.TransactionStatus;
import com.southernwavebank.account_service.model.externaldto.TransactionDto;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class TransactionEventListener {

  
    private TransactionEventProducer producer;

    @Autowired
    public TransactionEventListener(TransactionEventProducer producer) {
        this.producer = producer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(TransactionEvent event) {
        log.info("Transaction committed successfully, sending SUCCESS event");
        TransactionDto dto = event.getTransactionDto();
        dto.setStatus(TransactionStatus.SUCCESS);
        producer.sendTransactionEvent(dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterRollback(TransactionEvent event) {
    	 log.info("Transaction rolled back, sending FAILED event");
         TransactionDto dto = event.getTransactionDto();
         dto.setStatus(TransactionStatus.FAILED);
         producer.sendTransactionEvent(dto);
    }
}

