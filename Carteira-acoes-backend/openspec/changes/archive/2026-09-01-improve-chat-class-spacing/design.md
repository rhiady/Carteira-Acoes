## Context

See `proposal.md` for motivation. The Maven project has Java 21 source and test trees but no configured code formatter or style check. Source formatting is therefore dependent on an individual contributor's IDE settings.

## Goals / Non-Goals

**Goals:**

- Establish one automated, repeatable Java formatting convention for all maintained source and test classes.
- Reformat the existing Java code so indentation, wrapping, and whitespace consistently follow that convention.
- Give developers a Maven command to apply formatting and a lifecycle-safe command to verify it.

**Non-Goals:**

- Changing code behavior, package structure, APIs, dependencies used by the application, or database artifacts.
- Enforcing broader static analysis rules such as naming, documentation, or complexity limits.
- Formatting generated files, migrations, logs, or non-Java assets.

## Decisions

### Use the Spotless Maven plugin with `google-java-format`

Configure Spotless to format `src/main/java` and `src/test/java` with `google-java-format`, exposing `spotless:apply` for local fixes and `spotless:check` for verification. This provides a deterministic convention that requires no IDE-specific configuration and supports Java 21.

Alternative considered: Checkstyle. It can report style violations but does not provide an equally direct, canonical automated reformatting workflow. Manual IDE formatting was also rejected because it leaves outcomes configuration-dependent.

### Reformat all Java source files in one dedicated style-only change

Apply the formatter to all tracked Java production and test files, without hand-editing logic. A whole-tree formatting pass prevents mixed styles and establishes a clean baseline for later formatter checks.

Alternative considered: format only currently edited files. That would retain the inconsistent layout the change is intended to solve and makes future checks difficult to introduce.

### Verify semantic safety with the existing Maven test suite

The formatting check confirms style conformance; the existing test suite confirms that mechanical formatting did not change compilation or behavior. Review should expect whitespace-only diffs in Java sources, apart from Maven configuration needed for the formatter.

## Risks / Trade-offs

- [A repository-wide formatting diff can overlap active feature work] → Apply and review this change separately, and rebase feature branches before merging where necessary.
- [Formatter output may surprise developers accustomed to IDE defaults] → Document the Maven apply/check commands in the change handoff and use them as the canonical convention.
- [A plugin version can introduce Java compatibility issues] → Pin a stable Spotless plugin version and run both formatter verification and the complete Maven test suite under the project's Java 21 toolchain.

## Migration Plan

1. Add the formatter plugin configuration and run the formatter across Java sources.
2. Run formatter verification and the complete Maven test suite.
3. Merge as a standalone formatting baseline; contributors run `./mvnw spotless:apply` before future commits.

Rollback is a normal source-control revert of the formatter configuration and mechanical formatting commit; no data migration or deployment change is involved.
