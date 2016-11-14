package net.skjr.wtq.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 创建者     huangbo
 * 创建时间   2016/11/1 13:44
 * 描述	      自定义圆角图片
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RadiusImageView extends ImageView {
    private Paint paint;

    public RadiusImageView(Context context) {
        this(context,null);
    }

    public RadiusImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RadiusImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint  = new Paint();
    }

    /**
     * 绘制圆角矩形图片
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (null != drawable) {
//            Bitmap bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            Bitmap bitmap = drawableToBitamp(drawable);
            Bitmap b = getRoundBitmap(bitmap, 14);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0,0,getWidth(),getHeight());
            paint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, paint);

        } else {
            super.onDraw(canvas);
        }
    }
    private Bitmap drawableToBitamp(Drawable drawable) {
                Bitmap bitmap;
               int w = drawable.getIntrinsicWidth();
               int h = drawable.getIntrinsicHeight();
               System.out.println("Drawable转Bitmap");
               Bitmap.Config config =
                              drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                               : Bitmap.Config.RGB_565;
                bitmap = Bitmap.createBitmap(w,h,config);
                //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, w, h);
                drawable.draw(canvas);
                return bitmap;
            }

    /**
     * 获取圆角矩形图片方法
     * @param bitmap
     * @param roundPx,一般设置成14
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;


    }
}
