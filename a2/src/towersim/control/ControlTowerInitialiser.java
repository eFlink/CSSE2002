package towersim.control;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Utility class that contains static methods for loading a control tower and associated
 * entities from files.
 */
public class ControlTowerInitialiser {

    /**
     * Loads the number of ticks elapsed from the given reader instance.
     * The contents of the reader should match the format specified in the tickWriter row
     * of in the table shown in ViewModel.saveAs().
     * <p>
     * For an example of valid tick reader contents, see the provided saves/tick_basic.txt
     * and saves/tick_default.txt files.
     * <p>
     * The contents read from the reader are invalid if any of the following conditions
     * are true:
     * <p>
     * The number of ticks elapsed is not an integer (i.e. cannot be parsed by
     * Long.parseLong(String)).
     * The number of ticks elapsed is less than zero.
     *
     * @param reader - reader from which to load the number of ticks elapsed
     * @return number of ticks elapsed
     * @throws MalformedSaveException - if the format of the text read from the reader is
     * invalid according to the rules above
     * @throws IOException            - if an IOException is encountered when reading
     * from the reader
     */
    public static long loadTick(Reader reader) throws MalformedSaveException, IOException {
        if (reader == null) {
            throw new MalformedSaveException();
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new MalformedSaveException();
        }
        int ticks;
        try {
            ticks = Integer.parseInt(line);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        if (ticks < 0) {
            throw new MalformedSaveException();
        }
        bufferedReader.close();
        return ticks;
    }

    /**
     * Loads the list of all aircraft managed by the control
     * tower from the given reader instance.
     * The contents of the reader should match the
     * format specified in the aircraftWriter row of
     * in the table shown in ViewModel.saveAs().
     *
     * For an example of valid aircraft reader contents, see the provided
     * saves/aircraft_basic.txt and saves/aircraft_default.txt files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     *
     * The number of aircraft specified on the first line of the reader is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     * The number of aircraft specified on the first line is not equal to the number of
     * aircraft actually read from the reader.
     * Any of the conditions listed in the Javadoc for readAircraft(String) are true.
     * This method should call readAircraft(String).
     * @param reader - reader from which to load the list of aircraft
     * @return list of aircraft read from the reader
     * @throws IOException - if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the text read from the
     * reader is invalid according to the rules above
     */
    public static List<Aircraft> loadAircraft(Reader reader)
            throws IOException, MalformedSaveException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        int aircrafts;
        try {
            aircrafts = Integer.parseInt(line);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        int i = 0;
        List<Aircraft> listOfAircraft = new ArrayList<>();
        // go through each encoded Aircraft
        while (i < aircrafts) {
            line = bufferedReader.readLine();
            Aircraft aircraft = readAircraft(line);
            String[] contents = line.split(":");
            if (Boolean.parseBoolean(contents[4])) {
                aircraft.declareEmergency();
            }
            listOfAircraft.add(aircraft);
            i++;
        }
        return listOfAircraft;
    }

    /**
     * Loads the takeoff queue, landing queue and map of loading
     * aircraft from the given reader instance.
     * Rather than returning a list of queues, this method does
     * not return anything. Instead,
     * it should modify the given takeoff queue, landing queue
     * and loading map by adding aircraft, etc.
     *
     * The contents of the reader should match the format specified in the queuesWriter row
     * of in the table shown in ViewModel.saveAs().
     *
     * For an example of valid queues reader contents, see the provided saves/queues_basic.txt
     * and saves/queues_default.txt files.
     *
     * The contents read from the reader are invalid if any of the conditions listed in the Javadoc
     * for readQueue(BufferedReader, List, AircraftQueue)
     * and readLoadingAircraft(BufferedReader, List, Map) are true.
     *
     * This method should call readQueue(BufferedReader, List, AircraftQueue) and
     * readLoadingAircraft(BufferedReader, List, Map).
     * @param reader - reader from which to load the queues and loading map
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @param takeoffQueue - empty takeoff queue that aircraft will be added to
     * @param landingQueue - empty landing queue that aircraft will be added to
     * @param loadingAircraft - empty map that aircraft and loading times will be added to
     * @throws MalformedSaveException - if the format of the text read from the reader is
     * invalid according to the rules above
     * @throws IOException - if an IOException is encountered when reading from the reader
     */
    public static void loadQueues(Reader reader, List<Aircraft> aircraft, TakeoffQueue takeoffQueue,
                                  LandingQueue landingQueue, Map<Aircraft, Integer> loadingAircraft)
            throws MalformedSaveException, IOException {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(reader);
        } catch (Exception e) {
            throw new IOException();
        }

        // Add aircrafts to Queues as LoadingAircraft
        readQueue(bufferedReader, aircraft, takeoffQueue);
        readQueue(bufferedReader, aircraft, landingQueue);
        readLoadingAircraft(bufferedReader, aircraft, loadingAircraft);
        bufferedReader.close();
    }

