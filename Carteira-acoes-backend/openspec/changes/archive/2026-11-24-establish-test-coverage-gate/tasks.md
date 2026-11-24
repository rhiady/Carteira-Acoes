## 1. Establish the measured baseline

- [x] 1.1 Run `./mvnw clean verify` and record the actual test count, line coverage, branch coverage, and generated report paths.
- [x] 1.2 Compare the measured repository state with the supplied audit, noting factual differences such as the current Flyway-based test setup and the existing JaCoCo properties/plugin configuration.
- [x] 1.3 Inspect the JaCoCo configuration in `pom.xml` for formatting, lifecycle ordering, property overrides, and compatibility with Java 21 and the current Spring Boot/Maven setup.

## 2. Complete the build quality gate

- [x] 2.1 Keep or correct the JaCoCo version and explicit `jacoco.minimum.line.coverage=0.85` and `jacoco.minimum.branch.coverage=0.85` properties in `pom.xml`.
- [x] 2.2 Ensure `prepare-agent` instruments tests and `report` plus `check` execute during `verify` at aggregate bundle scope.
- [x] 2.3 Verify that `./mvnw clean verify` creates `target/site/jacoco/index.html`, `jacoco.xml`, and `jacoco.csv`, and that `./mvnw test` does not claim to have run the verification gate.
- [x] 2.4 Run `./mvnw verify -Djacoco.minimum.line.coverage=0.90 -Djacoco.minimum.branch.coverage=0.90` and capture the expected failure without modifying committed thresholds.

## 3. Protect high-risk behavior with tests

- [x] 3.1 Locate or create the unit-test fixture for `UsuarioService` using real request/response objects and Mockito only for repository, mapper, and password-encoder collaborators.
- [x] 3.2 Add a passing registration scenario for an available, normalized email/login and assert the returned response and saved entity behavior.
- [x] 3.3 Add a duplicate-email scenario and assert the public `EMAIL_DUPLICADO` conflict behavior.
- [x] 3.4 Add update scenarios for retaining the user's own email and rejecting an email already owned by another user.
- [x] 3.5 Add an unknown-user lookup scenario and a password-change scenario that asserts the stored value is encoded rather than the submitted plaintext.
- [x] 3.6 Add only additional behavior-focused tests needed for meaningful uncovered branches in authorization, validation, mapping, exception, financial-calculation, or data-changing paths; do not add assertion-free coverage tests.
- [x] 3.7 Run the focused tests after each Red → Green → Refactor cycle, then run the full suite to detect regressions.

## 4. Document the policy and workflow

- [x] 4.1 Update `README.md` or the repository's chosen documentation location with the tools and responsibilities used by the current test suite, correcting names that do not match the repository.
- [x] 4.2 Document the TDD Red → Green → Refactor cycle, behavior-first test selection, Mockito boundaries, test-double vocabulary, and boundary-value examples.
- [x] 4.3 Document how to open and interpret the HTML report, the difference between passing tests and a passing coverage gate, the canonical CI command, and the staged branch target roadmap.
- [x] 4.4 Document the current baseline and the unrelated test-package issue as a follow-up review item rather than deleting it automatically.

## 5. Final verification and handoff

- [x] 5.1 Run formatting checks and the complete `./mvnw clean verify` command with the versioned 85% line and 85% branch thresholds. The full suite passes (166 tests; 94.40% line coverage; 85.10% branch coverage), and Spotless passes across the project.
- [x] 5.2 Confirm all requirements in `specs/test-quality-gate/spec.md` are evidenced by tests, build output, or documentation.
- [x] 5.3 Review `git diff` and `git status` to ensure only the intended Maven, test, and documentation files changed, then record the final measured coverage.
