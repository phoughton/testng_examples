import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class APITests {
    private final String BASE_URL = "http://cribbage.azurewebsites.net";
    private final String SHOW_URL = "/score/show";

    @Test
    void test_1() {
        Response resp = RestAssured.post(BASE_URL + SHOW_URL);
        System.out.println("Status code: " + resp.getStatusCode());
        System.out.println("Time taken: " + resp.getTime());
        System.out.println("Body: " + resp.getBody().asString());
        int statusCode = resp.getStatusCode();
        Assert.assertEquals(statusCode, 400);
    }

    @Test
    void test_2() {

        String hand1 = new String("{\"starter\":[5,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}");

        given().header("Content-Type", "application/json")
                .body(hand1.toString())
                .post(BASE_URL + SHOW_URL)
                .then().statusCode(200);
    }

    @DataProvider(name = "kosherHands")
    public Object[][] createData1() {
        return new Object[][] {
                {"{\"starter\":[5,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}"},
                {"{\"starter\":[6,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}"}
        };
    }
    @Test(dataProvider = "kosherHands")
    void test_3(String a_hand) {

        //String hand1 = new String("{\"starter\":[5,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}");
        System.out.println(a_hand.toString());
        given().header("Content-Type", "application/json")
                .body(a_hand.toString())
                .post(BASE_URL + SHOW_URL)
                .then().statusCode(200);
    }

}
