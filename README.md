# donus-backend-challenge  
O objetivo é criar uma API com algumas funções essenciais relacionadas ao gerenciamento de contas bancárias:  
  
- Para abrir uma conta é necessário apenas o nome completo e CPF da pessoa, mas só é permitido uma conta por pessoa;  
- Com essa conta é possível realizar transferências para outras contas, depositar e retirar o dinheiro;  
- Ao depositar dinheiro na conta, o cliente recebe da Donus mais meio por cento do valor depositado como bônus;  
- Ao retirar o dinheiro é cobrado o valor de um por cento sobre o valor retirado, e não aceitamos valores negativos nas contas;  
- As transferências entre contas são gratuitas e ilimitadas;  
- É importante ter o histórico de todas as movimentações dos clientes.

# Requirements for this App
For building and running the application you need:

-   JDK 1.8
-   Spring Boot
-   Gradle
-   PostgreSQL database (I am using Kubernetes, the docker-compose file is included)

# Before run the application, set up the database
In order to run the application, you need to manually create the database ***partner***, the tables *tab_account* and *tab_transaction* will be automatically created on the execution time.

The script ***V1__Create_Initial_Schema.sql*** is included in case we want to use Flyway for upcoming upgrades to the app database schema, then we need to add the V2__ scripts, V3__ scripts, VN__ scripts as needed.

## Running the application locally

## Running the application tests

## CRUD Endpoints
Please note that I am usinng curl to perform my local tests and write this documentation, but the tests could be executed in a browser, postman, insonia, and so on.

 1. Create account
 To create a new account, you need to perform a call like this:
```
curl --header "Content-Type: application/json" --request POST --data '{"name":"Renato","taxId":"744.966.720-73"}' http://localhost:8080/accounts
```
You are going to receive a JSON like this (that includes the account id and the initial balance):
```
{
	"id": "cea123bc-0b10-4898-b117-ebd7af844a84",
	"name": "Renato",
	"taxId": "74496672073",
	"balance": 0
}
```
 Error checking:
 - [ ] Tax Id Validation
```
curl --header "Content-Type: application/json" --request POST --data '{"name":"Renato","taxId":"744.966.721-73"}' http://localhost:8080/accounts
```
Error message: Invalid Tax Id: 744.966.721-73

 - [ ] Try to open a second account for the same tax id
```
curl --header "Content-Type: application/json" --request POST --data '{"name":"Joao","taxId":"744.966.720-73"}' http://localhost:8080/accounts
```
Error message: Account already exists for the tax id: 744.966.720-73

 2. Retrieve account by tax id

 To retrieve an account by tax id, you need to perform a call like this:
```
curl --request GET http://localhost:8080/accounts/taxid/74496672073
```
You are going to receive a JSON similar to the example on the item 1.

Please note that only the numbers are persisted on the database, and the calls will work with or without pontuaction marks.

 Error checking:
 - [ ] Check the existence of the account
```
curl --request GET http://localhost:8080/accounts/taxid/487.281.780-09
```
Error Message: Account not found for the tax id...
 

 3. Retrieve account by id
 To retrieve an account by id, you need to perform a call like this:
```
curl --request GET http://localhost:8080/accounts/id/aa8aed5b-f2b2-407f-807a-e89eea558922
```
You are going to receive a JSON similar to the example on the item 1, and we also have in place the check for the existence, or not, of the account. The UUID correctness is also verified (by Java).

 4. List all accounts

To retrieve all accounts stored on the database, you need to execute a call similar the one bellow.
```
curl --request GET http://localhost:8080/accounts
```
You are going to receive a JSON like this:
```
{
	"page": 0,
	"pageSize": 20,
	"total": 2,
	"items": [{
		"id": "95ee5aae-34ed-4bf3-8ce3-df97652a1a58",
		"name": "Joao",
		"taxId": "30878308016",
		"balance": 0.00
	}, {
		"id": "cea123bc-0b10-4898-b117-ebd7af844a84",
		"name": "Renato",
		"taxId": "74496672073",
		"balance": 0.00
	}]
}
```
Please note that for this endpoint, we are using ***Pageable*** to accomodate a great quantity of accounts, without transporting (and retrieving from the database) a lot of data in a single time. The quantity of items per page is configurable and set by pageSize. 

## Deposit

When a deposit occurs, the following call, for example, need to be executed.
```
curl --request PUT http://localhost:8080/accounts/cea123bc-0b10-4898-b117-ebd7af844a84/deposit/1000
```
The default reply for this endpoint is NO_CONTENT(204, Series.SUCCESSFUL, "No Content").

