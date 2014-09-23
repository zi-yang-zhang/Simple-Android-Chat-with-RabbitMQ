package com.rros.dev.groupchat.rabbit.core.resources;

/**
 * Created by ZiYang on 2014-09-22.
 */
public class Util {
    private static final String uri = "amqp://9DqbH5fS:ghxvlG_IVRpFuJncpm8sFBu2tqnmJzES@skinny-woundwort-15.bigwig.lshift.net:10632/7k9USvqwPOZm";

    public static String getURI() {
        return uri;
    }

    public static boolean stringParser(String msg, int userID) {
        String messageUserID = msg.split(":")[0];
        if (messageUserID.equalsIgnoreCase(String.valueOf(userID))) {
            return false;
        } else {
            return true;
        }
    }
}
