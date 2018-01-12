package app.android.carlosmartin.offimate.adapters.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.models.Channel;

/**
 * Created by carlos.martin on 07/12/2017.
 */

public class ChannelsListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<Channel> channelList;

    public ChannelsListAdapter(Context context, int layout, List<Channel> channelList){
        this.layout = layout;
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return this.channelList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.channelList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Channel channel = this.channelList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.list_item_channels, null);

            holder = new ViewHolder();
            holder.channelTextView = convertView.findViewById(R.id.textViewCell);
            holder.counterTextView = convertView.findViewById(R.id.channelCounterTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.channelTextView.setText(channel.name);

        if (channel.message != null && channel.message.size() > 0) {
            holder.counterTextView.setVisibility(View.VISIBLE);
            holder.counterTextView.setText(channel.message.size()+"");
        } else {
            holder.counterTextView.setVisibility(View.INVISIBLE);
            //holder.counterTextView.setText("");
        }


        return convertView;
    }

    static class ViewHolder {
        private TextView channelTextView;
        private TextView counterTextView;
    }
}