    /**
     * Loads the list of terminals and their gates from the given reader instance.
     * The contents of the reader should match the format
     * specified in the terminalsWithGatesWriter
     * row of in the table shown in ViewModel.saveAs().
     *
     * For an example of valid queues reader contents, see the provided
     * saves/terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     *
     * The contents read from the reader are invalid if any of
     * the following conditions are true:
     *
     * The number of terminals specified at the top of the file is not an integer
     * (i.e. cannot be parsed by Integer.parseInt(String)).
     * The number of terminals specified is not equal to the number of terminals
     * actually read from the reader.
     * Any of the conditions listed in the Javadoc for readTerminal(String,
     * BufferedReader, List) and readGate(String, List) are true.
     * This method should call readTerminal(String, BufferedReader, List).
     * @param reader - reader from which to load the list of terminals and their gates
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader
     * is invalid according to the rules above
     * @throws IOException - if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader, List<Aircraft> aircraft)
            throws MalformedSaveException, IOException {
        List<Terminal> terminals = new ArrayList<>();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(reader);
        } catch (Exception e) {
            throw new IOException();
        }
        String line = bufferedReader.readLine();
        int amountOfTerminals;
        try {
            amountOfTerminals = Integer.parseInt(line);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        // iterate through terminals and adding to
        // terminals List declared earlier
        int i = 0;
        while (i < amountOfTerminals) {
            line = bufferedReader.readLine();
            terminals.add(readTerminal(line, bufferedReader, aircraft));
            i++;
        }

        return terminals;
    }

    /**
     * Creates a control tower instance by reading various airport
     * entities from the given readers.
     * The following methods should be called in this order,
     * and their results stored temporarily,
     * to load information from the readers:
     *
     * loadTick(Reader) to load the number of elapsed ticks
     * loadAircraft(Reader) to load the list of all aircraft
     * loadTerminalsWithGates(Reader, List) to load the terminals and their gates
     * loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map) to load the takeoff queue,
     * landing queue and map of loading aircraft to their loading time remaining
     * @param tick - reader from which to load the number of ticks elapsed
     * @param aircraft - reader from which to load the list of aircraft
     * @param queues - reader from which to load the aircraft queues
     *              and map of loading aircraft
     * @param terminalsWithGates - reader from which to load the terminals and their gates
     * @return control tower created by reading from the given readers
     * @throws MalformedSaveException - if reading from any of the given
     * readers results in a MalformedSaveException,
     * indicating the contents of that reader are invalid
     * @throws IOException - if an IOException is encountered when reading from any of the readers
     */
    public static ControlTower createControlTower(Reader tick, Reader aircraft, Reader queues,
                                                  Reader terminalsWithGates)
            throws MalformedSaveException, IOException {
        long ticksElapsed = loadTick(tick);
        List<Aircraft> aircrafts = loadAircraft(aircraft);
        TakeoffQueue takeoffQueue = new TakeoffQueue();
        LandingQueue landingQueue = new LandingQueue();
        TreeMap<Aircraft, Integer> loadingAircraft =
                new TreeMap<>(Comparator.comparing(Aircraft::getCallsign));
        loadQueues(queues, aircrafts,
                takeoffQueue, landingQueue, loadingAircraft);
        List<Terminal> terminals = loadTerminalsWithGates(terminalsWithGates, aircrafts);
        ControlTower controlTower = new ControlTower(ticksElapsed,
                aircrafts, landingQueue, takeoffQueue, loadingAircraft);

        // add terminals to tower
        for (Terminal terminal : terminals) {
            controlTower.addTerminal(terminal);
        }
        return controlTower;
    }

