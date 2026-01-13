<a name="readme-top"></a>
# Obelisk

Developed using Java, Obelisk provides an all-in-one solution for tracking, analyzing, and visualizing state budget information.

---

## About The Project

Obelisk is a Java based application designed for comprehensive monitoring, analysis, and visualization of national budget data.  It enables dynamic budget tracking, multi-year comparisons, scenario modeling, and cross-country financial analysis. Of course, with a clean, user-friendly graphical interface. The system processes official state budget PDFs, converts them into structured CSV data, and provides interactive tools for exploring income, expenses and ministries.

---

## Features

- **Budget Import & Parsing** ~ Extracts and cleans data from PDF budget reports into CSV format
- **Multi-Year Analysis** ~ Compare budget data across a 5-year period with detailed breakdowns
- **Scenario Simulation** ~ Model hypothetical budget changes and evaluate their impact
- **Cross-Country Comparisons** ~ Benchmark national budget data against other countries
- **Interactive GUI** ~ Built with JavaFX for intuitive navigation and real-time feedback
- **Data Validation & Limits** ~ Automated checks for data consistency and threshold alerts
- **Chart Visualizations** ~ Graphical representation of budget categories and trends

---

## Technologies Used

### Development & Programming
- **Java 11+** - Core programming language
- **JavaFX** - GUI framework for desktop interface
- **Maven** - Build automation and dependency management
- **Git** - Version control with multi-branch workflow

### Data Processing
- **CSV parsing** - Custom parsers for budget data conversion
- **PDF extraction tools** - For processing official budget documents
- **Data validation libraries** - Automated consistency checking

### Development Tools
- **VS Code** - Primary Integrated Development Environment
- **GitHub & GitHub Insights** - Repository hosting and project analytics
- **JDK (Java Development Kit)** - Java compiler and runtime

### Testing & Quality
- **JUnit** - Unit testing framework
- **Code coverage tools** - For ensuring test completeness

---

## Team Roles

| Feature                          | Role                | Assignees                     |
|----------------------------------|---------------------|-------------------------------|
| Budget Analysis & Categorization | Backend + Analyst   | Soubasis Nektarios            |
| Charts & Visualizations          | Frontend            | Riga Kornilia, Apostolidou Afroditi |
| Scenario Execution               | Backend + Analyst   | Roumeil Elisavet              |
| Multi-Year Operations            | Backend + Analyst   | Pittas Panagiotis             |
| Cross-Country Comparison         | Analyst             | Chatzinikolaou Pinelopi       |
| Limit Checks & Validation        | Backend             | Germeni Eleftheria            |
| GUI Design & Display             | Frontend            | Riga Kornilia, Apostolidou Afroditi |
| Git Management & Presentations   | Coordinator         | Chatzinikolaou Pinelopi       |
| Promotional Content              | Content             | Germeni Eleftheria, Pittas Panagiotis |
| Documentation                    | Technical Writers   | Roumeil Elisavet, Soubasis Nektarios |

---

## How to Build & Run

### Prerequisites
- Java 11+
- Maven
- JavaFX SDK

### Installation Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/pchatzin/Obelisk.git
2. Navigate into the folder:
   ```bash
   cd Obelisk
3. Build the project with maven:
   ```bash
   mvn clean install
