package tilda.bot.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class APIUtil {
    private static String API_KEY;

    public enum API_ENDPOINT {
        SUMMONERID("https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/", 1);

        private String url;
        private int numArgs;
        API_ENDPOINT(String url, int numArgs) {
            this.url = url;
            this.numArgs = numArgs;
        }
        public String getUrl() {
            return url;
        }
        public int getNumArgs() {
            return numArgs;
        }
    }

    public static void setApiKey(String key) {
        API_KEY = key;
    }

    public static Map<String, Object> execute(API_ENDPOINT apiEndpoint, List<String> args) {
        if (apiEndpoint.getNumArgs() != args.size()) {
            return null;
        }

        String apiUri = apiEndpoint.getUrl() + args.get(0);
        BufferedReader reader;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(apiUri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Origin", "https://developer.riotgames.com");
            connection.setRequestProperty("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
            connection.setRequestProperty("X-Riot-Token", API_KEY);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line + "\n");
//            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(reader, Map.class);
            return map;

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
