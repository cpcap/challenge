package com.n26.codechallenge.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.TransactionDoesNotExistException;
import com.n26.codechallenge.repository.TransactionalRepository;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Set of unit tests to ensure the implementation {@link WriteOptimizedTransactionService}
 */
public class WriteOptimizedTransactionServiceTest {

    @Test(expectedExceptions = TransactionDoesNotExistException.class)
    public void
    inexisting_transaction_fails() throws Exception {
        TransactionalService service = new WriteOptimizedTransactionService();
        Transaction inexistingTransaction = service.transaction(23l);
    }

    @Test public void
    transaction_by_type_simply_works() throws Exception {
        Map<Long, Transaction> mockTransactionStorage = new HashMap<>();
        mockTransactionStorage.put(1l, new Transaction(1d, "type1"));
        mockTransactionStorage.put(2l, new Transaction(1d, "type2"));
        mockTransactionStorage.put(3l, new Transaction(1d, "type2"));
        mockTransactionStorage.put(4l, new Transaction(1d, "type2"));
        TransactionalRepository repository = Mockito.mock(TransactionalRepository.class);
        when(repository.allTransactions()).thenReturn(mockTransactionStorage);
        TransactionalService service = new WriteOptimizedTransactionService(repository);
        Assert.assertEquals(service.transactionByType("type1"), Sets.newHashSet(1l));
        Assert.assertEquals(service.transactionByType("type2"), Sets.newHashSet(2l, 3l, 4l));
        Assert.assertEquals(service.transactionByType("type3"), Collections.emptySet());
    }

    @Test public void
    transaction_sum_by_parent_simply_woks() throws Exception {
        final double fixedAmountPerTransaction = 3d;
        final long parentIdWith3Children = 2l;
        final long parentIdWith1Child = 7l;

        TransactionalRepository repository = mockRepository(fixedAmountPerTransaction, parentIdWith3Children, parentIdWith1Child);

        TransactionalService service = new WriteOptimizedTransactionService(repository);
        assertEquals(service.transactionSum(parentIdWith3Children), 4 * fixedAmountPerTransaction);
        assertEquals(service.transactionSum(parentIdWith1Child), 2 * fixedAmountPerTransaction);
        assertEquals(service.transactionSum(1l), fixedAmountPerTransaction);
    }

    @Test public void
    transaction_sum_simply_works() throws Exception {
        final long root = 8l;
        final long middle = 1l;
        final long leaf = 2l;
        final double fixedAmountPerTransaction = 3d;

        TransactionalRepository repository = Mockito.mock(TransactionalRepository.class);
        final Transaction genericTransaction = new Transaction(fixedAmountPerTransaction, "any_type");
        when(repository.transaction(root)).thenReturn(genericTransaction);
        when(repository.transaction(middle)).thenReturn(genericTransaction);
        when(repository.transaction(leaf)).thenReturn(genericTransaction);

        when(repository.children(root)).thenReturn(Collections.singletonMap(middle,genericTransaction));
        when(repository.children(middle)).thenReturn(Collections.singletonMap(leaf, genericTransaction));


        TransactionalService service = new WriteOptimizedTransactionService(repository);
        assertEquals(service.transactionSum(root), 3*fixedAmountPerTransaction);
        assertEquals(service.transactionSum(middle), 2*fixedAmountPerTransaction);
        assertEquals(service.transactionSum(leaf), 1*fixedAmountPerTransaction);
    }


    @Test public void
    transaction_sum_with_multiple_children_works() throws Exception {
        final long root = 8l;
        final long middle = 1l;
        final long leaf = 2l;
        final long leaf2 = 2424l;
        final double fixedAmountPerTransaction = 3d;

        TransactionalRepository repository = Mockito.mock(TransactionalRepository.class);
        final Transaction genericTransaction = new Transaction(fixedAmountPerTransaction, "any_type");
        when(repository.transaction(root)).thenReturn(genericTransaction);
        when(repository.transaction(middle)).thenReturn(genericTransaction);
        when(repository.transaction(leaf)).thenReturn(genericTransaction);
        when(repository.transaction(leaf2)).thenReturn(genericTransaction);

        when(repository.children(root)).thenReturn(Collections.singletonMap(middle,genericTransaction));
        Map<Long, Transaction> map = new HashMap<>();
        map.put(leaf, genericTransaction);
        map.put(leaf2, genericTransaction);
        when(repository.children(middle)).thenReturn(map);


        TransactionalService service = new WriteOptimizedTransactionService(repository);
        assertEquals(service.transactionSum(root), 4*fixedAmountPerTransaction);
    }

