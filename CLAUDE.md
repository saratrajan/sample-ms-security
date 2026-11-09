# CLAUDE.md: sample-ms-security

Test fixture for the risk-based dependency regression tool (`riskbased-testing-ms`). Written from scratch; contains no proprietary code. Never copy code from settlement-coordinator into this repo.

## Facts
- Spring Boot 4.1.1, Spring Cloud 2025.1.2, Java 21, Gradle 8.14.3 wrapper. No Maven.
- Remote: https://github.com/saratrajan/sample-ms-security.git (branch `main`).
- Commit and push as: `Sarat Rajan <5793844+saratrajan@users.noreply.github.com>` (set in repo-local git config).
- `riskbased/expected-impact.json` is the ground truth the tool is scored against. Keep it in sync with any code change.
- `riskbased/feeds/` holds sample package feeds.

## Status (2026-11-09)
- The initial commit was pushed on request WITHOUT ever being compiled (the cloud session had no Maven Central access). First job: `gradlew.bat build`, fix Boot 4.1 issues, keep the design intent in README.md.
- Entries in expected-impact.json marked `"verify": true` (Jackson 2 vs 3 reach) must be confirmed from a real run.
- Add a feed for BOM-managed libraries (tomcat-embed-el, hibernate-validator) once their resolved versions are known (`gradlew.bat dependencies --configuration runtimeClasspath`).
- The planted mismatch must stay: `codegen` configuration resolves bcprov 1.80 while `implementation` pins 1.85.

## Rules
- From the first green build onward, the build must be green before any commit.
- Do not weaken the fixture features listed in README.md; they exist to challenge the tool.
