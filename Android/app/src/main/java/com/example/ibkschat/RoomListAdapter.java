package com.example.ibkschat;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RoomListAdapter extends BaseAdapter implements Filterable {
    static public List<MyChat> chats = new ArrayList<>();
    static private List<MyChat> mStringFilterList = chats;

    Context context;
    private ChatFilter chatFilter;

    public RoomListAdapter(Context context) {
        this.context = context;
    }

    public void add(MyChat chat) {
        chats.add(chat);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public Filter getFilter() {
        if (chatFilter == null) {
            chatFilter = new ChatFilter();
        }
        return chatFilter;
    }

    public class ChatFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults Result = new FilterResults();
            if (constraint.length() == 0) {
                Result.values = mStringFilterList;
                Result.count = mStringFilterList.size();
                return Result;
            }

            ArrayList<MyChat> Filtered_Names = new ArrayList<MyChat>();
            String filterString = constraint.toString().toLowerCase();
            String filterableString;

            for (MyChat c : chats) {
                if (c.messages.size() > 0 && c.messages.get(c.messages.size() - 1).content.toLowerCase().contains(filterString)) {
                    Filtered_Names.add(c);
                }
            }
            Result.values = Filtered_Names;
            Result.count = Filtered_Names.size();
            return Result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                chats = (ArrayList<MyChat>) results.values;
            }
            notifyDataSetChanged();
        }

    }


    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int i) {
        return chats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        RoomViewHolder holder = new RoomViewHolder();
        LayoutInflater roomInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MyChat chat = chats.get(i);

        convertView = roomInflater.inflate(R.layout.chat_list_entry, null);
        holder.avatar = convertView.findViewById(R.id.chat_avatar);
        holder.name = convertView.findViewById(R.id.chat_name);
        holder.lastMessage = convertView.findViewById(R.id.chat_last_msg);
        holder.time = convertView.findViewById(R.id.chat_msg_time);
        convertView.setTag(holder);

        holder.name.setText(chat.name);
        if (chat.messages.size() != 0) {
            holder.lastMessage.setText(chat.messages.get(chat.messages.size() - 1).content);
            Timestamp ts = Timestamp.valueOf(chat.messages.get(chat.messages.size() - 1).timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date_str = dateFormat.format(ts);
            holder.time.setText(date_str);
        }
        GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
        //drawable.setColor(Color.parseColor(message.color));
        return convertView;
    }

}

class RoomViewHolder {
    public View avatar;
    public TextView name;
    public TextView lastMessage;
    public TextView time;
}