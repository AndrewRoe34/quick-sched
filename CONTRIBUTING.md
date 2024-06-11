# Contributing to Agile Planner

This document provides guidelines for contributing to the project. Whether you're fixing a bug, adding a feature, or improving documentation, your help is appreciated.

## Prerequisites

Before you begin, ensure you have the following installed:
- **Java Development Kit (JDK)**: Version 11 (required to compile Java code).

Optional:
- **IntelliJ IDEA**: This project is developed using IntelliJ IDEA, which provides excellent support for Gradle projects. You can download it from the JetBrains website. The Community Edition is free, and the Ultimate Edition is available for a free trial.

### Installation
1. **Fork the repository and clone your fork**:
```bash
# This script demonstrates how to clone a forked repository.

# Step 1: Set your GitHub username
GITHUB_USERNAME="your_username"

# Step 3: Construct the clone URL for the forked repository
CLONE_URL="https://github.com/$GITHUB_USERNAME/agile-planner.git"

# Step 4: Clone the forked repository
git clone $CLONE_URL
```
2. **Navigate to the project directory**:
```bash
cd agile-planner
```
3. **Build the project using the Gradle Wrapper**:
```bash
# Unix/Linux
./gradlew build

# Windows Command Prompt
gradlew.bat build
```
4. **Run the application**:
```bash
# Unix/Linux
./gradlew run --console=plain

# Windows Command Prompt
gradlew.bat run --console=plain
```
### Configure IntelliJ IDEA
After building the project, you can open it in IntelliJ IDEA:
1. Open IntelliJ IDEA and select Open or Import.
2. Navigate to your project directory and select the build.gradle file.
3. Choose ‘Open as Project’.
4. IntelliJ IDEA will import the project and set everything up based on the Gradle configuration.

Now, you’re ready to start developing with Agile Planner!

## Making Contributions

1. **Check the Issue Tracker**: Look for open issues that match your skills or open a new issue to discuss proposed changes. Issues range from features, bugs, refactoring, testing, documenting, updates to CI/CD, etc.
2. **Fork the Repository**: Fork the repository on GitHub to start making your changes.
3. **Create a New Branch**: Create a new branch for your changes. [TODO]
4. **Make Your Changes**: Implement your changes in your branch.
5. **Write or Update Tests**: Ensure you write new tests or update existing ones to reflect your changes. All new features should have corresponding tests that achieve code coverage of at least 80%.
6. **Build and Test**: Make sure the project builds and all tests pass. See Building and Testing for detailed instructions.
7. **Commit Your Changes**: Commit your changes with a clear and detailed message. Follow our Commit Message Guidelines.
8. **Push Your Changes**: Push your changes to your fork on GitHub.
9. **Submit a Pull Request**: Submit a pull request to the main repository. Our maintainers will review your changes and merge them if appropriate.

Thank you for contributing!

## Issue and Pull Request Templates
We use templates for issues and pull requests to ensure consistency and completeness. Please use the provided templates when creating issues or pull requests.

## Building and Testing

## Commit Message Guidelines
For our community, we utilize one of the following styles ... [TODO] [TODO, include link to a commit]

## Code Style
Code is a written expression of logic, and as such, must be formatted properly if we are to maintain readability and clarity.

### Variable Naming
As the saying goes, good code explains itself. Well, the most fundamental aspect to any programming language is how you name your variables. When creating a variable, it is important that you give it a meaningful/purposeful name.

Below contain two examples of both good and bad variable naming standards for our community:

**Poor variables**
```java
String x2 = TableFormatter.formatPrettyScheduleTable(x1);
```
**Good variables**
```java
String prettyScheduleTable = TableFormatter.formatPrettyScheduleTable(schedule);
```
The first version doesn't give any detail about what x2 contains besides the method call. Instead, the latter provides an actual description about what data it's possessing. Take note also of the camel case utilized.

### Class & Method Naming
Similar to variable naming, methods and classes need to provide an almost immediate description about their functionality just through their name. We will tackle classes first.

When creating a class, it's important that you both package it appropriately and give it a proper name. In a grammar sense, class names should emulate a noun. Below are two examples as to what is and is not appropriate:

**Poor class**
```java
public class CreateCompactSchedule {}
```
**Good class**
```java
public class CompactScheduler {}
```
Thus, we maintain the consistency of the latter example with all class scenarios. However, methods operate quite differently. Their naming convention acts closer to a verb as demonstrated below:

**Poor method**
```java
public static String the_jbin(List<Card> cards) {}
```
**Good method**
```java
public static String createJBin(List<Card> cards) {}
```
Notice the camel case in the latter example and the absence of the underscore character? This should hopefully resolve any possible confusion between the two naming conventions.


### Vertical Spacing
Similar to a well-formatted paper, code should also possess distinguishing structure to separate certain components from others. The general philosophy we adhere to here is that code that works towards a common goal should possess no vertical spacing, but code that is dissimilar in general goal should not. Below is an example.

