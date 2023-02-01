import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

public class TrelloApi {

    public class TRELLORESTAPIs {
        static EnvironmentVariables env = new EnvironmentVariables();

        // public static  String[] IDS = new String[3]  ;
        //public static String ID;
        public static void main(String[] args) {

            //Create_Organization_Then_Get_ID & Create_Get_Boards
            RestAssured.baseURI = "https://api.trello.com/1/organizations";
            RequestSpecification request = RestAssured.given();
            request.header("Content-Type", "application/json");
            request.queryParam("displayName", "Testing Organization1");
            request.queryParam("key", env.keys);
            request.queryParam("token", env.tokens);
            Response response = request.post();
            response.prettyPrint();
            int code = response.getStatusCode();
            System.out.println("Status Code is " + code);
            Assert.assertEquals(code, 200);
            JsonPath path = response.jsonPath();
            String ID = path.getString("id");
            System.out.println("Organization_ID is " + ID);
//***************************************************************************************************
            // Create_Board_inside Organization

            RestAssured.baseURI = "https://api.trello.com/1/boards/";
            RequestSpecification request1 = RestAssured.given();
            request1.header("Content-Type", "application/json");
            request1.queryParam("name", "BoardInsideOrganization");
            request1.queryParam("key", env.keys);
            request1.queryParam("token", env.tokens);
            request1.queryParam("OrganizationID", ID);
            Response response1 = request1.post();
            response1.prettyPrint();
            JsonPath path1 = response1.jsonPath();
            String IDBoard = path1.getString("id");
            System.out.println("Board_ID is " + IDBoard);
            //***************************************************************************************************
            //Get_boards in an organization:
            //https://api.trello.com/1/"+"organizations/"+ID+"/boards
            RestAssured.baseURI = "https://trello.com/1/boards/xBkMKUrd/";
            RequestSpecification request2 = RestAssured.given();
            request2.header("Content-Type", "application/json");
            request2.queryParam("key", env.keys);
            request2.queryParam("token", env.tokens);
            Response response2 = request2.get();
            response2.prettyPrint();
            String statusLine = response2.getStatusLine();
            System.out.println("Status line is " + statusLine);
            Assert.assertEquals(statusLine, "HTTP/1.1 200 OK");

            //***************************************************************
            //Create_List_Inside_the_Organization
            //https://api.trello.com/1/lists?name={name}&idBoard={{BoardsId}}&key={{APIKEY}}&token={{APIToken}}
            RestAssured.baseURI = "https://api.trello.com/1/lists/";
            RequestSpecification requestList = RestAssured.given();
            requestList.header("Content-Type", "application/json");
            requestList.queryParam("name", "Lists");
            requestList.queryParam("idBoard", IDBoard);//https://trello.com/b/xBkMKUrd/boardfeb#
            // System.out.println(IDBoard);
            requestList.queryParam("key", env.keys);
            requestList.queryParam("token", env.tokens);
            Response response_List = requestList.post();
            response_List.prettyPrint();
            JsonPath pathn = response_List.jsonPath();
            String ID_List = pathn.getString("id");
            System.out.println("List_ID  is  " + ID_List);


            //*********************************************
            //Get_List-
            // https://api.trello.com/1/boards/{{BoardsId}}/lists?key={{APIKEY}}&token={{APIToken}}
            RestAssured.baseURI = "https://api.trello.com/1/boards/" + IDBoard + "/lists";
            RequestSpecification request_List = RestAssured.given();
            request_List.header("Content-Type", "application/json");
            request_List.queryParam("key", env.keys);
            request_List.queryParam("token", env.tokens);
            request_List.queryParam("name", "List_two");
            Response responseList = request_List.get();
            responseList.prettyPrint();
            String statusList = responseList.getStatusLine();
            System.out.println("Status line for Get List " + statusList);
            Assert.assertEquals(statusList, "HTTP/1.1 200 OK");


            //Archive_List
            //Archive_List
            //https://api.trello.com/1/lists/{{ListId}}/closed?key={{APIKEY}}&token={{APIToken}}&value=true
            RestAssured.baseURI = "https://api.trello.com/1/lists/" + ID_List + "/closed";
            RequestSpecification archiveList = RestAssured.given();
            archiveList.queryParam("key", env.keys);
            archiveList.queryParam("token", env.tokens);
            archiveList.queryParam("value", true);
            Response putList = archiveList.put();
            putList.prettyPrint();
            int ArchiiveCode = putList.getStatusCode();
            System.out.println("Status Code for Archiving is " + ArchiiveCode);
            Assert.assertEquals(ArchiiveCode, 200);


            //UnArchive_List
            //https://api.trello.com/1/lists/{{ListId}}/closed?key={{APIKEY}}&token={{APIToken}}&value=false
            RestAssured.baseURI = "https://api.trello.com/1/lists/" + ID_List + "/closed";
            RequestSpecification unarchiveList = RestAssured.given();
            // requestList.header("Content-Type" , "application/json");
            unarchiveList.queryParam("key", env.keys);
            unarchiveList.queryParam("token", env.tokens);
            unarchiveList.queryParam("value", false);
            Response putList1 = unarchiveList.put();
            putList1.prettyPrint();
            int unArchiiveCode = putList1.getStatusCode();
            System.out.println("Status_UnArchive_Code is " + unArchiiveCode);
            Assert.assertEquals(unArchiiveCode, 200);

            //Delete_Board
            //https://api.trello.com/1/boards/{{BoardsId}}?key={{APIKEY}}&token={{APIToken}}
            RestAssured.baseURI = "https://api.trello.com/1/boards/" + IDBoard;
            RequestSpecification delBoard = RestAssured.given();
            delBoard.queryParam("key", env.keys);
            delBoard.queryParam("token", env.tokens);
            Response Del_response = delBoard.delete();
            Del_response.prettyPrint();
            int del_BoardCode = Del_response.getStatusCode();
            System.out.println("Status_Delete_Board Code is " + del_BoardCode);
            Assert.assertEquals(del_BoardCode, 200);

            //https://api.trello.com/1/organizations/{{OrganizationID}}?key={{APIKEY}}&token={{APIToken}}
            RestAssured.baseURI = "https://api.trello.com/1/organizations/" + ID;
            RequestSpecification delOrg = RestAssured.given();
            delOrg.queryParam("key", env.keys);
            delOrg.queryParam("token", env.tokens);
            Response Del_Org_response = delOrg.delete();
            Del_Org_response.prettyPrint();
            int del_Orga_Code = Del_Org_response.getStatusCode();
            System.out.println("Status_Delete_Organization Code is " + del_Orga_Code);
            Assert.assertEquals(del_Orga_Code, 200);

            //initializing array
            // IDS={ ID ,IDBoard ,ID_List };
       /* IDS[0] = ID;
        IDS[1] = IDBoard;
        IDS[2] = ID_List;*/
        }


    }
}
