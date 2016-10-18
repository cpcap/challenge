package com.n26.codechallenge.service;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.ParentTransactionDoesNotExistException;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;
import com.n26.codechallenge.exception.TransactionDoesNotExistException;

import java.util.Set;

public interface TransactionalService {

    /**
     * Register a transaction, associated with a unique identifier.
     *
     * If there is already another transaction associated with the given identifier,
     * {@link TransactionAlreadyExistsException} is thrown and the client must react accordingly
     *
     * @param id
     * @param transaction
     * @throws TransactionAlreadyExistsException
     * @throws ParentTransactionDoesNotExistException
     */
    void addTransaction(Long id, Transaction transaction) throws TransactionAlreadyExistsException, ParentTransactionDoesNotExistException;

    /**
     * Recover a single transaction by its unique identifier.
     *
     * If there is no transaction associated with the identified provided,
     * a {link TransactionDoesNotExistException} is thrown
     * @param transactionId
     * @throws TransactionDoesNotExistException
     * @return
     */
    Transaction transaction(Long transactionId) throws TransactionDoesNotExistException;

    /**
     * Return a set of transactions that share a given type.
     *
     * If there is no transaction with the given type, an empty set is returned
     * @param type
     * @return
     */
    Set<Long> transactionByType(String type);

    /**
     * The sum of transactions amount linked to a particular transaction
     *
     * If there is no transaction associated with the identified provided,
     * {@link TransactionDoesNotExistException} is thrown
     * @param parentId
     * @return
     */
    Double transactionSum(Long parentId) throws TransactionDoesNotExistException;
}
