// TestBase.java
package Base;

import Utils.ConfigReaderWriter;
import Utils.LogManager;
import Utils.TestDataHelper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;

public abstract class TestBase {

    // REST Assured configuration
    protected static RestAssuredConfig restAssuredConfig;
    protected RequestSpecification requestSpecification;
    protected String testClassName;

    // Timeout configuration
    private static final int HTTP_TIMEOUT_MS = 30000;

    @BeforeClass
    public void setupTestClass() {
        testClassName = this.getClass().getSimpleName();
        LogManager.log(testClassName, "=== INITIALIZING TEST CLASS: " + testClassName + " ===");

        configureRestAssured();
        configureRequestSpecification();
        verifyUser();

        LogManager.log(testClassName, "=== TEST CLASS SETUP COMPLETED ===");
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        LogManager.logTestStart(testClassName, method.getName());
    }

    @AfterMethod
    public void afterMethod(Method method, ITestResult result) {
        String status = result.isSuccess() ? "PASSED" : "FAILED";
        LogManager.logTestEnd(testClassName, method.getName(), status);

        if (!result.isSuccess()) {
            LogManager.log(testClassName, "ERROR: " + result.getThrowable().getMessage());
        }
    }

    @AfterClass
    public void tearDownTestClass() {
        LogManager.log(testClassName, "=== TEARING DOWN TEST CLASS: " + testClassName + " ===");
    }

    private void configureRestAssured() {
        // Set base URI from config
        String baseUri = ConfigReaderWriter.getPropKey("trello.api.base.url");
        RestAssured.baseURI = baseUri;
        LogManager.log(testClassName, "Configured base URI: " + baseUri);

        restAssuredConfig = config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", HTTP_TIMEOUT_MS)
                        .setParam("http.socket.timeout", HTTP_TIMEOUT_MS));

        LogManager.log(testClassName, "Configured HTTP timeouts: " + HTTP_TIMEOUT_MS + "ms");
    }

    private void configureRequestSpecification() {
        // Get API key and token from config
        String apiKey = ConfigReaderWriter.getPropKey("trello.api.key");
        String apiToken = ConfigReaderWriter.getPropKey("trello.api.token");

        requestSpecification = new RequestSpecBuilder()
                .setConfig(restAssuredConfig)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addQueryParam("key", apiKey)
                .addQueryParam("token", apiToken)
                .addFilters(getDefaultFilters())
                .build();

        LogManager.log(testClassName, "Request specification configured successfully");
    }

    private List<Filter> getDefaultFilters() {
        return Arrays.asList(
                new RequestLoggingFilter(LogDetail.ALL, LogManager.getLogStream(testClassName)),
                new ResponseLoggingFilter(LogDetail.ALL, LogManager.getLogStream(testClassName))
        );
    }

    private void verifyUser() {
        LogManager.log(testClassName, "Starting user verification...");

        Response response = given()
                .spec(requestSpecification)
                .when()
                .get(getEndpoint("trello.endpoint.members"));

        response.then().statusCode(200);
        LogManager.logApiCall(testClassName, "GET", getEndpoint("trello.endpoint.members"), response.getStatusCode());

        JsonPath path = response.jsonPath();
        String username = path.getString("username");
        assertEquals(username, TestDataHelper.getExpectedUsername(), "Username verification failed");

        LogManager.log(testClassName, "User verification successful: " + username);
    }

    // Helper method to get API endpoints from config
    protected String getEndpoint(String endpointKey) {
        return ConfigReaderWriter.getPropKey(endpointKey);
    }

    // Helper method for logging API calls in test methods
    protected void logApiCall(String method, String endpoint, Response response) {
        LogManager.logApiCall(testClassName, method, endpoint, response.getStatusCode());
    }
}
