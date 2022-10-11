package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.endava.internship.mocking.model.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final User TEST_USER = new User(8402,"Rinn", ACTIVE);
    private static final Payment TEST_PAYMENT = new Payment(8402,1300d,"Payment");
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private PaymentService paymentService;
    @Captor
    ArgumentCaptor<Payment> argumentCaptor;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldSuccessCreatePayment() {
        //Given
        when(userRepository.findById(TEST_USER.getId())).thenReturn(Optional.of(TEST_USER));

        //When
        paymentService.createPayment(TEST_USER.getId(),1300d);
        verify(validationService).validateUserId(TEST_USER.getId());
        verify(validationService).validateAmount(TEST_PAYMENT.getAmount());
        verify(userRepository).findById(TEST_USER.getId());
        verify(validationService).validateUser(TEST_USER);
        verify(paymentRepository).save(argumentCaptor.capture());

        //Then
        assertAll(
                () -> assertEquals(TEST_USER.getId(),argumentCaptor.getValue().getUserId()),
                () -> assertEquals(TEST_PAYMENT.getAmount(),argumentCaptor.getValue().getAmount()),
                () -> assertEquals("Payment from user " + TEST_USER.getName(),argumentCaptor.getValue().getMessage())
        );
    }

    @Test
    void shouldSuccessEditMessage() {
        //Given
        final UUID paymentId = TEST_PAYMENT.getPaymentId();
        final String newMessage = TEST_PAYMENT.getMessage();
        when(paymentRepository.editMessage(paymentId,newMessage)).thenReturn(TEST_PAYMENT);

        final Payment payment = paymentService.editPaymentMessage(paymentId, newMessage);

        //When
        verify(validationService).validateMessage(payment.getMessage());
        verify(validationService).validatePaymentId(payment.getPaymentId());
        verify(paymentRepository).editMessage(paymentId,newMessage);

        //Then
        assertEquals(TEST_PAYMENT, payment);

    }
    @Test
    void shouldThrowExceptionWhenCreatePayment(){
        assertThrows(NoSuchElementException.class,() -> paymentService.createPayment(0,0d));
    }

    @Test
    void shouldGetAllByAmountExceeding() {
        final Double amount = 1000d;
        final List<Payment> paymentList = new ArrayList<>(Arrays.asList(
                new Payment(1,300d,"Payment1"),
                new Payment(2,325d,"Payment2"),
                new Payment(3,4000d,"Payment3"),
                new Payment(4,350d,"Payment4")
        ));
        final List<Payment> result =new ArrayList <>();
        result.add(paymentList.get(2));

        when(paymentRepository.findAll()).thenReturn(paymentList);
        final List<Payment>actualPayments = paymentService.getAllByAmountExceeding(amount);

        assertEquals(result,actualPayments);
        verify(paymentRepository).findAll();
    }
}
