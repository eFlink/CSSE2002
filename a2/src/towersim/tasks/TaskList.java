package towersim.tasks;

import java.util.List;

/**
 * Represents a circular list of tasks for an aircraft to cycle through.
 *
 * @ass1
 */
public class TaskList {
    /**
     * List of tasks to cycle through.
     */
    private final List<Task> tasks;
    /**
     * Index of current task in tasks list.
     */
    private int currentTaskIndex;

    /**
     * Creates a new TaskList with the given list of tasks.
     * <p>
     * Initially, the current task (as returned by {@link #getCurrentTask()})
     * should be the first
     * task in the given list.
     *
     * @param tasks list of tasks
     * @ass1
     */
    public TaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        int nextI = 1;
        while (i < tasks.size()) {
            if (i == tasks.size() - 1) {
                nextI = 0;
            }
            if (tasks.get(i).getType().name().equals("AWAY")) {
                if (!tasks.get(nextI).getType().name().equals("AWAY")
                        && !tasks.get(nextI).getType().name().equals("LAND")) {
                    throw new IllegalArgumentException();
                }
            } else if (tasks.get(i).getType().name().equals("LAND")
                    || tasks.get(i).getType().name().equals("WAIT")) {
                if (!tasks.get(nextI).getType().name().equals("WAIT")
                        && !tasks.get(nextI).getType().name().equals("LOAD")) {
                    throw new IllegalArgumentException();
                }
            } else if (tasks.get(i).getType().name().equals("LOAD")) {
                if (!tasks.get(nextI).getType().name().equals("TAKEOFF")) {
                    throw new IllegalArgumentException();
                }
            } else if (tasks.get(i).getType().name().equals("TAKEOFF")) {
                if (!tasks.get(nextI).getType().name().equals("AWAY")) {
                    throw new IllegalArgumentException();
                }
            }
            i++;
            nextI++;
        }
        this.tasks = tasks;
        this.currentTaskIndex = 0;
    }

    /**
     * Returns the current task in the list.
     *
     * @return current task
     * @ass1
     */
    public Task getCurrentTask() {
        return this.tasks.get(this.currentTaskIndex);
    }

    /**
     * Returns the task in the list that comes after the current task.
     * <p>
     * After calling this method, the current task should still be the same as
     * it was before calling
     * the method.
     * <p>
     * Note that the list is treated as circular, so if the current task
     * is the last in the list,
     * this method should return the first element of the list.
     *
     * @return next task
     * @ass1
     */
    public Task getNextTask() {
        int nextTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
        return this.tasks.get(nextTaskIndex);
    }

    /**
     * Moves the reference to the current task forward by one
     * in the circular task list.
     * <p>
     * After calling this method, the current task should be
     * the next task in the circular list
     * after the "old" current task.
     * <p>
     * Note that the list is treated as circular, so if the current task
     * is the last in the list,
     * the new current task should be the first element of the list.
     *
     * @ass1
     */
    public void moveToNextTask() {
        this.currentTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
    }

    /**
     * Returns the human-readable string representation of this task list.
     * <p>
     * The format of the string to return is
     * <pre>TaskList currently on currentTask [taskNum/totalNumTasks]</pre>
     * where {@code currentTask} is the {@code toString()}
     * representation of the current task as
     * returned by {@link Task#toString()},
     * {@code taskNum} is the place the current task occurs in the task list, and
     * {@code totalNumTasks} is the number of tasks in the task list.
     * <p>
     * For example, a task list with the list of tasks
     * {@code [AWAY, LAND, WAIT, LOAD, TAKEOFF]}
     * which is currently on the {@code WAIT} task would have a string representation of
     * {@code "TaskList currently on WAIT [3/5]"}.
     *
     * @return string representation of this task list
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("TaskList currently on %s [%d/%d]",
                this.getCurrentTask(),
                this.currentTaskIndex + 1,
                this.tasks.size());
    }

    /**
     * Returns the machine-readable string representation of this task list.
     * The format of the string to return is
     * <p>
     * encodedTask1,encodedTask2,...,encodedTaskN
     * where encodedTaskX is the encoded representation of the Xth
     * task in the task list, for X between 1
     * and N inclusive, where N is the number of tasks in the task
     * list and encodedTask1 represents the current task.
     * For example, for a task list with 6 tasks and a current task of WAIT:
     * <p>
     * WAIT,LOAD@75,TAKEOFF,AWAY,AWAY,LAND
     *
     * @return encoded string representation of this task list
     */
    public String encode() {
        String result = "";
        result += getCurrentTask().encode();
        int i = 0;
        while (i < tasks.size() - 1) {
            result += "," + getNextTask().encode();
            moveToNextTask();
            i++;
        }
        moveToNextTask();
        return result;
    }
}
