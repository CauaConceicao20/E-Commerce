package com.compass.e_commerce.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.e_commerce.exception.personalized.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorDetails> handleRedisConnectionFailure(RedisConnectionFailureException ex, WebRequest request)  {
        System.out.println("Erro ao conectar com Redis: " + ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Serviço indisponivel temporariamente",
                "Não foi possivel conectar ao redis",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorDetails);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                "Não Encontrado",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorDetails> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                "Não Encontrado",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder message = new StringBuilder();
        ex.getFieldErrors().forEach(error -> message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                message.toString(),
                request.getDescription(false)
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(DeletionNotAllowedException.class)
    public ResponseEntity<ErrorDetails> handleDeletionNotAllowedException(DeletionNotAllowedException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                "Conflito",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(SaleAlreadyConfirmedException.class)
    public ResponseEntity<ErrorDetails> handleSaleAlreadyConfirmedException(SaleAlreadyConfirmedException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                "Conflito",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(GameIsInactiveException.class)
    public ResponseEntity<ErrorDetails> handleGameIsInactiveException(GameIsInactiveException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                "Conflito",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(ExceededStockException.class)
    public ResponseEntity<ErrorDetails> handleExceededStockException(ExceededStockException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Não Autorizado",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ErrorDetails> handleUserInactiveException(UserInactiveException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "Proibido",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }

    @ExceptionHandler(AccessRestrictException.class)
    public ResponseEntity<ErrorDetails> accessRestrictException(JWTDecodeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "Não Autorizado",
                "Usuario não disponivel",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<ErrorDetails> handleJWTDecodeException(JWTDecodeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Não Autorizado",
                "Token inválido.",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorDetails> handleJWTVerificationException(JWTVerificationException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Não Autorizado",
                "Token inválido ou expirado.",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }
}

