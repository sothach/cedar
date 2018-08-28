package org.seefin.cedar.persist;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.persist.mapper.TaskMapper;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.service.TaskService;
import org.seefin.cedar.service.internal.PersistTaskService;

import java.util.Optional;

/**
 * Test the failure path in the Task service logic, ensuring database connection
 * errors are correctly handled and not propagated out of the service interface
 *
 * @author phillipsr
 */
public class TestTaskDBErrors {
    private static final TaskId TASK_ID = new TaskId("5ab2b0f3-c5d5-4bdb-8c05-207ba9291e98");
    private static final Individual TEST_OWNER = new Individual("test", "password-hash");
    private static final String TEST_DESCRIPTION = "This is the description";

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private PartyService partyService;
    private TaskService taskService;

    @Before
    public void
    creatServices() {
        taskService = new PersistTaskService(taskMapper, partyService);
    }

    @Test
    public void
    testFindPartyByIdFailure() {
        context.checking(new Expectations() {{
            oneOf(taskMapper).find(TASK_ID.toString());
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        Optional<Task> response = taskService.findTaskById(TASK_ID);
        Assert.assertFalse(response.isPresent());

        context.assertIsSatisfied();
    }

    @Test
    public void
    testSaveTaskFailure() {
        context.checking(new Expectations() {{
            oneOf(taskMapper).insert(with(any(Task.class)));
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        taskService.saveTask(new Task(TEST_OWNER, TEST_DESCRIPTION));

        context.assertIsSatisfied();
    }

    @Test
    public void
    testUpdateTaskFailure() {
        context.checking(new Expectations() {{
            oneOf(taskMapper).update(with(any(Task.class)));
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        taskService.updateTask(new Task(TEST_OWNER, TEST_DESCRIPTION));

        context.assertIsSatisfied();
    }

    @Test
    public void
    testDeleteTaskFailure() {
        context.checking(new Expectations() {{
            oneOf(taskMapper).delete(with(any(String.class)));
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        taskService.deleteTask(TASK_ID);

        context.assertIsSatisfied();
    }

    @Test
    public void
    testGetTaskCountForUserFailureInGetOwner() {
        context.checking(new Expectations() {{
            oneOf(partyService).findPartyById(with(any(PartyId.class)));
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        taskService.getTaskCountForUser(TEST_OWNER.getId());

        context.assertIsSatisfied();
    }

    @Test
    public void
    testGetTaskCountForUserFailure() {
        context.checking(new Expectations() {{
            oneOf(partyService).findPartyById(with(any(PartyId.class)));
            will(returnValue(Optional.of(TEST_OWNER)));
            oneOf(taskMapper).getTaskCount(with(any(String.class)));
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        taskService.getTaskCountForUser(TEST_OWNER.getId());

        context.assertIsSatisfied();
    }

}
