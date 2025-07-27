
        package Tests;

import Base.TestBase;
import Utils.LogManager;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.*;
import Utils.TestDataHelper;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;

public class BoardTests extends TestBase {

    private String organizationId;
    private String boardId;

    @BeforeClass(dependsOnMethods = "setupTestClass")
    public void createOrganization() {
        LogManager.log(testClassName, "Creating organization for board tests: " + TestDataHelper.getOrganizationName());

        Response response = given()
                .spec(requestSpecification)
                .queryParam("displayName", TestDataHelper.getOrganizationName())
                .when()
                .post(getEndpoint("trello.endpoint.organizations"));

        response.then().statusCode(200);
        logApiCall("POST", getEndpoint("trello.endpoint.organizations"), response);

        organizationId = response.jsonPath().getString("id");
        LogManager.log(testClassName, "Created organization for board tests with ID: " + organizationId);
    }

    @Test(priority = 1)
    public void createBoardInOrganization() {
        LogManager.log(testClassName, "Creating board: " + TestDataHelper.getBoardName() + " in organization: " + organizationId);

        Response response = given()
                .spec(requestSpecification)
                .queryParam("name", TestDataHelper.getBoardName())
                .queryParam("idOrganization", organizationId)
                .when()
                .post(getEndpoint("trello.endpoint.boards"));

        response.then().statusCode(200);
        logApiCall("POST", getEndpoint("trello.endpoint.boards"), response);

        JsonPath path = response.jsonPath();
        boardId = path.getString("id");
        LogManager.log(testClassName, "Successfully created board with ID: " + boardId);
    }

    @Test(priority = 2)
    public void getBoard() {
        LogManager.log(testClassName, "Retrieving board with ID: " + boardId);

        String endpoint = getEndpoint("trello.endpoint.boards") + "/" + boardId;
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get(endpoint);

        response.then().statusCode(200);
        logApiCall("GET", endpoint, response);

        JsonPath path = response.jsonPath();
        assertEquals(path.getString("name"), TestDataHelper.getBoardName(),
                "Board name doesn't match");
        assertEquals(path.getString("idOrganization"), organizationId,
                "Organization ID doesn't match");

        LogManager.log(testClassName, "Board retrieval successful - Name: " + path.getString("name") +
                ", Organization ID: " + path.getString("idOrganization"));
    }

    @AfterClass
    public void cleanup() {
        if (boardId != null) {
            LogManager.log(testClassName, "Starting cleanup for board ID: " + boardId);

            String boardEndpoint = getEndpoint("trello.endpoint.boards") + "/" + boardId;
            Response boardResponse = given()
                    .spec(requestSpecification)
                    .when()
                    .delete(boardEndpoint);

            boardResponse.then().statusCode(200);
            logApiCall("DELETE", boardEndpoint, boardResponse);
            LogManager.log(testClassName, "Successfully cleaned up board with ID: " + boardId);
        }

        if (organizationId != null) {
            LogManager.log(testClassName, "Starting cleanup for organization ID: " + organizationId);

            String orgEndpoint = getEndpoint("trello.endpoint.organizations") + "/" + organizationId;
            Response orgResponse = given()
                    .spec(requestSpecification)
                    .when()
                    .delete(orgEndpoint);

            orgResponse.then().statusCode(200);
            logApiCall("DELETE", orgEndpoint, orgResponse);
            LogManager.log(testClassName, "Successfully cleaned up organization with ID: " + organizationId);
        }
    }
}