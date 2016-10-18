package com.n26.codechallenge.service;

import com.google.common.collect.Sets;
import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.TransactionDoesNotExistException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Set of unit tests to ensure the implementation {@link ReadOptimizedTransactionService}
 */
public class ReadOptimizedTransactionServiceTest {

    @Test public void
    transaction_sum_by_recursive_parent_fails() throws Exception {
        final long root = 1l;
        final long middleNode = 2l;
        final long leaf = 8l;
        final double fixedAmountPerTransaction = 3d;

        TransactionalService service = new ReadOptimizedTransactionService();
        service.addTransaction(root, new Transaction(fixedAmountPerTransaction, "any_type"));
        service.addTransaction(middleNode, new Transaction(fixedAmountPerTransaction, "any_type", root));
        service.addTransaction(leaf, new Transaction(fixedAmountPerTransaction, "any_type", middleNode));
        assertEquals(service.transactionSum(root), 3*fixedAmountPerTransaction);
    }

    @Test public void
    transaction_add_per_type_simply_works() throws Exception {
        final double fixedAmountPerTransaction = 3d;

        TransactionalService service = new ReadOptimizedTransactionService();
        service.addTransaction(1l, new Transaction(fixedAmountPerTransaction, "any_type"));
        service.addTransaction(2l, new Transaction(fixedAmountPerTransaction, "any_type"));
        service.addTransaction(3l, new Transaction(fixedAmountPerTransaction, "any_type"));
        assertEquals(service.transactionByType("any_type"), Sets.newHashSet(1l, 2l,3l));
    }

    @Test(expectedExceptions = TransactionDoesNotExistException.class) public void
    inexisting_transaction_fails() throws Exception {
        TransactionalService service = new ReadOptimizedTransactionService();
        Transaction inexistingTransaction = service.transaction(23l);
    }

}