/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finessence.user.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Kabugi
 */
public class SMSSender {
    
    public void sendSMS(String phoneNumber,String message){
    // Specify your login credentials
       
      String username = "finessence";
      String apiKey = "447e1de7f3f5fb3306bd440262e3738a1df975514c460b77c4f37bee49c4cafa";
      String recipients = "+254"+phoneNumber.substring(1);
        AfricasTalkingGateway gateway  = new AfricasTalkingGateway(username, apiKey);
        /*************************************************************************************
            NOTE: If connecting to the sandbox:
            1. Use "sandbox" as the username
            2. Use the apiKey generated from your sandbox application
                https://account.africastalking.com/apps/sandbox/settings/key
            3. Add the "sandbox" flag to the constructor
            AfricasTalkingGateway gateway = new AfricasTalkingGateway(username, apiKey, "sandbox");
        **************************************************************************************/
        // Thats it, hit send and we'll take care of the rest. Any errors will
        // be captured in the Exception class below
        try {
            JSONArray results = gateway.sendMessage(recipients, message);
            for( int i = 0; i < results.length(); ++i ) {
                JSONObject result = results.getJSONObject(i);
                System.out.print(result.getString("status") + ","); // status is either "Success" or "error message"
                System.out.print(result.getString("number") + ",");
                System.out.print(result.getString("messageId") + ",");
                System.out.println(result.getString("cost"));
            }
        } catch (Exception e) {
            System.out.println("Encountered an error while sending " + e.getMessage());
        }
    }    
    
    
}
