# Upgrade Plan: solid-04-isp (b103a987-41ef-4fa5-a50b-230cea677424)

- **Generated**: 2026-10-20T15:56:00-03:00
- **HEAD Branch**: master
- **HEAD Commit ID**: 935db8f8720c01b6044d674095ac52f6fcc7c77c

## Available Tools

**JDKs**
- JDK 17: C:\Program Files\Java\jdk-17 (current project JDK, used by Step 2)
- JDK 21: C:\Program Files\Java\jdk-21 (required by Steps 3-5)

**Build Tools**
- Maven 3.9.11: C:\apache-maven
- Maven Wrapper: Not present (using system Maven)

## Guidelines

- Upgrade Java from 17 to 21 (latest LTS)
- Maintain backward compatibility where possible
- Keep changes minimal and focused on the upgrade
- All tests must pass after upgrade

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-b103a987-41ef-4fa5-a50b-230cea677424
- Run tests before and after the upgrade: true

## Upgrade Goals

- Java: 17 → 21 (latest LTS)

## Technology Stack

| Technology/Dependency    | Current | Min Compatible | Why Incompatible                               |
| ------------------------ | ------- | -------------- | ---------------------------------------------- |
| Java                     | 17      | 21             | User requested latest LTS                      |
| Maven                    | 3.9.11  | 3.9+           | Compatible with Java 21                        |
| maven-compiler-plugin    | 3.11.0  | 3.11+          | Compatible with Java 21                        |
| maven-surefire-plugin    | 3.1.2   | 3.0+           | Compatible with Java 17+                       |
| JUnit Jupiter            | 5.10.0  | 5.8+           | Compatible with Java 21                        |
| AssertJ                  | 3.25.3  | 3.24+          | Compatible with Java 21                        |

## Derived Upgrades

- Java 21 → No additional derived upgrades needed (Maven 3.9.11, maven-compiler-plugin 3.11.0, maven-surefire-plugin 3.1.2, JUnit 5.10.0, AssertJ 3.25.3 are all compatible with Java 21)

## Impact Analysis

### Subsection: Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|-----------|---------|--------|--------|--------|
| pom.xml (root) | maven.compiler.source | 17 | upgrade | 21 | Java 21 target |
| pom.xml (root) | maven.compiler.target | 17 | upgrade | 21 | Java 21 target |
| pom.xml (root) | maven-compiler-plugin configuration source | 17 | upgrade | 21 | Java 21 target |
| pom.xml (root) | maven-compiler-plugin configuration target | 17 | upgrade | 21 | Java 21 target |

### Subsection: Source Code Changes

No source code changes required. The codebase uses standard Java APIs compatible with Java 21.

### Subsection: Configuration Changes

No configuration files (application.properties, application.yml, etc.) present in the project.

### Subsection: CI/CD Changes

No CI/CD files (Dockerfile, GitHub Actions, Azure Pipelines, Jenkinsfile) present in the project.

### Subsection: Risks & Warnings

- **Low risk**: This is a simple educational project with no external framework dependencies (no Spring, no Jakarta EE). The upgrade only involves changing the Java version in the POM files.
- **No reflection into JDK internals** detected.
- **No removed/deprecated API usage** detected that would affect Java 21 compatibility.
- **No module system issues** - project doesn't use module-info.java.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Verify all required JDKs and build tools are available
  - **Changes to Make**: None (JDK 17 and 21 already installed, Maven 3.9.11 available)
  - **Verification**: `java -version` (should show 21), `mvn -version` (should show 3.9.11), Expected: All tools available

- Step 2: Setup Baseline
  - **Rationale**: Establish baseline compilation and test results with current JDK 17
  - **Changes to Make**: None
  - **Verification**: `mvn clean compile test-compile -q && mvn clean test -q` with JDK 17, Expected: Compilation SUCCESS, Tests pass

- Step 3: Upgrade Java Version in Root POM
  - **Rationale**: Update the parent POM to target Java 21
  - **Changes to Make**: Apply Dependency Changes from Impact Analysis (root pom.xml properties and maven-compiler-plugin configuration)
  - **Verification**: `mvn clean test-compile -q` with JDK 21, Expected: Compilation SUCCESS

- Step 4: Upgrade Java Version in Module POMs
  - **Rationale**: Child modules inherit from parent, but verify they compile with Java 21
  - **Changes to Make**: No direct changes needed (modules inherit from parent), but verify compilation
  - **Verification**: `mvn clean test-compile -q` with JDK 21, Expected: Compilation SUCCESS

- Step 5: Final Validation
  - **Rationale**: Verify all goals met, run full test suite with Java 21
  - **Changes to Make**: None (resolve any issues if found)
  - **Verification**: `mvn clean test -q` with JDK 21, Expected: 100% test pass rate