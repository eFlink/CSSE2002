package towersim.aircraft;

import towersim.tasks.TaskList;
import towersim.util.EmergencyState;
import towersim.util.OccupancyLevel;
import towersim.util.Tickable;

import static towersim.tasks.TaskType.AWAY;
import static towersim.tasks.TaskType.LOAD;

/**
 * Represents an aircraft whose movement is managed by the system.
 */
public abstract class Aircraft implements OccupancyLevel, Tickable, EmergencyState {

    /** unique callsign */
    private String callsign;

    /** characteristics that describe this aircraft */
    private AircraftCharacteristics characteristics;

    /** task list to be used by aircraft */
    private TaskList tasks;

    /** current amount of fuel onboard, in litres */
    private double fuelAmount;

    /** emergency state status */
    private boolean status;

    /** fuel weight per litre */
    public static final double LITRE_OF_FUEL_WEIGHT = 0.8;

    /**
     * Creates a new aircraft with the given callsign, task list, fuel capacity and amount.
     * Newly created aircraft should not be in a state of emergency by default.
     *
     * If the given fuel amount is less than zero or greater than the aircraft's
     * maximum fuel capacity as defined in the aircraft's characteristics,
     * then an IllegalArgumentException should be thrown.
     *
     * @param callsign unique callsign.
     * @param characteristics characteristics that describe this aircraft.
     * @param tasks task list to be used by aircraft.
     * @param fuelAmount current amount of fuel onboard, in litres
     *
     * @throws IllegalArgumentException - if fuelAmount < 0 or if fuelAmount > fuel capacity
     */
    protected Aircraft(String callsign, AircraftCharacteristics characteristics, TaskList tasks,
                       double fuelAmount) {
        if (fuelAmount < 0 || fuelAmount > characteristics.fuelCapacity) {
            throw new IllegalArgumentException();
        }
        this.callsign = callsign;
        this.characteristics = characteristics;
        this.tasks = tasks;
        this.fuelAmount = fuelAmount;
        status = false;

    }

    /**
     * Returns the callsign of the aircraft.
     *
     * @return aircraft callsign.
     */
    public String getCallsign() {
        return callsign;
    }

    /**
     * Returns this aircraft's characteristics.
     *
     * @return aircraft characteristics.
     */
    public AircraftCharacteristics getCharacteristics() {
        return characteristics;
    }

    /**
     * Returns the current amount of fuel onboard, in litres.
     *
     * @return current fuel amount.
     */
    public double getFuelAmount() {
        return fuelAmount;
    }

    /**
     * Returns the percentage of fuel remaining,
     * rounded to the nearest whole percentage, 0 to 100.
     * This is calculated as 100 multiplied by the fuel amount divided by the fuel capacity,
     * rounded to the nearest integer.
     *
     * @return percentage of fuel remaining.
     */
    public int getFuelPercentRemaining() {
        double toPercentage = 100;
        double decimal = (getFuelAmount() / getCharacteristics().fuelCapacity) * toPercentage;
        return (int) Math.round(decimal);
    }

    /**
     * Returns the total weight of the aircraft in its current state.
     *
     * @return total weight of aircraft in kilograms.
     */
    public double getTotalWeight() {
        return LITRE_OF_FUEL_WEIGHT * getFuelAmount();
    }

    /**
     * Returns the task list of this aircraft.
     *
     * @return task list.
     */
    public TaskList getTaskList() {
        return tasks;
    }

    /**
     * Returns the number of ticks required to load the aircraft at the gate.
     * Different types and models of aircraft have different loading times.
     *
     * @return time to load aircraft, in ticks.
     */
    public abstract int getLoadingTime();

    /**
     * Updates the aircraft's state on each tick of the simulation.
     * Aircraft burn fuel while flying. If the aircraft's current task is AWAY,
     * the amount of fuel on the aircraft should decrease by 10% of the total capacity.
     * If the fuel burned during an AWAY tick would result in the aircraft having
     * a negative amount of fuel, the fuel amount should instead be set to zero.
     */
    @Override
    public void tick() {
        double fuelCapacity = getCharacteristics().fuelCapacity;
        if (getTaskList().getCurrentTask().getType() == AWAY) {
            double fuelDecrement = fuelCapacity / 10;
            fuelAmount -= fuelDecrement;
            if (getFuelAmount() < 0) {
                fuelAmount = 0;
            }
        } else if (tasks.getCurrentTask().getType() == LOAD) {
            double fuelIncrement = fuelCapacity / getLoadingTime();
            fuelAmount += fuelIncrement;
            if (getFuelAmount() > getCharacteristics().fuelCapacity) {
                fuelAmount = getCharacteristics().fuelCapacity;
            }
        }

    }

    /**
     * Returns the human-readable string representation of this aircraft.
     * The format of the string to return is
     *
     * aircraftType callsign model currentTask
     * where aircraftType is the AircraftType of the aircraft's AircraftCharacteristics,
     * callsign is the aircraft's callsign, model is the string representation of
     * the aircraft's AircraftCharacteristics,
     * and currentTask is the task type of the aircraft's current task.
     * If the aircraft is currently in a state of emergency, the format of the string to return is
     *
     * aircraftType callsign model currentTask (EMERGENCY)
     * For example, "AIRPLANE ABC123 AIRBUS_A320 LOAD (EMERGENCY)".
     */
    @Override
    public String toString() {
        String result = "";
        result += getCharacteristics().type + " ";
        result += getCallsign() + " ";
        result += getCharacteristics() + " ";
        result += getTaskList().getCurrentTask();
        if (hasEmergency()) {
            result += " (EMERGENCY)";
        }
        return result;
    }

    /**
     * Declares a state of emergency.
     */
    @Override
    public void declareEmergency() {
        status = true;
    }

    /**
     * Clears any active state of emergency.
     * Has no effect if there was no emergency prior to calling this method.
     */
    @Override
    public void clearEmergency() {
        status = false;
    }

    /**
     * public boolean hasEmergency()
     * @return true if in emergency; false otherwise.
     */
    @Override
    public boolean hasEmergency() {
        return status;
    }
}