    /**
     * Reads an aircraft from its encoded representation in the given string.
     * If the AircraftCharacteristics.passengerCapacity of the encoded aircraft is greater
     * than zero, then a PassengerAircraft should be created and returned. Otherwise,
     * a FreightAircraft should be created and returned.
     *
     * The format of the string should match the encoded representation of an aircraft,
     * as described in Aircraft.encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * More/fewer colons (:) are detected in the string than expected.
     * The aircraft's AircraftCharacteristics is not valid, i.e. it is not one of those
     * listed in AircraftCharacteristics.values().
     * The aircraft's fuel amount is not a double (i.e. cannot
     * be parsed by Double.parseDouble(String)).
     * The aircraft's fuel amount is less than zero or greater
     * than the aircraft's maximum fuel capacity.
     * The amount of cargo (freight/passengers) onboard the aircraft is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     * The amount of cargo (freight/passengers) onboard the aircraft is less than zero or
     * greater than the aircraft's maximum freight/passenger capacity.
     * Any of the conditions listed in the Javadoc for readTaskList(String) are true.
     * This method should call readTaskList(String).
     * @param line - line of text containing the encoded aircraft
     * @return decoded aircraft instance
     * @throws MalformedSaveException - if the format of the given
     * string is invalid according to the rules above
     */
    public static Aircraft readAircraft(String line) throws MalformedSaveException {
        String[] contents;
        try {
            contents = line.split(":");
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        if (contents.length == 6) {
            try {
                AircraftCharacteristics characteristics =
                        AircraftCharacteristics.valueOf(contents[1]);
                TaskList tasks = readTaskList(contents[2]);
                double fuel = Double.parseDouble(contents[3]);
                int freight = Integer.parseInt(contents[5]);
                if (characteristics.passengerCapacity == 0) {
                    return new FreightAircraft(contents[0], characteristics, tasks, fuel, freight);
                } else {
                    return new PassengerAircraft(contents[0],
                            characteristics, tasks, fuel, freight);
                }
            } catch (Exception e) {
                throw new MalformedSaveException();
            }
        } else {
            throw new MalformedSaveException();
        }
    }

    /**
     * Reads a task list from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a task list,
     * as described in TaskList.encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * The task list's TaskType is not valid (i.e. it is not one of
     * those listed in TaskType.values()).
     * A task's load percentage is not an integer (i.e. cannot be
     * parsed by Integer.parseInt(String)).
     * A task's load percentage is less than zero.
     * More than one at-symbol (@) is detected for any task in the task list.
     * The task list is invalid according to the rules specified in TaskList(List).
     * @param taskListPart - string containing the encoded task list
     * @return decoded task list instance
     * @throws MalformedSaveException - if the format of the given string is
     * invalid according to the rules above
     */
    public static TaskList readTaskList(String taskListPart) throws MalformedSaveException {
        String[] tasks = taskListPart.split(",");
        List<Task> taskString = new ArrayList<>();
        for (String task : tasks) {
            if (task.length() < 4) {
                throw new MalformedSaveException();
            }
            if (task.equals("AWAY") || task.equals("LAND")
                    || task.equals("WAIT") || task.equals("TAKEOFF")) {
                try {
                    taskString.add(new Task(TaskType.valueOf(task)));
                } catch (Exception e) {
                    throw new MalformedSaveException();
                }
            } else if (task.startsWith("LOAD")) {
                String[] loadPercentage = task.split("@");
                int value;
                try {
                    value = Integer.parseInt(loadPercentage[1]);
                } catch (Exception e) {
                    throw new MalformedSaveException();
                }
                if (loadPercentage.length == 2 && value >= 0) {
                    taskString.add(new Task(TaskType.valueOf(loadPercentage[0]),
                            value));
                } else {
                    throw new MalformedSaveException();
                }
            } else {
                throw new MalformedSaveException();
            }
        }
        return new TaskList(taskString);
    }

    /**
     * Reads an aircraft queue from the given reader instance.
     * Rather than returning a queue, this method does not return anything. Instead,
     * it should modify the given aircraft queue by adding aircraft to it.
     * <p>
     * The contents of the text read from the reader should match the encoded representation
     * of an aircraft queue, as described in AircraftQueue.encode().
     * <p>
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <p>
     * The first line read from the reader is null.
     * The first line contains more/fewer colons (:) than expected.
     * The queue type specified in the first line is not equal to the simple class name
     * of the queue provided as a parameter.
     * The number of aircraft specified on the first line is not an integer (i.e. cannot be
     * parsed by Integer.parseInt(String)).
     * The number of aircraft specified is greater than zero and the second line read is null.
     * The number of callsigns listed on the second line is not equal to the number of aircraft
     * specified on the first line.
     * A callsign listed on the second line does not correspond to the callsign of any aircraft
     * contained in the list of aircraft given as a parameter.
     *
     * @param reader   - reader from which to load the aircraft queue
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @param queue    - empty queue that aircraft will be added to
     * @throws IOException            - if an IOException is
     *                                  encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader
     *                                  is invalid according to the rules above
     */
    public static void readQueue(BufferedReader reader,
                                 List<Aircraft> aircraft, AircraftQueue queue)
            throws IOException, MalformedSaveException {
        String line = reader.readLine();
        String[] content = line.split(":");
        if (content.length != 2 || !(queue.getClass().getSimpleName().equals(content[0]))) {

            throw new MalformedSaveException();
        }
        int aircraftAmount;
        try {
            aircraftAmount = Integer.parseInt(content[1]);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        int i = 0;
        if (aircraftAmount >= 1) {
            line = reader.readLine();
            content = line.split(",");
        }
        while (i < aircraftAmount) {
            boolean callsignExists = false;
            for (Aircraft aircraftEntry : aircraft) {
                if (aircraftEntry.getCallsign().equals(content[i])) {
                    queue.addAircraft(aircraftEntry);
                    callsignExists = true;
                }
            }
            if (!callsignExists) {
                throw new MalformedSaveException();
            }
            i++;
        }

    }

    /**
     * Reads the map of currently loading aircraft from the given reader instance.
     * Rather than returning a map, this method does not return anything. Instead,
     * it should modify the given map by adding entries (aircraft/integer pairs) to it.
     *
     * The contents of the text read from the reader should match the format specified
     * in the queuesWriter row of in the table shown in ViewModel.saveAs(). Note
     * that this method should only read the map of loading aircraft, not the takeoff queue or
     * landing queue. Reading these queues is handled in the
     * readQueue(BufferedReader, List, AircraftQueue) method.
     *
     * For an example of valid encoded map of loading aircraft,
     * see the provided saves/queues_basic.txt
     * and saves/queues_default.txt files.
     *
     * The contents read from the reader are invalid if any
     * of the following conditions are true:
     *
     * The first line read from the reader is null.
     * The number of colons (:) detected on the first line is more/fewer than expected.
     * The number of aircraft specified on the first line is
     * not an integer (i.e. cannot be
     * parsed by Integer.parseInt(String)).
     * The number of aircraft is greater than zero and the
     * second line read from the reader is null.
     * The number of aircraft specified on the first line is not equal to the number of
     * callsigns read on the second line.
     * For any callsign/loading time pair on the second line,
     * the number of colons detected
     * is not equal to one. For example, ABC123:5:9 is invalid.
     * A callsign listed on the second line does not correspond
     * to the callsign of any aircraft
     * contained in the list of aircraft given as a parameter.
     * Any ticksRemaining value on the second line is not
     * an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * Any ticksRemaining value on the second line is less than one (1).
     * @param reader - reader from which to load the map of loading aircraft
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @param loadingAircraft - empty map that aircraft and
     *                       their loading times will be added to
     * @throws IOException - if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader is
     * invalid according to the rules above
     */
    public static void readLoadingAircraft(BufferedReader reader, List<Aircraft> aircraft,
                                           Map<Aircraft, Integer> loadingAircraft)
            throws IOException, MalformedSaveException {
        String line = reader.readLine();
        String[] contentOfLine = line.split(":");
        if (contentOfLine.length != 2) {
            throw new MalformedSaveException();
        }
        int aircraftAmount;
        try {
            aircraftAmount = Integer.parseInt(contentOfLine[1]);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        if (aircraftAmount == 0) {
            return;
        }
        line = reader.readLine();
        try {
            contentOfLine = line.split(",");
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        if (contentOfLine.length != aircraftAmount) {
            throw new MalformedSaveException();
        }
        for (String string : contentOfLine) {
            contentOfLine = string.split(":");
            int ticks;
            try {
                ticks = Integer.parseInt(contentOfLine[1]);
            } catch (Exception e) {
                throw new MalformedSaveException();
            }
            boolean callsignExists = false;
            for (Aircraft aircraftEntry : aircraft) {
                if (aircraftEntry.getCallsign().equals(contentOfLine[0])) {
                    loadingAircraft.put(aircraftEntry, ticks);
                    callsignExists = true;
                }
            }
            if (!callsignExists) {
                throw new MalformedSaveException();
            }
        }


    }

    /**
     * Reads a terminal from the given string and reads
     * its gates from the given reader instance.
     * The format of the given string and the text
     * read from the reader should match the encoded
     * representation of a terminal, as described in Terminal.encode().
     *
     * For an example of valid encoded terminal with gates, see the
     * provided saves/terminalsWithGates_basic.txt
     * and saves/terminalsWithGates_default.txt files.
     *
     * The encoded terminal is invalid if any of the following conditions are true:
     *
     * The number of colons (:) detected on the first
     * line is more/fewer than expected.
     * The terminal type specified on the first line
     * is neither AirplaneTerminal nor HelicopterTerminal.
     * The terminal number is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     * The terminal number is less than one (1).
     * The number of gates in the terminal is not an integer.
     * The number of gates is less than zero or is greater
     * than Terminal.MAX_NUM_GATES.
     * A line containing an encoded gate was expected,
     * but EOF (end of file) was received (i.e.
     * BufferedReader.readLine() returns null).
     * Any of the conditions listed in the Javadoc for readGate(String, List) are true.
     * @param line - string containing the first line of the encoded terminal
     * @param reader - reader from which to load the gates of the terminal (subsequent lines)
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @return decoded terminal with its gates added
     * @throws IOException - if an IOException is
     *                       encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the given
     * string or the text read from the reader is invalid according to the rules above
     */
    public static Terminal readTerminal(String line,
                                        BufferedReader reader, List<Aircraft> aircraft)
            throws IOException, MalformedSaveException {
        String[] content = line.split(":");
        if (content.length != 4) {
            throw new MalformedSaveException();
        }
        int amountOfTerminals;
        int terminalNumber;
        boolean state;
        try {
            amountOfTerminals = Integer.parseInt(content[3]);
            terminalNumber = Integer.parseInt(content[1]);
            state = Boolean.parseBoolean(content[2]);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        Terminal terminal;
        if (content[0].equals("AirplaneTerminal")) {
            terminal = new AirplaneTerminal(terminalNumber);
        } else if (content[0].equals("HelicopterTerminal")) {
            terminal = new HelicopterTerminal(terminalNumber);
        } else {
            throw new MalformedSaveException();
        }
        int i = 0;
        while (i < amountOfTerminals) {
            line = reader.readLine();
            try {
                terminal.addGate(readGate(line, aircraft));
            } catch (NoSpaceException e) {
                throw new MalformedSaveException();
            }
            i++;
        }
        if (state) {
            terminal.declareEmergency();
        }
        return terminal;

    }

    /**
     * Reads a gate from its encoded representation in the given string.
     * The format of the string should match the encoded representation
     * of a gate, as described in Gate.encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * The number of colons (:) detected was more/fewer than expected.
     * The gate number is not an integer (i.e. cannot be parsed
     * by Integer.parseInt(String)).
     * The gate number is less than one (1).
     * The callsign of the aircraft parked at the gate is
     * not empty and the callsign does not
     * correspond to the callsign of any aircraft contained
     * in the list of aircraft given as a parameter.
     * @param line - string containing the encoded gate
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @return decoded gate instance
     * @throws MalformedSaveException - if the format of the given
     * string is invalid according to the rules above
     */
    public static Gate readGate(String line, List<Aircraft> aircraft)
            throws MalformedSaveException {
        String[] content = line.split(":");
        if (content.length != 2) {
            throw new MalformedSaveException();
        }
        int gateNumber;
        try {
            gateNumber = Integer.parseInt(content[0]);
        } catch (Exception e) {
            throw new MalformedSaveException();
        }
        Gate gate = new Gate(gateNumber);
        if (!(content[1].equals("empty"))) {
            for (Aircraft aircraftEntry : aircraft) {
                if (aircraftEntry.getCallsign().equals(content[1])) {
                    try {
                        gate.parkAircraft(aircraftEntry);
                    } catch (NoSpaceException e) {
                        throw new MalformedSaveException();
                    }
                }
            }
        }
        return gate;
    }
}
