package app.android.carlosmartin.offimate.adapters.coworkers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.models.Coworker;

/**
 * Created by carlos.martin on 08/12/2017.
 */

public class CoworkersListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<Coworker> coworkerList;

    public CoworkersListAdapter(Context context, int layout, List<Coworker> coworkerList) {
        this.layout = layout;
        this.context = context;
        this.coworkerList = coworkerList;
    }

    @Override
    public int getCount() {
        return this.coworkerList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.coworkerList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.list_item_coworkers, null);

            holder = new ViewHolder();
            holder.coworkerTextView = convertView.findViewById(R.id.textViewCell);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.coworkerTextView.setText(this.coworkerList.get(position).name);

        return convertView;
    }

    static class ViewHolder {
        private TextView coworkerTextView;
    }
}
