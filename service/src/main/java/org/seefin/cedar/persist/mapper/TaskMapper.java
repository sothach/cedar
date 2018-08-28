package org.seefin.cedar.persist.mapper;

import org.seefin.cedar.model.tasks.Task;

import java.util.List;
import java.util.Map;

public interface TaskMapper {
    Map<String, Object> find(String taskId);

    void insert(Task task);

    List<Map<String, Object>> findAllTasksForParty(String partyId);

    void update(Task task);

    void delete(String taskId);

    int getTaskCount(String taskId);
}