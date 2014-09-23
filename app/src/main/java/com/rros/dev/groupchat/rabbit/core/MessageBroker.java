package com.rros.dev.groupchat.rabbit.core;

import android.os.Handler;

import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;

/**
 * Created by ZiYang on 2014-09-22.
 */
public class MessageBroker extends Connector {

    final Runnable mConsumeRunner = new Runnable() {
        @Override
        public void run() {
            consume();

        }
    };
    private String queueName;
    private QueueingConsumer consumer;
    private byte[] mLastMessage;
    final Runnable mReturnMessage = new Runnable() {
        @Override
        public void run() {
            mOnReceiveMessageHandler.onReceiveMessage(mLastMessage);
        }
    };
    private int userID = this.hashCode();
    private OnReceiveMessageHandler mOnReceiveMessageHandler;
    private Handler mMessageHandler = new Handler();
    private Handler mConsumeHandler = new Handler();

    public MessageBroker() {
        super();
    }

    public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler) {
        mOnReceiveMessageHandler = handler;
    }

    @Override
    public boolean connect() {
        if (super.connect()) {

            try {
                queueName = channel.queueDeclare().getQueue();
                consumer = new QueueingConsumer(channel);
                channel.basicConsume(queueName, false, consumer);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (exchangeType == "fanout")
                AddBinding("");//fanout has default binding

            running = true;
            mConsumeHandler.post(mConsumeRunner);

            return true;
        }
        return false;
    }

    /**
     * Add a binding between this consumers Queue and the Exchange with routingKey
     *
     * @param routingKey the binding key eg GOOG
     */
    public void AddBinding(String routingKey) {
        try {
            channel.queueBind(queueName, exchangeName, routingKey);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Remove binding between this consumers Queue and the Exchange with routingKey
     *
     * @param routingKey the binding key eg GOOG
     */
    public void RemoveBinding(String routingKey) {
        try {
            channel.queueUnbind(queueName, exchangeName, routingKey);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void consume() {
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (running) {
                    QueueingConsumer.Delivery delivery;
                    try {
                        delivery = consumer.nextDelivery();
                        mLastMessage = delivery.getBody();
                        mMessageHandler.post(mReturnMessage);
                        try {
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        };
        thread.start();

    }

    public int getUserID() {
        return userID;
    }

    public void publish(String msg) {
        msg = String.valueOf(this.userID) + ":" + msg;
        try {
            channel.basicPublish(exchangeName, "", null, msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        running = false;
    }

    public interface OnReceiveMessageHandler {
        public void onReceiveMessage(byte[] message);
    }
}
