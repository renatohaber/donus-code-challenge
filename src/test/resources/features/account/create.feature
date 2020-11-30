# language: en
@account
Feature: Create a new account
  Users want to create a new account

  Background:
    Given database contains accounts as:
      | id                                   | name   | taxId       | balance | creationDate        |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | Renato | 74496672073 | 504     | 2020-11-17T18:33:15 |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | Joao   | 30878308016 | 400     | 2019-11-27T18:33:15 |

  Scenario: Create a new account successfully
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the next account insertion data is:
      | nextId | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | now    | 12/12/2020 - 12:25:21 UTC            |
    When this user request to insert the account:
      | name  | Sarah Melo     |
      | taxId | 087.969.380-05 |
    Then the system will return 200 status code
    And the service will reply this account: "{\"id\": \"1bec7c83-d0b9-46be-aad8-90383c01ac52\",\"name\": \"Sarah Melo\",\"taxId\": \"08796938005\",\"balance\": 0}"

  Scenario: Try to create a new account for a tax id that is already registered
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the next account insertion data is:
      | nextId | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | now    | 12/12/2020 - 12:25:21 UTC            |
    When this user request to insert the account:
      | name  | Sarah Melo  |
      | taxId | 30878308016 |
    Then an exception is thrown

  Scenario: Try to create a new account for a invalid tax id
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the next account insertion data is:
      | nextId | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      | now    | 12/12/2020 - 12:25:21 UTC            |
    When this user request to insert the account:
      | name  | Sarah Melo  |
      | taxId | 12345678901 |
    Then an exception is thrown

  Scenario Outline: Trye to create a new account with bad request
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the next account insertion data is:
      | nextId | <accountId>               |
      | now    | 12/12/2020 - 12:25:21 UTC |
    When this user request to insert the account:
      | name  | <name>  |
      | taxId | <taxId> |
    Then no exception is thrown
    # 404 >> missing a exception handler
    When this user requests details about the account "<accountId>"
    # the account does not exist, was not created
    Then an exception is thrown
    Examples:
      | name       | taxId          | accountId                            |
      | Sarah Melo |                | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      |            | 087.969.380-05 | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |
      |            |                | 1bec7c83-d0b9-46be-aad8-90383c01ac52 |

