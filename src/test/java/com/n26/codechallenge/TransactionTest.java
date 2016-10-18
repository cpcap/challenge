package com.n26.codechallenge;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Set of unit tests to ensure expected behaviour of {@link Transaction}
 *
 */
public class TransactionTest {

    @Test
    public void
    amount_without_parent_simply_works() throws Exception {
        Transaction transaction = new Transaction(23d, "any_type");
        assertEquals(transaction.amount(), 23d);
    }

    @Test
    public void
    amount_with_parent_simply_works() throws Exception {
        Transaction transaction = new Transaction(324d, "any_type", 32l);
        assertEquals(transaction.amount(), 324d);
    }

    @Test
    public void
    has_type_returns_true_simply_works() throws Exception {
        Transaction transaction = new Transaction(324d, "any_type");
        assertTrue(transaction.hasType("any_type"));
        Transaction anotherTransaction = new Transaction(324d, "any_type", 32l);
        assertTrue(anotherTransaction.hasType("any_type"));
    }

    @Test
    public void
    has_type_returns_false_simply_works() throws Exception {
        Transaction transaction = new Transaction(324d, "any_type");
        assertFalse(transaction.hasType("another_type"));
        Transaction anotherTransaction = new Transaction(324d, "any_type", 32l);
        assertFalse(anotherTransaction.hasType("another_type"));
    }


    @Test
    public void
    is_child_simply_works() throws Exception {
        Transaction transaction = new Transaction(324d, "any_type", 32l);
        assertTrue(transaction.isChild(32l));
    }

    @Test
    public void
    is_not_child_simply_works() throws Exception {
        Transaction transactionWithoutParent = new Transaction(324d, "any_type");
        assertFalse(transactionWithoutParent.isChild(32l));
    }

    @Test
    public void
    parent_id_simply_works() throws Exception {
        assertEquals(new Transaction(32d, "a", 34l).parentId(), (Long) 34l);
        assertNull(new Transaction(32d, "a").parentId());
    }

    @Test
    public void
    json_without_parent_simply_works() throws Exception {
        assertEquals(new Transaction(23d, "type").toJson(), "{\"amount\":23.0,\"type\":\"type\"}");
    }

    @Test
    public void
    json_with_parent_simply_works() throws Exception {
        assertEquals(new Transaction(23d, "type", 2l).toJson(), "{\"amount\":23.0,\"type\":\"type\",\"parent_id\":2}");
    }

}