package com.n26.codechallenge.repository;

import com.google.common.collect.Sets;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.*;

public class InMemoryTypeRepositoryTest {

    @Test
    public void
    no_transaction_added_return_empty() throws Exception {
        TypeRepository typeRepository = new InMemoryTypeRepository();
        assertEquals(typeRepository.getTransactionIdsByType("type"), Collections.emptySet());
    }

    @Test
    public void
    add_transaction_per_type_simply_works() throws Exception {
        TypeRepository typeRepository = new InMemoryTypeRepository();
        typeRepository.addTransaction("type", 3l);
        assertEquals(typeRepository.getTransactionIdsByType("type"), Collections.singleton(3l));
    }

    @Test
    public void
    add_multiple_transactions_per_type_simply_works() throws Exception {
        TypeRepository typeRepository = new InMemoryTypeRepository();
        typeRepository.addTransaction("type", 3l);
        typeRepository.addTransaction("type", 4l);
        assertEquals(typeRepository.getTransactionIdsByType("type"), Sets.newHashSet(3l, 4l));
    }

    @Test
    public void
    add_multiple_transactions_with_same_id_exclude_duplicates() throws Exception {
        TypeRepository typeRepository = new InMemoryTypeRepository();
        typeRepository.addTransaction("type", 3l);
        typeRepository.addTransaction("type", 3l);
        assertEquals(typeRepository.getTransactionIdsByType("type"), Sets.newHashSet(3l));
    }


}