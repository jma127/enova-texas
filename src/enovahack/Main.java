package enovahack;

import java.io.*;
import java.util.*;
import org.json.*;

public class Main {
    public static void main (String [] args) {
        PokerLib.init();
        try {
            PokerAI.CORES = Integer.parseInt(System.getenv("ENOVACORES"));
        } catch (Exception e) {
            PokerAI.CORES = 4;
        }
        try {
            Client.client_num = System.getenv("ENOVAKEY");
            if (Client.client_num == null)
                throw new Exception();
        } catch (Exception e) {
            Client.client_num = "bad";
        }
        System.out.printf("Using %d cores\n", PokerAI.CORES);
        Client client = new Client();
        client.run();
    }
}
