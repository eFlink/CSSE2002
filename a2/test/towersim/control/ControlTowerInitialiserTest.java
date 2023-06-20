package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;

import static org.junit.Assert.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ControlTowerInitialiserTest {

    private AirplaneTerminal airplaneTerminal1;
    private AirplaneTerminal airplaneTerminal2;
    private HelicopterTerminal helicopterTerminal1;

    private Gate gate1;
    private Gate gate2;
    private Gate gate3;
    private Gate gate4;

    private Aircraft passengerAircraft1;
    private Aircraft passengerAircraft2;
    private Aircraft passengerAircraft3;
    private Aircraft passengerAircraftAway;
    private Aircraft passengerAircraftTakingOff;
    private Aircraft passengerAircraftLanding;
    private Aircraft passengerAircraftLoading;
    private Aircraft passengerAircraftLoadingSingleTick;
    private Aircraft freightAircraftLoadingMultipleTicks;

    private ControlTower tower;
    private BufferedReader aircraft;
    private BufferedReader queues;
    private BufferedReader terminalsWithGates;
    private BufferedReader ticks;

    @Before
    public void setup() throws IOException, MalformedSaveException {
        this.airplaneTerminal1 = new AirplaneTerminal(1);
        this.airplaneTerminal2 = new AirplaneTerminal(2);
        this.helicopterTerminal1 = new HelicopterTerminal(1);

        this.gate1 = new Gate(1);
        this.gate2 = new Gate(2);
        this.gate3 = new Gate(3);
        this.gate4 = new Gate(4);

        TaskList taskList1 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 50),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 35),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskListTakeoff = new TaskList(List.of(
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100)));

        TaskList taskListLand = new TaskList(List.of(
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)));

        TaskList taskListLoad = new TaskList(List.of(
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT)));

        TaskList taskListAway = new TaskList(List.of(
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 60),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY)));

        this.passengerAircraft1 = new PassengerAircraft("ABC001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 10, 0);

        this.passengerAircraft2 = new PassengerAircraft("ABC002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList2,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 0);

        this.passengerAircraft3 = new PassengerAircraft("ABC003",
                AircraftCharacteristics.ROBINSON_R44,
                taskList3,
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity / 2, 0);

        this.passengerAircraftTakingOff = new PassengerAircraft("TAK001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListTakeoff,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 100);

        this.passengerAircraftLanding = new PassengerAircraft("LAN001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListLand,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 100);

        this.passengerAircraftLoading = new PassengerAircraft("LOD001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListLoad,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 8, 0);

        this.passengerAircraftLoadingSingleTick = new PassengerAircraft("LOD002",
                AircraftCharacteristics.ROBINSON_R44,
                taskListLoad, // current task is LOAD @ 70%
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity / 2, 0);

        this.freightAircraftLoadingMultipleTicks = new FreightAircraft("LOD003",
                AircraftCharacteristics.BOEING_747_8F,
                taskListLoad, // current task is LOAD @ 70%
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.passengerAircraftAway = new PassengerAircraft("AWY001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListAway,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 120);

        List<String> filenames = new ArrayList<>();
        filenames.add("saves/tick_basic.txt");
        filenames.add("saves/aircraft_basic.txt");
        filenames.add( "saves/queues_basic.txt");
        filenames.add("saves/terminalsWithGates_basic.txt");

        this.tower = ControlTowerInitialiser.createControlTower(
                new FileReader(filenames.get(0)),
                new FileReader(filenames.get(1)),
                new FileReader(filenames.get(2)),
                new FileReader(filenames.get(3)));
    }

    @Test
    public void readAircraftTest() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132");
        assertEquals("QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132", aircraft.encode());
        Aircraft aircraftPass = ControlTowerInitialiser.readAircraft("UPS119:BOEING_747_8F:WAIT," +
                "LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0");
        assertEquals("UPS119:BOEING_747_8F:WAIT," +
                "LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0", aircraftPass.encode());
    }

    @Test(expected = MalformedSaveException.class)
    public void readAircraftTestFreight() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:f:false:132");
    }

    @Test(expected = MalformedSaveException.class)
    public void readAircraftTestFreight1() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:f");
    }


    @Test(expected = MalformedSaveException.class)
    public void readAircraftTestFreight3() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("QFA481:AIRUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132");
    }

    //standard test
    @Test
    public void readAircraftTestFreight4() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132");
    }


    @Test(expected = MalformedSaveException.class)
    public void readAircraftTestPassenger() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("UPS119:BOEIG_747_8F:WAIT," +
                "LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0");
    }


    @Test(expected = MalformedSaveException.class)
    public void readAircraftTestPassenger3() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("UPS119:BOEING_747_8F:WAIT," +
                "LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:f:false:0");
    }


    @Test(expected = MalformedSaveException.class)
    public void readAircraftTestPassenger5() throws MalformedSaveException {
        Aircraft aircraft = ControlTowerInitialiser.readAircraft("UPS119:BOEING_747_8F:WAIT," +
                "LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:f");
    }

    @Test
    public void loadAircraftTest() throws MalformedSaveException, IOException {
        String fileContents = String.join(System.lineSeparator(),
                "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        List<Aircraft> list = new ArrayList<>(ControlTowerInitialiser.loadAircraft(new StringReader(fileContents)));
        assertEquals("[AIRPLANE QFA481 AIRBUS_A320 AWAY, AIRPLANE UTD302 BOEING_787 WAIT," +
                " AIRPLANE UPS119 BOEING_747_8F WAIT, HELICOPTER VH-BFK ROBINSON_R44 LAND]", list.toString());
    }

    @Test(expected = MalformedSaveException.class)
    public void loadAircraftTest1() throws MalformedSaveException, IOException {
        String fileContents = String.join(System.lineSeparator(),
                "5",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
    }

    @Test
    public void readTaskListTest() throws MalformedSaveException {
        String fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY";
        assertEquals("TaskList currently on AWAY [1/8]" ,ControlTowerInitialiser.
                readTaskList(fileContents).toString());
        TaskList taskListAway = new TaskList(List.of(
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 60),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY)));
        assertEquals(taskListAway.encode(), ControlTowerInitialiser.readTaskList(fileContents).encode());
    }

    @Test(expected = MalformedSaveException.class)
    public void readTaskListTest1() throws MalformedSaveException {
        String fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD,TAKEOFF,AWAY";
        ControlTowerInitialiser.readTaskList(fileContents);
    }

    @Test(expected = MalformedSaveException.class)
    public void readTaskListTest2() throws MalformedSaveException {
        String fileContents = "AWAY,AAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY";
        ControlTowerInitialiser.readTaskList(fileContents);
    }

    @Test(expected = MalformedSaveException.class)
    public void readTaskListTest3() throws MalformedSaveException {
        String fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@-1,TAKEOFF,AWAY";
        ControlTowerInitialiser.readTaskList(fileContents);
    }

    @Test(expected = MalformedSaveException.class)
    public void readTaskListTest4() throws MalformedSaveException {
        String fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@6@0,TAKEOFF,AWAY";
        ControlTowerInitialiser.readTaskList(fileContents);
    }

    @Test(expected = MalformedSaveException.class)
    public void readTaskListTest5() throws MalformedSaveException {
        String fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@f,TAKEOFF,AWAY";
        ControlTowerInitialiser.readTaskList(fileContents);
    }

    @Test(expected = MalformedSaveException.class)
    public void readTaskListTest6() throws MalformedSaveException {
        String fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@,TAKEOFF,AWAY";
        ControlTowerInitialiser.readTaskList(fileContents);
    }
}
