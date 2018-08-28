package org.seefin.cedar.service.specs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Scenario: A logged-on user should be able to view their current tasks
 * Given a User has logged-on successfully
 * When 4 tasks are stored in the database
 * Then the user should be able to view 4 tasks
 *
 * @author phillipsr
 */

public class ViewTasks {
    @Autowired
    private PartyService partyService;
    @Autowired
    private TaskService taskService;

    private Individual user;

    @Given("^a User has logged-on successfully$")
    public void a_User_has_logged_on_successfully() {
        Optional<Individual> logonAttempt = partyService.logon("test", "abc123");
        Assert.assertTrue("The user was logged on", logonAttempt.isPresent());
        user = logonAttempt.get();
    }

    @When("^(\\d+) tasks are stored in the database$")
    public void tasks_are_stored_in_the_database(int expectedTasks) {
        int userTasks = taskService.getTaskCountForUser(user.getId());
        Assert.assertEquals("Correct number of tasks found", expectedTasks, userTasks);
    }

    @Then("^the user should be able to view (\\d+) tasks$")
    public void the_user_should_be_able_to_view_(int expectedTasks) {
        List<Task> tasks = taskService.findTasksForUser(user.getId());
        Assert.assertEquals("Correct number of tasks returned", expectedTasks, tasks.size());
        for (Task task : tasks) {
            Assert.assertEquals("Tasks belong to user", user, task.getOwner());
        }
    }

}
