package com.backend.store.ecommerce.exception;

import com.backend.store.ecommerce.api.model.ApiError;
import com.backend.store.ecommerce.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //@Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

   //// @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(apiError);
    }

    // Handle UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // Handle EmailFailureException
    @ExceptionHandler(EmailFailureException.class)
    protected ResponseEntity<Object> handleEmailFailure(EmailFailureException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("Error sending email");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // Handle UserNotVerifiedException
    @ExceptionHandler(UserNotVerifiedException.class)
    protected ResponseEntity<Object> handleUserNotVerified(UserNotVerifiedException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage("User not verified");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // Handle AddressFailureException
    @ExceptionHandler(AddressFailureExeption.class)
    protected ResponseEntity<Object> handleAddressFailure(AddressFailureExeption ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Error processing address");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // Handle MissingTokenException
    @ExceptionHandler(MissingTokenException.class)
    protected ResponseEntity<Object> handleMissingToken(MissingTokenException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
        apiError.setMessage("Authentication token missing");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // Handle PasswordFailureException
    @ExceptionHandler(PasswordFailureException.class)
    protected ResponseEntity<Object> handlePasswordFailure(PasswordFailureException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Password error");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}