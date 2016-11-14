package chat.session.viewholder;

import android.widget.TextView;

import com.imlibrary.R;

import chat.common.util.sys.ScreenUtil;
import chat.image.photo.views.MsgThumbImageView;
import chat.media.ImageUtil;
import chat.session.attachment.LocationAttachment;
/**
 *  消息子布局：位置
 */
public class MsgViewHolderLocation extends MsgViewHolderBase {

    public MsgThumbImageView mapView;
    public TextView addressText;

    @Override
    protected int getContentResId() {
        return R.layout.im_message_item_location;
    }

    @Override
    protected void inflateContentView() {
        mapView = (MsgThumbImageView) view.findViewById(R.id.message_item_location_image);
        addressText = (TextView) view.findViewById(R.id.message_item_location_address);
    }

    @Override
    protected void bindContentView() {
        final LocationAttachment location = message.getAttachment();
        addressText.setText(location.getAddress());

        int[] bound = ImageUtil.getBoundWithLength(getLocationDefEdge(), R.drawable.im_location_bk, true);
        int width = bound[0];
        int height = bound[1];

        setLayoutParams(width, height, mapView);
        setLayoutParams(width, (int) (0.38 * height), addressText);

        mapView.loadAsResource(R.drawable.im_location_bk, width, height, R.drawable.im_message_item_round_bg);
    }

    @Override
    protected void onItemClick() {
    }

    public static int getLocationDefEdge() {
        return (int) (0.5 * ScreenUtil.screenWidth);
    }
}
