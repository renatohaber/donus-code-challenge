# language: en
@account
Feature: Find accounts by id and tax id
  Users want to retrieve accounts by id and tax id

  Background:
    Given database contains accounts as:
      | id                                   | name   | taxId       | balance | creationDate        |
      | cea123bc-0b10-4898-b117-ebd7af844a84 | Renato | 74496672073 | 504     | 2020-11-17T18:33:15 |
      | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | Joao   | 30878308016 | 400     | 2019-11-27T18:33:15 |

  Scenario Outline: Retrieve all accounts successfully
    Given a user as follows:
      | logon | <logon> |
      | email | <email> |
    When this user requests details for all the accounts:
      | page | <page>     |
      | size | <pageSize> |
    Then the system will return 200 status code
    And the service will reply this list of accounts: "<response>"
    Examples:
      | logon   | email                 | page | pageSize | response                                                                                                                                                                                                                                                                                          |
      | XYZ1234 | email@siannodel.com   | 1    | 20       | {\"page\": 1, \"pageSize\": 20, \"total\": 22, \"items\":[{\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 504},{\"id\": \"95ee5aae-34ed-4bf3-8ce3-df97652a1a58\",\"name\": \"Joao\",\"taxId\": \"30878308016\",\"balance\": 400}]} |
      | ABC1234 | email@joao.com        | 2    | 30       | {\"page\": 2, \"pageSize\": 30, \"total\": 62, \"items\":[{\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 504},{\"id\": \"95ee5aae-34ed-4bf3-8ce3-df97652a1a58\",\"name\": \"Joao\",\"taxId\": \"30878308016\",\"balance\": 400}]} |
      | ABC1234 | email@joao.com        |      |          | {\"page\": 0, \"pageSize\": 20, \"total\": 2, \"items\": [{\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 504},{\"id\": \"95ee5aae-34ed-4bf3-8ce3-df97652a1a58\",\"name\": \"Joao\",\"taxId\": \"30878308016\",\"balance\": 400}]} |
      | KKKLLA1 | email@strongheart.com | 0    | 10       | {\"page\": 0, \"pageSize\": 10, \"total\": 2, \"items\": [{\"id\": \"cea123bc-0b10-4898-b117-ebd7af844a84\",\"name\": \"Renato\",\"taxId\": \"74496672073\",\"balance\": 504},{\"id\": \"95ee5aae-34ed-4bf3-8ce3-df97652a1a58\",\"name\": \"Joao\",\"taxId\": \"30878308016\",\"balance\": 400}]} |

  Scenario: Try to retrieve all accounts with database offline
    Given a user as follows:
      | logon | ABC1234             |
      | email | email@siannodel.com |
    And the database is offline
    When this user requests details for all the accounts:
      | page | 1  |
      | size | 20 |
    Then the system will return 500 status code
