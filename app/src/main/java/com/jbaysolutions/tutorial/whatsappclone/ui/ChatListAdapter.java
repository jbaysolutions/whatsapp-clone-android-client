package com.jbaysolutions.tutorial.whatsappclone.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jbaysolutions.tutorial.whatsappclone.R;

import java.util.Vector;

/**
 * Created by rui on 9/16/16.
 */
public class ChatListAdapter extends BaseAdapter {

    private Vector<ChatMessage> messages = new Vector<>();
    private Activity context;

    public ChatListAdapter(Activity context) {
        this.context = context;
    }

    public void addMessage(String user, String message, boolean isMe) {
        messages.add(new ChatMessage(user,message, isMe));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        LayoutInflater vi = LayoutInflater.from(context);

        ChatMessage p = getItem(position);

        if (p.isMe) {
            v = vi.inflate(R.layout.chatlayout_me, null);
        } else {
            v = vi.inflate(R.layout.chatlayout_others, null);
            ((TextView)v.findViewById(R.id.whoTV)).setText(p.user);
        }

        ((TextView)v.findViewById(R.id.messageTV)).setText(p.message);


        return v;
    }
}

class ChatMessage {
    String user;
    String message;
    boolean isMe = false;

    public ChatMessage(String user, String message, boolean isMe) {
        this.user = user;
        this.message = message;
        this.isMe = isMe;
    }
}