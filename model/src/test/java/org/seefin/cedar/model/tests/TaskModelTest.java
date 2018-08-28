package org.seefin.cedar.model.tests;

import org.junit.Assert;
import org.junit.Test;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.model.tasks.Task.TaskState;

import java.util.UUID;

/**
 * Unit tests for driving domain model development
 * <p>
 * Each �task� contains a text description and date when the entry was created.
 * Both fields should be displayed in the UI.
 *
 * @author phillipsr
 */
public class TaskModelTest {
    private static final Individual TEST_OWNER = new Individual("test", "password-hash");
    private static final String TEST_DESCRIPTION = "This is the description";

    @Test
    public void
    createANewTask() {
        Task task = new Task(TEST_OWNER, TEST_DESCRIPTION);

        Assert.assertEquals(TEST_OWNER, task.getOwner());
        Assert.assertEquals(TEST_DESCRIPTION, task.getDescription());
        Assert.assertNotNull(task.getTaskId());
        Assert.assertNotNull(task.getCreateTime());
        Assert.assertTrue(task.toString().contains("state=UNCHECKED"));
        Assert.assertTrue(task.toString().contains("description=" + TEST_DESCRIPTION));
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testTaskRecreationNullId() {
        try {
            new Task(null, null, null, null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("task id cannot be null", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testTaskRecreationNullState() {
        try {
            new Task(new TaskId(UUID.randomUUID().toString()), null, null, null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("task state cannot be null", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testTaskRecreationNullOwner() {
        try {
            new Task(new TaskId(UUID.randomUUID().toString()), TaskState.CHECKED, null, null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("task owner cannot be null", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testTaskRecreationNullDescription() {
        try {
            new Task(new TaskId(UUID.randomUUID().toString()), TaskState.CHECKED, TEST_OWNER, null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("description cannot be null", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testTaskRecreationNullCreateDate() {
        try {
            new Task(new TaskId(UUID.randomUUID().toString()), TaskState.CHECKED, TEST_OWNER, "xxx", null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("created time cannot be null", e.getMessage());
            throw e;
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void
    ensureNewTaskWithoutOwnerFails() {
        new Task(null, "desc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    ensureNewTaskWithoutDescriptionFails() {
        new Task(TEST_OWNER, null);
    }

    @Test
    public void
    testTaskStates() {
        Task task = new Task(TEST_OWNER, TEST_DESCRIPTION);
        Assert.assertEquals(TEST_OWNER.getId(), task.getOwnerId());
        Assert.assertEquals(TaskState.UNCHECKED, task.getState());

        task = task.check();
        Assert.assertEquals(TaskState.CHECKED, task.getState());

        task = task.uncheck();
        Assert.assertEquals(TaskState.UNCHECKED, task.getState());

        task = task.delete();
        Assert.assertEquals(TaskState.DELETED, task.getState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testCheckDeletedTaskFails() {
        Task task = new Task(TEST_OWNER, TEST_DESCRIPTION);
        Assert.assertEquals(TaskState.UNCHECKED, task.getState());
        task = task.delete();
        Assert.assertEquals(TaskState.DELETED, task.getState());

        try {
            task.check();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot check a deleted task", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testUncheckDeletedTaskFails() {
        Task task = new Task(TEST_OWNER, TEST_DESCRIPTION);
        Assert.assertEquals(TaskState.UNCHECKED, task.getState());
        task = task.delete();
        Assert.assertEquals(TaskState.DELETED, task.getState());

        try {
            task.uncheck();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot check a deleted task", e.getMessage());
            throw e;
        }
    }

    @Test
    public void
    testTaskStateValues() {
        for (TaskState state : TaskState.values()) {
            TaskState newState = TaskState.valueOf(state.toString());
            Assert.assertEquals(state, newState);
        }
    }

}
