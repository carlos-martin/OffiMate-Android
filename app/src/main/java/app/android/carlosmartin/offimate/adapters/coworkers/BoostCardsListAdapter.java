package app.android.carlosmartin.offimate.adapters.coworkers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.android.carlosmartin.offimate.R;

/**
 * Created by carlos.martin on 08/12/2017.
 */

public class BoostCardsListAdapter extends BaseAdapter {

    private int layoutHeader;
    private int layoutBody;
    private Context context;
    private List<String> passionList;
    private List<String> executionList;

    public BoostCardsListAdapter(Context context, int layoutHeader, int layoutBody) {
        this.layoutHeader = layoutHeader;
        this.layoutBody = layoutBody;
        this.context = context;

        this.passionList = new ArrayList<String>();
        passionList.add("Customer focus");
        passionList.add("Manages ambiguity");
        passionList.add("Self-development");

        this.executionList = new ArrayList<String>();
        executionList.add("Action oriented");
        executionList.add("Ensures accountability");
        executionList.add("Drives results");
    }

    @Override
    public int getCount() {
        return passionList.size() + executionList.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        int index;
        switch (position){
            case 0:
                return "Passion";
            case 1:
            case 2:
            case 3:
                index = position-1;
                return this.passionList.get(index);
            case 4:
                return "Execution";
            default:
                index = position-5;
                return this.executionList.get(index);
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO: complete getView

        ViewHolder holder;

        switch (position) {
            case 0:
            case 4:
                //Header: 0 > passion, 4 > execution

                if (convertView == null || ((ViewHolder)convertView.getTag()).headerLayout == null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(this.context);
                    convertView = layoutInflater.inflate(R.layout.list_item_boostcard_header, null);

                    holder = new ViewHolder();
                    holder.iconImageView = convertView.findViewById(R.id.headerImageView);
                    holder.headerLayout = convertView.findViewById(R.id.headerLayout);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (position == 0) {
                    //PASSION
                    holder.headerLayout.setBackgroundColor(context.getResources().getColor(R.color.passion));
                    holder.iconImageView.setBackgroundResource(R.drawable.ic_action_passion);
                } else {
                    //EXECUTION
                    holder.headerLayout.setBackgroundColor(context.getResources().getColor(R.color.execution));
                    holder.iconImageView.setBackgroundResource(R.drawable.ic_action_execution);
                }

                return convertView;
            default:
                int index;

                //Body: 1..3 > passion body; 5..7 > execution body
                if (convertView == null || ((ViewHolder)convertView.getTag()).titleTextView == null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(this.context);
                    convertView = layoutInflater.inflate(R.layout.list_item_boostcard_body, null);

                    holder = new ViewHolder();
                    holder.titleTextView = convertView.findViewById(R.id.bodyTextView);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (position < 4) {
                    //PASSION
                    index = position-1;
                    holder.titleTextView.setText(passionList.get(index));
                } else {
                    //EXECUTION
                    index = position-5;
                    holder.titleTextView.setText(executionList.get(index));
                }

                return convertView;
        }


    }

    @Override
    public boolean isEnabled(int position) {
        switch (position) {
            case 0:
            case 4:
                return false;
            default:
                return super.isEnabled(position);
        }
    }
    //TODO: static class ViewHolder
    static class ViewHolder {
        private LinearLayout headerLayout;
        private ImageView iconImageView;
        private TextView titleTextView;
    }
}