    @Test public void
    transaction_sum_with_multiple_children_works_at_multiple_level() throws Exception {
        final long root = 8l;
        final long middle = 1l;
        final long middle2 = 12l;
        final long leaf = 2l;
        final long leaf2 = 2424l;
        final long leaf3 = 21l;
        final long leaf4 = 12424l;
        final double fixedAmountPerTransaction = 3d;

        TransactionalRepository repository = Mockito.mock(TransactionalRepository.class);
        final Transaction genericTransaction = new Transaction(fixedAmountPerTransaction, "any_type");
        when(repository.transaction(root)).thenReturn(genericTransaction);
        when(repository.transaction(middle)).thenReturn(genericTransaction);
        when(repository.transaction(leaf)).thenReturn(genericTransaction);
        when(repository.transaction(leaf2)).thenReturn(genericTransaction);
        when(repository.transaction(middle2)).thenReturn(genericTransaction);
        when(repository.transaction(leaf3)).thenReturn(genericTransaction);
        when(repository.transaction(leaf4)).thenReturn(genericTransaction);

        Map<Long, Transaction> rootMap = new HashMap<>();
        rootMap.put(middle, genericTransaction);
        rootMap.put(middle2, genericTransaction);
        when(repository.children(root)).thenReturn(rootMap);
        Map<Long, Transaction> middleNode1 = new HashMap<>();
        middleNode1.put(leaf, genericTransaction);
        middleNode1.put(leaf2, genericTransaction);
        when(repository.children(middle)).thenReturn(middleNode1);
        Map<Long, Transaction> middleNode2 = new HashMap<>();
        middleNode2.put(leaf3, genericTransaction);
        middleNode2.put(leaf4, genericTransaction);
        when(repository.children(middle2)).thenReturn(middleNode2);

        TransactionalService service = new WriteOptimizedTransactionService(repository);
        assertEquals(service.transactionSum(root), 7*fixedAmountPerTransaction);
    }

    private TransactionalRepository mockRepository(double fixedAmountPerTransaction, long parentIdWith3Children, long parentIdWith1Child) {
        TransactionalRepository repository = Mockito.mock(TransactionalRepository.class);
        when(repository.transaction(parentIdWith3Children)).thenReturn(new Transaction(fixedAmountPerTransaction, "any_type"));
        when(repository.transaction(parentIdWith1Child)).thenReturn(new Transaction(fixedAmountPerTransaction, "any_type"));
        when(repository.transaction(1l)).thenReturn(new Transaction(fixedAmountPerTransaction, "any_type"));
        when(repository.transaction(11l)).thenReturn(new Transaction(fixedAmountPerTransaction, "any_type"));
        when(repository.transaction(12l)).thenReturn(new Transaction(fixedAmountPerTransaction, "any_type"));
        when(repository.transaction(13l)).thenReturn(new Transaction(fixedAmountPerTransaction, "any_type"));
        when(repository.children(parentIdWith1Child)).thenReturn(Collections.singletonMap(1l, new Transaction(fixedAmountPerTransaction, "any_type", parentIdWith1Child)));
        when(repository.children(parentIdWith3Children))
                .thenReturn(ImmutableMap.of(
                        11l,new Transaction(fixedAmountPerTransaction, "any_type", parentIdWith3Children),
                        12l,new Transaction(fixedAmountPerTransaction, "any_type", parentIdWith3Children),
                        13l,new Transaction(fixedAmountPerTransaction, "any_type", parentIdWith3Children)));
        return repository;
    }



}