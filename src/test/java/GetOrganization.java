import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

public class GetOrganization {

        static   EnvironmentVariables env = new EnvironmentVariables();

        public static void main(String[] args) {
            RestAssured.baseURI ="https://api.trello.com/1/members/me";
            RequestSpecification request = RestAssured.given();
            request.queryParam("key", env.keys);
            request.queryParam("token", env.tokens);
            Response response =  request.get();
            response.prettyPrint();
            JsonPath path = response.jsonPath();
            String username = path.getString("username");
            Assert.assertEquals(username ,"abdullahmostafa6" );

        }
    }

