package com.n26.codechallenge.repository;


import com.n26.codechallenge.Transaction;

/**
 * A repository to manipulate the sum of amounts related to a {@link com.n26.codechallenge.Transaction }
 */
public interface SumRepository {

    /**
     * Register an transaction amount
     * @param id
     * @param transaction
     */
    void addAmount(Long id, Transaction transaction);


    /**
     * Get the amount associated transitively with a transaction
     * @param id
     * @return
     */
    Double transitiveSum(Long id);

}