4. Run the application:
   ```bash
   mvn javafx:run

### Using the Application
-  Launch the application using the command above
-  The main GUI window will open with budget data loaded
   Use the menu options to:
-  View budget categories and amounts
-  Compare data across multiple years
-  Run scenario simulations
-  Generate charts and visualizations
-  Export analysis results

## Project Structure
### Main Directories and Files
- **Obelisk/root**
  - `README.md` - This documentation file
  - `pom.xml` - Maven configuration
  - `Test.java` - Test file
  - `metrics.sh` - Code metrics script

- **data/** - Database files
  - `budgetdb.mv.db` - H2 database

- **docs/** - Documentation
  - `Ex guide.docx` - Exercise guide
  - `KED-*.pdf` - Client specifications
  - `tech-report.md` - Technical report

- **promo/** - Promotional materials
  - `demo.mp4` - Demo video

- **src/main/java/com/obelisk/** - Main Java source
  - **budget_db/** - Database layer
    - `BudgetEntry.java`
    - `BudgetEntryRepository.java`
    - `BudgetImporter.java`
    - `PdfBudgetParser.java`
  - Core application classes
    - `BudgetAnalyzer.java`
    - `CountryComp.java`
    - `Scenarios.java`
    - `YearComp.java`
  - GUI controllers
    - `BudgetAnalyzerPage.java`
    - `CountryCompPage.java`
    - `ScenariosPage.java`
    - `YearCompPage.java`
  - Main files
    - `Main.java`
    - `GUI.java`
    - `ObeliskApplication.java`

- **resources/** - Application resources
  - **budget/** - Budget data (2020-2025 CSV/PDF)
  - **countries/** - International data
  - **GUI/** - GUI assets
  - `application.properties` - Configuration

- **uml/** - UML diagrams
  - `*.puml` - PlantUML source files
  - `*.png` - Generated images

### Package Organization
- **com.obelisk** - Main application package with core classes
- **com.obelisk.budget_db** - Database layer for budget data persistence
- **GUI Classes** - BudgetAnalyzerPage, CountryCompPage, ScenariosPage, YearCompPage
- **Core Logic** - BudgetAnalyzer, CountryComp, Scenarios, YearComp
- **Entry Point** - Main.java, ObeliskApplication.java, GUI.java

### Key Directories
- **resources/budget/** - Contains CSV and PDF budget files for 2020-2025
- **resources/countries/** - International comparison data
- **uml/** - PlantUML diagrams and generated images
- **docs/** - Project documentation and specifications
- **promo/** - Promotional materials including demo video

### Technical Design
#### UML Diagram
The application follows a layered architecture with clear separation of concerns:
1. Presentation Layer (GUI) - JavaFX views and controllers
2. Business Logic Layer - Scenario execution, analysis, comparison
3. Data Access Layer - Budget data models and repositories
4. Import Layer - PDF/CSV parsing and data cleaning

### Data Structures & Algorithms
-  BudgetData: Main data structure storing budget entries with fields: line number, category (income/expense), amount, ministry, description
-  BudgetRepository: Central data store acting as application database
-  ScenarioEngine: Executes what-if analyses using budget modifications
-  ComparisonService: Handles multi-year and cross-country comparisons
-  ChartGenerator: Creates visual representations of budget distributions

### Data Flow
1. PDF ~ CSV conversion with data cleaning
2. CSV ~ Java objects via custom parsers
3. Objects stored in BudgetRepository
4. GUI interacts with repository through service classes
5. Results displayed and exported as needed

### Additional Technical Documentation
-  Code Coverage: Unit tests cover core business logic
-  Build Status: Maven builds successfully with all dependencies resolved
-  License: Open Source

### Data Sources
-  Primary Data: Greek State Budget 2025.
-  Historical Data: 5-year budget series for trend analysis.
-  International Data: Selected OECD countries for comparative analysis.

### Testing
-  Unit tests for core logic (JUnit).
-  Integration tests for GUI and data import.
-  Pre-merge validation for each feature branch.
   ```bash
   mvn test

### Links
-  GitHub Repository: https://github.com/pchatzin/Obelisk
-  Demo Video: [demo.mp4](promo/demo.mp4)

### Contact
Project Link: https://github.com/pchatzin/Obelisk

### Why Obelisk?
Named after the ancient monument symbolizing clarity and structure. Obelisk transforms complex budgetary data into actionable insights, promoting transparency and informed decision-making for governmental and public use.

<p align="right">(<a href="#readme-top">back to top</a>)</p>```
