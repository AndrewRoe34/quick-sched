# Agile Planner v0.4.0
![Maven Central Version](https://img.shields.io/maven-central/v/com.google.code.gson/gson?versionPrefix=2.10.1&style=flat&label=gson&labelColor=%23f8b500&color=%23555555)
![Maven Central Version](https://img.shields.io/maven-central/v/com.google.api-client/google-api-client?versionSuffix=2.6.0&style=flat&label=google-api-client&labelColor=%230f9d58&color=%23555555)
![Maven Central Version](https://img.shields.io/maven-central/v/com.google.oauth-client/google-oauth-client-jetty?versionSuffix=1.34.1&style=flat&label=google-oauth-client-jetty&labelColor=%234285f4&color=%23555555)
![Maven Central Version](https://img.shields.io/maven-central/v/com.google.apis/google-api-services-calendar?versionSuffix=v3-rev20220715-2.0.0&style=flat&label=google-api-services-calendar&labelColor=%23db4437&color=%23555555)
![Maven Central Version](https://img.shields.io/maven-central/v/org.junit.jupiter/junit-jupiter-api?versionSuffix=5.9.2&style=flat&label=junit-jupiter-api&labelColor=%234caf50&color=%23555555)
![Maven Central Version](https://img.shields.io/maven-central/v/org.junit.jupiter/junit-jupiter-engine?versionSuffix=5.9.2&style=flat&label=junit-jupiter-engine&labelColor=%239c27b0&color=%23555555)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/AndrewRoe34/agile-planner/gradle.yml?style=flat)
![GitHub Release](https://img.shields.io/github/v/release/AndrewRoe34/agile-planner?include_prereleases&color=%23ffeb3b)

Agile Planner is a robust, dynamic shceduling platform that provides unparalled automation for students seeking to generate both daily and weekly planners. It is the only scheduling platform to date that provides its own Object Oriented scripting language fully integrated with a CLI interface for streamlining the pipeline even further. Futher, Agile Planner offers plenty of proprietary software such as logging, scheduling algorithms, serialization, and as stated before, a custom scripting language. The goal is to continue expanding upon the current CLI iteration and eventually offer a web-based component.

## Getting Started

These instructions will guide you through the setup process to get the project running on your local machine for development and testing purposes.

### Prerequisites

Before you begin, ensure you have the following installed:
- **Java Development Kit (JDK)**: Version 11 (required to compile Java code).

Optional:
- **IntelliJ IDEA**: This project is developed using IntelliJ IDEA, which provides excellent support for Gradle projects. You can download it from the JetBrains website. The Community Edition is free, and the Ultimate Edition is available for a free trial.

### Installation
1. **Clone the repository**:
```bash
git clone https://github.com/AndrewRoe34/agile-planner.git
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

## Usage
Agile Planner runs directly off Simple Script with creating and managing your entire weekly schedule. This section will go over all core components need to have your schedules running!

## Configuration
Agile Planner can be customized through a `settings.cfg` file. Below are the available settings and their descriptions:

- **range**: An array defining the start and end hours of your daily schedule (in 24-hour format). For example, `[9, 20]` sets the schedule from 9 AM to 8 PM.
- **week**: An array representing the number of hours allocated for each day of the week. For example, `[8, 8, 8, 8, 8, 8, 8]` allocates 8 hours per day.
- **maxDays**: The maximum number of days for which a schedule can be generated. For example, `14` allows for a two-week schedule.
- **archiveDays**: The number of days past tasks are stored before being archived. For example, `5` stores tasks for five days.
- **priority**: A boolean value indicating whether task priority is considered in scheduling (currently not implemented).
- **overflow**: A boolean value that, when set to `true`, reports the overflow status of tasks.
- **fitDay**: A boolean value that determines whether tasks are capped at the end of the day.
- **schedulingAlgorithm**: An integer representing the scheduling algorithm used. `1` corresponds to the 'Compact' algorithm.
- **minHours**: The minimum number of hours a task can be assigned in a day. For example, `0.5` allows for half-hour tasks.
- **optimizeDay**: A boolean value that, when `true`, reorganizes tasks to maximize their positioning throughout the day.
- **defaultAtStart**: A boolean value that determines whether scheduling begins at the start of the day.
- **localScheduleColors**: A boolean value that, when `true`, enables local color settings for the schedule.

Here's an example `settings.cfg` file with default values:
```json
{
  "range": [9, 20],
  "week": [8, 8, 8, 8, 8, 8, 8],
  "maxDays": 14,
  "archiveDays": 5,
  "priority": false,
  "overflow": true,
  "fitDay": true,
  "schedulingAlgorithm": 1,
  "minHours": 1.0,
  "optimizeDay": false,
  "defaultAtStart": true,
  "localScheduleColors": true
}
```

## Contributing

If you're interested in contributing to this project, be sure to check out <file> and join our slack group down below.

[![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)](https://join.slack.com/t/agileplannergroup/shared_invite/zt-2k0bmf49j-V6avYCrNJFFWVTpdER69tg)

## License
