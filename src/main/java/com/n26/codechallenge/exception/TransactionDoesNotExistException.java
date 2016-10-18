package com.n26.codechallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates a failure because a required {@link com.n26.codechallenge.Transaction }
 * does not exist
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionDoesNotExistException extends Exception {

    public TransactionDoesNotExistException(Long id){
        super(String.format("There is no transaction with id: %s", id));
    }
}
