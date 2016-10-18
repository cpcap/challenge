package com.n26.codechallenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.InvalidTransaction;
import com.n26.codechallenge.exception.TransactionDoesNotExistException;
import com.n26.codechallenge.service.WriteOptimizedTransactionService;
import com.n26.codechallenge.service.TransactionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Bind HTTPs requests to Java methods.
 *
 * The behaviour is delegated to an implemenation of {@link TransactionalService},
 * which defaults to {@link WriteOptimizedTransactionService}
 */
@Controller
@RequestMapping(value = "/transactionservice", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
class TransactionalController {

    private final TransactionalService transactionalService;

    TransactionalController(){
        this(new WriteOptimizedTransactionService());
    }

    TransactionalController(TransactionalService transactionalService){
        this.transactionalService = transactionalService;
    }

    @PutMapping(value = "/transaction/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Object addTransaction(@PathVariable Long id, @RequestBody @Validated Transaction transaction, BindingResult validationResult) throws InvalidTransaction {
        if (validationResult.hasErrors()) {
            throw new InvalidTransaction(validationResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()).toString());
        }
        transactionalService.addTransaction(id, transaction);
        return Collections.singletonMap("status", "ok");
    }

    @GetMapping(value = "/transaction/{id}")
    @ResponseBody
    public Object getTransaction(@PathVariable Long id) throws TransactionDoesNotExistException, JsonProcessingException {
        return transactionalService.transaction(id).toJson();
    }

    @GetMapping(value = "/types/{type}")
    @ResponseBody
    public Object getTransactionIdsByType(@PathVariable String type) {
        return transactionalService.transactionByType(type);
    }

    @GetMapping(value = "/sum/{parentId}")
    @ResponseBody
    public Object getChildrensSum(@PathVariable Long parentId) throws TransactionDoesNotExistException {
        return Collections.singletonMap("sum", transactionalService.transactionSum(parentId));
    }

    @ExceptionHandler
    public String handleException(MethodArgumentNotValidException exception) throws InvalidTransaction {
        throw new InvalidTransaction(exception.getMessage());
    }
}
