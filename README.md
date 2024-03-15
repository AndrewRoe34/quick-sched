## Agile Planner v0.3.9

### Getting Started

To start off the program, we will run the following:
```bash
java -jar agile-planner-0.3.9-SNAPSHOT.jar
```
Once this has been entered, you will be prompted with the name of the program followed by a request to “Please open the following address in your browser: …”

Once you head over to the webpage, which should have opened up automatically, you’ll be greeted with a Google Authorization prompt. If you allow access, you’ll then be able to continue on with the app and get started!

### An Introduction to Simple Script

Simple is an Object-Oriented interpreted programming language that functions similar to Python. Its syntax can be summarized as the following:
```
include: __CURR_CONFIG__, __LOG__, __IMPORT__, __BUILD__

google_import()
import_jbin(“data/jbin/foo.jbin”)

func foo(flag)
  if(flag)
    println(“Flag is “, flag)

my_task: task(“Math”, 4, 2)
foo(true)

add_all_tasks()
build()
google_export()
```
We will cover each of the core components along with a list of built-in functions and data types/methods.

#### Include Flags:
This part of the script tells Simple what to include when it’s interpreting your code. This can range from type of configuration settings to logging out the stack trace. Here’s all the possible options:
```
__CURR_CONFIG__  (Uses the current config settings)
__DEF_CONFIG__   (Uses the default config settings)
__LOG__	         (Stores a log of the stack trace)
__IMPORT__	 (Allows importing your schedule)
__EXPORT__       (Allows exporting your schedule)
__BUILD__        (Allows building your schedule)
```

#### Variables:
Variables, similar to Python, are dynamic in nature and allow switching between types as often as you please. Declaration and instantiation are done simultaneously in Simple and cannot be separated. Below is the syntax:
```
<var_name>: <data_type>(<arg1>, <arg2>, ...>)
```
And the following are examples of how you could create an instance of each built-in type:
```
c1: card(“Calc 3”)
my_task: task(“Math HW”, 4, 2)
var: label(“HW”, 2)
_cl: checklist(“ToDo”)
x: 34
str: “Hello World”
flag: false
```
Whenever a variable is attempting to hold an object instance, it must attach the ‘:’ to the end of its name. Thankfully, memory is dynamic here and allows for switching of types with storage like the following:
```
c1: card(“Calc 3”)
c1: “This is some string”
c1: 34
# Will print out the value of ‘34’
println(c1)
```

#### Built-In Functions:
Simple script provides an extensive list of built-in functions that solve a wide variety of problems. We will cover some of the more important ones and a link will be included to the entire list in Appendix A.
These two functions are for reading and writing scheduling data via the JBin format:
```
import_schedule(<filename : String>)
export_schedule(<filename : String>)
```
The google import/export functions deal with reading and writing Calendar data back and forth. The import function will display all Agile Planner tasks with a JSON format while the export function will schedule the tasks with timeslots according to the generated schedule (note: scheduled tasks are printed with links to their Calendar counterparts):
```
google_import()
google_export()
```
These two functions are necessary when attempting to build a schedule with newly created tasks. The add_all_tasks() function deals with adding all task variables (whether past or current) to the schedule manager and the build() function creates and outputs a schedule:
```
add_all_tasks()
build()
```
And finally, we arrive at one of the more interesting functions available with Simple. This operation allows the user to inject code while the script is being interpreted! Simply include whatever function calls, variable declarations, etc. as you’d like (making sure to close off with ```__END__```). Note: You cannot use inject_code() inside of custom functions or use it to create a function:
```
inject_code()
```

#### Custom Functions:
Simple’s custom functions are very similar to Python and allow for repeated efficiency and offer recursive capabilities. They follow this format here:
```
func <func_name>(<arg1>, <arg2>, . . .>)
```
A sample script is provided below as to what is possible outside of scheduling:
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

#### Control Structure:
If conditions work a bit differently compared to Python in that arguments are comma delimited (this is being changed with the next version of Simple script). Here is a typical example below:
```
if(x.==(0))
  print(“Play games with friends”)
elif(x.==(1))
  print(“Watch latest Marvel movie”)
else
  print(“Study for test”)
...
```

