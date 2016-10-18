package com.n26.codechallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates a {@link com.n26.codechallenge.Transaction } is invalid because there is already
 * another {@link com.n26.codechallenge.Transaction } referenced by the same identifier
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionAlreadyExistsException extends InvalidTransaction {

    public TransactionAlreadyExistsException(Long id) {
        super(String.format("The identifier: %s is already used", id));
    }
}
