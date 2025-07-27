# Trello API Test Automation

This project is a test automation suite for the Trello API using **RestAssured**, **TestNG**, and **Java**. It includes automated tests for creating, retrieving, and deleting Trello organizations, boards, and lists, ensuring robust validation of API functionality. The project is designed to be modular, maintainable, and easy to extend for additional test cases.

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Project Structure](#project-structure)
- [Running the Tests](#running-the-tests)
- [Configuration](#configuration)
- [Test Cases](#test-cases)
- [Contributing](#contributing)

## Project Overview
This project automates testing of the Trello API endpoints for managing organizations, boards, and lists. It uses RestAssured for HTTP requests, TestNG for test execution, and a custom configuration reader for managing test data and API credentials. The tests validate key functionalities such as creating resources, verifying their properties, and cleaning up after tests.

GitHub Repository: [Abdullahmostafah/EgFwd_API_Postman_RestAssured](https://github.com/Abdullahmostafah/EgFwd_API_Postman_RestAssured)

## Features
- **Modular Design**: Organized into utility, base, and test packages for clean code structure.
- **Configuration Management**: Uses a properties file to manage API keys, tokens, and test data.
- **Thread-Safe Configuration**: Implements `ReentrantLock` for safe concurrent access to configuration properties.
- **Comprehensive Test Coverage**: Includes tests for:
  - Creating and verifying Trello organizations.
  - Creating and retrieving boards within organizations.
  - Creating, retrieving, archiving, and unarchiving lists on boards.
- **Logging**: Detailed request and response logging using RestAssured filters.
- **Cleanup**: Automatic deletion of created resources after tests to maintain a clean environment.

## Prerequisites
To run this project, ensure you have the following installed:
- **Java 11** or later
- **Maven** (for dependency management and test execution)
- **Git** (to clone the repository)
- A valid Trello API key and token (obtainable from [Trello Developer](https://trello.com/app-key))

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Abdullahmostafah/EgFwd_API_Postman_RestAssured.git
   cd EgFwd_API_Postman_RestAssured
   ```

2. **Configure Trello API Credentials**:
   - Open `src/test/resources/config.properties`.
   - Replace the placeholder values for `trello.api.key` and `trello.api.token` with your valid Trello API credentials.
   - Update `trello.username` with your Trello username for user verification tests.

   Example:
   ```properties
   trello.api.key=your_api_key_here
   trello.api.token=your_api_token_here
   trello.username=your_trello_username
   ```

3. **Install Dependencies**:
   Run the following command to download all required dependencies:
   ```bash
   mvn clean install
   ```

## Project Structure
```
EgFwd_API_Postman_RestAssured/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Base/
│   │   │   │   └── TestBase.java        # Base class for test setup and configuration
│   │   │   ├── Utils/
│   │   │   │   ├── ConfigReaderWriter.java  # Utility for reading/writing config properties
│   │   │   │   └── TestDataHelper.java     # Helper for retrieving test data
│   ├── test/
│   │   ├── java/
│   │   │   ├── Tests/
│   │   │   │   ├── BoardTests.java       # Tests for Trello board operations
│   │   │   │   ├── ListTests.java        # Tests for Trello list operations
│   │   │   │   └── OrganizationTests.java # Tests for Trello organization operations
│   │   ├── resources/
│   │   │   └── config.properties         # Configuration file for API keys and test data
├── TestNG.xml                            # TestNG suite configuration
├── pom.xml                               # Maven project configuration
└── README.md                             # Project documentation
```

## Running the Tests
To execute all tests, run:
```bash
mvn test
```

This command uses the `TestNG.xml` suite file to run the test classes (`OrganizationTests`, `BoardTests`, `ListTests`) in parallel with a thread count of 3.

### Test Output
- Test results are logged to the console, including request and response details.
- Created resource IDs (organizations, boards, lists) are printed for reference.
- Assertions verify expected behavior, and failures are reported with detailed messages.

## Configuration
The `config.properties` file (`src/test/resources/config.properties`) contains:
- **API Credentials**:
  - `trello.api.key`: Your Trello API key.
  - `trello.api.token`: Your Trello API token.
  - `trello.api.base.url`: Trello API base URL (`https://api.trello.com/1/`).
  - `trello.username`: Expected Trello username for verification.
- **Endpoints**:
  - `trello.endpoint.members`: `/members/me`
  - `trello.endpoint.organizations`: `/organizations`
  - `trello.endpoint.boards`: `/boards`
  - `trello.endpoint.lists`: `/lists`
- **Test Data**:
  - `test.organization.name`: Name for test organization.
  - `test.board.name`: Name for test board.
  - `test.list.name`: Name for test list.

## Test Cases
### Organization Tests
- **createOrganization**: Creates a new organization and verifies the response status.
- **getOrganization**: Retrieves the created organization and validates its name.

### Board Tests
- **createBoardInOrganization**: Creates a board within an organization and verifies the response.
- **getBoard**: Retrieves the board and validates its name and organization ID.

### List Tests
- **createList**: Creates a list on a board and verifies the response.
- **getListsOnBoard**: Retrieves all lists on the board and validates the list name.
- **archiveAndUnarchiveList**: Archives and unarchives a list, verifying both operations.

Each test class includes setup to create required resources and cleanup to delete them after execution.

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes and commit (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a Pull Request.

Please ensure your code follows the existing style and includes appropriate tests.
