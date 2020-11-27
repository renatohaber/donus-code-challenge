# language: en
@account
Feature: Retrieve all transactions stored on the database
  Users want to retrieve all the transactions stored on the database

  Background:
    Given database contains transactions as:
      | id                                   | accountId                            | value | creationDate          | type              |
      | e1a677d3-1e3a-4e69-975b-380163297929 | cea123bc-0b10-4898-b117-ebd7af844a84 | 1000  | 2020-11-17T8:17:25    | DEPOSIT           |
      | baabe6c2-f1a9-429e-9052-6a7ea372ac6b | cea123bc-0b10-4898-b117-ebd7af844a84 | 5     | 2020-11-17T8:17:25    | BONUS             |
      | 68fefb9b-b8b9-40c5-ba53-2fbca80b6262 | cea123bc-0b10-4898-b117-ebd7af844a84 | 100   | 2020-11-17T8:42:31    | WITHDRAW          |
      | 42318ba9-9c17-4a7b-93ac-f7cf7e5d6a34 | cea123bc-0b10-4898-b117-ebd7af844a84 | 1     | Nov 25, 2020, 8:42:31 | TAX               |
      | c8543178-67ad-42f5-971e-26f72397a0c4 | cea123bc-0b10-4898-b117-ebd7af844a84 | 400   | 2020-11-17T8:52:26    | TRANSFER_SEND     |
      | d8c76f3f-f6ec-498f-a321-2ff17e10b386 | 95ee5aae-34ed-4bf3-8ce3-df97652a1a58 | 400   | 2020-11-17T8:52:26    | TRANSFER_RECEIVED |