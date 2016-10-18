package com.n26.codechallenge.controller;

import com.n26.codechallenge.Transaction;
import com.n26.codechallenge.exception.TransactionAlreadyExistsException;
import com.n26.codechallenge.service.TransactionalService;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import java.util.Collections;
import static org.mockito.Mockito.when;

/**
 * Set of unit tests to ensure expected behaviour of {@link TransactionalController}
 *
 * The only dependency {@link TransactionalService} is always mocked using Mockito.
 *
 * {@link #example_in_the_description_works} ensures the sequence of results provided on the description example works
 */
public class TransactionalControllerTest {

    @Test
    public void
    get_transaction_simply_works() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        Transaction transaction = new Transaction(32d, "type", 2l);
        when(mockService.transaction(32l)).thenReturn(transaction);
        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(get("/transactionservice/transaction/32"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.amount", is(32.0)))
                .andExpect(jsonPath("$.type", is("type")))
                .andExpect(jsonPath("$.parent_id", is(2)));
    }

    @Test
    public void
    get_transaction_per_type() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        when(mockService.transactionByType("some_type")).thenReturn(Collections.singleton(10l));
        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(get("/transactionservice/types/some_type"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("[10]"));
    }

    @Test
    public void
    get_sum_transaction_simply_works() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        when(mockService.transactionSum(10l)).thenReturn(10d);
        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(get("/transactionservice/sum/10"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.sum", is(10.0)));
    }

    @Test
    public void
    put_transaction_simply_works() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        Transaction transaction = new Transaction(32d, "type", 2L);

        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(put("/transactionservice/transaction/32").content(transaction.toJson()).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status", is("ok")));
    }

    @Test
    public void
    put_existing_transaction_return_400() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        Transaction transaction = new Transaction(32d, "type", 2l);
        doThrow(new TransactionAlreadyExistsException(2l)).when(mockService).addTransaction(anyLong(), any(Transaction.class));
        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(put("/transactionservice/transaction/32").content(transaction.toJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void
    put_transaction_without_amount_return_400() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        Transaction transaction = new Transaction(null, "type", 2l);
        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(put("/transactionservice/transaction/32").content(transaction.toJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void
    put_transaction_without_type_return_400() throws Exception {
        TransactionalService mockService = Mockito.mock(TransactionalService.class);
        Transaction transaction = new Transaction(32d, null, 2l);
        MockMvcBuilders.standaloneSetup(new TransactionalController(mockService))
                .build()
                .perform(put("/transactionservice/transaction/32").content(transaction.toJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void
    example_in_the_description_works() throws Exception {
        Transaction transaction = new Transaction(5000d, "cars");
        Transaction anotherTransaction = new Transaction(10000d, "shopping", 10l);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TransactionalController()).build();

        mockMvc
                .perform(put("/transactionservice/transaction/10").content(transaction.toJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status", is("ok")));

        mockMvc
                .perform(put("/transactionservice/transaction/11").content(anotherTransaction.toJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status", is("ok")));

        mockMvc
                .perform(get("/transactionservice/types/cars"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("[10]"));

        mockMvc
                .perform(get("/transactionservice/sum/10"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.sum", is(15000.0)));

        mockMvc
                .perform(get("/transactionservice/sum/11"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.sum", is(10000.0)));
    }

}