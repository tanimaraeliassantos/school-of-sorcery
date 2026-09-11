# School of Sorcery
 
School of Sorcery is a full stack admission management system built with **Java Spring Boot** and **Angular 17** that automates the selection process for a magical academy — processing 200 applications, applying council rules, ranking candidates and assigning each accepted student to one of four houses.
 
This project was built as a technical exercise to demonstrate production-level full stack engineering skills across backend logic, REST API design, frontend architecture and UI/UX.
 
![School of Sorcery](school-sorcery-showcase.gif)
 
---
 
# Project Overview
 
The primary goal of this project is to demonstrate end-to-end full stack development skills across several key areas:
 
- **Rule-Driven Business Logic**
  - All admission rules — vetoes, scoring weights, age ranges, banned families, house assignments — are read from a JSON configuration file at runtime.
  - No values are hardcoded. Changing the rules file changes the outcome without touching the code.
- **Clean Architecture**
  - Clear separation of responsibilities: models, services, and controller each handle one thing.
  - `AdmissionService` handles vetoes, scoring and ranking. `HouseService` handles house assignment. The controller only orchestrates.
- **Deterministic Processing**
  - The same input always produces the same output regardless of the order of the applications file.
- **Unit Testing**
  - Backend unit tests covering vetoes, invitations, scoring, tie-breaking and house assignment.
- **Angular 17 Modern Syntax**
  - Built with standalone components and the new `@if` / `@for` control flow syntax.
---
 
## AI-Assisted Engineering
 
Claude (Anthropic) and Gemini were used as development tools to accelerate implementation while solving complex issues involving:
 
- Business logic design and edge case analysis
- Spring Boot configuration and Jackson date handling
- Angular standalone component architecture
- UI/UX design decisions
All code was reviewed and understood before inclusion. See [AI-NOTES.md](AI-NOTES.md) for a detailed account of how AI was used in this project.
 
---
 
## Architecture
 
The application follows a classic client-server architecture:
 
- **Backend Layer**
  - Spring Boot REST API
  - All business logic lives here
  - Reads `rules.json` from classpath on startup
  - Accepts `applications.json` as a file upload via `POST /api/admissions`
  - Returns 200 results: accepted with house, rejected with reason
- **Service Layer**
  - `RulesLoader` — loads council configuration on startup
  - `AdmissionService` — applies vetoes, calculates scores, ranks candidates
  - `HouseService` — assigns houses independently from admission scoring
- **Presentation Layer**
  - Angular 17 standalone components
  - File upload with drag & drop
  - Ranking table with filters, real-time search and sortable columns
  - Candidate detail panel with score breakdown and house scores
  - House distribution cards
---
 
# Getting Started
 
## Requirements
 
- Java 17
- Node 18+
- Angular CLI (`npm install -g @angular/cli`)
## Run the Backend
 
```bash
cd backend
./mvnw spring-boot:run
```
 
The API will be available at `http://localhost:8080`.
 
## Run the Frontend
 
Open a second terminal:
 
```bash
cd frontend
npm install
ng serve
```
 
Open `http://localhost:4200` in your browser.
 
## Run Backend Tests
 
```bash
cd backend
./mvnw test
```
 
---
 
# How It Works
 
1. Upload an `applications.json` file from the UI
2. The backend reads the council rules from `rules.json` (included in the project)
3. Vetoes are applied in priority order: banned family → age out of range → unacceptable weakness → outside application dates
4. Candidates invited by the headmaster are accepted regardless of vetoes
5. Remaining candidates are scored and ranked. Tie-breaking: younger first → family name alphabetically → first name alphabetically
6. The top 50 receive a place. The rest are rejected with their position as the reason
7. Each accepted candidate is assigned to the house that scores them highest using independent house calculations
8. Results are displayed in the UI: ranking table, house distribution and individual score breakdowns
---
 
# Features
 
### File Upload with Drag & Drop
 
Upload the applications JSON file by clicking or dragging it onto the upload zone.
 
### Full Ranking Table
 
All 200 candidates in a single view showing position, name, age, virtue, score, status and house or rejection reason.
 
### Filters and Search
 
Filter by status (all, accepted, rejected) and search by name in real time.
 
### Sortable Columns
 
Click any column header to sort ascending or descending: rank, age, score, status, house.
 
### House Distribution Cards
 
Four cards showing how many students were assigned to each house with a percentage bar.
 
### Candidate Detail Panel
 
Click any row to open a side panel with:
- Full score breakdown (virtue, family, weakness, age with individual points)
- House scores for accepted candidates — which house won and by how much
---
 
# Technologies
 
## Backend
 
- Java 17
- Spring Boot 3.3
- Maven
- Jackson + JavaTimeModule
- Lombok
- JUnit 5
## Frontend
 
- Angular 17
- TypeScript
- SCSS
- FormsModule + HttpClient
---
 
# Project Structure
 
```
school-of-sorcery/
├── backend/
│   └── src/main/java/com/sorcery/
│       ├── model/              ← Application, Rules, AdmissionResult, RejectionReason
│       ├── service/            ← RulesLoader, AdmissionService, HouseService
│       └── controller/         ← AdmissionController
│   └── src/main/resources/
│       └── rules.json          ← Council configuration for 2026-2027
├── frontend/
│   └── src/app/
│       ├── components/         ← UploadComponent, RankingComponent
│       ├── services/           ← AdmissionService
│       └── models/             ← TypeScript interfaces
├── data/
│   └── applications.json       ← Sample applications file
├── README.md
└── AI-NOTES.md
```
 
---
 
# Author
 
**Tanimara Elias Santos**
 
---
 
# Version
 
**1.0.0**
