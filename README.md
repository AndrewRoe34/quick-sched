## Agile Planner

### <ins>Summary</ins>

Agile Planner is an easy-to-use CLI that offers configurable scheduling capabilities to minimize the hassles as a college student as well as providing custom scripting language integration. Here's a brief summary of what this system offers: 

* Designed and developed a highly adaptable scheduling system, accommodating diverse scheduling requirements.
* Integrated my own custom functional and object-oriented scripting languages for flexible user interaction and customization within the Agile Planner.
* Implemented an extensive logging mechanism to record system actions, enhancing transparency, and facilitating debugging.
* Employed Java binary serialization for efficient storage and retrieval of scheduling data, optimizing system performance.


### <ins>Scheduling Algorithms</ins>

My system offers two core scheduling algorithms at the moment for user convenience:
* Compact Scheduling
* Dynamic Scheduling

Compact Scheduling, deals with LJF (Largest Jobs First) scheduling, whereby tasks are sorted (by due_date and number of hours) and then are completed in bulk before progressing further. This is ideal for students that prioritize early completion over distribution.

Dynamic Scheduling, is a mixture of LRTF (Largest Remaining Time First) and Round Robin so as to equally distribute all the tasks within the system while ensuring the user has enough time to complete said tasks. I am currently working on optimization algorithms to provide clients more feedback as to how they can configure their schedule for better success.

### <ins>Custom Scripting Languages</ins>

The goal of the scripting language was to offer the user more efficiency in terms of interacting with the system in comparison to the current CLI (Command Line Interface). At the moment, there are two working versions at hand (though, both utilize completely different paradigms).

#### Functional Paradigm:
This version utilizes the State Pattern for performing context switches between each operation that it parses. A basic script is provided below:
```
#Preprocessor setup
START:
  __CURR_CONFIG__
  __DEBUG__
  __LOG__
  __IMPORT__
  __EXPORT__
  __BUILD__
END:

#Creates a Task (<name>, <hours>, <due_date>)
task: a, 3, 2
task: b, 2, 1
task: c, 1, 0

#Creates a Card (<name>)
card: homework

#Calls functions on the data, which are referenced by providing an '_' in front of the type to access the top of the Stack
#In the first example, the most recent task and card are used in the function 
add: _task, _card
#In the second example, we provide a number to access a specific index of the Stack
add: _task 1, _card

#Exports the scheduling data to a jbin file (Java Binary Serialization that I developed)
export: update6.jbin
```
While this iteration was very simple since it utilized dynamic memory and required no variable naming, it resulted in increased complexity with edit operations and recursive routines. As a result, I ended development with this version and moved on to my next iteration.

#### Object Oriented Paradigm:

This version of my scripting language required a complete overhaul in design and approach. I opted for a Parser class to manage categorizing data and returning that to my ScriptFSM, which would then interpret the parsed data. The syntax of my language is very similar to Python for ease of access to more people. Here's a code snippet below:

```
include: __CURR_CONFIG__, __LOG__, __IMPORT__, __BUILD__

# Setups the Checklist
func setup_cl(cl1)
  in: input_word("Add an Item(y/n) ")
  if(in.==("y"))
    in: input_line("Description -> ")
    cl1.add_item(in)
    setup_cl(cl1)

# Sets up the Task
func setup_task(t1)
  in: input_word("Create a Checklist(y/n) ")
  if(in.==("y"))
    in: input_line("Title -> ")
    _cl: checklist(in)
    t1.add(_cl)
    setup_cl(_cl)

# Sets up the week's schedule
func setup_schedule()
  in: input_word("Create a Task(y/n) ")
  if(in.==("y"))
    _name: input_line("Name -> ")
    _hours: input_int("Hours -> ")
    _due: input_int("Due -> ")
    _task: task(_name, _hours, _due)
    setup_task(_task)
    setup_schedule()

setup_schedule()

# Adds all tasks to manager and builds the schedule
add_all_tasks()
build()

# Exports schedule to Google Calendar
google_export()
```
In stark contrast to before, this language allows for more flexibility with function calls, accessing class methods and attributes, managing a Stack of variables (which are dynamic like Python), and much more. It is essentially Python but for Agile Planner.

It also has the functionality needed to write non-scheduling related scripts as demonstrated below:
```
include: __CURR_CONFIG__, __LOG__

# Outputs all binary codes of a specified length
str: ""
func binary(bin, x)
  if(x.==(0))
    str.concat(bin, "\n")
    return
  x.--()
  binary(bin.add("0"), x)
  binary(bin.add("1"), x)

print("Enter number: ")
x: input_int()
binary("", x)

write_file("data/bin.txt", str)
```
```
include: __CURR_CONFIG__, __LOG__

# prints out a line of '*'
func line(y)
  if(y.==(0))
    return
  print("*")
  line(y.--())

# determines number of lines and length for each
func base(x)
  if(x.==(0))
    return
  line(x)
  println()
  base(x.--())

print("Enter size of triangle: ")
num: input_int()
base(num)
```
### <ins>Logging Mechanics</ins>

