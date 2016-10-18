package com.n26.codechallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates a {@link com.n26.codechallenge.Transaction } is invalid for any reason
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTransaction extends Exception {

    public InvalidTransaction(String erroMessage) {
        super(String.format("The transaction is invalid because %s", erroMessage));
    }
}
