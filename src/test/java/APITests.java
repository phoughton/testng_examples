import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;
import org.testng.annotations.BeforeSuite;

public class APITests {
    private final String BASE_URL = "http://cribbage.azurewebsites.net";
    private final String SHOW_URL = "/score/show";

    @BeforeSuite
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

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

        String hand1 = new String(
                "{\"starter\":[5,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}");

        given().header("Content-Type", "application/json")
                .body(hand1.toString())
                .post(BASE_URL + SHOW_URL)
                .then().statusCode(200);
    }

    @DataProvider(name = "kosherHands")
    public Object[][] createData1() {
        return new Object[][] {
                { "{\"starter\":[5,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}" },
                { "{\"starter\":[6,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}" }
        };
    }

    @Test(dataProvider = "kosherHands")
    void test_3(String a_hand) {

        System.out.println(a_hand.toString());
        given().header("Content-Type", "application/json")
                .body(a_hand.toString())
                .post(BASE_URL + SHOW_URL)
                .then().statusCode(200);
    }

    @DataProvider(name = "ofAKindHands")
    public Object[][] createData2() {
        return new Object[][] {
                { "{\"starter\":[5,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}", 29 },
                { "{\"starter\":[6,\"H\"],\"hand\":[[5,\"D\"],[5,\"C\"],[5,\"S\"],[11,\"H\"]],\"crib\":false}", 15 }
        };
    }

    @Test(dataProvider = "ofAKindHands")
    void test_of_a_kind(String a_hand, int a_score) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(a_hand.toString())
                .when()
                .post(SHOW_URL)
                .then()
                .extract().response();


        Assert.assertEquals(200, response.statusCode());
        System.out.println(response.getBody().asString());

        JsonPath jsnPath = response.jsonPath();

        Assert.assertTrue(jsnPath.get("message").toString().contains("of a kind"));
        Assert.assertEquals(jsnPath.getInt("score"), a_score);

    }

}
