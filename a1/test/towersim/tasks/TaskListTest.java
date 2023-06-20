package towersim.tasks;

// add any required imports here

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static towersim.tasks.TaskType.*;

public class TaskListTest {

    private TaskList tasks;

    @Before
    public void setUp(){
        List<Task> listOfTasks = new ArrayList<>();
        listOfTasks.add(new Task(TAKEOFF));
        listOfTasks.add(new Task(AWAY,40));
        listOfTasks.add(new Task(LAND));
        listOfTasks.add(new Task(WAIT));
        listOfTasks.add(new Task(LOAD,65));
        tasks = new TaskList(listOfTasks);
    }

    @Test
    public void testGetCurrentTask(){
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
    }

    @Test
    public void testGetNextTask(){
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
        assertEquals(AWAY,tasks.getNextTask().getType());
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
    }

    @Test
    public void testMoveToNextTask(){
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
        tasks.moveToNextTask();
        assertEquals(AWAY,tasks.getCurrentTask().getType());
    }

    @Test
    public void testGetLoadPercent(){
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
        tasks.moveToNextTask();
        assertEquals(AWAY,tasks.getCurrentTask().getType());
        assertEquals(40,tasks.getCurrentTask().getLoadPercent());
    }

    @Test
    public void testLoop(){
        testEnd();
        tasks.moveToNextTask();
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
    }

    @Test
    public void testEnd(){
        assertEquals(TAKEOFF,tasks.getCurrentTask().getType());
        tasks.moveToNextTask();
        tasks.moveToNextTask();
        assertEquals(LAND,tasks.getCurrentTask().getType());
        tasks.moveToNextTask();
        tasks.moveToNextTask();
        assertEquals(LOAD,tasks.getCurrentTask().getType());
    }

    @Test
    public void testEndGetNext() {
        testEnd();
        assertEquals(TAKEOFF,tasks.getNextTask().getType());
        assertEquals(LOAD,tasks.getCurrentTask().getType());
    }

    @Test
    public void testToString() {
        assertEquals("TaskList currently on TAKEOFF [1/5]",tasks.toString());
    }

    @Test
    public void testToString2() {
        tasks.moveToNextTask();
        assertEquals("TaskList currently on AWAY [2/5]", tasks.toString());
    }

    @Test
    public void testLoopToString() {
        testLoop();
        testToString();
    }

}
