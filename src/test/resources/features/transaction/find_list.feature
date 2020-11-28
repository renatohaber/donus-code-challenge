# language: en
@account
Feature: Retrieve all transactions stored on the database
  Users want to retrieve all the transactions stored on the database

  Background:
    Given database contains transactions as:
      | id                                   | accountId                            | amount | creationDate        | transactionType   |
      | e1a677d3-1e3a-4e69-975b-380163297929 | cea123bc-0b10-4898-b117-ebd7af844a84 | 1000   | 2020-10-17T18:17:25 | DEPOSIT           |
      | baabe6c2-f1a9-429e-9052-6a7ea372ac6b | cea123bc-0b10-4898-b117-ebd7af844a84 | 5      | 2020-10-17T18:17:25 | BONUS             |
      | 68fefb9b-b8b9-40c5-ba53-2fbca80b6262 | cea123bc-0b10-4898-b117-ebd7af844a84 | 100    | 2020-10-17T18:42:31 | WITHDRAW          |
      | 42318ba9-9c17-4a7b-93ac-f7cf7e5d6a34 | cea123bc-0b10-4898-b117-ebd7af844a84 | 1      | 2020-10-17T18:42:31 | TAX               |
      | c8543178-67ad-42f5-971e-26f72397a0c4 | cea123bc-0b10-4898-b117-ebd7af844a84 | 400    | 2020-11-17T18:52:26 | TRANSFER_SEND     |
      | d8c76f3f-f6ec-498f-a321-2ff17e10b386 | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | 400    | 2020-11-17T18:52:26 | TRANSFER_RECEIVED |

  Scenario Outline: Retrieve all accounts successfully
    Given a user as follows:
      | logon | <logon> |
      | email | <email> |
    And the next transaction search data is:
      | now | 28/11/2020 - 10:00:00 UTC |
    When this user requests the transactions for the account "<accountId>":
      | startDate | <startDate> |
      | endDate   | <endDate>   |
    Then the system will return 200 status code
    And the service will reply this list of transactions: "<response>"
    Examples:
      | logon   | email                 | accountId                            | startDate  | endDate    | response                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | XYZ1234 | email@siannodel.com   | cea123bc-0b10-4898-b117-ebd7af844a84 | 2020-10-01 | 2020-11-30 | [{\"transactionType\":\"DEPOSIT\",\"amount\":1000,\"creationDate\":\"2020-10-17T18:17:25\"},{\"transactionType\":\"BONUS\",\"amount\":5,\"creationDate\":\"2020-10-17T18:17:25\"},{\"transactionType\":\"WITHDRAW\",\"amount\":100,\"creationDate\":\"2020-10-17T18:42:31\"},{\"transactionType\":\"TAX\",\"amount\":1,\"creationDate\":\"2020-10-17T18:42:31\"},{\"transactionType\":\"TRANSFER_SEND\",\"amount\":400,\"creationDate\":\"2020-11-17T18:52:26\"}] |
      | ABC1234 | email@joao.com        | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | 2020-11-01 | 2020-11-20 | [{\"transactionType\":\"TRANSFER_RECEIVED\",\"amount\":400,\"creationDate\":\"2020-11-17T18:52:26\"}]                                                                                                                                                                                                                                                                                                                                                             |
      | KKKLLA1 | email@strongheart.com | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 |            |            | [{\"transactionType\":\"TRANSFER_RECEIVED\",\"amount\":400,\"creationDate\":\"2020-11-17T18:52:26\"}]                                                                                                                                                                                                                                                                                                                                                             |
      | KKKLLA1 | email@strongheart.com | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | 2020-10-01 | 2020-10-10 | [{\"transactionType\":\"TRANSFER_RECEIVED\",\"amount\":400,\"creationDate\":\"2020-11-17T18:52:26\"}]                                                                                                                                                                                                                                                                                                                                                             |
      | KKKLLA1 | email@strongheart.com | f70bb99d-d1d3-4169-be14-dfa5781b7688 | 2020-10-01 | 2020-10-10 | []                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | KKKLLA1 | email@strongheart.com | f70bb99d-d1d3-4169-be14-dfa5781b7688 |            |            | []                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | KKKLLA1 | email@strongheart.com | 9985fbd7-012f-4306-9fe7-2257783a15bb | 2020-01-01 | 2020-12-31 | []                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | KKKLLA1 | email@strongheart.com | 9985fbd7-012f-4306-9fe7-2257783a15bb |            |            | []                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
