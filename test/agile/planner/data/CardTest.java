//package agile.planner.data;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
///**
// * Tests Card functionality
// *
// * @author lucialanganey
// */
//public class CardTest {
//
//    /**
//     * Tests getLabel() functionality
//     */
//    @Test
//    public void testGetLabel() {
//        Card c1 = new Card("0");
//        Card c2 = new Card("1");
//        Card c3 = new Card("2");
//        assertEquals("0", c1.getLabel());
//        assertEquals("1", c2.getLabel());
//        assertEquals("2", c3.getLabel());
//    }
//
//    /**
//     * Tests setLabel() functionality
//     */
//    @Test
//    public void testSetLabel() {
//        Card c1 = new Card("0");
//        Card c2 = new Card("1");
//        Card c3 = new Card("2");
//        c1.setLabel("1");
//        c2.setLabel("2");
//        c3.setLabel("0");
//        assertEquals("1", c1.getLabel());
//        assertEquals("2", c2.getLabel());
//        assertEquals("0", c3.getLabel());
//    }
//
//    /**
//     * Tests getList() functionality
//     */
//    @Test
//    public void testGetList() {
//        Card c1 = new Card("0");
//        Task t1 = new Task(0, "A", 5, 2);
//        Task t2 = new Task(1, "B", 5, 2);
//        Task t3 = new Task(2, "C", 5, 2);
//        List<Task> list = new ArrayList<>();
//        list.add(t1);
//        list.add(t2);
//        list.add(t3);
//
//        c1.setCardTasks(list);
//        assertEquals(list, c1.getCardTasks());
//    }
//
//    /**
//     * Tests setList() functionality
//     */
//    @Test
//    public void testSetList() {
//        Card c1 = new Card("0");
//        Task t1 = new Task(0, "A", 5, 2);
//        Task t2 = new Task(1, "B", 5, 2);
//        Task t3 = new Task(2, "C", 5, 2);
//        List<Task> list = new ArrayList<>();
//        list.add(t1);
//        list.add(t2);
//        list.add(t3);
//
//        c1.setCardTasks(list);
//        assertEquals(list, c1.getCardTasks());
//    }
//
//    /**
//     * Tests addTask() functionality
//     */
//    @Test
//    public void testAddTask() {
//        Card c1 = new Card("0");
//        Task t1 = new Task(0, "A", 5, 2);
//        Task t2 = new Task(1, "B", 5, 2);
//        Task t3 = new Task(2, "C", 5, 2);
//
//        List<Task> list = new ArrayList<>();
//        list.add(t1);
//        list.add(t2);
//        list.add(t3);
//        c1.setCardTasks(list);
//
//        Task t4 = new Task(2, "C", 5, 2);
//        c1.addTask(t4);
//
//        assertEquals(list, c1.getCardTasks());
//    }
//
//    /**
//     * Tests removeTask() functionality
//     */
//    @Test
//    public void testRemoveTask() {
//        Card c1 = new Card("0");
//        Task t1 = new Task(0, "A", 5, 2);
//        Task t2 = new Task(1, "B", 5, 2);
//        Task t3 = new Task(2, "C", 5, 2);
//
//        List<Task> list = new ArrayList<>();
//        list.add(t1);
//        list.add(t2);
//        list.add(t3);
//        c1.setCardTasks(list);
//
//        c1.removeTask(1);
//
//        assertEquals(list, c1.getCardTasks());
//    }
//}