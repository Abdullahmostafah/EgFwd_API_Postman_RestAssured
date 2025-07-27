
// OrganizationTests.java
package Tests;

import Base.TestBase;
import Utils.LogManager;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import Utils.TestDataHelper;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;

public class OrganizationTests extends TestBase {

    private String organizationId;

    @Test(priority = 1)
    public void createOrganization() {
        LogManager.log(testClassName, "Creating organization: " + TestDataHelper.getOrganizationName());

        Response response = given()
                .spec(requestSpecification)
                .queryParam("displayName", TestDataHelper.getOrganizationName())
                .when()
                .post(getEndpoint("trello.endpoint.organizations"));

        response.then().statusCode(200);
        logApiCall("POST", getEndpoint("trello.endpoint.organizations"), response);

        JsonPath path = response.jsonPath();
        organizationId = path.getString("id");

        LogManager.log(testClassName, "Successfully created organization with ID: " + organizationId);
    }

    @Test(priority = 2)
    public void getOrganization() {
        LogManager.log(testClassName, "Retrieving organization with ID: " + organizationId);

        String endpoint = getEndpoint("trello.endpoint.organizations") + "/" + organizationId;
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get(endpoint);

        response.then().statusCode(200);
        logApiCall("GET", endpoint, response);

        JsonPath path = response.jsonPath();
        assertEquals(path.getString("displayName"), TestDataHelper.getOrganizationName(),
                "Organization name doesn't match");

        LogManager.log(testClassName, "Organization retrieval successful - Name verified: " + path.getString("displayName"));
    }

    @AfterClass
    public void cleanup() {
        if (organizationId != null) {
            LogManager.log(testClassName, "Starting cleanup for organization ID: " + organizationId);

            String endpoint = getEndpoint("trello.endpoint.organizations") + "/" + organizationId;
            Response response = given()
                    .spec(requestSpecification)
                    .when()
                    .delete(endpoint);

            response.then().statusCode(200);
            logApiCall("DELETE", endpoint, response);

            LogManager.log(testClassName, "Successfully cleaned up organization with ID: " + organizationId);
        }
    }
}