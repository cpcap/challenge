# Transaction service

This is a small service to manipulate transactions. Specifically, it offers endpoints to:

1. Create a transaction
2. Read a transaction
3. Obtain all transaction of a certain type
4. Obtain the sum of all transactions that are transitively linked to a certain transaction


## Technologies

The following technologies were used to accomplish this task:

* Spring Boot, for the web part, both implementation and test

* TestNG, for tests


## Build & Dependencies

The build is managed by Maven. All you need is to have Maven installed and it will take care of downloading the required jars.

## Tests

All classes are covered by unit tests. Mock was used whenever applicable to ensure only the target class behaviour was under test.

There is also one integration test (TransactionControllerIT) that send requests against the application deployed on tomcat.

## Run

    mvn clean package exec:java

## Assumptions

The challenge description let some points open, so here are the assumptions taken for development:

1. A transaction is immutable. This means that any attempt to modify an existing transaction result in an error.

2. A transaction that refers to a parent transaction that does not exist is *NOT* a valid transaction.


## HTTP API Documentation

### Create a transaction

Request:

    PUT /transactionservice/transaction/{transaction_id}


    {
      "amount":double,
      "type":string,
      "parent_id":long
    }


Response (201):

    { "status": "ok" }

Errors:
    2. 400, if amount of type are not provide or parent_id does not exist


### Get a transaction

Request:

    GET /transactionservice/transaction/{transaction_id}

Response (200):

      {
          "amount":double,
          "type":string,
          "parent_id":long
        }

Errors:
    2. 404 if there is no transaction associated with $transaction_id

### Get a transaction IDs by type

Request:

    GET /transactionservice/types/$type

Response (200):

    [{transaction_id},{transaction_id},...]


### Get the sum of transactions

Request:

    GET /transactionservice/sum/{transaction_id}

Response (200):

    {"sum":double}

Returns:

    1. 404 if there is no transaction associated with $transaction_id



## Implementation

The code was structured using a common 3 layer architecture: controller, service and repository.

Two questions rose while implementing this challenge. The first one was whether to put more
effort (logic) on read or write operations while the second one whether to put more logic
on the repository or service layer.

Two of the operations, namely PUT by id an GET by ID, are straightforward to implement because they
did not involve any data manipulation, i.e. the involved the direct representation of the data provided.

The other two operations, to obtain IDs by a type and sum of children transactions, require
some manipulation (filter and aggregation) over the raw data.

Two opposite approaches were considered:

1. Whenever storing a transaction, we could update the different views (filter and aggregated)
so that, when a read request arrives, the result is ready. (see ReadOptimizedTransactionService implementation)

  Advantages:

  * Read is very fast, since the target value is ready to be served.

  Disadvantages:

  * Writing is slow, which keeps the lock (for thread safety) longer.
  * Consistency: all views must be update in a transactional (atomic) fashion, otherwise there might be the chance an error occurs and the different storages will have inconsistent values.


2. We do the minimum when storing a transaction and perform all the data manipulation at once when a read request arrives.
(see WriteOptimizedTransactionService implementation)

  Advantages:
  * Write is very fast, hence the lock is kept for a shorter period.
  * Consistency: since there is only one place where data is stored, there is no need to worry about consistency.
  * Disadvantage: Read operation becomes slow, since all the process has to be done "on the fly".


If the expected traffic pattern (share among all endpoints) were known,
such decision would be probably easier to take.
Taking into consideration the available information, the second approach looks more appealing due to
the following reasons (assumptions):
1. In the banking domain, consistency has probably one of the highest priorities.
2. As a client, making sure a transaction is consistently and rapidly stored has more value than checking an extract in the same manner.
3. The disadvantage of having to do the hard-work on the fly is highly affected by the current implementation, which is backed by a in-memory storage.
In this particularly case, data was stored in a hash map, which is far from optimal for both use cases (filter by type and a sum of nodes in a tree).
This disadvantage could be substantially mitigated by a more robust implementation of the TransactionRepository, making use of modern databases.
4. From an API design perspective, by limiting the implementation to solely add the transaction (and nothing more), we end up closer to what the client of the API expects.


### Concurrency and scalability

Concurrency is solved by syncing all methods of the transactional repository. By doing so, we ensure just one thread
has the lock at time, which means that every thread will read the latest value. The drawback is the bottleneck
introduced. While the controller and service layer are stateless, making it proper to horizontal scaling and high parallelism,
the overall throughput of this application is limited by the repository implementation, where actions can be taken only one at time.
The synchronized operation was reduced to the minimum required, to ensure the lock was acquired only when indispensable.
