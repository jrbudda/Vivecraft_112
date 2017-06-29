import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.main.Main;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.Session;
import org.json.JSONObject;   // JAR available at http://mvnrepository.com/artifact/org.json/json/20140107

public class Start
{
    public static void main(String[] args) throws Exception
    {
        // Support --username <username> and --password <password> parameters as args.
        /** LEAVE THE LINE BELOW - IT'S UPDATED BY THE INSTALL SCRIPTS TO THE CORRECT MINECRAFT VERSION */
        args = concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.7.10", "--userProperties", "{}"}, args);

        // Authenticate --username <username> and --password <password> with Mojang.
        // *** Username should most likely be an email address!!! ***
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        ArgumentAcceptingOptionSpec username = optionParser.accepts("username").withRequiredArg();
        ArgumentAcceptingOptionSpec password = optionParser.accepts("password").withRequiredArg();
        ArgumentAcceptingOptionSpec launchwrapper = optionParser.accepts("useLaunchWrapper").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(0), new Integer[0]);
        OptionSet optionSet = optionParser.parse(args);
        String user = (String)optionSet.valueOf(username);
        String pass = (String)optionSet.valueOf(password);
        boolean useLaunchwrapper = (((Integer)optionSet.valueOf(launchwrapper)).intValue() == 0 ? false : true);
        
        if (user != null && pass != null)
        {
            Session session = null;

            try
            {
                session = Start.GetSSID(user, pass);

                if (session == null)
                {
                    throw new Exception("Bad login!");
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Main.main(args);
            }

            ArrayList<String> newArgs = new ArrayList<String>();

            for (int i = 0; i < args.length; i++)
            {
                if (args[i].compareToIgnoreCase("--username") == 0 ||
                        args[i].compareToIgnoreCase("--password") == 0 ||
                        args[i].compareToIgnoreCase("--session") == 0 ||
                        args[i].compareToIgnoreCase("--uuid") == 0 ||
                        args[i].compareToIgnoreCase("--accessToken") == 0)
                {
                    i++;
                }
                else
                {
                    newArgs.add(args[i]);
                }
            }
            
            newArgs.add("--username");
            newArgs.add(session.getUsername());
            newArgs.add("--uuid");
            newArgs.add(session.getPlayerID());
            newArgs.add("--accessToken");
            newArgs.add(session.getToken());
            if (!useLaunchwrapper) {
                Main.main(newArgs.toArray(new String[0]));
            }
            else {
                Launch.main(newArgs.toArray(new String[0]));
            }
        }
        else
        {
            if (!useLaunchwrapper) {
                Main.main(args);
            }
            else {
                Launch.main(args);
            }
        }
    }

    private static void dumpArgs(String[] newArgs)
    {
        StringBuilder sb = new StringBuilder();

        for (String s : newArgs)
        {
            sb.append(s).append(" ");
        }

        System.out.println("[Minecrift] Calling Main.main with args: " + sb.toString());
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static final Session GetSSID(String username, String password)
    {
        byte[] b = null;
        String jsonEncoded =
                "{\"agent\":{\"name\":\"Minecraft\",\"version\":1},\"username\":\""
                        + username
                        + "\",\"password\":\""
                        + password + "\"}";
        String response = executePost("https://authserver.mojang.com/authenticate", jsonEncoded);
        if (response == null || response.isEmpty())
            return null;

        // **** JSON parsing courtesy of ssewell ****
        // Create a parsable JSON object from our response string
        JSONObject jsonRepsonse = new JSONObject(response);

        // Obtain our current profile (which contains the user ID and name
        JSONObject jsonSelectedProfile = jsonRepsonse.getJSONObject("selectedProfile");

        // Session ID = "token:<accessToken>:<profile ID>"
        // Username will probably *not be an email address
        String accessToken = jsonRepsonse.getString("accessToken");
        String id = jsonSelectedProfile.getString("id");
        String sessionID = "token:" + jsonRepsonse.getString("accessToken") + ":" + jsonSelectedProfile.getString("id");
        String userName = jsonSelectedProfile.getString("name");

        Session session = new Session(userName, id, accessToken, "legacy");
        return session;
    }

    public static String executePost(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
