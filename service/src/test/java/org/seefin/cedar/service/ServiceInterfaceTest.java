package org.seefin.cedar.service;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.model.tasks.Task.TaskState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Discover test to define shape of service interface
 *
 * @author phillipsr
 */
public class ServiceInterfaceTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    @Mock
    private PartyService partyService;
    @Mock
    private TaskService taskService;

    private static final Individual TEST_OWNER = new Individual("test", "password-hash");
    List<Task> TEST_TASKS = new ArrayList<Task>() {{
        add(new Task(new TaskId(), TaskState.CHECKED, TEST_OWNER, "task 1", LocalDateTime.now()));
        add(new Task(new TaskId(), TaskState.CHECKED, TEST_OWNER, "task 2", LocalDateTime.now()));
        add(new Task(new TaskId(), TaskState.CHECKED, TEST_OWNER, "task 3", LocalDateTime.now()));
        add(new Task(new TaskId(), TaskState.CHECKED, TEST_OWNER, "task 4", LocalDateTime.now()));
    }};

    private Individual user;

    @Test
    public void test() {
        context.checking(new Expectations() {{
            oneOf(partyService).logon("test", "abc123");
            will(returnValue(Optional.of(TEST_OWNER)));
            oneOf(taskService).getTaskCountForUser(TEST_OWNER.getId());
            will(returnValue(TEST_TASKS.size()));
            oneOf(taskService).findTasksForUser(TEST_OWNER.getId());
            will(returnValue(TEST_TASKS));
        }});

        Optional<Individual> logonAttempt = partyService.logon("test", "abc123");
        Assert.assertNotNull("response returned", logonAttempt);
        Assert.assertTrue("The user was logged on", logonAttempt.isPresent());
        user = logonAttempt.get();

        int userTasks = taskService.getTaskCountForUser(user.getId());
        Assert.assertEquals("Correct number of tasks found", TEST_TASKS.size(), userTasks);

        List<Task> tasks = taskService.findTasksForUser(user.getId());
        Assert.assertEquals("Correct number of tasks returned", TEST_TASKS.size(), tasks.size());
        for (Task task : tasks) {
            Assert.assertEquals("Tasks belong to user", user, task.getOwner());
        }

        context.assertIsSatisfied();
    }


}
