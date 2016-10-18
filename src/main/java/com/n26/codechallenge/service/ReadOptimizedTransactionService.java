package com.n26.codechallenge.service;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.ParentTransactionDoesNotExistException;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;
import com.n26.codechallenge.exception.TransactionDoesNotExistException;
import com.n26.codechallenge.repository.*;

import java.util.Set;

/**
 *
 * An implementation optimized for read operations.
 *
 * All the hard work is done during adding a transaction, while read operations are fast
 */
public class ReadOptimizedTransactionService implements TransactionalService {

    private final TransactionalRepository transactionalRepository = new InMemoryTransactionalRepository();
    private final TypeRepository typeRepository = new InMemoryTypeRepository();
    private final SumRepository sumRepository = new InMemorySumRepository(transactionalRepository);

    /**
     * Add a transaction to main repository and update two additional views:
     * 1. transactions IDs per type
     * 2. sum of all amounts related to a single transaction
     *
     * @param id
     * @param transaction
     * @throws TransactionAlreadyExistsException
     * @throws ParentTransactionDoesNotExistException
     */
    @Override
    public synchronized void addTransaction(Long id, Transaction transaction) throws TransactionAlreadyExistsException, ParentTransactionDoesNotExistException {
        transactionalRepository.addTransaction(id, transaction);
        typeRepository.addTransaction(transaction.type(), id);
        sumRepository.addAmount(id, transaction);
    }

    /**
     * Returns the transaction from the repository by Id
     * @param transactionId
     * @return
     * @throws TransactionDoesNotExistException if the ID does not refert to any transaction
     */
    @Override
    public Transaction transaction(Long transactionId) throws TransactionDoesNotExistException {
        Transaction transaction = transactionalRepository.transaction(transactionId);
        if(transaction == null){
            throw new TransactionDoesNotExistException(transactionId);
        }
        return transaction;
    }

    /**
     * Returns the set of IDs directly from the repository
     * @param type
     * @return
     */
    @Override
    public Set<Long> transactionByType(String type) {
        return typeRepository.getTransactionIdsByType(type);
    }

    /**
     * Returns the amount directly from the repository
     * @param parentId
     * @return
     * @throws TransactionDoesNotExistException if the parent ID does not refer to any transaction
     */
    @Override
    public Double transactionSum(Long parentId) throws TransactionDoesNotExistException {
        return sumRepository.transitiveSum(parentId);
    }
}
