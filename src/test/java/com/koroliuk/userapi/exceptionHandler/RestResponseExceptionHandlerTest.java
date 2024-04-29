package com.koroliuk.userapi.exceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestResponseExceptionHandlerTest {

    @InjectMocks
    private RestResponseExceptionHandler exceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpInputMessage httpInputMessage;

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameters");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(ex);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("There is no accepted data", response.getBody().get("reason"));
        assertEquals("Invalid parameters", response.getBody().get("message"));
    }

    @Test
    void testHandleEntityNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("User not found");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleEntityNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no entity with such id", Objects.requireNonNull(response.getBody()).get("reason"));
        assertEquals("User not found", response.getBody().get("message"));
    }

    @Test
    void testHandleValidationErrors() {
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("user", "email", "Email is invalid"));

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Input data is not valid", Objects.requireNonNull(response.getBody()).get("reason"));
        assertEquals(List.of("Email is invalid"), response.getBody().get("message"));
    }

    @Test
    void testHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Request body is missing", httpInputMessage);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Input data is not valid", Objects.requireNonNull(response.getBody()).get("reason"));
        assertEquals("Request body is missing", response.getBody().get("message"));
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("General error occurred");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred", Objects.requireNonNull(response.getBody()).get("reason"));
        assertEquals("General error occurred", response.getBody().get("message"));
    }
}