Agile Planner currently offers logging mechanisms for two areas: System and Scripting.

#### System:
All core system actions, events, and exceptions are reported on a date/time perspective when they occur. The goal is to allow the user to report any errors or issues similar to Epic Game's logging system. Here's an example below:
```
[07-03-2024] Log of all activities from current session: 

[15:46:16] [INFO] CURRENT SESSION HAS BEGUN...
[15:46:16] [INFO] Reading Config: FILE=profile.cfg
[15:46:16] [INFO] USERNAME=null ,EMAIL=null ,WEEK_HOURS=[8, 8, 8, 8, 8, 8, 8] ,MAX_DAYS=14 ,ARCHIVE_DAYS=14 ,PRIORITY=false ,OVERFLOW=true ,FIT_SCHEDULE=false ,SCHEDULE_ALGO=1 ,MIN_HOURS=1
[15:46:17] [INFO] GOOGLE CALENDAR AUTHORIZATION PROCESSED...
[15:46:21] [INFO] SCRIPT_NAME=C:\Users\Student\Desktop\agile-planner\data\google.smpl , SCRIPT INSTANCE HAS BEGUN...
[15:47:03] [INFO] ADD(TASK):  ID=0, NAME=Study Math Test 1, HOURS=6, DUE_DATE=03-10-2024
[15:47:03] [INFO] ADD(TASK):  ID=0, NAME=Study for CH Test 2, HOURS=12, DUE_DATE=03-13-2024
[15:47:03] [INFO] Scheduling has begun...
[15:47:03] [INFO] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=2, HOURS_FILLED=6, TASK ADDED=0, OVERFLOW=false
[15:47:03] [INFO] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=0, HOURS_FILLED=8, TASK ADDED=0, OVERFLOW=false
[15:47:03] [INFO] DAY_ID=1, CAPACITY=8, HOURS_REMAINING=0, HOURS_FILLED=8, TASK ADDED=0, OVERFLOW=false
[15:47:03] [INFO] DAY_ID=2, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[15:47:03] [INFO] Scheduling has finished...
[15:47:03] [INFO] 0 TASKS REMOVED FROM GOOGLE CALENDAR...
[15:47:04] [INFO] SCHEDULE EXPORTED TO GOOGLE CALENDAR...
[15:47:04] [INFO] Display Schedule: DAYS=3, NUM_TASKS=2, STDOUT=true
[15:47:04] [INFO] SCRIPT_NAME=C:\Users\Student\Desktop\agile-planner\data\google.smpl , SCRIPT INSTANCE HAS ENDED...
```

#### Scripting:
The same is done with the Scripting Log. It essentially provides an extensive stacktrace as to all operations that occur while the script is being executed.
```
[07-03-2024] Log of all activities from current session: 

[15:46:21] PREPROC_ATTR: DEF_CONFIG=false, IMPORT=true, EXPORT=false, LOG=true, BUILD=true, STATS=false
[15:46:21] FUNC_SETUP: NAME= setup_cl, PARAM=[]
[15:46:21] FUNC_SETUP: NAME= setup_task, PARAM=[]
[15:46:21] FUNC_SETUP: NAME= setup_schedule, PARAM=[]
[15:46:23] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[15:46:23] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[15:46:42] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[15:46:42] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[15:46:42] FUNC_CALLS: NAME=setup_task, ARGS=[_task]
[15:46:44] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[15:46:44] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[15:46:58] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[15:46:58] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[15:46:58] FUNC_CALLS: NAME=setup_task, ARGS=[_task]
[15:47:00] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[15:47:00] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[15:47:00] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[15:47:00] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[15:47:00] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[15:47:03] FUNC_CALLS: NAME=inject_code, ARGS=[]
[15:47:03] FUNC_CALLS: NAME=add_all_tasks, ARGS=[]
[15:47:03] FUNC_CALLS: NAME=build, ARGS=[]
[15:47:04] FUNC_CALLS: NAME=google_export, ARGS=[]
```

### <ins>Java Binary Serialization</ins>

The goal of JBIN was to maintain data persistence while being able to easily store the data in a structured and efficient manner. Inspiration from JSON was used when developing JBIN.
```
11-12-2023

LABEL {
  LOL, 3
  Party, 4
  math, 3
  lol, 8
}

CHECKLIST {
  To Do, Item 1, Item 2
}

TASK {
  Read, 4, 4, L0
  Write, 2, 2, CL0
  suffer, 5, 5, L2, L3
  based, 3, 3, L2, L3
}

CARD {
  HW, T0, T1, L0, L1
  MA, T2, T3, L2, L3
  SCIENCE, T2, T3, L2, L3
}
```
Data is written in this format in order to allow reconstruction from top to bottom for an efficient time complexity. We start off with Label and CheckList, but Task is where we begin to store those Labels and CheckLists (hence the variable naming and index values). Cards have their name at the beginning followed by the Task and Label indices of what they store. I am currently seeking encryption/decryption options to secure the data for the end user.
