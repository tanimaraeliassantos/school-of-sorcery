# AI Notes — School of Sorcery

This file describes how I used AI during the development of this project, as requested in the exercise brief.

---

## Tools used

- **Claude (Anthropic)**: planning, architecture decisions, code generation and review
- **Gemini (Google)**: understanding generated code, catching typos, troubleshooting compilation errors
- **ChatGPT (OpenAI)**: generating the background image for the upload screen

---

## How I approached AI use

I never copied and pasted code directly. Every snippet was typed out manually so I understood what was being written before it went into the project. When something didn't make sense, I asked for an explanation before moving on, usually to Gemini, as a second opinion or an opinionated senior.

My rule was if I couldn't explain a line of code myself, even after asking for help, it didn't stay in the project.

---

## What I did before involving AI

Before writing any code I prepared the data models myself, both the Java backend models and the TypeScript frontend interfaces. I wanted to make sure they matched. Also needed to map every field in both JSON files and understand exactly what the system needed to handle before generating anything. That exercise also helped me spot edge cases early.

I also made the main architectural decisions upfront: Angular over React because it is the framework I am most comfortable with; a single table with filters instead of multiple views; one additional feature executed well rather than several unfinished ones; no database because the exercise requires no persistence.

---

## What AI helped with

Planning and structure: I used Claude to think through the project before writing code, going through the rules and defining the structure and actions for the three-day deadline.

Code generation:Claude generated the service and controller layers once I had the models. I reviewed each one of them before accepting it.

UI scaffolding: The ranking table structure and the SCSS base were generated with Claude. I restyled everything to match my own design vision. And added features later on.

Copy: Little easter eggs from the books this project was based on were scattered around, because 'words are our most inexhaustible source of magic'.

Background image: Generated with ChatGPT using a prompt I wrote myself specifying the dark colour palette, size and other styling details.

---

## Where AI was wrong or incomplete

**Package structure.** The file `BackendApplication.java` had the package declared as `com.sorcery.backend` instead of `com.sorcery`, which meant Spring Boot could not find the controller or services. The application compiled and started but the data would not show. I diagnosed and fixed this myself.

**House score calculation.** The initial frontend implementation hardcoded the scoring values from the 2026-2027 rules JSON. This meant the score breakdown panel showed incorrect values when a different rules file was used. I caught this while testing with a second set of rules and decided to move the breakdown calculation to the backend and return the individual point components in the API response.

**Test for house tie-breaking.** A unit test for the case where all houses score zero used a candidate with `untidiness` as a weakness, which gives Raven four points, making it not a true tie. The test passed for the wrong reason. I identified it, understood why it failed, and fixed the test by choosing a weakness that appears in no other house.

**Table not rendering.** The ranking table was invisible after integration. I traced the issue to the container being placed before the table instead of after it — a structural HTML error that I found by reading the DOM.

---

## What I decided not to use AI for

- The design. The visual concept for both screens was mine: the upload page as a dark magical scene with a centred upload zone, the results page as an old council document with serif typography and gold accents. The SCSS was adjusted line by line to match what I had in mind.
- Debugging compilation errors. I worked through Spring Boot dependency conflicts and Angular build issues independently, using the compiler output and Stack Overflow rather than generating fixes.
- Lombok management. I decided to use VSCode for the whole project because I feel more at ease with this IDE, even though I know there was the possibility of Lombok not working as it should. Result of this is that I have a lot more red squiggly lines than if I were to use Eclipse.
- The unit test structure. I decided to keep all tests in a single file organised by behaviour rather than splitting by class. That made the test file easier to read as a specification of the system.
- Feature scope. I chose to add search by name, sortable columns, a candidate detail panel with score breakdown, and house distribution cards. I considered adding filter by house but left it out to keep the implementation clean within the deadline.

---

## Prompts that mattered

Before touching the IDE I asked Claude to act as a senior developer and walk me through the full project plan: architecture, technology choices with justifications, design decisions, and a day-by-day to-do list. That conversation produced the technical memory document that guided the entire build.

A second prompt that mattered came later, when I was testing with a different rules file and noticed the score breakdown panel was showing incorrect values. I had already identified that the frontend was hardcoding the 2026-2027 scoring values instead of reading from the backend response. I used Claude to implement the fix I had already designed, moving the breakdown calculation to the backend and returning the individual point components in the API response.

Both prompts share the same pattern: I knew what I needed before I asked.

---

## Summary

AI accelerated the parts of the project that would otherwise have been repetitive: boilerplate, scaffolding, SCSS structure. The parts that required understanding the problem: data modelling, edge case analysis, debugging, design decisions, test design, I handled myself. Every bug listed above was something I found, diagnosed and fixed. The AI did not catch them. Mischief managed, by me, also a wizard.

---
