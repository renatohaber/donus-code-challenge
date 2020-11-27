# language: en
@account
Feature: Find all the accounts stored on the database
  Users want to retrieve all the accounts

  Background:
    Given database contains accounts as:
      | id                                   | name   | taxId       | balance | creationDate        |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | Renato | 74496672073 | 504     | 2020-11-17T18:33:15 |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | Joao   | 30878308016 | 400     | 2019-11-27T18:33:15 |

  Scenario Outline: Find an account by id successfully
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user requests details about the account "<accountId>"
    Then the system will return 200 status code
    And the service will reply this account: "<response>"
    Examples:
      | accountId                            | response                                                                                                            |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | {\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 504} |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | {\"id\": \"95ee5aae-34ed-4bf3-8ce3-df97652a1a58\",\"name\": \"Joao\",\"taxId\": \"30878308016\",\"balance\": 400}   |

  Scenario Outline: Find an account by tax id successfully
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user requests details about the account by the tax id "<taxId>"
    Then the system will return 200 status code
    And the service will reply this account: "<response>"
    Examples:
      | taxId       | response                                                                                                            |
      | 74496672073 | {\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 504} |
      | 30878308016 | {\"id\": \"95ee5aae-34ed-4bf3-8ce3-df97652a1a58\",\"name\": \"Joao\",\"taxId\": \"30878308016\",\"balance\": 400}   |

  Scenario: Try to find an account by id that is not registered
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user requests details about the account "2761db3f-0c3b-4931-b3de-4f42b85fadc5"
    Then an exception is thrown

  Scenario: Try to find an account by tax id that is not registered
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user requests details about the account by the tax id "196.965.280-27"
    Then an exception is thrown

  Scenario: Try to find an account by invalid tax id
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    When this user requests details about the account by the tax id "196.888.280-27"
    Then an exception is thrown

  Scenario: Try to find with database offline
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the database is offline
    When this user requests details about the account "2761db3f-0c3b-4931-b3de-4f42b85fadc5"
    Then an exception is thrown
