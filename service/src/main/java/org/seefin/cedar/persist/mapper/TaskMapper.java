package org.seefin.cedar.persist.mapper;

import org.seefin.cedar.model.tasks.Task;

import java.util.List;
import java.util.Map;

public interface TaskMapper {
    Map<String, Object> find(final String taskId);

    void insert(Task task);

    List<Map<String, Object>> findAllTasksForParty(final String partyId);

    void update(final Task task);

    void delete(final String taskId);

    int getTaskCount(final String taskId);
}