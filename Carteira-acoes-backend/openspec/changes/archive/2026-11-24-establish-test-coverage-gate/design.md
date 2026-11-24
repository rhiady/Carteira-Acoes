## Context

The project already uses Maven, JUnit 5, AssertJ, Mockito, MockMvc, Spring Security Test, H2, and Liquibase across unit and integration tests. The change is build- and test-quality oriented: it must provide coverage evidence without changing runtime API or persistence behavior. See proposal.md for motivation and `specs/test-quality-gate/spec.md` for the observable contract.

## Goals / Non-Goals

**Goals:**

- Attach JaCoCo to the Maven test lifecycle and run its report/check goals during `verify`.
- Keep line and branch thresholds explicit, configurable, and visible in `pom.xml`.
- Preserve the distinction between `test` and `verify`.
- Add focused tests for relevant uncovered behavior, prioritizing authentication/authorization, data-changing rules, calculations, validation, mapping, and exception paths.
- Document the baseline, TDD loop, dublê guidance, boundary testing, report reading, CI command, and staged branch-coverage roadmap.

**Non-Goals:**

- No production endpoint, domain model, database schema, or authentication behavior change.
- No automatic deletion or relocation of the unrelated `LocalUserDetailsServiceTest`.
- No mutation-testing tool or CI workflow change is included in this update.
- No artificial tests or blanket coverage exclusions.

## Decisions

1. **Use JaCoCo through the existing Maven build.** Configure `prepare-agent` for test instrumentation, `report` during `verify`, and `check` during `verify`. This keeps local and CI behavior identical and avoids a second test runner. A separate reporting script was rejected because it could drift from the build developers actually run.

2. **Check aggregate bundle coverage.** Apply minimum 85% line and 85% branch limits at the bundle level. Package-specific gates remain out of scope because they would add policy complexity without improving the requested test-suite coverage target.

3. **Expose thresholds as Maven properties.** Store JaCoCo and minimum-coverage versions/values in project properties so a temporary command-line override can demonstrate a stricter target without editing committed configuration. Permanent policy changes require a reviewed repository change.

4. **Improve coverage through behavior-first tests.** Use Mockito for service collaborators and real MVC/security/H2/Liquibase integration for resource behavior, following the project's existing test split. Do not mock DTOs or the class under test. The test plan will use Red → Green → Refactor cycles for `UsuarioService` scenarios and selected high-risk branches.

5. **Document rather than automate educational guidance.** TDD, test-double definitions, mutation-testing direction, report interpretation, and the staged branch roadmap belong in project documentation; they do not require new runtime abstractions.

## Risks / Trade-offs

- **[Risk]** The current repository may not yet meet the 85% line and branch floors. → **Mitigation:** measure with `clean verify`, add behavior-focused tests for meaningful uncovered paths, and do not weaken the requested floors.
- **[Risk]** JaCoCo or Maven plugin compatibility may fail with the project's Java/Maven versions. → **Mitigation:** use the project-supported JaCoCo release, run the full verification command, and resolve compatibility before raising any threshold.
- **[Risk]** A new gate can expose existing uncovered branches and fail a previously green build. → **Mitigation:** add behavior-focused tests for relevant paths; do not weaken the gate or hide production code.
- **[Risk]** CI may run `test` only and silently skip the gate. → **Mitigation:** document and configure the canonical command as `./mvnw --batch-mode clean verify` wherever CI is added or updated.

## Migration Plan

1. Add the JaCoCo Maven configuration and documentation.
2. Run the full verification command and inspect HTML/XML/CSV output.
3. Add or refine relevant tests until the initial floors pass.
4. Commit the build and documentation changes together so the policy and evidence workflow remain aligned.
5. Roll back by reverting the JaCoCo configuration and related tests/docs if compatibility cannot be resolved; no runtime data migration is required.
