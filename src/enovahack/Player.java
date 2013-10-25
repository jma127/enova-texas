package enovahack;

import org.json.*;

public class Player {
    private JSONObject jObject;
    public Action[] actions;
    public Player (JSONObject jObject)
    {
        this.jObject = jObject;
        JSONArray array = jObject.getJSONArray("actions");
        actions = new Action[array.length()];
        for (int i=0;i<array.length();i++)
        {
            actions[i] = new Action(array.getJSONObject(i));
        }

    }

    public String getName()
    {
        return jObject.getString("player_name");
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

    public boolean isFolded()
    {
        return jObject.getBoolean("folded");
    }

}


/*

      {
         "player_name":"Bill18",
         "initial_stack":250,
         "current_bet":10,
         "stack":240,
         "folded":false,
         "actions":[
            {
               "action":"ante",
               "amount":10
            }
         ]
      }
*/