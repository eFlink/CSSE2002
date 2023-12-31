package towersim.tasks;

import java.util.Objects;

/**
 * Represents a task currently assigned to an aircraft.
 * <p>
 * Tasks relate to an aircraft's movement and ground operations.
 *
 * @ass1
 */
public class Task {
    /**
     * Type of task.
     */
    private final TaskType type;

    /**
     * Percent of maximum capacity to be loaded at the gate. Used by LOAD type tasks.
     */
    private final int loadPercent;

    /**
     * Creates a new Task of the given task type.
     *
     * @param type type of task
     * @ass1
     */
    public Task(TaskType type) {
        this.type = type;
        this.loadPercent = 0;
    }

    /**
     * Creates a new Task of the given task type and stores the given load percentage in the task.
     * <p>
     * This constructor is used for tasks of the LOAD type, so that a percentage may be specified
     * for the load operation.
     *
     * @param type        type of task
     * @param loadPercent percentage of maximum capacity to load
     * @ass1
     */
    public Task(TaskType type, int loadPercent) {
        this.type = type;
        this.loadPercent = loadPercent;
    }

    /**
     * Returns the type of this task.
     *
     * @return task type
     * @ass1
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns the load percentage specified when constructing the task, or 0 if none was specified.
     *
     * @return task load percentage
     * @ass1
     */
    public int getLoadPercent() {
        return loadPercent;
    }

    /**
     * Returns the human-readable string representation of this task.
     * <p>
     * The format of the string to return is:
     * <ul>
     * <li>{@code "LOAD at percent%"} where {@code percent} is the load percentage, without the
     * enclosing double quotes, if the task type is {@code LOAD}.</li>
     * <li>{@code "taskType"} where {@code taskType} is the string representation of the task type,
     * without the enclosing double quotes, for any other task type.</li>
     * </ul>
     *
     * @return string representation of this task
     * @ass1
     */
    @Override
    public String toString() {
        if (this.type == TaskType.LOAD) {
            return this.type + " at " + this.loadPercent + "%";
        }
        return String.valueOf(this.type);
    }

    /**
     * Returns the machine-readable string representation of this task.
     * If this is a LOAD-type task, the format of the string to return is
     * <p>
     * LOAD@loadPercent
     * where loadPercent is the task's load percentage.
     * If this is not a LOAD-type task, the format of the string to return is
     * <p>
     * TASKTYPE
     * where TASKTYPE is the name of the task's task type (see Enum.name()).
     * For example:
     * <p>
     * LOAD@20
     * For example:
     * WAIT
     *
     * @return encoded string representation of this task
     */
    public String encode() {
        String result = "";
        result += getType().name();
        if (result.equals("LOAD")) {
            result += "@" + getLoadPercent();
        }
        return result;
    }

    /**
     * Returns true if and only if this task is equal to the other given task.
     * For two tasks to be equal, they must have the same task type and load percentage.
     *
     * @param obj - other object to check equality
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        Task task = (Task) obj;
        if (task.hashCode() == hashCode()) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the hash code of this task.
     * Two tasks that are equal according to equals(Object) should have the same hash code.
     *
     * @return hash code of this task
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode += getType().hashCode();
        hashCode += getLoadPercent();
        return hashCode;
    }
}
