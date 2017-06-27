package ac.robinson.bettertogether.plugin.base.cardgame.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ac.robinson.bettertogether.api.messaging.BroadcastMessage;
import ac.robinson.bettertogether.plugin.base.cardgame.utils.Constants;

/**
 * Created by t-sus on 4/8/2017.
 */

public class MessageHelper {

    private static MessageHelper mInstance = null;

    private BroadcastCardMessage message;

    private static Map<String,PlayerType> connectionMap = new HashMap<>();

    private static String mUser;

    public enum PlayerType{
        PLAYER, DEALER
    }

    public void parse(BroadcastMessage message) { // singleton
        Gson gson = new Gson();
        // Convert message.getMessage() to BroadcastCardMessage object using gson
        try {
            this.message = gson.fromJson(message.getMessage(), BroadcastCardMessage.class);
        }
        catch (NullPointerException e) {

        }
    }

    public static MessageHelper getInstance(Context mContext){
        if( mInstance == null){
            mInstance = new MessageHelper();
            mUser = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPreferences.Editor prefs = mContext.getSharedPreferences("Details", mContext.MODE_PRIVATE).edit();
            prefs.putString(Constants.USER_ANDROID_ID, mUser);
            prefs.commit();
        }

        return mInstance;
    }

    public String getDealerFromMap(){
//        if( !connectionMap.containsValue(PlayerType.DEALER)) {

            Iterator it = connectionMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                if (pair.getValue().equals(PlayerType.DEALER)) {
                    return (String)pair.getKey();
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
//        }
        return null;
    }

    public String getmUser(){
        return mUser;
    }

    public Map<String ,PlayerType> getConnectionMap(){
        return connectionMap;
    }

    public boolean ReceivedDiscoveryMessage(String message) {
        String[] nameAndType = message.split(";");
        String name = nameAndType[0];
        PlayerType type = null;
        if(nameAndType[1].equals("PLAYER")) {
            type = PlayerType.PLAYER;
        }else if (nameAndType[1].equals("DEALER")){
            type = PlayerType.DEALER;
        }
        if (!connectionMap.containsKey(name)) {
            connectionMap.put(name, type);
            return true;
        }
        return false;
    }

    public BroadcastMessage Discovery(String name, PlayerType type) {
        // Return Android_ID and Status
        // 1. Add the required items to the discovery for local singleton.
        if (!connectionMap.containsKey(name)) {
            // If key doesnot exist. Add it to the map
            connectionMap.put(name, type);
        }
        // 2. Trigger a broadcast
        // Discovery will use integer for discovery as 999 and will have ID;TYPE
        return new BroadcastMessage(999, name+";"+type.toString());
    }

    public void sendPlayerMessage(BroadcastCardMessage message){
        Gson gson = new Gson();
        new BroadcastMessage(MessageType.PLAYER_TO_DEALER, gson.toJson(message));
    }

    public void PlayerReceivedMessage() {
        Action localCardAction = this.message.getCardAction();
        String localFromUser = this.message.getCardFrom();
        String localToUser = this.message.getCardTo();
        // Now having To and From and Action in place. It's time for the player to receive this card.
        List<String> localCardStrings = this.message.getCards();
        // Step 1: Check if the Player is the player intended.
        // Step 2: Perform the required card action to the player list of cards.

    }

    public void ServerReceivedMessage() {
        Action localCardAction = this.message.getCardAction();
        String localFromUser = this.message.getCardFrom();
        String localToUser = this.message.getCardTo();
        // Now having To and From and Action in place. It's time for the player to receive this card.
        List<String> localCardsItem = this.message.getCards();
        // Step 1: Perform the required card action to the server deck of cards.

    }

}
