## 1. Formatter setup

- [x] 1.1 Configure a pinned Spotless Maven plugin using `google-java-format` for Java 21 production and test source trees.
- [x] 1.2 Confirm `./mvnw spotless:apply` applies the canonical format and `./mvnw spotless:check` verifies it without modifying files.

## 2. Source formatting baseline

- [x] 2.1 Apply the formatter to every tracked Java file under `src/main/java` and `src/test/java`.
- [x] 2.2 Review the resulting diff to ensure Java source changes are mechanical formatting only and do not include unrelated working-tree changes.

## 3. Verification

- [x] 3.1 Run `./mvnw spotless:check` successfully after the formatting pass.
- [x] 3.2 Run the complete Maven test suite successfully with the project's Java 21 toolchain.
