package com.compass.e_commerce.exception;

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

    @ExceptionHandler(GameAssociationConflictException.class)
    public ResponseEntity<?> gameAssociationConflictException(GameAssociationConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroDetails("Não é possivel excluir o Game",ex.getMessage()));
    }

    @ExceptionHandler(SaleAlreadyConfirmedException.class)
    public ResponseEntity<?> saleAlreadyConfirmedException(SaleAlreadyConfirmedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroDetails("Alteração Negada", ex.getMessage()));
    }
}
