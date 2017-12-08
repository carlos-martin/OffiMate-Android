package app.android.carlosmartin.offimate.adapters.coworkers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.helpers.CoworkerOption;

/**
 * Created by carlos.martin on 08/12/2017.
 */

public class CoworkerProfileListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<CoworkerOption> options;

    public CoworkerProfileListAdapter(Context context, int layout, List<CoworkerOption> options) {
        this.layout = layout;
        this.context = context;
        this.options = options;
    }

    @Override
    public int getCount() {
        return this.options.size();
    }

    @Override
    public Object getItem(int position) {
        return this.options.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        CoworkerOption currentOption = this.options.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.list_item_coworker_options, null);

            holder = new ViewHolder();
            holder.optionTextView = convertView.findViewById(R.id.textViewCell);
            holder.iconImageView = convertView.findViewById(R.id.imageViewCell);
            holder.forwardImageView = convertView.findViewById(R.id.imageViewForward);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.optionTextView.setText(currentOption.getTitle());
        holder.iconImageView.setBackgroundResource(currentOption.getIconRes());
        if (currentOption.hasArrow) {
            holder.forwardImageView.setVisibility(View.VISIBLE);
        } else {
            holder.forwardImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }

    static class ViewHolder {
        private TextView optionTextView;
        private ImageView iconImageView;
        private ImageView forwardImageView;
    }
}
