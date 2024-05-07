package com.planner.schedule;

import com.planner.models.Task;

import java.util.Comparator;

/**
 * Comparator utilized for CompactScheduling to place greater emphasis on shorter tasks
 *
 * @author Andrew Roe
 */
public class ShortOrderComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        long timeDiff = o1.getDueDate().compareTo(o2.getDueDate());
        if(timeDiff < 0 || timeDiff == 0 && o1.getTotalHours() < o2.getTotalHours()) {
            return -1;
        } else if(timeDiff > 0 || o1.getTotalHours() > o2.getTotalHours()) {
            return 1;
        } else {
            return 0;
        }
    }
}
