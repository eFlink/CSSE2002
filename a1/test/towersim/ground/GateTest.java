package towersim.ground;

// add any required imports here

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.PassengerAircraft;
import towersim.util.NoSpaceException;

import static org.junit.Assert.*;
import static towersim.aircraft.AircraftCharacteristics.AIRBUS_A320;


public class GateTest {

    // add unit tests here
    private Gate gate;

    @Before
    public void setUp() {
        gate=new Gate(3);
    }

    @Test
    public void testGetGateNumber() {
        int expected = 3;
        assertEquals(expected,gate.getGateNumber());
    }

    @Test
    public void testEmptyIsOccupied() {
        assertFalse(gate.isOccupied());
    }

    @Test
    public void testGetAircraftAtGate() {
        assertNull(gate.getAircraftAtGate());
    }

    @Test
    public void testParkAircraft() throws NoSpaceException {
        PassengerAircraft aircraft = new PassengerAircraft("ALPHA",
                AIRBUS_A320,null,1000,100);
        gate.parkAircraft(aircraft);
        assertTrue(gate.isOccupied());
        assertEquals(aircraft,gate.getAircraftAtGate());
    }

    @Test(expected = NoSpaceException.class)
    public void testParkAircraft2() throws NoSpaceException {
        testParkAircraft();
        PassengerAircraft aircraft2 = new PassengerAircraft("BEA",
                AIRBUS_A320,null,1000,100);
        gate.parkAircraft(aircraft2);
    }

    @Test
    public void testAircraftLeaves() throws NoSpaceException {
        PassengerAircraft aircraft = new PassengerAircraft("ALPHA",
                AIRBUS_A320,null,1000,100);
        gate.parkAircraft(aircraft);
        gate.aircraftLeaves();
        assertFalse(gate.isOccupied());
        assertNull(gate.getAircraftAtGate());
    }

    @Test
    public void testToString() throws NoSpaceException {
        String expected = "Gate 3 [ALPHA]";
        PassengerAircraft aircraft = new PassengerAircraft("ALPHA",
                AIRBUS_A320,null,1000,100);
        gate.parkAircraft(aircraft);
        assertEquals(expected, gate.toString());
    }

    @Test
    public void testToStringNoPlane() {
        String expected = "Gate 3 [empty]";
        assertEquals(expected, gate.toString());
    }
}
