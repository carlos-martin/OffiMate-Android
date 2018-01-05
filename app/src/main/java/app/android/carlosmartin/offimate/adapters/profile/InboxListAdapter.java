package app.android.carlosmartin.offimate.adapters.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.BoostCard;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.NewDate;

/**
 * Created by carlos.martin on 04/01/2018.
 */

public class InboxListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<BoostCard> boostCardList;

    public InboxListAdapter(Context context, int layout, List<BoostCard> boostCardList) {
        this.layout = layout;
        this.context = context;
        this.boostCardList = boostCardList;
    }

    @Override
    public int getCount() {
        return this.boostCardList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.boostCardList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.list_item_boost_card, null);

            holder = new ViewHolder();
            holder.senderTextView = convertView.findViewById(R.id.userNameTextView);
            holder.creationDateTextView = convertView.findViewById(R.id.creationDateTextView);
            holder.boostCardHeaderTextView = convertView.findViewById(R.id.headerTextView);
            holder.boostCardBodyTextView = convertView.findViewById(R.id.bodyTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String senderName;
        BoostCard boostCard = this.boostCardList.get(position);
        if (OffiMate.coworkers.get(boostCard.senderId) != null) {
            Coworker sender = (Coworker) OffiMate.coworkers.get(boostCard.senderId);
            senderName = sender.name;
        } else {
            senderName = "Unknown Coworker";
        }

        holder.senderTextView.setText(senderName);
        holder.creationDateTextView.setText(boostCard.date.getChannelFormat());
        holder.boostCardHeaderTextView.setText(boostCard.header);
        holder.boostCardBodyTextView.setText(boostCard.message);
        //TODO: if the boostCard is unread, put header and body in bold style

        return convertView;
    }

    static class ViewHolder {
        private TextView senderTextView;
        private TextView boostCardHeaderTextView;
        private TextView boostCardBodyTextView;
        private TextView creationDateTextView;
    }
}
