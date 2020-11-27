# language: en
@account
Feature: Make a withdraw on a existent account
  Users want to make withdraws

  Background:
    Given database contains accounts as:
      | id                                   | name       | taxId       | balance | creationDate        |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | Renato     | 74496672073 | 1509    | 2020-11-17T18:33:15 |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | Joao       | 30878308016 | 400     | 2019-11-27T18:33:15 |
      | 1bec7c83-d0b9-46be-aad8-90383c01ac52 | Sarah Melo | 08796938005 | 10050   | 2020-12-12T12:25:21 |

  Scenario Outline: Withdraw money from an account successfully
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request a withdraw from the account:
      | accountId | <accountId> |
      | value     | <value>     |
    Then the system will return 204 status code
    When this user requests details about the account by the tax id "<taxId>"
    Then the system will return 200 status code
    And the service will reply this account: "<response>"
    Examples:
      | accountId                            | value | taxId       | response                                                                                                                    |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | 500   | 74496672073 | {\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 1004.00}     |
      | 1bec7c83-d0b9-46be-aad8-90383c01ac52 | 1000  | 08796938005 | {\"id\": \"1bec7c83-d0b9-46be-aad8-90383c01ac52\",\"name\": \"Sarah Melo\",\"taxId\": \"08796938005\",\"balance\": 9040.00} |

  Scenario: Try to make a withdraw in an account by id that is not registered
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request a withdraw from the account:
      | accountId | 2761db3f-0c3b-4931-b3de-4f42b85fadc5 |
      | value     | 1000000                              |
    Then an exception is thrown

  Scenario Outline: Try to make a withdraw in an account that does not have enough funds
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request a withdraw from the account:
      | accountId | cea123bc-0b10-4898-b117-ebd7af844a84 |
      | value     | <value>                              |
    Then an exception is thrown
    Examples:
      | value |
      # not enough for the tax
      | 1505  |
      # not enough amount
      | 15000 |

  Scenario: Try to make a withdraw with database offline
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the database is offline
    When this user request a withdraw from the account:
      | accountId | cea123bc-0b10-4898-b117-ebd7af844a84 |
      | value     | 1000000                              |
    Then an exception is thrown
