package com.backend.store.ecommerce.exception;

import com.backend.store.ecommerce.api.model.ApiError;
import com.backend.store.ecommerce.api.model.ApiValidationError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // === Spring MVC Exceptions ===
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.error("Malformed JSON request", ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Malformed JSON request");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");

        // Add detailed field errors
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            apiError.addSubError(new ApiValidationError(
                    fieldError.getObjectName(),
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage()
            ));
        });

        // Add global errors
        ex.getBindingResult().getGlobalErrors().forEach(objectError -> {
            apiError.addSubError(new ApiValidationError(
                    objectError.getObjectName(),
                    objectError.getDefaultMessage()
            ));
        });

        return buildResponseEntity(apiError);
    }

    // === Authentication & Authorization Exceptions ===
    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage("User registration failed");
        apiError.setDebugMessage("A user with the provided email or username already exists");
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    protected ResponseEntity<Object> handleUserNotVerified(UserNotVerifiedException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage("Email verification required");
        apiError.setDebugMessage("Please verify your email address before proceeding");
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MissingTokenException.class)
    protected ResponseEntity<Object> handleMissingToken(MissingTokenException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
        apiError.setMessage("Authentication required");
        apiError.setDebugMessage("Valid authentication token is required");
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage("Access denied");
        apiError.setDebugMessage("You don't have permission to perform this action");
        return buildResponseEntity(apiError);
    }

    // === Business Logic Exceptions ===
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        log.debug("Entity not found", ex);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage("Resource not found");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AddressFailureExeption.class)
    protected ResponseEntity<Object> handleAddressFailure(AddressFailureExeption ex) {
        log.error("Address processing error", ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Invalid address information");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PasswordFailureException.class)
    protected ResponseEntity<Object> handlePasswordFailure(PasswordFailureException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Password validation failed");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // === Infrastructure Exceptions ===
    @ExceptionHandler(EmailFailureException.class)
    protected ResponseEntity<Object> handleEmailFailure(EmailFailureException ex) {
        log.error("Email service error", ex);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("Email delivery failed");
        apiError.setDebugMessage("Unable to send email. Please try again later.");
        return buildResponseEntity(apiError);
    }

    // === Generic Exception Handler ===
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        log.error("Uncaught exception", ex);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("An unexpected error occurred");
        apiError.setDebugMessage("Please contact support if the problem persists");
        return buildResponseEntity(apiError);
    }

    // === Validation Exceptions ===
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");

        ex.getConstraintViolations().forEach(violation -> {
            apiError.addSubError(new ApiValidationError(
                    violation.getRootBeanClass().getSimpleName(),
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue(),
                    violation.getMessage()
            ));
        });

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}