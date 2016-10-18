package com.n26.codechallenge.repository;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.ParentTransactionDoesNotExistException;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;
import org.testng.annotations.Test;


import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class InMemoryTransactionalRepositoryTest {

    @Test public void
    add_transaction_without_parent_simply_works() throws Exception {
        final TransactionalRepository repository = new InMemoryTransactionalRepository();
        final Transaction transaction = new Transaction(3d, "some_type");
        repository.addTransaction(1l, transaction);
    }

    @Test(expectedExceptions = TransactionAlreadyExistsException.class) public void
    add_existing_transaction_does_not_work() throws Exception {
        final long conflictId = 10L;
        TransactionalRepository repository = new InMemoryTransactionalRepository();
        repository.addTransaction(conflictId, new Transaction(2d, "any_type"));
        repository.addTransaction(conflictId, new Transaction(25d, "another_type"));
    }

    @Test(expectedExceptions = ParentTransactionDoesNotExistException.class) public void
    add_transaction_with_inexsting_parent_fails() throws Exception {
        final TransactionalRepository repository = new InMemoryTransactionalRepository();
        Transaction transaction = new Transaction(3d, "some_type", 8l);
        repository.addTransaction(1l, transaction);
    }

    @Test public void
    add_transaction_with_existing_parent_simply_works() throws Exception {
        final TransactionalRepository repository = new InMemoryTransactionalRepository();
        Transaction parentTransaction = new Transaction(3d, "some_type");
        repository.addTransaction(1l,parentTransaction);
        Transaction childTransaction = new Transaction(434d, "another_type", 1l);
        repository.addTransaction(32l, childTransaction);
    }

    @Test public void
    inexisting_transaction_return_null() throws Exception {
        final TransactionalRepository emptyRepository = new InMemoryTransactionalRepository();
        Transaction inexistingTransaction = emptyRepository.transaction(32l);
        assertNull(inexistingTransaction);
    }

    @Test public void
    existing_transaction_returns_succesfully() throws Exception {
        final TransactionalRepository repository = new InMemoryTransactionalRepository();
        Transaction newTransaction = new Transaction(3d, "some_type");
        repository.addTransaction(1l, newTransaction);
        Transaction returnedTransaction = repository.transaction(1l);
        assertEquals(newTransaction, returnedTransaction);
    }

}