package org.seefin.cedar.service.internal;

import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.model.tasks.Task.TaskState;
import org.seefin.cedar.persist.mapper.TaskMapper;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the task services, backed by a persistence storage mechanism
 *
 * @author phillipsr
 */
@Service("taskService")
@Transactional
public class PersistTaskService
        implements TaskService {
    private static final Logger log = LoggerFactory.getLogger(PersistTaskService.class);

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private PartyService partyService;

    /**
     * Constructor allowing mappers and services required to be passed in,
     * useful for testing against mocks, etc.
     *
     * @param taskMapper   mapper access to persistent storage
     * @param partyService access to party information
     */
    public PersistTaskService(TaskMapper taskMapper, PartyService partyService) {
        this.taskMapper = taskMapper;
        this.partyService = partyService;
    }

    /**
     * default constructor (for, e.g., Spring)
     */
    public PersistTaskService() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Task> findTaskById(TaskId id) {
        log.debug("find(taskId={})", id);
        Map<String, Object> values = null;
        try {
            values = taskMapper.find(id.toString());
        } catch (RuntimeException e) {
            log.warn("Error accessing database", e);
            return Optional.empty();
        }
        if (values == null) {
            log.debug("query returned no results");
            return Optional.empty();
        }
        log.debug("query results={}", values);
        PartyId ownerId = new PartyId((String) values.get("OWNER_ID"));
        Optional<Individual> owner = partyService.findPartyById(ownerId);
        if (!owner.isPresent()) {
            log.warn("Owner missing for task, ownerId={} taskId={}", values.get("OWNER_ID"), id);
            return Optional.empty();
        }
        return Optional.of(getTaskFromMap(owner.get(), values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> findTasksForUser(PartyId id) {
        Optional<Individual> party = getOwnerById(id);
        if (!party.isPresent()) {
            log.warn("Task owner not found: ownerId={}", id);
            return new ArrayList<>();
        }
        List<Map<String, Object>> userTasks = taskMapper.findAllTasksForParty(id.getExternalForm());
        if (userTasks.size() == 0) {
            log.debug("No tasks for ownerId={}", id);
            return new ArrayList<>();
        }
        List<Task> result = new ArrayList<>(userTasks.size());
        for (Map<String, Object> valueMap : userTasks) {
            result.add(getTaskFromMap(party.get(), valueMap));
        }
        return result;
    }

    /**
     * helper to instantiate a Task from the mapped properties
     *
     * @param owner of the task
     * @param map  properties to use in creating result
     * @return new task, initialized from properties supplied
     */
    private Task getTaskFromMap(Individual owner, Map<String, Object> map) {
        return new Task(new TaskId((String) map.get("ID")),
                TaskState.valueOf((String) map.get("STATE")),
                owner, (String) map.get("DESCRIPTION"),
                LocalDateTime.ofEpochSecond((Long) map.get("CREATE_TIME"), 0, ZoneOffset.UTC));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveTask(Task task) {
        try {
            taskMapper.insert(task);
        } catch (RuntimeException e) {
            log.warn("Error inserting into database", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTask(Task task) {
        try {
            taskMapper.update(task);
        } catch (RuntimeException e) {
            log.warn("Error updating database", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTask(TaskId taskId) {
        try {
            taskMapper.delete(taskId.getExternalForm());
        } catch (RuntimeException e) {
            log.warn("Error deleting from database", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTaskCountForUser(PartyId id) {
        Optional<Individual> party = getOwnerById(id);
        if (!party.isPresent()) {
            return 0;
        }
        Individual owner = party.get();
        try {
            return taskMapper.getTaskCount(owner.getId().toString());
        } catch (RuntimeException e) {
            log.warn("Error accessing database", e);
            return 0;
        }
    }

    private Optional<Individual> getOwnerById(PartyId id) {
        Optional<Individual> owner;
        try {
            owner = partyService.findPartyById(id);
        } catch (RuntimeException e) {
            log.warn("Error accessing database", e);
            return Optional.empty();
        }
        if (!owner.isPresent()) {
            log.warn("Task owner not found: ownerId={}", id);
            return Optional.empty();
        }
        return owner;
    }
}