In this method, we see a formatting of an Agile Planner Event to a Google Calendar Event:
```java
public static Event formatEventToGoogleEvent(com.planner.models.Event e) {
        Event event = new Event().setSummary(e.getName());

        event.setDescription("Agile Planner\n\neb007aba6df2559a02ceb17ddba47c85b3e2b930");

        DateTime startDateTime = new DateTime(e.getTimeStamp().getStart().getTime());
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(e.getTimeStamp().getEnd().getTime());
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);

        if (e.getColor() == null) return event;
        else event.setColorId(convertAnsiToGoogleColor(e.getColor()));

        return event;
}
```
Let's break down each code snippet from above to visualize our thoughts:
1. A new Google Calendar event is created
2. An Agile Planner hash code is attached to the description
3. A start time is formatted
4. An end time is formatted
5. If the Agile Planner event has a color, attach it to the Google Calendar variant
6. Return the event

Each individual snippet is purposeful with what it's aiming to solve and serves a common purpose, thus improving readability for the user.

### Control Structures
With Agile Planner, we require that all control structures maintain proper horizontal spacing in order to increase readability as demonstrated below:

**Poor spacing**
```java
while(x > 3){
    foo(x);
    x--;
}
if(flag)System.out.println();
```
**Good spacing**
```java
while (x > 3) {
    foo(x);
    x--;
}
if (flag) System.out.println();
```
This provides for improved clarity to the reader, which allows us to work more efficiently.

Another topic to touch upon is the usage of curly braces with loops and conditions. With Agile Planner, we require that all loops maintain curly braces to avoid possible confusion or cryptic bugs, but single line conditions are permitted due to their once-off nature and their intrinsic simplicity:

**Poor style**
```java
for (int i = 0; i < arr.length; i++)
    foo(arr[i]);
```
**Good style**
```java
for (int i = 0; i < arr.length; i++) {
    foo(arr[i]);
}
if (status) System.out.println("Success...");
```

### Method chaining
When dealing with Object Oriented languages, you're apt to encounter a long series of method chaining. Here we discuss what are the standards we enforce when encountering said situations.

Here is a sample code snippet for our discussion:
```java
StringBuilder sb = new StringBuilder("Due: ");
sb.append(task.getDueDate().get(Calendar.YEAR))
        .append("-")
        .append(task.getDueDate().get(Calendar.MONTH) + 1)
        .append("-")
        .append(task.getDueDate().get(Calendar.DAY_OF_MONTH))
        .append("\n\n");
```
In this case, we make the first method call and then append the remaining series just below it. Our rule of thumb is three or more method chainings requires some verticality to improve clarity.

Another option is to simply use one line for each call as shown below (albeit, less optimal compared to the first variant):
```java
StringBuilder sb = new StringBuilder("Due: ");
sb.append(task.getDueDate().get(Calendar.YEAR));
sb.append("-");
sb.append(task.getDueDate().get(Calendar.MONTH) + 1);
sb.append("-");
sb.append(task.getDueDate().get(Calendar.DAY_OF_MONTH));
sb.append("\n\n");
```

### Additional Notes
For further clarification on proper standards, refer to Effective Java and Gang of Four Design Patterns. These two texts offer plenty of valuable insights for how you should be writing Object-Oriented Java code. 

## Documentation
When integrating a new feature or performing an edit, good documentation must always persist with change. While we don't require comments, classes and methods are an absolute requirement. We will go through the style enforced by this community.

### Classes
When documenting a class, we require that you provide a detailed description discussing:
1. What the class is (via @code tag)
2. What purpose the class serves
3. Discussion of any relevant classes being utilized (via the @link tag)
4. An author tag

Below I've provided contrasting examples for both poor and good documentation:

**Poor documentation**
```java
/**
 * Handles scheduling in a compact manner
 */
public class CompactScheduler implements Scheduler {}
```

**Good documentation**
```java
/**
 * The class {@code CompactScheduler} implements the interface {@link Scheduler} for compact scheduling actions.
 * This involves the utilization of Longest-Job-First in order to compute the schedule for the week. {@link UserConfig}
 * options allow for another style such as Shortest-Job-First well as other restraints for scheduling.
 * <p>
 * Day optimizations occur when {@link com.planner.models.Task.SubTask} can be more uniformly fitted around {@link com.planner.models.Event}.
 * However, the user must have 'optimizeDay' config option set to true via the {@link UserConfig}.
 *
 * @author Andrew Roe
 */
public class CompactScheduler implements Scheduler {}
```

### Methods
When documenting a method, we require that you provide an informative description discussing:
1. What the method does
2. Discussion of any relevant classes being utilized (via the @link tag)
3. Provides all needed @param, @return, @throws tags as needed

Below I've provided contrasting examples for both poor and good documentation:

**Poor documentation**
```java
/**
 * Updates day by optimizing it
 */
int optimizeDay(Day day);
```

**Good documentation**
```java
/**
 * Optimizes a {@link Day}'s set of {@link com.planner.models.Task.SubTask} around {@link com.planner.models.Event}
 * by reducing the amount of interruptions in task blocks. This functions similar to how dynamic memory is fitted
 * to maximize spacing.
 * <p>
 * NOTE: This operation will only occur if the user has the 'optimizeDay' config option set to 'true'
 *
 * @param day day being optimized around {@link com.planner.models.Event}
 * @return number of times a {@link com.planner.models.Task.SubTask} was not uniformly fitted
 */
int optimizeDay(Day day);
```

## Review Process
After you submit a pull request, the project maintainers will review your changes, suggest any needed changes/improvements, approve your changes, and finally, merge your pull request.

Here's a typical interaction as part of the review process:
[Include image here]

## Recognition
Contributors who provide significant improvements or help fix major issues will be recognized in the project's documentation.
