package com.n26.codechallenge.repository;

import com.n26.codechallenge.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a map between a {@link com.n26.codechallenge.Transaction } identifier and the sum of all amounts related to this {@link com.n26.codechallenge.Transaction }.
 *
 * Performs all the work on the write operation, which means that on every update, it needs to ensure all parents are updated
 */
public class InMemorySumRepository implements SumRepository {

    private final TransactionalRepository transactionalRepository;
    private final Map<Long, Double> amountPerSubTree = new HashMap<>();

    public InMemorySumRepository(TransactionalRepository transactionalRepository) {
        this.transactionalRepository = transactionalRepository;
    }

    /**
     * Add an entry for the given transaction and ensure all the parents
     * are updated too
     * @param id the identifier of the transaction
     * @param transaction the transaction to be added
     */
    @Override
    public synchronized void addAmount(Long id, Transaction transaction) {
        // For the new transaction, there should not entry so we can just put it
        amountPerSubTree.put(id, transaction.amount());
        addToParents(transaction.parentId(), transaction.amount());
    }

    private void addToParents(Long transactionId, Double increment){
        if(transactionId == null){
            //end of tree
            return;
        }
        Double currentValue = amountPerSubTree.get(transactionId);
        if(currentValue == null){
            throw new IllegalStateException("A transaction has no sum");
        }
        amountPerSubTree.put(transactionId, currentValue + increment);
        addToParents(transactionalRepository.transaction(transactionId).parentId(), increment);
    }

    @Override
    public synchronized Double transitiveSum(Long id) {
        return amountPerSubTree.get(id);
    }
}
