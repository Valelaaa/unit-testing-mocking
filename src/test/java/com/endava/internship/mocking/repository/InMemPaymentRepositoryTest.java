package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemPaymentRepositoryTest {
    private final Payment PAYMENT = new Payment(21, 590d, "Payment");
    private InMemPaymentRepository inMemPaymentRepository;

    @BeforeEach
    void Init() {
        inMemPaymentRepository = new InMemPaymentRepository();
    }

    @Test
    void shouldThrowExceptionWhenSaveNullPayment(){
        //Given
        //When
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        ()->inMemPaymentRepository.save(null));
        //Then
        assertEquals("Payment must not be null",exception.getMessage(),"Throw wrong exception");

    }
    @Test
    void shouldSuccessSavePayment(){
        //When
        final Payment savedPayment = inMemPaymentRepository.save(PAYMENT);

        //Then
        assertEquals(PAYMENT,savedPayment);
    }
    @Test
    void shouldThrowExceptionWhenPaymentInMap(){
        //Given
        inMemPaymentRepository.save(PAYMENT);

        //When
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> inMemPaymentRepository.save(PAYMENT));

        //Then
        assertEquals("Payment with id " + PAYMENT.getPaymentId() + "already saved",
                exception.getMessage(),"Payment not in memRepository");
    }

    @Test
    void shouldThrowExceptionWhenPaymentIdIsNull() {
        //When
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> inMemPaymentRepository.findById(null));

        //Then
        assertEquals("Payment id must not be null", exception.getMessage());
    }

    @Test
    void shouldSuccessFindById() {
        //Given
        final UUID paymentId1 = PAYMENT.getPaymentId();
        inMemPaymentRepository.save(PAYMENT);

        //When
        Optional<Payment> result = inMemPaymentRepository.findById(paymentId1);

        //Then
        assertEquals(PAYMENT, result.get(), "Find by Id failed");

    }

    @Test
    void shouldSuccessFindAll() {
        final Payment secondPayment = new Payment(4, 300d, "Payment2");
        //Given
        final List<Payment> expectedList = new ArrayList<>(
                Arrays.asList(
                        PAYMENT,
                        secondPayment
                )
        );
        inMemPaymentRepository.save(PAYMENT);
        inMemPaymentRepository.save(secondPayment);

        //When
        assertThat(inMemPaymentRepository.findAll()).hasSameElementsAs(expectedList);
        //Then
    }
    @Test
    void shouldThrowExceptionWhenPaymentNotInRepository(){
        //Given
        final UUID paymentId = randomUUID();
        //When
        final NoSuchElementException exception =
                assertThrows(NoSuchElementException.class,
                        ()->inMemPaymentRepository.editMessage(paymentId,"Payment"));
        //Then
        assertEquals("Payment with id " + paymentId + " not found",exception.getMessage());
    }
    @Test
    void shouldSuccessEditMessage(){
        //Given
        inMemPaymentRepository.save(PAYMENT);
        final UUID paymentId = PAYMENT.getPaymentId();
        final String newMessage = "ChangedMessage";
        //When
        final Payment payment = inMemPaymentRepository
                .editMessage(paymentId,newMessage);

        //Then
        assertEquals(newMessage,payment.getMessage());
    }
}