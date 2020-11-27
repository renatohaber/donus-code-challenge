# language: en
@account
Feature: Make a deposit on a existent account
  Users want to make deposits

  Background:
    Given database contains accounts as:
      | id                                   | name       | taxId       | balance | creationDate        |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | Renato     | 74496672073 | 504     | 2020-11-17T18:33:15 |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | Joao       | 30878308016 | 400     | 2019-11-27T18:33:15 |
      | 1bec7c83-d0b9-46be-aad8-90383c01ac52 | Sarah Melo | 08796938005 | 0       | 2020-12-12T12:25:21 |

  Scenario Outline: Deposit money in an account successfully
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request to deposits to the account:
      | accountId | <accountId> |
      | value     | <value>     |
    Then the system will return 204 status code
    When this user requests details about the account by the tax id "<taxId>"
    Then the system will return 200 status code
    And the service will reply this account: "<response>"
    Examples:
      | accountId                            | value | taxId       | response                                                                                                                  |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | 1000  | 74496672073 | {\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 1509.000}      |
      | 1bec7c83-d0b9-46be-aad8-90383c01ac52 | 10000 | 08796938005 | {\"id\": \"1bec7c83-d0b9-46be-aad8-90383c01ac52\",\"name\": \"Sarah Melo\",\"taxId\": \"08796938005\",\"balance\": 10050.000} |

  Scenario: Try to make a deposit in an account by id that is not registered
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user request to deposits to the account:
      | accountId | 2761db3f-0c3b-4931-b3de-4f42b85fadc5 |
      | value     | 1000000                              |
    Then an exception is thrown

  Scenario: Try to make a deposit with database offline
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the database is offline
    When this user request to deposits to the account:
      | accountId | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 |
      | value     | 1000000                              |
    Then an exception is thrown


