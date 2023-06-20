package towersim.aircraft;

/**
 * Stores information about particular models of aircraft.
 * Characteristics of an individual aircraft include
 * the type of aircraft, its empty weight, fuel capacity, etc.
 */
public enum AircraftCharacteristics {

    /** Narrow-body twin-jet airliner. */
    AIRBUS_A320(AircraftType.AIRPLANE, 42600, 0, 27200, 150),

    /** Wide-body quad-jet freighter. */
    BOEING_747_8F(AircraftType.AIRPLANE, 197131, 137756, 226117, 0),

    /** Long range, wide-body twin-jet airliner. */
    BOEING_787(AircraftType.AIRPLANE, 119950, 0, 126206, 242),

    /** Twin-jet regional airliner. */
    FOKKER_100(AircraftType.AIRPLANE, 24375, 0, 13365, 97),

    /** Four-seater light helicopter. */
    ROBINSON_R44(AircraftType.HELICOPTER, 658, 0, 190, 4),

    /** Twin-engine heavy-lift helicopter. */
    SIKORSKY_SKYCRANE(AircraftType.HELICOPTER, 8724, 9100, 3328, 0);

    /** Type of aircraft. */
    public final AircraftType type; // Type of aircraft.

    /** Weight of aircraft with no load or fuel, in kilograms. */
    public final int emptyWeight; // Weight of aircraft with no load or fuel, in kilograms.

    /** Maximum amount of fuel able to be carried, in litres. */
    public final double fuelCapacity; // Maximum amount of fuel able to be carried, in litres.

    /** Maximum number of passengers able to be carried. */
    public final int passengerCapacity; // Maximum number of passengers able to be carried.

    /** Maximum amount of freight able to be carried, in kilograms. */
    public final int freightCapacity; // Maximum amount of freight able to be carried, in kilograms.

    /**
     * Assigns parameters to given type, emptyWeight, freightCapacity, fuelCapacity
     * and passengerCapacity
     *
     * @param type Type of aircraft.
     * @param emptyWeight Weight of aircraft with no load or fuel, in kilograms.
     * @param freightCapacity Maximum amount of freight able to be carried, in kilograms.
     * @param fuelCapacity Maximum amount of fuel able to be carried, in litres.
     * @param passengerCapacity Maximum number of passengers able to be carried.
     */
    AircraftCharacteristics(AircraftType type, int emptyWeight, int freightCapacity,
                            double fuelCapacity, int passengerCapacity) {
        this.type = type;
        this.emptyWeight = emptyWeight;
        this.freightCapacity = freightCapacity;
        this.fuelCapacity = fuelCapacity;
        this.passengerCapacity = passengerCapacity;
    }
}
