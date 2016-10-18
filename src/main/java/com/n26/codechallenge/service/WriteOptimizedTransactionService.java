package com.n26.codechallenge.service;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.ParentTransactionDoesNotExistException;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;
import com.n26.codechallenge.exception.TransactionDoesNotExistException;
import com.n26.codechallenge.repository.InMemoryTransactionalRepository;
import com.n26.codechallenge.repository.TransactionalRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An implementation of transactional service that is optimized for write operations.
 *
 * All calculation for read operations are done on the fly.
 *
 * The task of saving and retrieving individual transactions is delegated to a @link {@link TransactionalRepository}
 */
public class WriteOptimizedTransactionService implements TransactionalService {

    private final TransactionalRepository transactionalRepository;

    WriteOptimizedTransactionService(TransactionalRepository transactionalRepository) {
        this.transactionalRepository = transactionalRepository;
    }

    public WriteOptimizedTransactionService(){
        this(new InMemoryTransactionalRepository());
    }

    /**
     * Add a transaction, associated with a given identifier.
     *
     * @param id the unique identifies of the transaction
     * @param transaction the transaction to be added
     * @throws TransactionAlreadyExistsException if there is already another transaction associated with this identifier
     * @throws ParentTransactionDoesNotExistException if the transaction is linked to an inexistent transaction
     */
    @Override
    public void addTransaction(Long id, Transaction transaction) throws TransactionAlreadyExistsException, ParentTransactionDoesNotExistException {
        transactionalRepository.addTransaction(id, transaction);
    }

    /**
     * Returns a transaction associated with a given identifier
     *
     * @param transactionId the identifier of the transaction to return
     * @return
     * @throws TransactionDoesNotExistException if there is no transaction associated with the given identifier
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
     * Completely delegates its behaviour to the underlying {@link TransactionalRepository}
     * @param type
     * @return
     */
    @Override
    public Set<Long> transactionByType(final String type) {
        return transactionalRepository.allTransactions().entrySet().stream().filter(t -> t.getValue().hasType(type)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Sums up the amount of the given {@link Transaction} with its children's amount
     *
     * @param parentId the id of the parent transaction
     * @return
     * @throws TransactionDoesNotExistException
     */
    @Override
    public Double transactionSum(Long parentId) throws TransactionDoesNotExistException {
        return subTreeAmount(parentId);
    }

    /**
     * Recursive depth-first read from a tree stored in a HashMap
     * @param parentId
     * @return
     * @throws TransactionDoesNotExistException
     */
    private Double subTreeAmount(Long parentId) throws TransactionDoesNotExistException {
        Map<Long, Transaction> children = transactionalRepository.children(parentId);
        Double acc = transaction(parentId).amount();
        for (Long id: children.keySet()){
            acc = acc + subTreeAmount(id);
        }
        return acc;
    }

}
