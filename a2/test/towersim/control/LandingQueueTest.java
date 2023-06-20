package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LandingQueueTest {

    private TaskList taskList1;
    private Aircraft aircraft1;
    private Aircraft aircraft2;
    private Aircraft emptyAircraft; // no freight
    private Aircraft emptyAircraft2;
    private Aircraft fullAircraft; // nearly full freight
    private Aircraft aircraftPass;

    private LandingQueue aircrafts;

    @Before
    public void setUp() {

        aircrafts = new LandingQueue();

        this.taskList1 = new TaskList(List.of(
                new Task(TaskType.LOAD, 0), // load no freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.LOAD, 65), // load 65% of capacity of freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.LOAD, 30), // load 30% of capacity of freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        this.aircraft1 = new FreightAircraft("ABC001", AircraftCharacteristics.BOEING_747_8F,
                taskList1,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                AircraftCharacteristics.BOEING_747_8F.freightCapacity);

        this.aircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                60000);

        this.emptyAircraft = new FreightAircraft("EMP001", AircraftCharacteristics.BOEING_747_8F,
                taskList1,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.emptyAircraft2 = new FreightAircraft("EMP002", AircraftCharacteristics.BOEING_747_8F,
                taskList2,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.fullAircraft = new FreightAircraft("FUL001", AircraftCharacteristics.BOEING_747_8F,
                taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 110000);


        aircrafts.addAircraft(aircraft1);
        aircrafts.addAircraft(aircraft2);
        aircrafts.addAircraft(emptyAircraft);
        aircrafts.addAircraft(emptyAircraft2);
        aircrafts.addAircraft(fullAircraft);
    }

    @Test
    public void peekAircraftTestStandard() {
        assertEquals(aircraft1, aircrafts.peekAircraft());
    }

    @Test
    public void peekAircraftTestEmergency() {
        emptyAircraft.declareEmergency();
        assertEquals(emptyAircraft, aircrafts.peekAircraft());
    }

    @Test
    public void peekAircraftTestFuel1() {
        emptyAircraft.getTaskList().moveToNextTask();
        emptyAircraft.getTaskList().moveToNextTask();
        emptyAircraft.tick();
        emptyAircraft.tick();
        emptyAircraft.tick();
        assertEquals(emptyAircraft, aircrafts.peekAircraft());
    }

    @Test
    public void peekAircraftTestFuel2() {
        emptyAircraft.getTaskList().moveToNextTask();
        emptyAircraft.getTaskList().moveToNextTask();
        emptyAircraft.tick();
        emptyAircraft.tick();
        emptyAircraft.tick();

        aircraft2.getTaskList().moveToNextTask();
        aircraft2.getTaskList().moveToNextTask();
        aircraft2.tick();
        aircraft2.tick();
        aircraft2.tick();
        aircraft2.tick();
        assertEquals(aircraft2, aircrafts.peekAircraft());
    }

    @Test
    public void peekAircraftTestPassenger() {
        this.aircraftPass = new PassengerAircraft("PASS",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);
        aircrafts.addAircraft(aircraftPass);
        assertEquals(aircraftPass, aircrafts.peekAircraft());
    }

    @Test
    public void AircraftContains() {
        assertTrue(aircrafts.containsAircraft(aircraft1));
        assertTrue(aircrafts.containsAircraft(aircraft2));
        assertTrue(aircrafts.containsAircraft(emptyAircraft));
        assertTrue(aircrafts.containsAircraft(emptyAircraft2));
        assertTrue(aircrafts.containsAircraft(fullAircraft));
    }

    @Test
    public void removeAircraft1() {
        assertEquals(aircraft1, aircrafts.removeAircraft());
        assertFalse(aircrafts.containsAircraft(aircraft1));
    }

    @Test
    public void removeAircraftEmergency() {
        peekAircraftTestEmergency();
        assertEquals(emptyAircraft, aircrafts.removeAircraft());
        assertFalse(aircrafts.containsAircraft(emptyAircraft));
    }

    @Test
    public void removeAircraftEmergencyTestFuel1() {
        peekAircraftTestFuel1();
        assertEquals(emptyAircraft, aircrafts.removeAircraft());
        assertFalse(aircrafts.containsAircraft(emptyAircraft));
    }

    @Test
    public void removeAircraftEmergencyTestFuel2() {
        peekAircraftTestFuel2();
        assertEquals(aircraft2, aircrafts.removeAircraft());
        assertFalse(aircrafts.containsAircraft(aircraft2));
    }

    @Test
    public void removeAircraftEmergencyPassenger() {
        peekAircraftTestPassenger();
        assertEquals(aircraftPass, aircrafts.removeAircraft());
        assertFalse(aircrafts.containsAircraft(aircraftPass));
    }

    @Test
    public void getAircraftInOrderTest() {
        List<Aircraft> aircraftTemp = new ArrayList<>();
        aircraftTemp.add(aircraft1);
        aircraftTemp.add(aircraft2);
        aircraftTemp.add(emptyAircraft);
        aircraftTemp.add(emptyAircraft2);
        aircraftTemp.add(fullAircraft);
        assertEquals(aircraftTemp ,aircrafts.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderTestEmergency() {
        peekAircraftTestEmergency();
        List<Aircraft> aircraftTemp = new ArrayList<>();
        aircraftTemp.add(emptyAircraft);
        aircraftTemp.add(aircraft1);
        aircraftTemp.add(aircraft2);
        aircraftTemp.add(emptyAircraft2);
        aircraftTemp.add(fullAircraft);
        assertEquals(aircraftTemp ,aircrafts.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderTestFuel1() {
        peekAircraftTestFuel1();
        List<Aircraft> aircraftTemp = new ArrayList<>();
        aircraftTemp.add(emptyAircraft);
        aircraftTemp.add(aircraft1);
        aircraftTemp.add(aircraft2);
        aircraftTemp.add(emptyAircraft2);
        aircraftTemp.add(fullAircraft);
        assertEquals(aircraftTemp ,aircrafts.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderTestFuel2() {
        peekAircraftTestFuel2();
        List<Aircraft> aircraftTemp = new ArrayList<>();
        aircraftTemp.add(aircraft2);
        aircraftTemp.add(emptyAircraft);
        aircraftTemp.add(aircraft1);
        aircraftTemp.add(emptyAircraft2);
        aircraftTemp.add(fullAircraft);
        assertEquals(aircraftTemp ,aircrafts.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderPassenger() {
        peekAircraftTestPassenger();
        List<Aircraft> aircraftTemp = new ArrayList<>();
        aircraftTemp.add(aircraftPass);
        aircraftTemp.add(aircraft1);
        aircraftTemp.add(aircraft2);
        aircraftTemp.add(emptyAircraft);
        aircraftTemp.add(emptyAircraft2);
        aircraftTemp.add(fullAircraft);
        assertEquals(aircraftTemp ,aircrafts.getAircraftInOrder());
    }

    @Test
    public void toStringTest() {
        List<Aircraft> aircraftTemp = new ArrayList<>();
        assertEquals("LandingQueue [ABC001, ABC002, EMP001, EMP002, FUL001]",aircrafts.toString());
        getAircraftInOrderTestFuel1();
        System.out.println(aircrafts.toString());
    }

    @Test
    public void encodeTest() {
        String expected = "LandingQueue:5\n" +"ABC001,ABC002,EMP001,EMP002,FUL001";
        assertEquals(expected,aircrafts.encode());
    }

}
