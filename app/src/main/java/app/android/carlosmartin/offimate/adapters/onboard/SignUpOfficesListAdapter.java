package app.android.carlosmartin.offimate.adapters.onboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.models.Office;

/**
 * Created by carlos.martin on 01/12/2017.
 */

public class SignUpOfficesListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<Office> officeList;

    public SignUpOfficesListAdapter(Context context, int layout, List<Office> officeList) {
        this.layout = layout;
        this.context = context;
        this.officeList = officeList;
    }

    @Override
    public int getCount() {
        return this.officeList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.officeList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        //View Holder Pattern
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.list_item_offices, null);

            holder = new ViewHolder();
            holder.officeTextView = convertView.findViewById(R.id.textViewCell);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String currentOffice = this.officeList.get(position).name;
        holder.officeTextView.setText(currentOffice);

        return convertView;
    }

    static class ViewHolder {
        private TextView officeTextView;
    }
}
