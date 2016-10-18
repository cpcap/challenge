package com.n26.codechallenge.repository;


import java.util.Set;

/**
 * Keeps an association between transaction type and transaction identifiers
 */
public interface TypeRepository {

    /**
     * Associate a identifier to a transaction type
     * @param type the type of the transaction
     * @param id the identifier associated with a transaction
     */
    void addTransaction(String type, Long id);

    /**
     * Returns the set of identifiers associated to transactions of a given type
     * @param type
     * @return
     */
    Set<Long> getTransactionIdsByType(String type);

}
