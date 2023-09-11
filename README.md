# Agile Planner

## Summary
Agile Planner is an easy to use CLI that offers configurable scheduling capabilities to minimize the hassles as a college student as well as providing custom scripting language integration. Here's a brief summary of what this system offers: 

* Designed and developed a highly adaptable scheduling system, accommodating diverse scheduling requirements.
* Integrated my own custom functional and object-oriented scripting languages for flexible user interaction and customization within the Agile Planner.
* Implemented an extensive logging mechanism to record system actions, enhancing transparency, and facilitating debugging.
* Employed Java binary serialization for efficient storage and retrieval of scheduling data, optimizing system performance.


## Scheduling Algorithms
My system offers two core scheduling algorithms at the moment for user convenience:
* Compact Scheduling
* Dynamic Scheduling

Compact Scheduling, deals with LJF (Largest Jobs First) scheduling, whereby tasks are sorted (by due_date and number of hours) and then are completed in bulk before progressing further. This is ideal for students that prioritize early completion over distribution.

Dynamic Scheduling, is a mixture of LRTF (Largest Remaining Time First) and Round Robin so as to equally distribute all the tasks within the system while ensuring the user has enough time to complete said tasks. I am currently working on optimization algorithms to provide clients more feedback as to how they can configure their schedule for better success.

## Scripting Languages
