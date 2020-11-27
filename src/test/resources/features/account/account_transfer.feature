# language: en
@account
Feature: Make a transfers to existent accounts
  Users want to make transfers

  Background:
    Given database contains accounts as:
      | id                                   | name       | taxId       | balance | creationDate        |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | Renato     | 74496672073 | 1509    | 2020-11-17T18:33:15 |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | Joao       | 30878308016 | 400     | 2019-11-27T18:33:15 |
      | 1bec7c83-d0b9-46be-aad8-90383c01ac52 | Sarah Melo | 08796938005 | 10050   | 2020-12-12T12:25:21 |

  Scenario Outline: Transfer money from accounts successfully
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request transfer from the account:
      | source | cea123bc-0b10-4898-b117-ebd7af844a84 |
      | target | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | value  | 500                                  |
    Then the system will return 204 status code
    When this user requests details about the account by the tax id "<taxId>"
    Then the system will return 200 status code
    And the service will reply this account: "<response>"
    Examples:
      | taxId       | response                                                                                                                  |
      | 74496672073 | {\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 1009}      |
      | 08796938005 | {\"id\": \"1bec7c83-d0b9-46be-aad8-90383c01ac52\",\"name\": \"Sarah Melo\",\"taxId\": \"08796938005\",\"balance\": 10550} |

  Scenario: Try to transfer money from account that does not exist
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request transfer from the account:
      | source | cea123bc-0b10-4898-b117-ebd7af844999 |
      | target | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | value  | 500                                  |
    Then the system will return 500 status code

  Scenario: Try to transfer money to account that does not exist
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request transfer from the account:
      | source | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | target | cea123bc-0b10-4898-b117-ebd7af844999 |
      | value  | 500                                  |
    Then the system will return 500 status code

  Scenario: Try to transfer money to account with no enough funds
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request transfer from the account:
      | source | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | target | cea123bc-0b10-4898-b117-ebd7af844999 |
      | value  | 51100                                |
    Then the system will return 500 status code

  Scenario: Try to transfer money with database offline
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the database is offline
    When this user request transfer from the account:
      | source | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | target | cea123bc-0b10-4898-b117-ebd7af844999 |
      | value  | 51100                                |
    Then the system will return 500 status code



