package com.n26.codechallenge.repository;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.ParentTransactionDoesNotExistException;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;

import java.util.Map;

/**
 * Exposes a methods to add and read transactions
 *
 * Must-be thread safe
 */
public interface TransactionalRepository {

    /**
     * Add a transaction to a repository at a specific index
     *
     * After this methods has been successfully executed,
     * {@link TransactionalRepository::transaction} should return a
     * non-null value for this identifier
     *
     * @param id a unique identifier to be associated to this transaction for reference purposes
     * @param transaction the transaction to be stored
     * @throws TransactionAlreadyExistsException
     * @throws ParentTransactionDoesNotExistException
     */
     void addTransaction(Long id, Transaction transaction) throws ParentTransactionDoesNotExistException, TransactionAlreadyExistsException;

    /**
     * Obtain a transaction given a unique identifier.
     *
     * @param id The unique identified for a transaction
     * @return
     */
     Transaction transaction(Long id);


    /**
     * Return the set of children transactions of a given transaction id
     * @param parentId
     * @return
     */
    Map<Long, Transaction> children(Long parentId);

    /**
     * Return all transactions
     * @return
     */
    Map<Long, Transaction> allTransactions();
}
