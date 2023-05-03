package agile.planner.util;

import java.util.ArrayList;
import java.util.List;

/**
 * CheckList for each individual task assigned to a day
 *
 * @param <E> generic object being linked to Item
 * @author Andrew Roe
 */
public class CheckList<E> {

    /** Name for CheckList */
    private String name;
    /** List of all Items for a task */
    private List<Item<E>> list;
    /** Number of Items completed */
    private int completed;

    /**
     * Primary instance for a CheckList
     *
     * @param name title of the CheckList
     */
    public CheckList(String name) {
        setName(name);
        list = new ArrayList<>();
    }

    /**
     * Sets the title for the CheckList
     *
     * @param name title of the CheckList
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the title for the CheckList
     *
     * @return title for CheckList
     */
    public String getName() {
        return name;
    }

    /**
     * Helper method for verifying the index of the list
     *
     * @param idx index to be verified
     * @return same index if valid (-1 if invalid)
     */
    private int verifyIndex(int idx) {
        if(idx < 0 || idx >= list.size()) {
            return -1;
        }
        return idx;
    }

    /**
     * Adds a new Item to the CheckList
     *
     * @param obj generic type being linked
     * @param description description for the Item
     * @return boolean value for successful add of Item
     */
    public boolean addItem(E obj, String description) {
        return list.add(new Item<E>(obj, description));
    }

    /**
     * Removes the Item from the CheckList
     *
     * @param idx index of CheckList
     * @return removed Item
     */
    public Item<E> removeItem(int idx) {
        if(verifyIndex(idx) == -1) {
            throw new IllegalArgumentException("Invalid index for checklist");
        }
        return list.remove(idx);
    }

    /**
     * Shifts the Item in the CheckList
     *
     * @param idx index of Item
     * @param shiftIdx new index for Item
     */
    public void shiftItem(int idx, int shiftIdx) {
        if(verifyIndex(idx) == -1 || verifyIndex(shiftIdx) == -1) {
            throw new IllegalArgumentException("Invalid index for checklist");
        }
        list.add(shiftIdx, list.remove(idx));
    }

    /**
     * Gets the Item from the CheckList
     *
     * @param idx index of Item
     * @return Item at specified index
     */
    public Item<E> getItem(int idx) {
        if(verifyIndex(idx) == -1) {
            throw new IllegalArgumentException("Invalid index for checklist");
        }
        return list.get(idx);
    }

    /**
     * Marks an Item as complete or incomplete
     *
     * @param idx index of Item
     * @param flag completion status flag
     */
    public void markItem(int idx, boolean flag) {
        if(verifyIndex(idx) == -1) {
            throw new IllegalArgumentException("Invalid index for checklist");
        }
        Item<E> i1 = getItem(idx);
        if(flag && !i1.isComplete()) {
            completed++;
        } else if(!flag && i1.isComplete()) {
            completed--;
        }
        i1.setCompletionStatus(flag);
    }

    /**
     * Resets the CheckList
     */
    public void resetCheckList() {
        this.list = new ArrayList<>();
    }

    /**
     * Size of the CheckList
     *
     * @return size of CheckList
     */
    public int size() {
        return list.size();
    }

    /**
     * Gets the completion percentage of CheckList
     *
     * @return completion percentage
     */
    public int getPercentage() {
        return list.size() == 0 ? 0 : (int) (completed * 1.0f / list.size() * 100);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + " [" + getPercentage() + "%]:\n");
        for(Item<E> i : list) {
            sb.append("* ").append(i.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Edits an Item from the CheckList
     *
     * @param description newly updated description
     */
    public void editItem(String description) {
        //TODO
    }

    /**
     * Represents a single Item in a CheckList
     *
     * @param <E> generic object being linked to Item
     * @author Andrew Roe
     */
    public static class Item<E> {

        /** Generic object being linked to Item (or null if not needed) */
        private E obj;
        /** Description for Item */
        private String description;
        /** Completion status of Item */
        private boolean status;

        /**
         * Primary constructor for Item
         *
         * @param obj generic object being linked to Item (or null if not needed)
         * @param description description for Item
         */
        private Item(E obj, String description) {
            setObj(obj);
            setDescription(description);
        }

        /**
         * Sets the generic object
         *
         * @param obj generic object (or null)
         */
        private void setObj(E obj) {
            this.obj = obj;
        }

        /**
         * Gets the generic object (or null)
         *
         * @return generic object (or null)
         */
        public E getObj() {
            return obj;
        }

        /**
         * Sets the description for Item
         *
         * @param description description for Item
         */
        private void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the description for Item
         *
         * @return description for Item
         */
        public String getDescription() {
            return description;
        }

        /**
         * Marks Item as incomplete
         *
         * @param flag boolean value for completion
         */
        private void setCompletionStatus(boolean flag) {
            status = flag;
        }

        /**
         * Gets the completion status of Item
         *
         * @return completion status
         */
        public boolean isComplete() {
            return status;
        }

        @Override
        public String toString() {
            if(status) {
                return description + "✅";
            }
            return description;
        }

    }
}
