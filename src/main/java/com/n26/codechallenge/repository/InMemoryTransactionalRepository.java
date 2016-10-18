package com.n26.codechallenge.repository;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.ParentTransactionDoesNotExistException;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A in-memory thread safe (synchronized) implementation of {@link TransactionalRepository}
 */
@Slf4j
public class InMemoryTransactionalRepository implements TransactionalRepository {

    private final Map<Long, Transaction> transactionMap;

    InMemoryTransactionalRepository(Map<Long, Transaction> transactionMap){
        this.transactionMap = transactionMap;
    }

    public InMemoryTransactionalRepository(){
        this(new HashMap<>());
    }

    /**
     * Add a transaction, if valid
     *
     * @param id the unique identifies used to refer to a {@link Transaction}
     * @param transaction the transaction to be added
     * @throws TransactionAlreadyExistsException if there is already another transaction associated with this identifier
     * @throws ParentTransactionDoesNotExistException if the transaction is linked to an inexistent transaction
     */
    @Override
    public synchronized void addTransaction(Long id, Transaction transaction) throws ParentTransactionDoesNotExistException, TransactionAlreadyExistsException {
        ensureIdIsNotUsed(id);
        validateParent(transaction);
        transactionMap.put(id, transaction);
    }

    /**
     * Get the value from the map, given its identifier
     *
     * If a transaction does not exist with the given identifier, null is returned.
     *
     * @param id The unique identified for a transaction
     * @return
     */
    @Override
    public synchronized Transaction transaction(Long id) {
        return transactionMap.get(id);
    }

    /**
     * Iterates through all existing {@link Transaction}, keeping only those that
     * are children of given transaction ID
     *
     * @param parentId
     * @return
     */
    @Override
    public Map<Long, Transaction> children(Long parentId) {
        return transactionMap
                .entrySet()
                .stream()
                .filter(e -> e.getValue().isChild(parentId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    /**
     * Simply returns the collection of values of the internal map
     *
     * @return
     */
    @Override
    public synchronized Map<Long, Transaction> allTransactions() {
        return transactionMap;
    }

    /**
     * Ensures a given identifier is not used
     * @param id the identifier to be checked
     * @throws TransactionAlreadyExistsException if id refers to a transaction
     */
    private void ensureIdIsNotUsed(Long id) throws TransactionAlreadyExistsException {
        if(transactionMap.get(id) != null){
            throw new TransactionAlreadyExistsException(id);
        }
    }

    /**
     * Ensure the parent id field is valid.
     *
     * A non existing parent id is considered valid.
     *
     * If the parent id exists, there must exist a transaction with such id
     * @param transaction
     * @throws ParentTransactionDoesNotExistException
     */
    private void validateParent(Transaction transaction) throws ParentTransactionDoesNotExistException {
        final Long parentId = transaction.parentId();
        if(parentId != null){
            log.info(String.format("A parent id %s has been provided and is now subject to further validation", parentId));
            if(!transactionMap.containsKey(parentId)){
                throw new ParentTransactionDoesNotExistException(parentId);
            }
        }
    }
}