If we retrieve the account, we will see that its balance is 1005.00 (1000.00 plus the 0.5% bonus).
```
curl --request GET http://localhost:8080/accounts/id/cea123bc-0b10-4898-b117-ebd7af844a84
```
```
{
	"id": "cea123bc-0b10-4898-b117-ebd7af844a84",
	"name": "Renato",
	"taxId": "74496672073",
	"balance": 1005.00
}
```
On the transaction table, we have 2 entries for this movimentation. 
```
|Account      |Type    |Value  |
|-------------|--------|-------|
|account_id   |DEPOSIT |  1000 |
|account_id   |BONUS   |     5 |
```
On the this table, we also have the transaction id and the creation and last modification dates.

Please note that an error message is thrown if the account to be credited does not exist, as similar to the find end points above.

## Withdraw

When a withdraw occurs, the following call, for example, need to be executed.
```
curl --request PUT http://localhost:8080/accounts/cea123bc-0b10-4898-b117-ebd7af844a84/withdraw/100
```
The default reply for this endpoint is also NO_CONTENT(204, Series.SUCCESSFUL, "No Content").

If we check the account, we will see that its balance is now 904.00 (it was removed from the balance the value [for the withdraw] plus the 1% tax).
```
curl --request GET http://localhost:8080/accounts/id/cea123bc-0b10-4898-b117-ebd7af844a84
```
```
{
	"id": "cea123bc-0b10-4898-b117-ebd7af844a84",
	"name": "Renato",
	"taxId": "74496672073",
	"balance": 904.00
}
```
On the transaction table, we have 2 new entries for this movimentation. 
```
|Account      |Type     |Value  |
|-------------|---------|-------|
|account_id   |WITHDRAW |   100 |
|account_id   |TAX      |     1 |
```
Please note that and error message is thrown if the account to be deduced does not exist, as similar to the find end points above.

Morover, we cannot retrive the total amount of (or more than) the balance; we may deduce the 1% tax. When this occurs a Not Enough Funds Exception is thrown.

## Transfer

To transfer money between accounts, we need to perform the following call for example.
```
curl --request PUT http://localhost:8080/accounts/cea123bc-0b10-4898-b117-ebd7af844a84/transfer/95ee5aae-34ed-4bf3-8ce3-df97652a1a58/400
```
As for all the account movimentation endpoints, the default reply for this endpoint is NO_CONTENT(204, Series.SUCCESSFUL, "No Content").

If we check the account, we will see that its balance is now 504.00, and the target account will have 400.00 on its balance.
```
curl --request GET http://localhost:8080/accounts
```
```
{
	"page": 0,
	"pageSize": 20,
	"total": 2,
	"items": [{
		"id": "95ee5aae-34ed-4bf3-8ce3-df97652a1a58",
		"name": "Joao",
		"taxId": "30878308016",
		"balance":400.00
	}, {
		"id": "cea123bc-0b10-4898-b117-ebd7af844a84",
		"name": "Renato",
		"taxId": "74496672073",
		"balance": 504.00
	}]
}
```
On the transaction table, we have 2 new entries for this movimentation. 
```
|Account                 |Type             |Value  |
|------------------------|-----------------|-------|
|account_id              |TRANSFER_SEND    |   400 |
|target_account_id       |TRANSFER_RECEIVED|   400 |
```

Please note that an error message is thrown if the source or the target accounts do not exist, similar to the find endpoints above.

Morover, we cannot transfer more than we have on the balance of the source account. When this occurs a Not Enough Funds Exception is thrown.

## Transactions
To retrieve the transactions for a given account, we need to execute the following call:
```
curl --request GET "http://localhost:8080/transactions/cea123bc-0b10-4898-b117-ebd7af844a84?startDate=2020-10-31&endDate=2020-11-28"
```
The start and end dates are not required. When they are omitted, we retrieve the transactions for the 1st to the last day of the previous month.

There is no account existence verification, if an invalid or non existent account is searched, no results will be returned.

The result is something like this:
```
[{
	"transactionType": "TRANSFER_SEND",
	"amount": 400.00,
	"creationDate": "2020-11-25T20:52:26.280404"
}, {
	"transactionType": "TAX",
	"amount": 1.00,
	"creationDate": "2020-11-25T20:42:31.525255"
}, {
	"transactionType": "WITHDRAW",
	"amount": 100.00,
	"creationDate": "2020-11-25T20:42:31.520187"
}, {
	"transactionType": "BONUS",
	"amount": 5.00,
	"creationDate": "2020-11-25T20:17:25.321535"
}, {
	"transactionType": "DEPOSIT",
	"amount": 1000.00,
	"creationDate": "2020-11-25T20:17:25.244061"
}]
```
