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
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;
import towersim.util.NoSpaceException;
import towersim.util.NoSuitableGateException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ControlTowerTest {
    private ControlTower tower;

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

    @Before
    public void setup() throws IOException, MalformedSaveException {
        // this.tower = new ControlTower();

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
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 70),
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
        filenames.add("saves/queues_basic.txt");
        filenames.add("saves/terminalsWithGates_basic.txt");

        this.tower = ControlTowerInitialiser.createControlTower(
                new FileReader(filenames.get(0)),
                new FileReader(filenames.get(1)),
                new FileReader(filenames.get(2)),
                new FileReader(filenames.get(3)));

    }



}
