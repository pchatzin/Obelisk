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

## Getting Started

### Prerequisites
- Java 11+
- Maven
- JavaFX SDK

### Installation
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
-  Demo Video: Available in /promo/demo.mp4

### Contact
Project Link: https://github.com/pchatzin/Obelisk

### Why Obelisk?
Named after the ancient monument symbolizing clarity and structure. Obelisk transforms complex budgetary data into actionable insights, promoting transparency and informed decision-making for governmental and public use.

<p align="right">(<a href="#readme-top">back to top</a>)</p>```
