package org.seefin.cedar.service;

import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.tasks.Task;

import java.util.List;
import java.util.Optional;

/**
 * Definition of the service interface for working with tasks;
 * note these services are a thin layer on top of the persistence
 * mapping, with little business logic - as and when stronger business
 * logic requirements are identified, these classes should be split
 * from the persistence implementation
 *
 * @author phillipsr
 */
public interface TaskService {
    /**
     * @param id (synthetic key) of requested task
     * @return the task with the id specified
     */
    Optional<Task> findTaskById(TaskId id);

    /**
     * @param userId (synthetic key) of user
     * @return the tasks with the user id specified
     */
    List<Task> findTasksForUser(PartyId userId);

    /**
     * Save the supplied task object
     *
     * @param task to be saved
     */
    void saveTask(Task task);

    /**
     * Update the supplied task object
     *
     * @param task to be updated
     */
    void updateTask(Task task);

    /**
     * Delete the supplied task object
     *
     * @param taskId to be deleted
     */
    void deleteTask(TaskId taskId);

    /**
     * @param userId
     * @return the number of tasks owned by the specified user
     */
    int getTaskCountForUser(PartyId userId);
}
