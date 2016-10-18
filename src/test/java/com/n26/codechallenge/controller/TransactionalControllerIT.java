package com.n26.codechallenge.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.n26.codechallenge.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Integration test to ensure the example given on the description succeeds with a real
 * web application server as well
 */
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionalControllerIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void
    documented_works() throws JsonProcessingException {
        Transaction transaction = new Transaction(5000d, "cars");
        Transaction anotherTransaction = new Transaction(10000d, "shopping", 10l);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        HttpEntity<String> firstTransaction = new HttpEntity<>(transaction.toJson(),headers);
        HttpEntity<String> secondTransaction = new HttpEntity<>(anotherTransaction.toJson(),headers);

        ResponseEntity firstPut = this.restTemplate.exchange("/transactionservice/transaction/10", HttpMethod.PUT, firstTransaction, Map.class);
        ResponseEntity secondPut = this.restTemplate.exchange("/transactionservice/transaction/11", HttpMethod.PUT, secondTransaction, Map.class);

        assertEquals(firstPut.getStatusCode(), HttpStatus.CREATED);
        assertEquals(secondPut.getStatusCode(), HttpStatus.CREATED);

        ResponseEntity<Map> sum10 = this.restTemplate.getForEntity("/transactionservice/sum/10", Map.class);
        assertEquals(sum10.getBody().get("sum").toString(), "15000.0");

        ResponseEntity<Map> sum11 = this.restTemplate.getForEntity("/transactionservice/sum/11", Map.class);
        assertEquals(sum11.getBody().get("sum").toString(), "10000.0");
    }



}