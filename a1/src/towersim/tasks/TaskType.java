package towersim.tasks;

/**
 * Enum to represent the possible types of tasks an aircraft can have.
 *
 * Task types have a written description to explain what each task type means.
 */
public enum TaskType {

    /** AWAY means that aircraft are either flying or at other airports. */
    AWAY("Flying outside the airport"),

    /** Aircraft in LAND are circling around the airport waiting for a slot to land. */
    LAND("Waiting in queue to land"),

    /** WAIT tells an aircraft to stay stationary at a gate and not load any cargo. */
    WAIT("Waiting idle at gate"),

    /**
     * LOAD tasks represent the aircraft loading its cargo at the gate.
     *
     * LOAD tasks also have a load percentage associated with them which tells the
     * aircraft how much cargo to load relative to their maximum capacity.
     */
    LOAD("Loading at gate"),

    /** Aircraft in TAKEOFF are waiting on taxiways for a slot to take off. */
    TAKEOFF("Waiting in queue to take off");

    /** written description */
    private final String description;

    /**
     * Assigns description to TaskType
     *
     * @param description written description
     */
    TaskType(String description) {
        this.description = description;
    }

    /**
     * Returns the written description of this task type.
     *
     * @return written description
     */
    public String getDescription() {
        return description;
    }


}
