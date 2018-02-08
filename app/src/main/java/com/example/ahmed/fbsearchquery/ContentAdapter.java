package com.example.ahmed.fbsearchquery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private String[] messages;
    private String[] dates;

    ContentAdapter(){

    }

    /**
     * Created by Ahmed on 1/22/2018.
     */

    class ContentViewHolder extends RecyclerView.ViewHolder {

        final TextView message, date;

        ContentViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.updated_time);
        }
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_view_items, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        String currentMessage = messages[position];
        String currentDate = dates[position];
        holder.message.setText(currentMessage);
        holder.date.setText(currentDate);
    }


    @Override
    public int getItemCount() {
        if(null == messages || null == dates) return 0;
        return messages.length;
    }

    public void setData(String[] newMessages, String[] newDates) {
        messages = newMessages;
        dates = newDates;
        notifyDataSetChanged();
    }
}
