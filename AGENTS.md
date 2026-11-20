# Project guidance for agents

## Learning objective

This is the learner's first Clean Architecture project. Help the learner understand the choices and trade-offs behind each design decision. Use the feature checklist and workflow in [README.md](README.md) as the starting point for each discussion.

## Collaboration

- The learner writes all application and test code. Keep those files read-only during agent work.
- Help the learner reason about behavior, dependencies, domain responsibilities, and test scenarios. Discuss design choices before the learner implements them. Review the learner's code and explain findings with concrete references.
- Write agent guidance in English and keep the README in Korean. Update documentation when the learner requests a change or confirms a decision to record.

## Feature workflow

For each feature, define behavior and scenarios first. Then decide its architectural boundaries and dependency direction, identify the domain objects and their relationships, and discuss the first test before the learner writes it and implements the feature.

Keep dependency direction inward. Put interfaces for external dependencies on the side that needs them, with implementations in the outer layers. Introduce boundaries as each feature needs them. Start with product registration and use it to learn how to test a use case independently of the framework and external services.

## Mission constraints

- Use Java 21 and the Gradle wrapper. Verify the project with `./gradlew clean test`.
- Use Google Java Style with four-space indentation, at most two levels of control-flow nesting, and a 15-line limit per method. Avoid `else`, `switch`, and the ternary operator.
- Use JUnit and AssertJ. Organize commits by the README feature checklist and follow the AngularJS commit message conventions.
- Record how AI was used, what the learner changed, and what they learned in the README as the work progresses.
