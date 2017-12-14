package app.android.carlosmartin.offimate.adapters.main;

import android.view.View;

import com.stfalcon.chatkit.messages.MessagesListAdapter;

import app.android.carlosmartin.offimate.models.Message;

/**
 * Created by carlos.martin on 14/12/2017.
 */

public class CustomIncomingMessageViewHolder extends MessagesListAdapter.IncomingMessageViewHolder<Message> {
    public CustomIncomingMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        time.setText(message.name);
    }
}
