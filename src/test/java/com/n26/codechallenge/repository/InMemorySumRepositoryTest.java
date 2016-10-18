package com.n26.codechallenge.repository;

import com.n26.codechallenge.Transaction;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class InMemorySumRepositoryTest {

    @Test
    public void
    no_parent_simply_works() throws Exception {
        TransactionalRepository repo =  Mockito.mock(TransactionalRepository.class);
        when(repo.transaction(1l)).thenReturn(new Transaction(32d, "type"));
        SumRepository sumRepository = new InMemorySumRepository(repo);
        sumRepository.addAmount(1l, new Transaction(32d, "type"));
        assertEquals(sumRepository.transitiveSum(1l), 32d);
    }

    @Test
    public void
    one_parent_simply_works() throws Exception {
        TransactionalRepository repo =  Mockito.mock(TransactionalRepository.class);
        when(repo.transaction(1l)).thenReturn(new Transaction(12d, "type"));
        when(repo.transaction(2l)).thenReturn(new Transaction(11d, "type", 1l));
        SumRepository sumRepository = new InMemorySumRepository(repo);
        sumRepository.addAmount(1l, new Transaction(12d, "type"));
        sumRepository.addAmount(2l, new Transaction(11d, "type", 1l));
        assertEquals(sumRepository.transitiveSum(1l), 23d);
    }
}