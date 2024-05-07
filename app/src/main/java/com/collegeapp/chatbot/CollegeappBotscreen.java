package com.collegeapp.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CollegeappBotscreen extends AppCompatActivity
        implements View.OnClickListener
{
    JadeManager jadeManager;
    RecyclerView recyclerView;
    EditText sendText;
    Button sendBtn;
    List<ChatMessage> messageList;
    MessageAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collegeapp_botscreen);
        messageList = new ArrayList<>(8);
        adapter = new MessageAdapter(messageList);
        recyclerView = findViewById(R.id.chat);

        sendBtn = findViewById(R.id.msg_send);
        sendBtn.setOnClickListener(this);
        sendText = findViewById(R.id.msg_text);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        jadeManager = new JadeManager(this);
        jadeManager.Initialize();
    }


    @Override
    public void onClick(View v) {
        String text = sendText.getText().toString();
        if(!text.equals(""))
        {
            addToChat(text, true);
            //Toast.makeText(this, text.trim(), Toast.LENGTH_LONG).show();
        }
    }

    public synchronized void addToChat(String message, boolean byClient)
    {
        if(byClient) jadeManager.notifyClientMessage(message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new ChatMessage(byClient, message));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }
}
