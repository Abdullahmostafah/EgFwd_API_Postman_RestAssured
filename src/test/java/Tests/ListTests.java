
package Tests;

import Base.TestBase;
import Utils.LogManager;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.*;
import Utils.TestDataHelper;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class ListTests extends TestBase {

    private String organizationId;
    private String boardId;
    private String listId;

    @BeforeClass(dependsOnMethods = "setupTestClass")
    public void setup() {
        LogManager.log(testClassName, "Setting up prerequisites for list tests...");

        // Create organization
        LogManager.log(testClassName, "Creating organization: " + TestDataHelper.getOrganizationName());
        Response orgResponse = given()
                .spec(requestSpecification)
                .queryParam("displayName", TestDataHelper.getOrganizationName())
                .when()
                .post(getEndpoint("trello.endpoint.organizations"));

        orgResponse.then().statusCode(200);
        logApiCall("POST", getEndpoint("trello.endpoint.organizations"), orgResponse);
        organizationId = orgResponse.jsonPath().getString("id");
        LogManager.log(testClassName, "Created organization for list tests with ID: " + organizationId);

        // Create board
        LogManager.log(testClassName, "Creating board: " + TestDataHelper.getBoardName());
        Response boardResponse = given()
                .spec(requestSpecification)
                .queryParam("name", TestDataHelper.getBoardName())
                .queryParam("idOrganization", organizationId)
                .when()
                .post(getEndpoint("trello.endpoint.boards"));

        boardResponse.then().statusCode(200);
        logApiCall("POST", getEndpoint("trello.endpoint.boards"), boardResponse);
        boardId = boardResponse.jsonPath().getString("id");
        LogManager.log(testClassName, "Created board for list tests with ID: " + boardId);
    }

    @Test(priority = 1)
    public void createList() {
        LogManager.log(testClassName, "Creating list: " + TestDataHelper.getListName() + " on board: " + boardId);

        Response response = given()
                .spec(requestSpecification)
                .queryParam("name", TestDataHelper.getListName())
                .queryParam("idBoard", boardId)
                .when()
                .post(getEndpoint("trello.endpoint.lists"));

        response.then().statusCode(200);
        logApiCall("POST", getEndpoint("trello.endpoint.lists"), response);

        JsonPath path = response.jsonPath();
        listId = path.getString("id");
        LogManager.log(testClassName, "Successfully created list with ID: " + listId);
    }

    @Test(priority = 2)
    public void getListsOnBoard() {
        LogManager.log(testClassName, "Retrieving lists on board: " + boardId);

        String endpoint = getEndpoint("trello.endpoint.boards") + "/" + boardId + "/lists";
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get(endpoint);

        response.then().statusCode(200);
        logApiCall("GET", endpoint, response);

        JsonPath path = response.jsonPath();
        // Find the list we created in the response array
        String listName = null;
        for (int i = 0; i < path.getList("").size(); i++) {
            if (path.getString("[" + i + "].id").equals(listId)) {
                listName = path.getString("[" + i + "].name");
                break;
            }
        }
        assertEquals(listName, TestDataHelper.getListName(), "List name doesn't match");
        LogManager.log(testClassName, "List retrieval successful - Found list: " + listName);
    }

    @Test(priority = 3)
    public void archiveAndUnarchiveList() {
        LogManager.log(testClassName, "Starting archive/unarchive operations for list: " + listId);

        // Archive list
        String archiveEndpoint = getEndpoint("trello.endpoint.lists") + "/" + listId + "/closed";
        Response archiveResponse = given()
                .spec(requestSpecification)
                .queryParam("value", true)
                .when()
                .put(archiveEndpoint);
        archiveResponse.then().statusCode(200);
        logApiCall("PUT", archiveEndpoint + "?value=true", archiveResponse);
        LogManager.log(testClassName, "Successfully archived list with ID: " + listId);

        // Unarchive list
        Response unarchiveResponse = given()
                .spec(requestSpecification)
                .queryParam("value", false)
                .when()
                .put(archiveEndpoint);
        unarchiveResponse.then().statusCode(200);
        logApiCall("PUT", archiveEndpoint + "?value=false", unarchiveResponse);
        LogManager.log(testClassName, "Successfully unarchived list with ID: " + listId);
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
        }
    }
}