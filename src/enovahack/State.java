package enovahack;

import org.json.*;

public class State {

    private JSONObject jObject;
    public Player [] players;

    public State (JSONObject jObject)
    {
        this.jObject = jObject;
        //set up players

        JSONArray array = jObject.getJSONArray("players_at_table");
        players = new Player[array.length()];
        for (int i=0;i<array.length();i++)
        {
            players[i] = new Player(array.getJSONObject(i));
        }

    }

    public int getRoundID()
    {
        return jObject.getInt("round_id");
    }

    public int getInitialStack()
    {
        return jObject.getInt("initial_stack");
    }

    public int getStack()
    {
        return jObject.getInt("stack");
    }

    public int getCurrentBet()
    {
        return jObject.getInt("current_bet");
    }

    public int getCallAmount()
    {
        return jObject.getInt("call_amount");
    }

    public int getPhase()
    {
        String bettingPhase = jObject.getString("betting_phase");
        if (bettingPhase.equalsIgnoreCase("deal"))
            return 0;
        if (bettingPhase.equalsIgnoreCase("flop"))
            return 1;
        if (bettingPhase.equalsIgnoreCase("turn"))
            return 2;
        if (bettingPhase.equalsIgnoreCase("river"))
            return 3;
        return -1;
    }

    public int [] getHand()
    {
        JSONArray array = jObject.getJSONArray("hand");
        int [] hand = new int[array.length()];
        for (int i = 0;i<array.length();i++)
        {
            //System.out.println(array.getString(i));
            hand[i] = PokerLib.stringToCard(array.getString(i));
        }
        return hand;
    }

    public boolean isOurTurn()
    {
        return jObject.getBoolean("your_turn");
    }

    public int getNonFolded ()
    {
        int activePlayers = 0;
        for (Player p : players) {
            if (!p.isFolded())
                activePlayers ++;
        }
        return activePlayers;
    }

    public int [] getCommunityCards() {
        JSONArray array = jObject.getJSONArray("community_cards");
        int [] hand = new int[array.length()];
        for (int i = 0;i<array.length();i++) {
            hand[i] = PokerLib.stringToCard(array.getString(i));
        }
        return hand;
    }
}


/*

{
    "name": "Bill13",
    "your_turn": true,
    "initial_stack": 250,
    "stack": 215,
    "current_bet": 35,
    "call_amount": 0,
    "hand": ["AH", "JD", "QD", "7D", "KH"],
    "betting_phase": "draw",
    "players_at_table": [{
        "player_name": "Bill12",
        "initial_stack": 250,
        "current_bet": 35,
        "stack": 215,
        "folded": false,
        "actions": [{
            "action": "ante",
            "amount": 10
        }, {
            "action": "bet",
            "amount": 25
        }, {
            "action": "replace",
            "cards": ["6S", "AD"]
        }]
    }, {
        "player_name": "Bill13",
        "initial_stack": 250,
        "current_bet": 35,
        "stack": 215,
        "folded": false,
        "actions": [{
            "action": "ante",
            "amount": 10
        }, {
            "action": "bet",
            "amount": 0
        }]
    }],
    "total_players_remaining": 2,
    "table_id": 766,
    "round_id": 823,
    "round_history": [{
        "round_id": 823,
        "table_id": 766,
        "stack_change": null
    }],
    "lost_at": null
}


*/