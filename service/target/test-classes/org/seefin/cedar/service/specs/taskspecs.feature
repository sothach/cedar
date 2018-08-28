Feature: User can view his/her current task list

  Scenario: A logged-on user should be able to view their current tasks
    Given a User has logged-on successfully
    When 4 tasks are stored in the database
    Then the user should be able to view 4 tasks