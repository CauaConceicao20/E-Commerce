package com.compass.e_commerce.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handler404(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroDetails("Elemento Não Encontrado", ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroDetails("Elemento não encontrado id incorreto", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handler400(MethodArgumentNotValidException ex) {
        var erro = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erro.stream().map(ErroDetails::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handlerInvalidEnum(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new ErroDetails("Valor inválido fornecido para o campo enum.", ex.getMessage()));
    }

    @ExceptionHandler( DeletionNotAllowedException.class)
    public ResponseEntity<?> gameAssociationConflictException( DeletionNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroDetails("Não foi possivel excluir",ex.getMessage()));
    }

    @ExceptionHandler(SaleAlreadyConfirmedException.class)
    public ResponseEntity<?> saleAlreadyConfirmedException(SaleAlreadyConfirmedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroDetails("Não foi possivel concluir a ação", ex.getMessage()));
    }

    @ExceptionHandler(GameIsInactiveExcpetion.class)
    public ResponseEntity<?> gameIsInactiveExcpetion(GameIsInactiveExcpetion ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroDetails("Game inativado", ex.getMessage()));
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<?> handleJWTDecodeException(JWTDecodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroDetails("Token invalido", ex.getMessage()));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<?> handleJWTVerificationException(JWTVerificationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroDetails("Token inválido ou expirado", ex.getMessage()));
    }
}
