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

1. **Check the Issue Tracker**: Look for open issues that match your skills or open a new issue to discuss proposed changes.
2. **Fork the Repository**: Fork the repository on GitHub to start making your changes.
3. **Create a New Branch**: Create a new branch for your changes. Use the format `Feature Request: [Description of the Desired Feature]` for new features or `Bug: [Brief Description of the Bug]` for bug fixes.
4. **Make Your Changes**: Implement your changes in your branch.
5. **Write or Update Tests**: Ensure you write new tests or update existing ones to reflect your changes. All new features should have corresponding tests.
6. **Build and Test**: Make sure the project builds and all tests pass. See Building and Testing for detailed instructions.
7. **Commit Your Changes**: Commit your changes with a clear and detailed message. Follow our Commit Message Guidelines.
8. **Push Your Changes**: Push your changes to your fork on GitHub.
9. **Submit a Pull Request**: Submit a pull request to the main repository. Our maintainers will review your changes and merge them if appropriate.

Thank you for contributing!

## Issue and Pull Request Templates
We use templates for issues and pull requests to ensure consistency and completeness. Please use the provided templates when creating issues or pull requests.

## Building and Testing

## Commit Message Guidelines

## Documentation
If you're adding a new feature or changing existing functionality, please update the documentation accordingly.

## Review Process
After you submit a pull request, the project maintainers will review your changes. We may suggest changes or improvements. Once approved, a maintainer will merge your pull request.

## Recognition
Contributors who provide significant improvements or help fix major issues will be recognized in the project's documentation.
