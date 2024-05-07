package com.planner.util;//package agile.planner.util;
//import org.junit.Test;
//
//
//import static org.junit.Assert.*;
//
///**
// * Tests Task core functionality
// *
// * @author Lucia Langaney
// */
//public class CheckListTest {
//
//    /**
//     * Tests setName() functionality
//     */
//    @Test
//    public void setName() {
//        CheckList checklist1 = new CheckList("0", 0);
//        CheckList checklist2 = new CheckList("1", 1);
//        CheckList checklist3 = new CheckList("2", 2);
//
//        checklist1.setName("1");
//        checklist2.setName("2");
//        checklist3.setName("0");
//
//        assertEquals("1", checklist1.getName());
//        assertEquals("2", checklist2.getName());
//        assertEquals("0", checklist3.getName());
//    }
//
//    /**
//     * Tests getName() functionality
//     */
//    @Test
//    public void getName() {
//        CheckList checklist1 = new CheckList("0", 0);
//        CheckList checklist2 = new CheckList("1", 1);
//        CheckList checklist3 = new CheckList("2", 2);
//        assertEquals("0", checklist1.getName());
//        assertEquals("1", checklist2.getName());
//        assertEquals("2", checklist3.getName());
//    }
//
//    /**
//     * Tests getChecklistId() functionality
//     */
//    @Test
//    public void getChecklistId() {
//        CheckList checklist1 = new CheckList("0", 0);
//        CheckList checklist2 = new CheckList("1", 1);
//        CheckList checklist3 = new CheckList("2", 2);
//        assertEquals(0, checklist1.getChecklistId());
//        assertEquals(1, checklist2.getChecklistId());
//        assertEquals(2, checklist3.getChecklistId());
//    }
//
//    /**
//     * Tests addItem() functionality
//     */
//    @Test
//    public void addItem() {
//        CheckList checklist1 = new CheckList("0", 0);
//        assertTrue(checklist1.addItem("Item #1"));
//
//        CheckList checklist2 = new CheckList("1", 1);
//        assertTrue(checklist2.addItem("Item #2"));
//    }
//
//    /**
//     * Tests removeItem() functionality
//     */
//    @Test
//    public void removeItem() {
//        CheckList checklist1 = new CheckList("0", 0);
//        checklist1.addItem("Item #1");
//        CheckList checklist2 = new CheckList("1", 1);
//        checklist1.addItem("Item #2");
//        checklist1.removeItem(1);
//
//        try {
//            checklist1.removeItem(-1);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//    }
//
//    /**
//     * Tests shiftItem() functionality
//     */
//    @Test
//    public void shiftItem() {
//        CheckList checkList1 = new CheckList("0", 0);
//        checkList1.addItem("A");
//        CheckList.Item i1 = checkList1.getItem(0);
//        checkList1.addItem("B");
//        CheckList.Item i2 = checkList1.getItem(1);
//        checkList1.addItem("C");
//        CheckList.Item i3 = checkList1.getItem(2);
//        checkList1.shiftItem(0, 1);
//        assertEquals(i1, checkList1.getItem(1));
//        assertEquals(i2, checkList1.getItem(0));
//        assertEquals(i3, checkList1.getItem(2));
//        checkList1.shiftItem(2, 0);
//        assertEquals(i3, checkList1.getItem(0));
//        assertEquals(i1, checkList1.getItem(2));
//        assertEquals(i2, checkList1.getItem(1));
//
//        try {
//            checkList1.shiftItem(-1, -1);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//    }
//
//    /**
//     * Tests getItem() functionality
//     */
//    @Test
//    public void getItem() {
//        CheckList checklist1 = new CheckList("0", 0);
//        checklist1.addItem("Item #1");
//
//        try {
//            checklist1.getItem(-1);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//    }
//
//    /**
//     * Tests markItem() functionality
//     */
//    @Test
//    public void markItem() {
//        CheckList checklist1 = new CheckList("A", 0);
//        checklist1.addItem("Item #1");
//        checklist1.markItem(0, true);
//        assertTrue((checklist1.getItem(0)).isComplete());
//        checklist1.addItem("Item #2");
//        checklist1.markItem(1, false);
//        assertFalse((checklist1.getItem(1)).isComplete());
//
//        try {
//            checklist1.markItem(-1, true);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//
//        try {
//            checklist1.markItem(5, true);
//            fail();
//        } catch(IllegalArgumentException e) {
//            assertEquals("Invalid index for checklist", e.getMessage());
//        }
//    }
//
//    /**
//     * Tests resetCheckList() functionality
//     */
//    @Test
//    public void resetCheckList() {
//        CheckList checklist1 = new CheckList("A", 0);
//        checklist1.addItem("Item #1");
//        checklist1.addItem("Item #2");
//        checklist1.addItem("Item #3");
//        checklist1.addItem("Item #4");
//        assertEquals(4, checklist1.size());
//        checklist1.resetCheckList();
//        assertEquals(0, checklist1.size());
//    }
//
//    /**
//     * Tests size() functionality
//     */
//    @Test
//    public void size() {
//        CheckList checklist1 = new CheckList("A", 0);
//        checklist1.addItem("Item #1");
//        assertEquals(1, checklist1.size());
//        checklist1.addItem("Item #2");
//        assertEquals(2, checklist1.size());
//        checklist1.removeItem(1);
//        assertEquals(1, checklist1.size());
//        checklist1.removeItem(0);
//        assertEquals(0, checklist1.size());
//    }
//
//    /**
//     * Tests getPercentage() functionality
//     */
//    @Test
//    public void getPercentage() {
//        CheckList checklist1 = new CheckList("A", 0);
//        checklist1.addItem("Item #1");
//        checklist1.addItem("Item #2");
//        assertEquals(0, checklist1.getPercentage());
//        checklist1.markItem(1, true);
//        assertEquals(50, checklist1.getPercentage());
//        checklist1.markItem(0, true);
//        assertEquals(100, checklist1.getPercentage());
//    }
//
//    /**
//     * Tests testToString() functionality
//     */
//    @Test
//    public void testToString() {
//        CheckList checklist1 = new CheckList("A", 0);
//        checklist1.addItem("Item #1");
//        checklist1.addItem("Item #2");
//
//        checklist1.markItem(1, true);
//        assertEquals("A [50%]:\n* Item #1\n* Item #2✅\n",checklist1.toString());
//    }
//
//    /**
//     * Tests editItem() functionality
//     */
//    @Test
//    public void editItem() {
//        CheckList checklist1 = new CheckList("A", 0);
//        checklist1.addItem("Item #1");
//        checklist1.addItem("Item #2");
//
//        checklist1.editItem(0,"Item #0");
//        assertEquals("Item #0", checklist1.getItem(0).getDescription());
//    }
//
//    /**
//     * Tests getId() functionality
//     */
//    @Test
//    public void getId() {
//        CheckList checklist1 = new CheckList("A", 0);
//        assertEquals(0, checklist1.getId());
//    }
//}