#### Object Methods:
Simple has an extensive list of methods for each type in order to leverage the Object-Oriented structure. A typical example would be as follows:
```
my_card: card(“HW”)
t1: task(“Math”, 4, 2)
# Adds the task to the card
my_card.add(t1)
```
While already sizable, the number of methods available continues to grow (you can see the current list via Appendix B).

### Scheduling Algorithms

Agile Planner utilizes two core algorithms when scheduling out user data throughout the upcoming weeks: Dynamic and Compact.

Dynamic scheduling is a combination of Longest-Remaining-Time-First (LRTF) + Round Robin (RR). This allows for a distributive mindset of scheduling so students have an optimal amount of time each day.

Compact scheduling is simply LJF and its primary focus is the cramming mindset. This allows the user to finish each task in as few days as possible since they are not being broken up.

These algorithms also come with plenty of configuration options to make scheduling even more adaptable to your weekly needs.

### Logging Tools

#### System Logging:
System logging reports all actions that are performed with managing data and performing scheduling operations or routine IO. It is meant to be thorough and complete while avoiding unnecessary reporting. A sample log is shown below:
```
[08-03-2024] Log of all activities from current session: 

[17:29:21] [INFO] CURRENT SESSION HAS BEGUN...
[17:29:21] [INFO] Reading Config: FILE=profile.cfg
[17:29:21] [INFO] USERNAME=null ,EMAIL=null ,WEEK_HOURS=[8, 8, 8, 8, 8, 8, 8] ,MAX_DAYS=14 ,ARCHIVE_DAYS=14 ,PRIORITY=false ,OVERFLOW=true ,FIT_SCHEDULE=false ,SCHEDULE_ALGO=1 ,MIN_HOURS=1
[17:29:22] [INFO] GOOGLE CALENDAR AUTHORIZATION PROCESSED...
[17:29:24] [INFO] SCRIPT_NAME=C:\Users\Student\Desktop\agile-planner-0.3.7\data\scripts\google.smpl , SCRIPT INSTANCE HAS BEGUN...
[17:29:25] [INFO] SCHEDULE IMPORTED FROM GOOGLE CALENDAR...
[17:31:02] [INFO] ADD(TASK):  ID=0, NAME=Study Math, HOURS=6, DUE_DATE=03-12-2024
[17:31:02] [INFO] ADD(TASK):  ID=0, NAME=Study OS, HOURS=8, DUE_DATE=03-10-2024
[17:31:02] [INFO] ADD(TASK):  ID=0, NAME=Study Physics, HOURS=10, DUE_DATE=03-15-2024
[17:31:02] [INFO] Scheduling has begun...
[17:31:02] [INFO] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=5, HOURS_FILLED=3, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=3, HOURS_FILLED=5, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=0, CAPACITY=8, HOURS_REMAINING=1, HOURS_FILLED=7, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=1, CAPACITY=8, HOURS_REMAINING=5, HOURS_FILLED=3, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=1, CAPACITY=8, HOURS_REMAINING=3, HOURS_FILLED=5, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=1, CAPACITY=8, HOURS_REMAINING=1, HOURS_FILLED=7, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=2, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=2, CAPACITY=8, HOURS_REMAINING=4, HOURS_FILLED=4, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=2, CAPACITY=8, HOURS_REMAINING=2, HOURS_FILLED=6, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=3, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] DAY_ID=4, CAPACITY=8, HOURS_REMAINING=6, HOURS_FILLED=2, TASK ADDED=0, OVERFLOW=false
[17:31:02] [INFO] Scheduling has finished...
[17:31:04] [INFO] 4 TASKS REMOVED FROM GOOGLE CALENDAR...
[17:31:08] [INFO] SCHEDULE EXPORTED TO GOOGLE CALENDAR...
[17:31:08] [INFO] Display Schedule: DAYS=5, NUM_TASKS=3, STDOUT=true
[17:31:08] [INFO] SCRIPT_NAME=C:\Users\Student\Desktop\agile-planner-0.3.7\data\scripts\google.smpl , SCRIPT INSTANCE HAS ENDED...
```

