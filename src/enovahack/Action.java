package enovahack;

import org.json.*;

public class Action {
    private String action_name;
    private JSONObject jObject;
    public Action (JSONObject jObject)
    {
        this.jObject = jObject;
    }
    
    
    public String getActionName()
    {
        this.action_name = jObject.getString("action");
        return this.action_name;
    }
    
    public int getAmount()
    {
        return jObject.getInt("amount");
    }
    
    public String toString()
    {
        String str = "action_name=" + this.action_name;
        // TODO need to be edited for amount and replace 
        return str;
    }
}

/*
"actions": [{
            "action": "ante",
            "amount": 10
        }
        
        
        */