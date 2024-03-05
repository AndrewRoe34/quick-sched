package com.agile.planner.data;//package agile.planner.data;
//
//import static org.junit.Assert.*;
//
//import java.util.Calendar;
//
//import agile.planner.util.CheckList;
//import org.junit.Test;
//
//import agile.planner.data.Task.SubTask;
//import agile.planner.util.Time;
//
///**
// * Tests Task core functionality
// *
// * @author Andrew Roe
// */
//public class TaskTest {

//    /**
//     * Tests AddSubTask() functionality
//     */
//    @Test
//    public void testAddSubTask() {
//        Task t1 = new Task(0, "CSC116", 4, 2);
//        assertNull(t1.addSubTask(0, false));
//        SubTask st1 = t1.addSubTask(2, false);
//        assertEquals(2, st1.getSubTaskHours());
//        assertNull(t1.addSubTask(3, false));
//        t1.addSubTask(2, false);
//        assertEquals(0, t1.getSubTotalHoursRemaining());
//    }
//
//    /**
//     * Tests getSubTotalRemaining() functionality
//     */
//    @Test
//    public void testGetSubTotalRemaining() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        SubTask st1 = t1.addSubTask(2, false);
//        assertEquals(2, st1.getSubTaskHours());
//        assertEquals(3, t1.getSubTotalHoursRemaining());
//        assertNull(t1.addSubTask(4, false));
//        t1.addSubTask(3, false);
//        assertEquals(0, t1.getSubTotalHoursRemaining());
//    }
//
//    /**
//     * Tests compareTo() functionality
//     */
//    @Test
//    public void testCompareTo() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        Task t2 = new Task(1, "CSC216", 1, 1);
//        Task t3 = new Task(2, "CSC316", 6, 2);
//        Task t4 = new Task(3, "Test", 1, 1);
//        Task t5 = new Task(0, "CSC116", 5, 2);
//        assertEquals(-1, t3.compareTo(t1));
//        assertEquals(0, t1.compareTo(t5));
//        assertEquals(1, t1.compareTo(t3));
//        assertEquals(-1, t2.compareTo(t3));
//        assertEquals(0, t2.compareTo(t4));
//        assertEquals(1, t1.compareTo(t2));
//    }
//
//    /**
//     * Tests toString() functionality
//     */
//    @Test
//    public void testToString() {
//        Task t1 = new Task(0, "A", 2, 0);
//        assertEquals("Task [name=A, total=2]", t1.toString());
//    }
//
//
//    /**
//     * Tests getters & setters for Task
//     */
//    @Test
//    public void testGettersAndSetters() {
//        Task t1 = new Task(0, "CSC316", 8, 3);
//        assertEquals("CSC316", t1.getName());
//        assertEquals(8, t1.getTotalHours());
//        Calendar date = Calendar.getInstance();
//        date.add(Calendar.DAY_OF_MONTH, 3);
//        date.set(Calendar.HOUR_OF_DAY, 0);
//        date.set(Calendar.MINUTE, 0);
//        date.set(Calendar.SECOND, 0);
//        date.set(Calendar.MILLISECOND, 0);
//        assertEquals(date, t1.getDueDate());
//    }
//
//    /**
//     * Tests SubTask functionality
//     */
//    @Test
//    public void testSubTask() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        SubTask st1 = t1.addSubTask(2, false);
//        assertEquals(t1, st1.getParentTask());
//        assertEquals(2, st1.getSubTaskHours());
//        assertEquals("SubTask [name=CSC116, hours=2]", st1.toString());
//        assertEquals(0, t1.getAverageNumHours());
//
//        Task t2 = new Task(1, "CSC216", 4, 1);
//        SubTask st2 = t2.addSubTask(3, false);
//
//        assertEquals(-1, st2.compareTo(st1));
//        assertEquals(0, st1.compareTo(st1));
//        assertEquals(1, st1.compareTo(st2));
//
//        assertEquals(1, t2.getSubTotalHoursRemaining());
//
//        t2.reset();
//        assertEquals(4, t2.getSubTotalHoursRemaining());
//        assertEquals(0, t2.getAverageNumHours());
//
//        assertFalse(st2.isOverflow());
//        assertTrue(t2.addSubTask(1, true).isOverflow());
//
//        Task t3 = new Task(2, "Z", 17, 1);
//        Calendar current = Time.getFormattedCalendarInstance(0);
//        t3.setAverageNumHours(current);
//        assertEquals(9, t3.getAverageNumHours());
//    }
//
//    /**
//     * Tests ID functionality
//     */
//    @Test
//    public void testId() {
//        Task t1 = new Task(0, "A", 5, 2);
//        Task t2 = new Task(1, "B", 5, 2);
//        Task t3 = new Task(2, "C", 5, 2);
//        assertEquals(0, t1.getId());
//        assertEquals(1, t2.getId());
//        assertEquals(2, t3.getId());
//    }
//
//    /**
//     * Tests equals() functionality
//     */
//    @Test
//    public void testEquals() {
//        Task t1 = new Task(0, "A", 5, 2);
//        Task t2 = new Task(1, "B", 5, 2);
//        Task t3 = new Task(1, "B", 5, 2);
//        assertNotEquals(t1, t2);
//        assertEquals(t2, t3);
//    }
//
//    /**
//     * Tests CheckList functionality
//     */
//    @Test
//    public void testTaskCheckList() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        CheckList cl = t1.addCheckList("Hello World", 0);
//        assertEquals("Hello World", cl.getName());
//        assertEquals(0, cl.size());
//    }
//
//    /**
//     * Tests CheckList setName() functionality
//     */
//    @Test
//    public void testTaskCheckListSetName() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        CheckList cl = t1.addCheckList("Hello World", 0);
//        assertEquals("Hello World", cl.getName());
//    }
//
//    /**
//     * Tests CheckList addItem() functionality
//     */
//   @Test
//   public void testTaskCheckListAddItem() {
//       Task t1 = new Task(0, "CSC116", 5, 2);
//       t1.addCheckList("Hello World", 0);
//       t1.addItem("Work to do");
//       assertEquals("Work to do", t1.getItem(0).getDescription());
//   }
//
//    /**
//     * Tests CheckList removeItem() functionality
//     */
//   @Test
//   public void testTaskCheckListRemoveItem() {
//       Task t1 = new Task(0, "CSC116", 5, 2);
//       t1.addCheckList("Hello World", 0);
//       t1.addItem("A");
//       t1.addItem("B");
//       t1.addItem("C");
//       CheckList.Item i1 = t1.removeItem(1);
//       assertEquals("B", i1.getDescription());
//       CheckList.Item i2 = t1.removeItem(0);
//       assertEquals("A", i2.getDescription());
//       CheckList.Item i3 = t1.removeItem(0);
//       assertEquals("C", i3.getDescription());
//
//       try {
//           t1.removeItem(2);
//           fail();
//       } catch(IllegalArgumentException e) {
//           assertEquals("Invalid index for checklist", e.getMessage());
//       }
//   }
//
//    /**
//     * Tests CheckList shiftItem() functionality
//     */
//   @Test
//   public void testTaskCheckListShiftItem() {
//       Task t1 = new Task(0, "CSC116", 5, 2);
//       assertFalse(t1.shiftItem(0, 1));
//       t1.addCheckList("Hello World", 0);
//       t1.addItem("A");
//       CheckList.Item i1 = t1.getItem(0);
//       t1.addItem("B");
//       CheckList.Item i2 = t1.getItem(1);
//       t1.addItem("C");
//       CheckList.Item i3 = t1.getItem(2);
//       t1.shiftItem(0, 1);
//       assertEquals(i1, t1.getItem(1));
//       assertEquals(i2, t1.getItem(0));
//       assertEquals(i3, t1.getItem(2));
//       t1.shiftItem(2, 0);
//       assertEquals(i3, t1.getItem(0));
//       assertEquals(i1, t1.getItem(2));
//       assertEquals(i2, t1.getItem(1));
//
//       try {
//           t1.shiftItem(0, 3);
//           fail();
//       } catch(IllegalArgumentException e) {
//           assertEquals("Invalid index for checklist", e.getMessage());
//       }
//   }
//
//    /**
//     * Tests CheckList markItem() functionality
//     */
//    @Test
//    public void testTaskCheckListMarkItem() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        assertFalse(t1.markItem(0, true));
//        t1.addCheckList("Hello World", 0);
//        t1.addItem("A");
//        assertFalse(t1.getItem(0).isComplete());
//        t1.addItem("B");
//        assertFalse(t1.getItem(1).isComplete());
//        t1.addItem("C");
//        assertFalse(t1.getItem(2).isComplete());
//        t1.markItem(0, true);
//        assertTrue(t1.getItem(0).isComplete());
//        t1.markItem(2, true);
//        assertTrue(t1.getItem(2).isComplete());
//        t1.markItem(1, true);
//        assertTrue(t1.getItem(1).isComplete());
//
//        t1.markItem(0,false);
//        assertFalse(t1.getItem(0).isComplete());
//        t1.markItem(1,false);
//        assertFalse(t1.getItem(1).isComplete());
//        t1.markItem(2,false);
//        assertFalse(t1.getItem(2).isComplete());
//
//        try {
//            t1.markItem(3, true);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//    }
//
//    /**
//     * Tests CheckList getItem() functionality
//     */
//    @Test
//    public void testTaskCheckListGetItem() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        assertNull(t1.getItem(1));
//        t1.addCheckList("Hello World", 0);
//        t1.addItem("A");
//        assertEquals("A", t1.getItem(0).getDescription());
//        assertFalse(t1.getItem(0).isComplete());
//
//        try {
//            t1.getItem(1);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//    }
//
//    /**
//     * Tests CheckList toString() functionality
//     */
//    @Test
//    public void testTaskCheckListString() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        assertNull(t1.getStringCheckList());
//        t1.addCheckList("Hello World", 0);
//        t1.addItem("A");
//        t1.addItem("B");
//        t1.addItem("C");
//        t1.markItem(0, true);
//        t1.markItem(2, true);
//        assertEquals("Hello World [66%]:\n" +
//                "* A✅\n" +
//                "* B\n" +
//                "* C✅\n", t1.getStringCheckList());
//    }
//
//    /**
//     * Tests CheckList reset() functionality
//     */
//    @Test
//    public void testTaskCheckListReset() {
//        Task t1 = new Task(0, "CSC116", 5, 2);
//        assertFalse(t1.resetCheckList());
//        CheckList cl = t1.addCheckList("Hello World", 0);
//        assertEquals(0, cl.size());
//        t1.addItem("A");
//        t1.addItem("B");
//        t1.addItem("C");
//        assertEquals(3, cl.size());
//        t1.resetCheckList();
//        assertEquals(0, cl.size());
//    }
//}