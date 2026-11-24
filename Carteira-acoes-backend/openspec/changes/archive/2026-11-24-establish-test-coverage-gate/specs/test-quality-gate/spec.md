## Purpose

Provides a reproducible quality gate so test execution, coverage evidence, and coverage regressions are visible in local development and continuous integration.

## ADDED Requirements

### Requirement: Verification produces coverage evidence
The project's standard Maven verification command SHALL execute the test suite with coverage instrumentation and SHALL produce navigable HTML plus machine-readable XML and CSV reports under the Maven target site.

#### Scenario: Generate the standard report
- **WHEN** a developer runs `./mvnw clean verify`
- **THEN** the build generates the JaCoCo report files under `target/site/jacoco/` when verification completes successfully

#### Scenario: Test-only execution is distinguished from verification
- **WHEN** a developer runs `./mvnw test`
- **THEN** tests execute without being treated as evidence that the verification-time report and coverage gate have run

### Requirement: Verification enforces minimum coverage
The standard verification command SHALL fail when aggregate line coverage is below 85% or aggregate branch coverage is below 85%.

#### Scenario: Coverage meets the versioned floor
- **WHEN** verification reports at least 85% line coverage and at least 85% branch coverage
- **THEN** the coverage check passes

#### Scenario: Coverage falls below the line floor
- **WHEN** verification reports line coverage below 85%
- **THEN** the build fails during verification and identifies the line threshold violation

#### Scenario: Coverage falls below the branch floor
- **WHEN** verification reports branch coverage below 85%
- **THEN** the build fails during verification and identifies the branch threshold violation

### Requirement: Stricter thresholds can be evaluated temporarily
The coverage thresholds SHALL be overridable through Maven properties for one-off evaluation without changing the versioned project configuration.

#### Scenario: Demonstrate an unmet stricter branch target
- **WHEN** a developer runs verification with the line and branch minimums overridden to 90% and the measured coverage is lower
- **THEN** the build fails for the temporary threshold while the repository's configured threshold remains unchanged

### Requirement: Quality policy protects behavior rather than test count
The quality policy SHALL treat relevant assertions, success and failure paths, boundary values, and security-sensitive behavior as the basis for coverage improvement; it SHALL NOT permit lowering thresholds, arbitrary exclusions, deleting tests, or assertion-free tests as a means to pass the gate.

#### Scenario: Add a behavior-focused test
- **WHEN** a missing branch represents a meaningful service, validation, authorization, or error behavior
- **THEN** the project may increase coverage by adding a test with an observable assertion for that behavior

#### Scenario: Reject coverage-only shortcuts
- **WHEN** a proposed change would lower a configured threshold, exclude arbitrary production code, or add a test with no meaningful assertion solely to increase coverage
- **THEN** that change does not satisfy the quality policy
