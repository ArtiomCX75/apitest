package helpers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestSender {
    public static List sendPost(JSONObject json) throws IOException {

        URL urlString = new URL("https://www.reqres.in/api/login");
        HttpURLConnection connection = (HttpURLConnection) urlString.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11) ");

        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        System.out.println("REQUEST " + new String(bytes, "UTF8"));

        connection.connect();
        try (OutputStream os = connection.getOutputStream()) {
            os.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int httpStatus = connection.getResponseCode();
        String message = connection.getResponseMessage();
        System.out.println("HTTP status " + httpStatus + " " + message);
        InputStreamReader inputStreamReader = (httpStatus != 200) ? new InputStreamReader(connection.getErrorStream()) : new InputStreamReader(connection.getInputStream());
        BufferedReader in = new BufferedReader(inputStreamReader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        System.out.println("RESPONSE " + content);
        connection.disconnect();
        return new ArrayList<>(Arrays.asList(httpStatus, content));
    }
}