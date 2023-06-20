package towersim.aircraft;

import towersim.tasks.TaskList;
import towersim.util.EmergencyState;
import towersim.util.OccupancyLevel;
import towersim.util.Tickable;

import static towersim.tasks.TaskType.LOAD;

/** Represents an aircraft capable of carrying freight cargo. */
public class FreightAircraft extends Aircraft implements EmergencyState, OccupancyLevel, Tickable {

    /** current amount of freight onboard, in kilograms */
    private int freightAmount;

    /**
     * Creates a new freight aircraft with the given callsign, task list, fuel capacity,
     * amount of fuel and kilograms of freight.
     * If the given amount of freight is less than zero or greater than the aircraft's
     * maximum freight capacity as defined in the aircraft's characteristics,
     * then an IllegalArgumentException should be thrown.
     *
     * @param callsign unique callsign
     * @param characteristics characteristics that describe this aircraft
     * @param tasks task list to be used by aircraft
     * @param fuelAmount current amount of fuel onboard, in litres
     * @param freightAmount current amount of freight onboard, in kilograms
     *
     * @throws IllegalArgumentException if freightAmount < 0 or if freightAmount > freight capacity
     */
    public FreightAircraft(String callsign, AircraftCharacteristics characteristics, TaskList tasks,
                           double fuelAmount, int freightAmount) {
        super(callsign, characteristics, tasks, fuelAmount);
        this.freightAmount = freightAmount;
    }


    /**
     * Returns the total weight of the aircraft in its current state.
     * The total weight for a freight aircraft is calculated as the sum of:
     *      the aircraft's empty weight
     *      the amount of fuel onboard the aircraft multiplied by the weight of a litre of fuel
     *      the weight of the aircraft's freight onboard
     *
     * @return total weight of aircraft in kilograms
     */
    @Override
    public double getTotalWeight() {
        return getCharacteristics().emptyWeight + (getFuelAmount() * LITRE_OF_FUEL_WEIGHT)
                + freightAmount;
    }

    /**
     *Returns the number of ticks required to load the aircraft at the gate.
     *
     * The freight to be loaded is equal to the maximum freight capacity of the aircraft
     * multiplied by the load ratio specified in the aircraft's current task
     * (see Task.getLoadPercent()). The result of this calculation should be rounded to
     * the nearest whole kilogram.
     *
     * @return loading time in ticks
     */
    @Override
    public int getLoadingTime() {
        double divideby = 100;
        double loadRatio = getTaskList().getCurrentTask().getLoadPercent() / divideby;
        double freightLoad = getCharacteristics().freightCapacity * loadRatio;
        int loadingTime = 0;
        if (freightLoad < 1000) {
            loadingTime = 1;
        } else if (freightLoad >= 1000 && freightLoad <= 50000) {
            loadingTime = 2;
        } else if (freightLoad > 50000) {
            loadingTime = 3;
        }
        return loadingTime;
    }

    /**
     * Returns the ratio of freight cargo onboard to maximum available freight capacity
     * as a percentage between 0 and 100.
     * 0 represents no freight onboard, and 100 represents the aircraft being at maximum
     * capacity of freight onboard.
     *
     * The calculated value should be rounded to the nearest percentage point.
     *
     * @return occupancy level as a percentage
     */
    @Override
    public int calculateOccupancyLevel() {
        double toPercentage = 100;
        double freightCapacity = getCharacteristics().freightCapacity;
        double ratio = (freightAmount / freightCapacity) * toPercentage;
        return (int) Math.round(ratio);
    }

    /**
     *Updates the aircraft's state on each tick of the simulation.
     * Firstly, the Aircraft.tick() method in the superclass should be called to perform
     * refueling and burning of fuel.
     *
     * Next, if the aircraft's current task is a LOAD task, freight should be loaded onto the
     * aircraft.
     * The amount of freight to load in a single call of tick() is equal to the total amount of
     * freight to be loaded based on the LOAD task's load percentage, divided by the loading time
     * given by getLoadingTime(). This ensures that freight is loaded in equal increments
     * across the entire loading time. The result of this division operation may yield
     * a freight amount that is not an integer, in which case it should be rounded to the
     * nearest whole integer (kilogram).
     *
     * Note that the total amount of freight on the aircraft should not be allowed to exceed the
     * maximum freight capacity of the aircraft, given by AircraftCharacteristics.freightCapacity.
     *
     * For example, suppose an aircraft initially has 0kg of freight onboard and has a current task
     * of type LOAD with a load percentage of 65%. The aircraft has a freight capacity of 40,000kg.
     * Then, the total amount of freight to be loaded is 65% of 40,000kg = 26,000kg. According to
     * getLoadingTime(), this amount of freight will take 2 ticks to load. So,
     * a single call to tick() should increase the amount of freight onboard
     * by 26,000kg / 2 = 13,000kg.
     */
    @Override
    public void tick() {
        super.tick();
        if (getTaskList().getCurrentTask().getType() == LOAD) {
            double loadPercent = getTaskList().getCurrentTask().getLoadPercent();
            double loadRatio = loadPercent / 100;
            double freightIncrementUnrouded = (loadRatio * getCharacteristics().freightCapacity)
                    / getLoadingTime();
            int freightIncrement = (int) Math.round(freightIncrementUnrouded);
            freightAmount += freightIncrement;
            if (freightAmount > getCharacteristics().freightCapacity) {
                freightAmount = getCharacteristics().freightCapacity;
            }
        }
    }

}
