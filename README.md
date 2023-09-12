## Agile Planner

### <ins>Summary</ins>

Agile Planner is an easy to use CLI that offers configurable scheduling capabilities to minimize the hassles as a college student as well as providing custom scripting language integration. Here's a brief summary of what this system offers: 

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
This version utlizes the State Pattern for performing context switches between each operation that it parses. A basic script is provided below:
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
#Sample script
include: __CURR_CONFIG__, __DEBUG__, __LOG__, __IMPORT__, __EXPORT__, __BUILD__, __STATS__

#Imports schedule data from prior session
import_schedule("data/week.jbin")

#Constructs class instances
val: string("3400")
c1: card("HW")

#Custom function to modify checklist instance with parameter references
func foo(cl, flag)
  cl.add_item("Step 1")
  cl.add_item("Step 2")
  cl.add_item("Step 3")
  cl.mark_item_by_id(0, flag)
  cl.mark_item_by_name("Step 2", flag)
  cl.mark_item_by_id(2, flag)

#Outputs the class data
println("length=", val.length(), ", int_val=", val.parse_int(), ", substring(1)=", val.sub_string(1))
println("")
println("card_name=", c1.get_title())

#Creates and modifies a checklist
status: bool(true)
my_cl: cl("List")
foo(my_cl, status)

#Outputs the checklist data to showcase Simple's data referencing system
println("checklist_id=", my_cl.id(), ", checklist_name=", my_cl.get_title(), ", checklist_percent=", my_cl.get_percent(), "%")
println(my_cl)
println("")

#Exports the schedule for later usage
export_schedule("fun_week.jbin")
```
In stark contrast to before, this language allows for more flexibility with function calls, accessing class methods and attributes, managing a Stack of variables (which are dynamic like Python), and much more. It is essentially Python but for Agile Planner.

### <ins>Logging Mechanics</ins>

Agile Planner currently offers logging mechanisms for two areas: System and Scripting.

#### System:
All core system actions, events, and exceptions are reported on a date/time perspective when they occurr. The goal is to allow the user to report any errors or issues similar to Epic Game's logging system. Here's an example below:
```
[28-08-2023] Log of all activities from current session: 

[01:07:42] Current session has begun...
[01:07:42] Reading Config: FILE=profile.cfg
[01:07:45] JBIN FILE CREATED
[01:07:45] WRITE(JBIN): FILE=data/update6.jbin
[01:07:45] ADD(TASK):  ID=0, NAME=a, HOURS=3, DUE_DATE=08-30-2023
[01:07:45] ADD(TASK):  ID=1, NAME=b, HOURS=2, DUE_DATE=08-29-2023
[01:07:45] ADD(TASK):  ID=2, NAME=c, HOURS=1, DUE_DATE=08-28-2023
[01:07:45] Scheduling has begun...
[01:07:45] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=7, HOURS_FILLED=1, TASK ADDED=2, OVERFLOW=false
[01:07:45] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=5, HOURS_FILLED=3, TASK ADDED=1, OVERFLOW=false
[01:07:45] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=2, HOURS_FILLED=6, TASK ADDED=0, OVERFLOW=false
[01:07:45] Scheduling has finished...
[01:07:45] Display Schedule: DAYS=1, NUM_TASKS=3, STDOUT=true
[01:07:45] JBIN FILE CREATED
[01:07:45] WRITE(JBIN): FILE=data/default.jbin
```

#### Scripting:

NOTE: The original framework was developed for the Functional Paradigm and is currently being ported over to the Object Oriented version. This is a work in progress below:
```
SCRIPT_LOG:
FUNC_CALLS: NAME=import_schedule, ARGS=[]
CARD_CREATED: ID=0, NAME="HW"
FUNC_SETUP: NAME= foo, PARAM=[cl, flag]
ATTR_CALL: VAR_NAME=val, NAME=length, ARGS[]
ATTR_CALL: VAR_NAME=val, NAME=parse_int, ARGS[]
ATTR_CALL: VAR_NAME=val, NAME=sub_string, ARGS[]
PRINTS: ARGS=["length=", "4", ", int_val=", "3400", ", substring(1)=", "400"]
FUNC_CALLS: NAME=println, ARGS=["length=", val.length(), ", int_val=", val.parse_int(), ", substring(1)=", val.sub_string(1)]
PRINTS: ARGS=[]
FUNC_CALLS: NAME=println, ARGS=[]
ATTR_CALL: VAR_NAME=c1, NAME=get_title, ARGS[]
PRINTS: ARGS=["card_name=", "HW"]
FUNC_CALLS: NAME=println, ARGS=["card_name=", c1.get_title()]
ATTR_CALL: VAR_NAME=cl, NAME=add_item, ARGS[]
ATTR_CALL: VAR_NAME=cl, NAME=add_item, ARGS[]
ATTR_CALL: VAR_NAME=cl, NAME=add_item, ARGS[]
ATTR_CALL: VAR_NAME=cl, NAME=mark_item_by_id, ARGS[0, flag]
ATTR_CALL: VAR_NAME=cl, NAME=mark_item_by_name, ARGS["Step 2", flag]
ATTR_CALL: VAR_NAME=cl, NAME=mark_item_by_id, ARGS[2, flag]
FUNC_CALLS: NAME=foo, ARGS=[my_cl, status]
ATTR_CALL: VAR_NAME=my_cl, NAME=id, ARGS[]
ATTR_CALL: VAR_NAME=my_cl, NAME=get_title, ARGS[]
ATTR_CALL: VAR_NAME=my_cl, NAME=get_percent, ARGS[]
PRINTS: ARGS=["checklist_id=", "0", ", checklist_name=", ""List"", ", checklist_percent=", "100", "%"]
FUNC_CALLS: NAME=println, ARGS=["checklist_id=", my_cl.id(), ", checklist_name=", my_cl.get_title(), ", checklist_percent=", my_cl.get_percent(), "%"]
PRINTS: ARGS=[]
FUNC_CALLS: NAME=println, ARGS=[]
PRINTS: ARGS=[]
FUNC_CALLS: NAME=println, ARGS=[]
```

### <ins>Java Binary Serialization</ins>

The goal of JBIN was to maintain data persistence while being able to easily store the data in a structured and efficient manner. Inspiration from JSON was used when developing JBIN.
```
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
Data is written in this format in order to allow reconstruction from top to bottom for an efficient time complexity. We start off with Label and CheckList, but Task is where we begin to store those Labels and CheckLists (hence the variable naming and index values). Cards have their name at the beginning followed by the Task and Label indices of what they store. I am currently seeking encyrption/decryption options to secure the data for the end user.
