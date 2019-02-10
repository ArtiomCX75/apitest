package tests;

import helpers.RequestSender;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

@Test
public class LoginTest {
    final String EMAIL_KEY = "email";
    final String PASSWORD_KEY = "password";
    final String TOKEN_KEY = "token";
    final String ERROR_KEY = "error";

    @Test(dataProvider = "LoginPositive")
    public void loginPositive(String email, String password, int expectedStatus, String token) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EMAIL_KEY, email).put(PASSWORD_KEY, password);
        List list = RequestSender.sendPost(jsonObject);
        int httpStatus = (int) list.get(0);
        JSONObject responseJson = new JSONObject(new JSONTokener(list.get(1).toString()));
        System.out.println("ststus " + httpStatus + "     json " + responseJson.toString());
        String responseToken = responseJson.get(TOKEN_KEY).toString();
        System.out.println("response " + responseToken);
        Assert.assertEquals(httpStatus, expectedStatus); //received, expected
        Assert.assertEquals(responseToken, token);
    }

    @DataProvider(name = "LoginPositive")
    public static Object[][] credentialsPositive() {
        return new Object[][]{
                {"peter@klaven", "cityslicka", 200, "QpwL5tke4Pnpja7X"},
                {"john@klaven", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //another email
                {"peterklaven", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //email without '@'
                {"@klaven", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //email without account name
                {"peter@", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //email without domain name
                {"p", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //min email length
                {"peterpeterpeterpeterpeterpeterpeterpeterpeterpeter@klavenklavenklavenklavenklavenklavenklavenklavenk", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //max email length (100)
                {"PeTeR@KlAvEn", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //CAPITAL and lowercase characters in email
                {"Питер@klaven", "cityslicka", 200, "QpwL5tke4Pnpja7X"}, //Cyrillic email

                {"peter@klaven", "cityslicka111", 200, "QpwL5tke4Pnpja7X"}, //another password
                {"peter@klaven", "q", 200, "QpwL5tke4Pnpja7X"}, //min password length
                {"peter@klaven", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv", 200, "QpwL5tke4Pnpja7X"}, //max password length (100)
                {"peter@klaven", "CITYSLICKAcityslicka", 200, "QpwL5tke4Pnpja7X"}, //CAPITAL and lowercase characters in password
                {"peter@klaven", "сшеныдшслф", 200, "QpwL5tke4Pnpja7X"}, //Cyrillic pass

                {"PeTer11@йцуГЕНklaven_+-=!№;%:?&", "PeTer11@йцуГЕНklaven_+-=!№;%:?&", 200, "QpwL5tke4Pnpja7X"}, //CAPITAL, lowercase, latin, cyrillic, special characters + numbers
                {"peter@klaven", "cityslicka", 200, "QpwL5tke4Pnpja7X"},
        };
    }

    @Test(dataProvider = "LoginNegative")
    public void loginNegative(String email, String password, int expectedStatus, String response) throws IOException {
        JSONObject jsonObject = new JSONObject();
        if (email != null)
            jsonObject.put(EMAIL_KEY, email);
        if (password != null)
            jsonObject.put(PASSWORD_KEY, password);
        List list = RequestSender.sendPost(jsonObject);
        int httpStatus = (int) list.get(0);
        JSONObject responseJson = new JSONObject(new JSONTokener(list.get(1).toString()));
        System.out.println("ststus " + httpStatus + "     json " + responseJson.toString());
        String responseToken = responseJson.get(ERROR_KEY).toString();
        System.out.println("response " + responseToken);
        Assert.assertEquals(httpStatus, expectedStatus); //received, expected
        Assert.assertEquals(responseToken, response);
    }

    @DataProvider(name = "LoginNegative")
    public static Object[][] credentialsNegative() {
        return new Object[][]{
                {"", "", 400, "Missing email or username"},
                {"peter@klaven", "", 400, "Missing password"},
                {"", "cityslicka", 400, "Missing email or username"},
                {null, "", 400, "Missing email or username"},
                {null, "cityslicka", 400, "Missing email or username"},
                {"", null, 400, "Missing email or username"},
                {"peter@klaven", null, 400, "Missing password"},
                {null, null, 400, "Missing email or username"}
        };
    }
}
