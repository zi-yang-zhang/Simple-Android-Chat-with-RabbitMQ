package com.rros.dev.groupchat.rabbit.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rros.dev.groupchat.rabbit.core.resources.Util;

import java.io.IOException;

/**
 * Created by ZiYang on 2014-09-22.
 */
public abstract class Connector {
    protected Channel channel;
    protected Connection connection;
    protected boolean running;
    protected String exchangeName = "chatroom";
    protected String exchangeType = "fanout";

    protected Connector() {
    }

    public boolean connect() {
        if (channel != null && channel.isOpen()) {
            return true;
        }


        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(Util.getURI());
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, exchangeType);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public void Dispose() {
        running = false;

        try {
            if (connection != null)
                connection.close();
            if (channel != null)
                channel.abort();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
