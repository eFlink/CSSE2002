package towersim.tasks;

import static towersim.tasks.TaskType.LOAD;

/**
 * Represents a task currently assigned to an aircraft.
 *
 * Tasks relate to an aircraft's movement and ground operations.
 */
public class Task {

    /** type of task */
    private TaskType task;

    /** percentage of maximum capacity to load */
    private int loadPercent;

    /**
     * Creates a new Task of the given task type.
     * @param type type of task
     */
    public Task(TaskType type) {
        task = type;
        loadPercent = 0;
    }

    /**
     * Creates a new Task of the given task type and stores the given load percentage in the task.
     * This constructor is used for tasks of the LOAD type, so that a percentage may be
     * specified for the load operation.
     *
     * @param type type of task
     * @param loadPercent percentage of maximum capacity to load
     */
    public Task(TaskType type, int loadPercent) {
        task = type;
        this.loadPercent = loadPercent;
    }

    /**
     * Returns the type of this task.
     *
     * @return task type
     */
    public TaskType getType() {
        return task;
    }

    /**
     * Returns the load percentage specified when constructing the task, or 0 if none was specified.
     *
     * @return task load percentage
     */
    public int getLoadPercent() {
        return loadPercent;
    }

    /**
     * Returns the human-readable string representation of this task.
     * The format of the string to return is:
     *
     * "LOAD at percent%" where percent is the load percentage, without the
     * enclosing double quotes, if the task type is LOAD.
     * "taskType" where taskType is the string representation of the task type,
     * without the enclosing double quotes, for any other task type.
     *
     * @return string representation of this task
     */
    public String toString() {
        String result = "";
        result = result + getType();
        if (getType() == LOAD) {
            result += " at " + getLoadPercent() + "%";
        }
        return result;
    }





}
