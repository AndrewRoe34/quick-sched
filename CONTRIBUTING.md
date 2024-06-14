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

We use templates for issues and pull requests to ensure consistency and completeness. When creating issues or pull requests, please use one of the following templates:

- **Bug Report**: Use this template when reporting a bug or issue. It helps us gather necessary information for debugging and fixing the problem.

- **Feature Request**: Use this template when proposing a new feature or enhancement. It helps us understand the purpose and scope of the proposed change.

Please select the appropriate template when creating your issue or pull request to provide clear and structured information. This ensures that your contributions are addressed promptly and effectively.

## Testing
![Testing](images/testing.png)

### Running Unit Tests

1. **Unit Tests:**
   - To run unit tests, navigate to the project root directory in IntelliJ.
   - Open the Gradle tool window (`View -> Tool Windows -> Gradle`), and expand the project name.
   - Navigate to `Tasks -> verification -> test` and double-click to run all unit tests.

### Writing Unit Tests

1. **Creating JUnit 5 Tests:**
   - IntelliJ provides a convenient way to generate JUnit 5 test classes and methods.
   - After creating a new Java class for your component or class, IntelliJ allows you to easily create corresponding test classes.
   - Inside the newly created class, position your cursor on the class name, press `Alt + Enter`, and select `Create Test` to generate a test class.
   - IntelliJ will prompt you to select methods to generate test methods for or create new ones.

2. **Organizing Tests:**
   - Make sure your unit tests are under the `src/test/java` directory.
   - They should be organized according to the appropriate package structure that mirrors your main source code (`src/main/java`).

3. **Running Tests from IntelliJ:**
   - To run a single test or a group of tests, navigate to the test class or method, right-click, and select `Run 'ClassName'` or `Run 'MethodName()'`.
   - IntelliJ will execute the selected tests and display the results in the Run tool window.

### Test Coverage

1. **Code Coverage Reports:**
   - IntelliJ provides built-in support for generating code coverage reports.
   - After running your tests, go to `Run -> Show Coverage Data` to view the coverage report.
   - Ensure that your tests cover critical parts of your codebase to maintain high code quality.

2. **Improving Test Coverage:**
   - Regularly review the coverage report to identify areas of code that are not adequately covered by tests.
   - Write additional tests or enhance existing ones to improve overall test coverage to at least 80%.


## Commit Message Guidelines

Each commit message should start with a tag indicating the type of change, followed by a brief description. Optionally, you can list specific items or details related to the commit.

### Tags and Examples

- **[Feature]**: Implemented user authentication
   - Added login form
   - Implemented JWT token handling
   - Added user registration endpoint

- **[Batch]**: Updated dependencies and fixed UI bugs
   - Updated packages to latest versions
   - Fixed responsive design issues

- **[Test]**: Added unit tests for user service
   - Created test cases for login and registration

- **[Refactor]**: Optimized database query performance
   - Refactored SQL queries for efficiency

- **[Doc]**: Updated API documentation
   - Added detailed usage instructions

- **[Fix/Hotfix]**: Fixed critical error when attempting to serialize schedule
    - Resolved issue causing serialization failure due to incorrect data format handling

By adhering to these guidelines, you help us maintain consistency and clarity in our project's commit history.

## Code Style
Code is an expression of logical thought, and therefore, it must be properly formatted to ensure readability and clarity.

### Variable Naming
As the saying goes, good code explains itself. Well, the most fundamental aspect to any programming language is how you name your variables. When creating a variable, it is important that you give it a meaningful/purposeful name.

Below contain two examples of both good and bad variable naming standards for our community:

**Bad**
```java
String x2 = TableFormatter.formatPrettyScheduleTable(x1);
```
**Good**
```java
String prettyScheduleTable = TableFormatter.formatPrettyScheduleTable(schedule);
```
The first version doesn't give any detail about what x2 contains besides the method call. Instead, the latter provides an actual description about what data it's possessing. Take note also of the camel case utilized.

### Class & Method Naming
Similar to variable naming, methods and classes need to provide an almost immediate description about their functionality just through their name. We will tackle classes first.

When creating a class, it's important that you both package it appropriately and give it a proper name. In a grammar sense, class names should emulate a noun. Below are two examples as to what is and is not appropriate:

**Bad**
```java
public class CreateCompactSchedule {}
```
**Good**
```java
public class CompactScheduler {}
```
Thus, we maintain the consistency of the latter example with all class scenarios. However, methods operate quite differently. Their naming convention acts closer to a verb as demonstrated below:

**Bad**
```java
public static String the_jbin(List<Card> cards) {}
```
**Good**
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

**Bad**
```java
while(x > 3){
    foo(x);
    x--;
}
if(flag)System.out.println();
```
**Good**
```java
while (x > 3) {
    foo(x);
    x--;
}
if (flag) System.out.println();
```
This provides for improved clarity to the reader, which allows us to work more efficiently.

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

## Documentation
When integrating a new feature or performing an edit, good documentation must always persist with change. While we don't require comments, classes and methods are an absolute requirement. We will go through the style enforced by this community.

### Classes
When documenting a class, we require that you provide a detailed description discussing:
1. What the class is (via @code tag)
2. What purpose the class serves
3. Discussion of any relevant classes being utilized (via the @link tag)
4. An author tag

Below I've provided contrasting examples for both poor and good documentation:

**Bad**
```java
/**
 * Handles scheduling in a compact manner
 */
public class CompactScheduler implements Scheduler {}
```

**Good**
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

**Bad**
```java
/**
 * Updates day by optimizing it
 */
int optimizeDay(Day day);
```

**Good**
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
Thank you for contributing to our project! Here’s what happens after you submit a pull request:
1. **Initial Review:**
    - Project maintainers will review your changes to ensure they align with our project's goals and guidelines.

2. **Feedback and Revisions:**
    - If any adjustments are needed, maintainers will provide feedback and suggest improvements. Please address these suggestions promptly to facilitate the review process.

3. **Approval:**
    - Once your changes meet the project standards, maintainers will approve your pull request.

4. **Merge:**
    - After approval, your changes will be merged into the main branch.

For reference, you can check out a [Sample Pull Request](https://github.com/AndrewRoe34/agile-planner/pull/45) to see an example of a successful review process.

We appreciate your contribution and look forward to collaborating with you!

## Recognition
Contributors who provide significant improvements or help fix major issues will be recognized in the project's documentation.
