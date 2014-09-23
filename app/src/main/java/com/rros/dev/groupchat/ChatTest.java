package com.rros.dev.groupchat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rros.dev.groupchat.rabbit.core.MessageBroker;
import com.rros.dev.groupchat.rabbit.core.resources.Util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;


public class ChatTest extends Activity {
    private MessageBroker mConsumer;
    private TextView mOutput;
    private EditText inputText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test);
        //The output TextView we'll use to display messages
        mOutput = (TextView) findViewById(R.id.chat_area);
        inputText = (EditText) findViewById(R.id.text_input);
        sendButton = (Button) findViewById(R.id.send_button);
        //Create the consumer
        mConsumer = new MessageBroker();

        //Connect to broker
        new receiveFromRabbitMQ().execute();


    }

    public void sendText(View view) {
        mConsumer.publish(inputText.getText().toString());
        inputText.getText().clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class receiveFromRabbitMQ extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mConsumer.connect();

            //register for messages
            mConsumer.setOnReceiveMessageHandler(new MessageBroker.OnReceiveMessageHandler() {

                public void onReceiveMessage(byte[] message) {
                    String text = "";
                    try {
                        text = new String(message, "UTF8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (Util.stringParser(text, (mConsumer.getUserID()))) {
                        mOutput.append("\n" + text + "\n" + new java.sql.Timestamp(Calendar
                                .getInstance().getTime().getTime()));
                    }

                }
            });
            return null;
        }
    }
}
