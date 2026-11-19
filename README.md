# HMS — Household Management System

A personal web application for running household records, designed, built and maintained by me in my spare time.

## Features

- **Bookkeeping** — record and search household expenses.
- **Guloo** — a timeline of important family milestones.
- **Desktop and mobile layouts** — the app detects the browser's screen resolution and serves the matching version automatically.

## Architecture

HMS applies two design patterns I originally created for enterprise systems:

- **Business Process Unit (BPU)** — each business operation is modeled as a "workstation": it checks what input it receives and what state that input is in, verifies it before processing, then runs.
- **Traveler** — multi-step transactions are composed as a sequence of described steps (like a manufacturing traveler card), which stops at the first failed step.

These patterns are described in my PhD research on object-oriented ontology for business–IT alignment (National Tsing Hua University, 2024).

## Tech stack

- Java, ZK 9.6
- Maven
- Docker, deployed to the cloud on Render

## Project structure

| Folder | Contents |
| --- | --- |
| `src` | Application source code |
| `srcTest` | Tests |
| `WebContent` | ZK pages and web resources |
| `deployment`, `release` | Deployment and release files |

## Note

The live site holds private household data, so only the source code is public.
