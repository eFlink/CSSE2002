package towersim.ground;

import towersim.util.EmergencyState;
import towersim.util.NoSpaceException;
import towersim.util.NoSuitableGateException;
import towersim.util.OccupancyLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** Represents an airport terminal building, containing several aircraft gates. */
public abstract class Terminal implements EmergencyState, OccupancyLevel {

    /** identifying number of this terminal */
    private int terminalNumber;

    /** list of terminal's gates */
    private List<Gate> gateList;

    /** emergency state status */
    private boolean status;

    /** Maximum possible number of gates allowed at a single terminal. */
    public static final int MAX_NUM_GATES = 6;

    /**
     * Creates a new Terminal with the given unique terminal number.
     * It is not the responsibility of the Terminal class to ensure terminal
     * numbers are unique. Instead, the user should check that no other terminal
     * of the same type exists with the same terminal
     * number when instantiating a new terminal.
     *
     * Newly created terminals should not be in a state of emergency by default.
     *
     * @param terminalNumber identifying number of this terminal
     */
    protected Terminal(int terminalNumber) {
        this.terminalNumber = terminalNumber;
        gateList = new ArrayList<>();
        status = false;
    }

    /**
     * Returns this terminal's terminal number.
     *
     * @return terminal number
     */
    public int getTerminalNumber() {
        return terminalNumber;
    }

    /**
     * Adds a gate to the terminal.
     * If the terminal is currently at maximum capacity (MAX_NUM_GATES),
     * then the gate should not be added, and instead a NoSpaceException should be thrown.
     *
     * @param gate gate to add to terminal
     * @throws NoSpaceException if there is no space at the terminal for the new gate
     */
    public void addGate(Gate gate) throws NoSpaceException {
        if (gateList.size() == 6) {
            throw new NoSpaceException();
        }
        gateList.add(gate);
    }

    /**
     * Returns a list of all gates in the terminal.
     * The order in which gates appear in this list should be the same as the order in
     * which they were added by calling addGate(Gate).
     *
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return list of terminal's gates
     */
    public List<Gate> getGates() {
        return new ArrayList<>(gateList);
    }

    /**
     * Finds and returns the first non-occupied gate in this terminal.
     * Gates should be searched in the same order as in getGates().
     *
     * If all gates in this terminal are occupied with an aircraft,
     * throws a NoSuitableGateException.
     *
     * @return first non-occupied gate in this terminal
     * @throws NoSuitableGateException if all gates in this terminal are occupied
     */
    public Gate findUnoccupiedGate() throws NoSuitableGateException {
        for (Gate current : gateList) {
            if (!current.isOccupied()) {
                return current;
            }
        }
        throw new NoSuitableGateException();
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
     * Returns whether or not a state of emergency is currently active.
     *
     * @return true if in emergency; false otherwise
     */
    @Override
    public boolean hasEmergency() {
        return status;
    }

    /**
     * Returns the ratio of occupied gates to total gates as a percentage from 0 to 100.
     * If there are no gates in this terminal, 0 should be returned. The ratio should be
     * rounded to the nearest whole percentage.
     *
     * For example, if the terminal has 3 gates and 2 are occupied,
     * the ratio should be 2/3 = 0.666... and the rounded percentage
     * is 67%, so 67 should be returned.
     *
     * @return percentage of occupied gates in this terminal, 0 to 100
     */
    @Override
    public int calculateOccupancyLevel() {
        ListIterator<Gate> it = gateList.listIterator();

        double i = 0;
        while (it.hasNext()) {
            if (it.next().isOccupied()) {
                i++;
            }
        }
        double toPercentage = 100;
        double decimal = (i / gateList.size()) * toPercentage;
        return (int) Math.round(decimal);
    }

    /**
     * Returns the human-readable string representation of this terminal.
     * The format of the string to return is
     *
     * TerminalType terminalNum, numGates gates
     *
     * where TerminalType is the class name of the concrete terminal class (i.e. AirplaneTerminal
     * or HelicopterTerminal), terminalNum is the terminal number and numGates is the number
     * of gates in the terminal. If the terminal is currently in a state of emergency,
     * the format of the string to return is
     *
     * TerminalType terminalNum, numGates gates (EMERGENCY)
     * For example, "Terminal 3, 5 gates (EMERGENCY)".
     *
     * @return string representation of this terminal
     */
    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " " + getTerminalNumber() + ", ";
        int numberOfGates = 0;
        for (Gate gate : gateList) {
            numberOfGates++;
        }
        result += numberOfGates + " gates";
        if (hasEmergency()) {
            result += "(EMERGENCY)";
        }
        return result;
    }
}

