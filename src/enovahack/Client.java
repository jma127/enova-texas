package enovahack;

import java.io.*;
import java.util.*;
import java.net.*;
import org.json.*;
import org.json.JSONObject;

public class Client implements Runnable {

    private static String serverName = "http://nolimitcodeem.com/api/players/";
    public static String client_num = "a0f018e5-137a-46b8-85bb-d3eafc575dab";
    //private static String serverName = "http://ec2-54-235-169-130.compute-1.amazonaws.com/sandbox/players/";
    //private static String client_num ="final-bet-key"; // replacement-stage-key final-bet-key";
    public static long lastGet = -1;



    public static void putMove (String urlParameters) {

        String request = serverName + client_num +"/action";
        //testing
        //request = "http://no-limit-code-em.com/sandbox/players/replacement-stage-key/action";
        //request = "http://no-limit-code-em.com/sandbox/players/initial-deal-key/action";
        //request = "http://no-limit-code-em.com/sandbox/players/final-bet-key/action";


        request += "?" + urlParameters;
        try{
            URL url = new URL(request);
            System.out.println(url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches (false);
/*
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(urlParameters);
            wr.flush();
            //wr.close();
 */
            String result="";
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }

            System.out.println("\n\n\nPOST\n\n\n" + result);

            connection.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    public static String getJSON(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

      return result;
   }

    public static State getState () {

        if (System.currentTimeMillis() - lastGet > 1250) {
            lastGet = System.currentTimeMillis();
            String url = serverName + client_num;

            //testing
            //url = serverName + "sandbox/players/initial-deal-key";
            //url = "http://nolimitcodeem.com/sandbox/players/flop-phase-key";

            String response = getJSON(url);
            System.out.println("\n\n\nGET\n\n\n" + response + "\n\n\n");
            try {
                JSONObject jObject  = new JSONObject(response);
                return new State(jObject);
            } catch (Exception e) {
                System.out.println(response);
            }

            return null;
        }
        return null;
    }

    public void run()
    {
        while (true)
        {
            State state = getState();

            if (state != null)
            {

                if (state.isOurTurn())
                {
                    int phase = state.getPhase();
                    if (phase < 0) { // invalid

                    }
                    else {
                        if (phase >= 0) {
                            System.out.println("\nRound: " + state.getRoundID());
                            putMove(PokerAI.getBestBet(state, 2500));
                        }
                    }
                }
            }
        }
    }

}
