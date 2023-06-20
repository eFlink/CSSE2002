package towersim.aircraft;

import towersim.tasks.TaskList;
import towersim.util.EmergencyState;
import towersim.util.OccupancyLevel;
import towersim.util.Tickable;

import static towersim.tasks.TaskType.LOAD;

/** Represents an aircraft capable of carrying passenger cargo. */
public class PassengerAircraft extends Aircraft implements EmergencyState,
        OccupancyLevel, Tickable {

    /** current number of passengers onboard */
    private int numPassengers;

    /** Average weight of a single passenger including their baggage, in kilograms. */
    public static final double AVG_PASSENGER_WEIGHT = 90.0;

    /**
     * Creates a new passenger aircraft with the given callsign, task list, fuel capacity,
     * amount of fuel and number of passengers.
     *
     * If the given number of passengers is less than zero or greater than the aircraft's
     * maximum passenger capacity as defined in the aircraft's characteristics,
     * then an IllegalArgumentException should be thrown.
     *
     * @param callsign unique callsign
     * @param characteristics characteristics that describe this aircraft
     * @param tasks task list to be used by aircraft
     * @param fuelAmount current amount of fuel onboard, in litres
     * @param numPassengers current number of passengers onboard
     */
    public PassengerAircraft(String callsign, AircraftCharacteristics characteristics,
                             TaskList tasks, double fuelAmount, int numPassengers) {
        super(callsign, characteristics, tasks, fuelAmount);
        this.numPassengers = numPassengers;
    }

    /**
     * Returns the total weight of the aircraft in its current state.
     * The total weight for a passenger aircraft is calculated as the sum of:
     *      the aircraft's empty weight
     *      the amount of fuel onboard the aircraft multiplied by the weight of a litre of fuel
     *      the number of passengers onboard multiplied by the weight of an average passenger,
     *      including baggage
     *
     * @return total weight of aircraft in kilograms
     */
    @Override
    public double getTotalWeight() {
        return getCharacteristics().emptyWeight + (getFuelAmount() * LITRE_OF_FUEL_WEIGHT)
                + (AVG_PASSENGER_WEIGHT * numPassengers);
    }

    /**
     * Returns the number of ticks required to load the aircraft at the gate.
     * The loading time for passenger aircraft is calculated as the logarithm (base 10) of the
     * number of passengers to be loaded, rounded to the nearest integer. Note that the loading
     * time is bounded below by 1, that is, if the result of this calculation gives a number less
     * than 1, then 1 should be returned instead.
     *
     * The number of passengers to be loaded is equal to the maximum passenger capacity of the
     * aircraft multiplied by the load ratio specified in the aircraft's current task
     * (see Task.getLoadPercent()). The result of this calculation should be rounded to the nearest
     * whole passenger.
     *
     * For example, suppose an aircraft has a capacity of 175 passengers and its current task is
     * a LOAD task with a load percentage of 65%. The number of passengers to load would be 114
     * (rounded from 113.75, which is 65% of 175). Then, the loading time would be
     * log(114) = 2.057 rounded to 2 ticks.
     *
     * @return loading time in ticks
     */
    @Override
    public int getLoadingTime() {
        double loadPercent = getTaskList().getCurrentTask().getLoadPercent();
        double loadRatio = loadPercent / 100;
        double variable = Math.round(getCharacteristics().passengerCapacity * loadRatio);
        int loadingTime = (int) Math.round(Math.log10(getCharacteristics().passengerCapacity
                * loadRatio));
        if (loadingTime < 1) {
            loadingTime = 1;
        }
        return loadingTime;
    }

    /**
     * Returns the ratio of passengers onboard to maximum passenger capacity as
     * a percentage between 0 and 100.
     *
     * 0 represents no passengers onboard, and 100 represents the aircraft being at maximum
     * capacity of passengers onboard.
     *
     * The calculated value should be rounded to the nearest percentage point.
     *
     * @return occupancy level as a percentage
     */
    @Override
    public int calculateOccupancyLevel() {
        double toPercentage = 100;
        double passengerCapacity = getCharacteristics().passengerCapacity;
        double ratio = (numPassengers / passengerCapacity) * toPercentage;
        return (int) Math.round(ratio);
    }


    /**
     * Updates the aircraft's state on each tick of the simulation.
     * Firstly, the Aircraft.tick() method in the superclass should be called to perform refueling
     * and burning of fuel.
     *
     * Next, if the aircraft's current task is a LOAD task, passengers should be loaded onto
     * the aircraft. The number of passengers to load in a single call of tick() is equal to the
     * total number of passengers to be loaded based on the LOAD task's load percentage, divided
     * by the loading time given by getLoadingTime(). This ensures that passengers are loaded in
     * equal increments across the entire loading time. The result of this division operation may
     * yield a number of passengers that is not an integer, in which case it should be rounded to
     * the nearest whole integer (whole passenger).
     *
     * Note that the total number of passengers on the aircraft should not be allowed to exceed
     * the maximum passenger capacity of the aircraft, given by
     * AircraftCharacteristics.passengerCapacity.
     *
     * For example, suppose an aircraft initially has 0 passengers onboard and has a current task
     * of type LOAD with a load percentage of 45%. The aircraft has a passenger capacity of 150.
     * Then, the total number of passengers to be loaded is 45% of 150 = 67.5 rounded to 68.
     * According to getLoadingTime(), this number of passengers will take 2 ticks to load.
     * So, a single call to tick() should increase the number of passengers onboard by 68 / 2 = 34.
     */
    @Override
    public void tick() {
        super.tick();
        if (getTaskList().getCurrentTask().getType() == LOAD) {
            double loadPercent = getTaskList().getCurrentTask().getLoadPercent();
            double loadRatio = loadPercent / 100;
            double passengerLoad = Math.round(loadRatio * getCharacteristics().passengerCapacity);
            int passengerIncrement = (int) Math.round((passengerLoad) / getLoadingTime());
            numPassengers += passengerIncrement;
            if (numPassengers > getCharacteristics().passengerCapacity) {
                numPassengers = getCharacteristics().passengerCapacity;
            }

        }
    }

}
