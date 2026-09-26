package com.corteBrabo.barbershopApi.handler;

import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.exception.PlanLimitException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(String message, Map<String, String> fields) {
        static ApiError of(String message) {
            return new ApiError(message, null);
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of(ex.getMessage()));
    }

    @ExceptionHandler(PlanLimitException.class)
    public ResponseEntity<ApiError> handlePlanLimit(PlanLimitException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(ApiError.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(ApiError.of("Parâmetro inválido: " + e.getName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(ApiError.of("Parâmetro obrigatório não informado: " + e.getParameterName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiError.of("Corpo da requisição inválido. Verifique os dados enviados."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String causeMsg = e.getMostSpecificCause().getMessage();
        if (causeMsg != null && causeMsg.contains("uk_user_business_telefone")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("Telefone já cadastrado"));
        }
        if (causeMsg != null && causeMsg.contains("uk_user_email")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("E-mail já cadastrado"));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("Violação de integridade dos dados"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.putIfAbsent(err.getField(), err.getDefaultMessage())
        );
        ex.getBindingResult().getGlobalErrors().forEach(err ->
                errors.putIfAbsent(err.getObjectName(), err.getDefaultMessage())
        );
        String first = errors.values().stream().findFirst().orElse("Dados inválidos");
        return ResponseEntity.badRequest().body(new ApiError(first, errors));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidation(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("Dados inválidos"));
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, DisabledException.class})
    public ResponseEntity<ApiError> handleBadCredentials(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError.of("E-mail ou senha incorretos"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError.of(
                e.getMessage() == null || e.getMessage().equals("Access Denied") ? "Você não tem permissão para isso" : e.getMessage()));
    }
}
