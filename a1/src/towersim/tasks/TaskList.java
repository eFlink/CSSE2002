package towersim.tasks;

import java.util.List;

/** Represents a circular list of tasks for an aircraft to cycle through. */
public class TaskList {

    /** list of tasks */
    private List<Task> tasks;

    /** current task in the list index */
    private int currentTask;

    /** amount of tasks */
    private int taskLength;

    /**
     * Creates a new TaskList with the given list of tasks.
     * Initially, the current task (as returned by getCurrentTask()) should be the
     * first task in the given list.
     *
     * @param tasks list of tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
        currentTask = 0;
        taskLength = tasks.size();
    }

    /**
     * Returns the current task in the list.
     *
     * @return current task
     */
    public Task getCurrentTask() {
        return tasks.get(currentTask);
    }

    /**
     * Returns the task in the list that comes after the current task.
     * After calling this method, the current task should still be the same as it was before
     * calling the method.
     *
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * this method should return the first element of the list.
     *
     * @return next task
     */
    public Task getNextTask() {
        if (currentTask >= taskLength - 1) {
            return tasks.get(0);
        }
        return tasks.get(currentTask + 1);
    }

    /**
     * Moves the reference to the current task forward by one in the circular task list.
     * After calling this method, the current task should be the next task in the circular
     * list after the "old" current task.
     *
     * Note that the list is treated as circular, so if the current task is the last in
     * the list, the new current task should be the first element of the list.
     */
    public void moveToNextTask() {
        currentTask++;
        if (currentTask >= taskLength) {
            currentTask = 0;
        }
    }

    /**
     * Returns the human-readable string representation of this task list.
     * The format of the string to return is
     *
     * TaskList currently on currentTask [taskNum/totalNumTasks]
     * where currentTask is the toString() representation of the current task
     * as returned by Task.toString(), taskNum is the place the current task occurs
     * in the task list, and totalNumTasks is the number of tasks in the task list.
     *
     * For example, a task list with the list of tasks [AWAY, LAND, WAIT, LOAD, TAKEOFF]
     * which is currently on the WAIT task would have a string representation of "TaskList
     * currently on WAIT [3/5]".
     *
     * @return string representation of this task list
     */
    @Override
    public String toString() {
        int taskNum = currentTask + 1;
        String result = "TaskList currently on ";
        result += getCurrentTask().getType();
        result += " [" + taskNum + "/" + taskLength + "]";
        return result;
    }


}
