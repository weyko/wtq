package chat.dialog;

import android.util.Pair;
import android.widget.TextView;

import com.imlibrary.R;

import chat.session.adapter.common.ImViewHolder;

public class CustomDialogViewHolder extends ImViewHolder {

	private TextView itemView;

	@Override
	protected int getResId() {
		return R.layout.im_custom_dialog_list_item;
	}

	@Override
	protected void inflate() {
		itemView = (TextView) view.findViewById(R.id.custom_dialog_text_view);
	}

	@Override
	protected void refresh(int position,Object item) {
        if(item instanceof Pair<?,?>){
            Pair<String,Integer> pair = (Pair<String, Integer>) item;
            itemView.setText(pair.first);
            itemView.setTextColor(context.getResources().getColor(pair.second));
        }
	}

}
