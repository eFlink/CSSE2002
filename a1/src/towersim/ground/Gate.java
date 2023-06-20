package towersim.ground;

import towersim.aircraft.Aircraft;
import towersim.util.NoSpaceException;

/** Represents an aircraft gate with facilities for a single aircraft to be parked. */
public class Gate {

    /** identifying number of this gate */
    private int gateNumber;

    /** whether an aircraft is currently parked */
    private boolean occupied;

    /** aircraft parked at gate */
    private Aircraft aircraft;

    /**
     * Creates a new Gate with the given unique gate number.
     * Gate numbers should be unique across all terminals in the airport.
     *
     * Initially, there should be no aircraft occupying the gate.
     *
     * @param gateNumber identifying number of this gate
     */
    public Gate(int gateNumber) {
        this.gateNumber = gateNumber;
        occupied = false;
        aircraft = null;
    }

    /**
     * Returns this gate's gate number.
     *
     * @return gate number
     */
    public int getGateNumber() {
        return gateNumber;
    }

    /**
     * Parks the given aircraft at this gate, so that the gate becomes occupied.
     * If the gate is already occupied, then a NoSpaceException should be thrown
     * and the aircraft should not be parked.
     *
     * @param aircraft aircraft to park at gate
     * @throws NoSpaceException if the gate is already occupied by an aircraft
     */
    public void parkAircraft(Aircraft aircraft) throws NoSpaceException {
        if (isOccupied()) {
            throw new NoSpaceException();
        }
        occupied = true;
        this.aircraft = aircraft;
    }

    /**
     * Removes the currently parked aircraft from the gate.
     * If no aircraft is parked at the gate, no action should be taken.
     */
    public void aircraftLeaves() {
        occupied = false;
        aircraft = null;
    }

    /**
     * Returns true if there is an aircraft currently parked at the gate, or false otherwise.
     *
     * @return whether an aircraft is currently parked
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Returns the aircraft currently parked at the gate, or null if there is no aircraft parked.
     *
     * @return currently parked aircraft
     */
    public Aircraft getAircraftAtGate() {
        return aircraft;
    }

    /**
     * Returns the human-readable string representation of this gate.
     * The format of the string to return is
     *
     * Gate gateNumber [callsign]
     * where gateNumber is the gate number of this gate and callsign is the
     * callsign of the aircraft parked at this gate, or empty if the gate is unoccupied.
     * For example: "Gate 15 [ABC123]" or "Gate 24 [empty]".
     *
     * @return string representation of this gate
     */
    @Override
    public String toString() {
        String result = "Gate ";
        result += getGateNumber();
        if (aircraft == null) {
            result += " [empty]";
        } else {
            result += " [" + getAircraftAtGate().getCallsign() + "]";
        }
        return result;
    }
}