#### Scripter Logging:
Scripter logging essentially serves as a stack trace and reports all operations that occur as the Simple script is parsed and interpreted. Changes are being planned with logging the following: variable creation, local stack, and global stack:
```
[08-03-2024] Log of all activities from current session: 

[17:29:24] PREPROC_ATTR: DEF_CONFIG=false, IMPORT=true, EXPORT=false, LOG=true, BUILD=true, STATS=false
[17:29:25] FUNC_CALLS: NAME=google_import, ARGS=[]
[17:29:25] FUNC_SETUP: NAME= setup_cl, PARAM=[]
[17:29:25] FUNC_SETUP: NAME= setup_task, PARAM=[]
[17:29:25] FUNC_SETUP: NAME= setup_schedule, PARAM=[]
[17:29:28] FUNC_CALLS: NAME=set_schedule, ARGS=[option]
[17:29:29] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:29:29] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:29:41] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:29:41] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:29:45] ATTR_CALL: VAR_NAME=t1, NAME=add, ARGS[_cl]
[17:29:46] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:29:46] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:29:50] ATTR_CALL: VAR_NAME=cl1, NAME=add_item, ARGS[in]
[17:29:51] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:29:51] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:29:57] ATTR_CALL: VAR_NAME=cl1, NAME=add_item, ARGS[in]
[17:29:59] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:29:59] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[17:29:59] FUNC_CALLS: NAME=setup_cl, ARGS=[cl1]
[17:29:59] FUNC_CALLS: NAME=setup_cl, ARGS=[cl1]
[17:29:59] FUNC_CALLS: NAME=setup_cl, ARGS=[_cl]
[17:29:59] FUNC_CALLS: NAME=setup_task, ARGS=[_task]
[17:30:01] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:30:01] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:30:12] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:30:12] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[17:30:12] FUNC_CALLS: NAME=setup_task, ARGS=[_task]
[17:30:13] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:30:13] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:30:44] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:30:44] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:30:49] ATTR_CALL: VAR_NAME=t1, NAME=add, ARGS[_cl]
[17:30:50] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:30:50] IF_CONDITION: ARGS=[in.==("y")], RESULT=true
[17:30:59] ATTR_CALL: VAR_NAME=cl1, NAME=add_item, ARGS[in]
[17:31:01] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:31:01] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[17:31:01] FUNC_CALLS: NAME=setup_cl, ARGS=[cl1]
[17:31:01] FUNC_CALLS: NAME=setup_cl, ARGS=[_cl]
[17:31:01] FUNC_CALLS: NAME=setup_task, ARGS=[_task]
[17:31:02] ATTR_CALL: VAR_NAME=in, NAME===, ARGS["y"]
[17:31:02] IF_CONDITION: ARGS=[in.==("y")], RESULT=false
[17:31:02] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[17:31:02] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[17:31:02] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[17:31:02] FUNC_CALLS: NAME=setup_schedule, ARGS=[]
[17:31:02] FUNC_CALLS: NAME=add_all_tasks, ARGS=[]
[17:31:02] FUNC_CALLS: NAME=build, ARGS=[]
[17:31:08] FUNC_CALLS: NAME=google_export, ARGS=[]
```
### Java Binary Serialization
Agile Planner utilizes custom serialization for maintaining data persistence with scheduling data. The following is a jbin file that has been properly formatted:
```
08-03-2024

LABEL {
  LOL, 3
  Party, 4
}

CHECKLIST {
  To Do, Item 1, Item 2
}

TASK {
  a, 3, 3, L1
  c, 1, 0, CL0
  b, 2, 1, L0
}

CARD {
  Default, T0, L0
  Homework, T1, T2
}
```
Data is configured so that objects can be read in with optimal time efficiency due to all the referencing at the bottom of the chain such as Task or Card.

### Conclusive Summary:
A lot of work has gone into making this all happen. The current plan is to continue developing the terminal version of Agile Planner until all core features hit the necessary mark. The current plan is to offer more 3rd party integrations, redesign the language, and implement optimization algorithms for scheduling.
If you have any questions or would like to report a bug, you can report an issue here.

Agile Planner – “Scheduling Made Simple” 
