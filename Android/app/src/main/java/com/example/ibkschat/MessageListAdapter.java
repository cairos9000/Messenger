package com.example.ibkschat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageListAdapter extends BaseAdapter {
    WeakReference<List<MyChat.MyMessage>> messages;
    Context context;

    public MessageListAdapter(Context context, List<MyChat.MyMessage> msgs) {
        this.context = context;
        messages = new WeakReference<>(msgs);
    }

    @Override
    public int getCount() {
        return messages.get().size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MyChat.MyMessage message = messages.get().get(i);
        Timestamp ts = Timestamp.valueOf(message.timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date_str = dateFormat.format(ts);

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.msg_sent, null);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            holder.time = convertView.findViewById(R.id.msg_time);
            convertView.setTag(holder);
            holder.messageBody.setText(message.content);
            holder.time.setText(date_str);
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.msg_received, null);
            holder.avatar = convertView.findViewById(R.id.avatar);
            holder.name = convertView.findViewById(R.id.name);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            holder.time = convertView.findViewById(R.id.msg_time);
            convertView.setTag(holder);

            holder.name.setText(message.author);
            holder.messageBody.setText(message.content);
            holder.time.setText(date_str);
            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
        }
        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
    public TextView time;
}