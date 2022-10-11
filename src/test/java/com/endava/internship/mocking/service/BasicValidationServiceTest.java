package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static com.endava.internship.mocking.model.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicValidationServiceTest {
    final BasicValidationService validationService = new BasicValidationService();

    @ParameterizedTest
    @ValueSource(doubles = {0d,-1d,-5d})
    void shouldThrowExceptionWhenAmountIsNull(final Double number){
        //Given
        final Double wrongAmount = null;

        //When
        Executable wrongMethod = () -> validationService.validateAmount(number);

        //Then
        assertThrows(IllegalArgumentException.class, wrongMethod,"Amount is null");
        assertThrows(IllegalArgumentException.class,()->validationService.validateAmount(wrongAmount),"Amount less than 0");
    }

    @Test()
    void shouldThrowExceptionWhenPaymentIdIsNull(){
        //When
        Executable wrongMethod = () -> validationService.validatePaymentId(null);

        //Then
        assertThrows(IllegalArgumentException.class,wrongMethod,"Payment id is null");
    }

    @Test()
    void shouldThrowExceptionWhenUserIdIsNull(){
        //When
        Executable wrongMethod = () -> validationService.validateUserId(null);

        //Then
        assertThrows(IllegalArgumentException.class,wrongMethod,"Payment id is null");
    }

    @Test()
    void shouldThrowExceptionWhenMessageIsNull(){
        //When
        Executable wrongMethod = () -> validationService.validateMessage(null);

        //Then
        assertThrows(IllegalArgumentException.class,wrongMethod, "Payment message must not be null");
    }

    @Test()
    void ShouldThrowExceptionWhenUserIsInactive(){
        //Given
        final User user =  new User(1,"UserName", INACTIVE);

        //When
        final IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class,
                ()-> validationService.validateUser(user));

        //Then
        assertEquals("User with id " + user.getId() + " not in ACTIVE status",exception.getMessage());
    }

}