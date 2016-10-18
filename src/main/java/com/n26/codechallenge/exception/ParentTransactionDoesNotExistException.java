package com.n26.codechallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates a {@link com.n26.codechallenge.Transaction } is invalid because it refers
 * to a parent {@link com.n26.codechallenge.Transaction } that does not exist
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParentTransactionDoesNotExistException extends InvalidTransaction {

    public ParentTransactionDoesNotExistException(Long parentId){
        super(String.format("There is not transaction with id: %s to be referenced as parent", parentId));
    }
}
