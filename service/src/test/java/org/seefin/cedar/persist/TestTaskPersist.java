package org.seefin.cedar.persist;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.model.tasks.Task.TaskState;
import org.seefin.cedar.service.TaskService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/seefin/cedar/persist/PersistTest-context.xml"})
@DirtiesContext
public class TestTaskPersist {
    private static final TaskId TASK_ID = new TaskId("cb9e0df0-072e-11e3-8ffd-0800200c9a66"); // ID from pre-populated test data
    private static final TaskId UNKNOWN_TASK_ID = new TaskId("aa9a9aa9-072e-11e3-8ffd-0800200c9a66");
    private static final TaskId TASK_MISSING_OWNER = new TaskId("cb9e0df0-072e-11e3-8ffd-0800200c9a77");
    private static final PartyId EXPECTED_OWNER = new PartyId("f70f76d0-072f-11e3-8ffd-0800200c9a66");
    private static final PartyId TASKLESS_USER = new PartyId("636fda50-081f-11e3-8ffd-0800200c9a66");
    private static final int EXPECTED_TASK_COUNT = 3;

    @Resource
    private TaskService taskService;

    @Test
    public void
    testFind() {
        Optional<Task> response = taskService.findTaskById(TASK_ID);
        Assert.assertTrue("Task returned", response.isPresent());
        Task task = response.get();
        Assert.assertEquals(EXPECTED_OWNER, task.getOwnerId());
    }

    @Test
    public void
    testFindAllForOwner() {
        List<Task> tasks = taskService.findTasksForUser(EXPECTED_OWNER);
        Assert.assertEquals(EXPECTED_TASK_COUNT, tasks.size());
    }

    @Test
    public void
    testTaskNotFound() {
        Optional<Task> response = taskService.findTaskById(UNKNOWN_TASK_ID);
        Assert.assertFalse("No Task returned", response.isPresent());
    }

    @Test
    public void
    testTaskMissingOwner() {
        Optional<Task> response = taskService.findTaskById(TASK_MISSING_OWNER);
        Assert.assertFalse("No Task returned", response.isPresent());
    }

    @Test
    public void
    testTaskCreation() {
        Individual owner = new Individual(EXPECTED_OWNER, "test", "bogus-password-hash", Locale.getDefault());
        Task task = new Task(owner, "New Task");
        taskService.saveTask(task);

        Optional<Task> response = taskService.findTaskById(task.getTaskId());
        Assert.assertTrue("Task returned", response.isPresent());
        Task newTask = response.get();
        Assert.assertEquals(EXPECTED_OWNER, newTask.getOwnerId());

        taskService.deleteTask(task.getTaskId());
    }

    @Test
    public void
    testStateChange() {
        Optional<Task> response = taskService.findTaskById(TASK_ID);
        Assert.assertTrue("Task returned", response.isPresent());
        Task task = response.get();
        Assert.assertEquals(TaskState.UNCHECKED, task.getState());

        taskService.updateTask(task.check());

        response = taskService.findTaskById(TASK_ID);
        Assert.assertTrue("Task returned", response.isPresent());
        Task savedTask = response.get();
        Assert.assertEquals(TaskState.CHECKED, savedTask.getState());
    }

    @Test
    public void
    testFindTasksForUser() {
        List<Task> userTasks = taskService.findTasksForUser(EXPECTED_OWNER);
        Assert.assertEquals(EXPECTED_TASK_COUNT, userTasks.size());
        for (Task task : userTasks) {
            Assert.assertEquals(EXPECTED_OWNER, task.getOwnerId());
            Assert.assertNotSame(TaskState.DELETED, task.getState());
        }
    }

    @Test
    public void
    testTaskCountForUser() {
        int userTasksCount = taskService.getTaskCountForUser(EXPECTED_OWNER);
        Assert.assertEquals(EXPECTED_TASK_COUNT, userTasksCount);
    }

    @Test
    public void
    testUserhasNoTasks() {
        List<Task> userTasks = taskService.findTasksForUser(TASKLESS_USER);
        Assert.assertEquals(0, userTasks.size());
    }

    @Test
    public void
    testFindTasksForUnknownUser() {
        List<Task> userTasks = taskService.findTasksForUser(
                new PartyId(UUID.randomUUID().toString()));
        Assert.assertEquals(0, userTasks.size());
    }

}
