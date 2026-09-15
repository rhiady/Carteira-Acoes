## Why

The Java source files are visually dense: indentation and blank-line spacing do not consistently make class structure easy to scan. A formatting pass is needed so contributors can read and maintain the code comfortably without changing its behavior.

## What Changes

- Normalize indentation, line wrapping, and whitespace in Java classes and related tests using a single project formatting convention.
- Add an automated formatter check or formatting command to make the convention repeatable for future changes.
- Preserve runtime behavior, public APIs, persistence mappings, and test semantics.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None.

This is a code-style-only change; it does not alter user-visible system behavior or requirements.

## Impact

- Affected code: Java production and test sources throughout `src/main/java` and `src/test/java`.
- Build tooling: Maven configuration may gain formatter support.
- APIs, dependencies, database schema, and integrations: no intended changes.
