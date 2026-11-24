## Why

The project has a substantial test suite, but test count alone does not show whether important execution paths are protected. A reproducible JaCoCo report and a versioned coverage gate are needed now to detect regressions while preserving the distinction between passing tests and meeting the project's quality policy.

## What Changes

- Add JaCoCo instrumentation, HTML/XML/CSV report generation, and verification-time coverage checks to the Maven build.
- Establish a minimum of 85% line coverage and 85% branch coverage for the test suite.
- Document the current test inventory, coverage interpretation, TDD workflow, test-double guidance, boundary-value strategy, mutation-testing direction, CI usage, and evaluation checklist.
- Add or refine behavior-focused tests for the identified `UsuarioService` scenarios and other high-risk uncovered branches without artificial coverage-only tests or exclusions.
- Document commands for generating reports and intentionally demonstrating a failed stricter branch gate.

## Capabilities

### New Capabilities

- `test-quality-gate`: Define the reproducible test execution, coverage reporting, and minimum line/branch coverage policy for the project.

### Modified Capabilities

<!-- No existing runtime capability requirements change. -->

## Impact

- `pom.xml` and Maven verification lifecycle.
- Unit and integration test sources, especially user-service and boundary/error scenarios.
- Project testing documentation and optional CI workflow documentation.
- Developers and CI will receive a failed build when the versioned coverage thresholds are not met.
- No production HTTP API, persistence schema, or user-facing runtime behavior is intentionally changed.
