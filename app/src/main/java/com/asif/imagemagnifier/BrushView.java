package com.asif.imagemagnifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class BrushView extends android.support.v7.widget.AppCompatImageView {
    int alpga;
    public float centerx;
    public float centery;
    int density;
    public float largeRadius;
    DisplayMetrics metrics;
    public float offset;
    public float smallRadius;
    public final float target_offset;
    public boolean isTouch;

    public BrushView(Context context) {
        super(context);
        this.metrics = getResources().getDisplayMetrics();
        this.density = (int) this.metrics.density;
        this.alpga = 200;
        this.target_offset = (float) (this.density * 66);
        this.offset = (float) (this.density * 100);
        this.centerx = (float) (this.density * 166);
        this.centery = (float) (this.density * 200);
        this.smallRadius = (float) (this.density * 3);
        this.largeRadius = (float) (this.density * 33);
        this.isTouch = false;

    }

    public BrushView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.metrics = getResources().getDisplayMetrics();
        this.density = (int) this.metrics.density;
        this.alpga = 200;
        this.target_offset = (float) (this.density * 66);
        this.offset = (float) (this.density * 100);
        this.centerx = (float) (this.density * 166);
        this.centery = (float) (this.density * 200);
        this.smallRadius = (float) (this.density * 3);
        this.largeRadius = (float) (this.density * 33);
        this.isTouch = false;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas.getSaveCount() > 1)
            canvas.restore();
        canvas.save();
        Paint p;
        if (this.offset > 0.0f) {
//            if (!isTouch)
//                return;
            p = new Paint();
            p.setColor(Color.argb(255, 255, 0, 0));
            p.setAntiAlias(true);
            canvas.drawCircle(this.centerx, this.centery, this.smallRadius, p);

            p = new Paint();
            p.setColor(Color.argb(this.alpga, 255, 0, 0));
            p.setStyle(Style.STROKE);
            p.setAntiAlias(true);
            p.setStrokeWidth((float) (this.density));


            float zoomScale = ((MainActivity) getContext()).getImageViewZoom();

            PointF transLation = ((MainActivity) getContext()).getImageViewTranslation();
            int projectedX = (int) ((float) (((double) (centerx - transLation.x)) / ((double) zoomScale)));
            int projectedY = (int) ((float) (((double) (centery - transLation.y)) / ((double) zoomScale)));
            if (projectedX < 0 || projectedY < 0)
                return;

            Matrix mat = ((MainActivity) getContext()).touchImageView.getImageMatrix();
            float[] values = new float[9];
            mat.getValues(values);

            projectedX = (int) (((centerx - values[2]) * zoomScale) / values[0]);
            projectedY = (int) (((centery - values[5]) * zoomScale) / values[4]);

            if (projectedX < 0 ||
                    projectedY < 0 ||
                    projectedX > ((MainActivity) getContext()).touchImageView.getImageWidth() ||
                    projectedY > ((MainActivity) getContext()).touchImageView.getImageHeight())
                return;
            if (((MainActivity) getContext()).scaledBitmap == null || ((MainActivity) getContext()).scaledBitmap.isRecycled()) {
                return;
            }

            Rect src = new Rect(projectedX - 100, projectedY - 100, projectedX + 100, projectedY + 100);
            Rect dst = new Rect((int) centerx - 100, (int) centery - 400, (int) centerx + 100, (int) centery - 200);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawBitmap(getCroppedBitmap(((MainActivity) getContext()).scaledBitmap, src, dst, zoomScale), centerx - this.largeRadius, centery - this.largeRadius*4, p);
            System.gc();

            canvas.drawCircle(this.centerx, this.centery - this.target_offset - 100, this.largeRadius, p);

//            canvas.drawLine(this.centerx - this.largeRadius, this.centery - this.target_offset - 100, this.centerx + this.largeRadius, this.centery - this.target_offset - 100, p);
            canvas.drawLine(this.centerx - this.largeRadius, this.centery - this.target_offset - this.largeRadius, this.centerx - 10, this.centery - this.target_offset - this.largeRadius, p);
            canvas.drawLine(this.centerx + 10, this.centery - this.target_offset - this.largeRadius, this.centerx + this.largeRadius, this.centery - this.target_offset - this.largeRadius, p);

//            canvas.drawLine(this.centerx, (this.centery - this.largeRadius) - this.target_offset - 100, this.centerx, (this.centery + this.largeRadius) - this.target_offset - 100, p);
            canvas.drawLine(this.centerx, (this.centery - this.largeRadius) - this.target_offset - this.largeRadius, this.centerx, (this.centery ) - this.target_offset - this.largeRadius-10, p);
            canvas.drawLine(this.centerx, (this.centery ) - this.target_offset - this.largeRadius+10, this.centerx, (this.centery + this.largeRadius) - this.target_offset - this.largeRadius, p);
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap, Rect src, Rect dst, float zoomScale) {
        Bitmap output1;
        int smallBmpSize=(int)largeRadious;
        int smallCircleSize=(int)largeRadious/2;
        int smallCircleRadius=(int)largeRadious/2;
        if (output1 != null && !output1.isRecycled())
            output1.recycle();


        Bitmap output = Bitmap.createBitmap(smallBmpSize,
                smallBmpSize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        canvas.drawCircle(smallCircleSize, smallCircleSize, smallCircleRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        dst = new Rect(0, 0, (int)largeRadious, (int)largeRadious);
        canvas.drawBitmap(bitmap, src, dst, paint);

        Matrix matrix = new Matrix();
        matrix.postScale(4, 4);
        output = Bitmap.createBitmap(output, 0, 0, (int)largeRadious, (int)largeRadious, matrix, true);


        int l = output.getWidth() / 4;
        int t = output.getHeight() / 4;
        int r = l + output.getWidth() / 2;
        int b = t + output.getHeight() / 2;

        Matrix matrix1 = new Matrix();
        output1 = Bitmap.createBitmap(output, l, t, (int)largeRadious*2, (int)largeRadious*2, matrix1, true);
        output.recycle();
        return getCroppedBitmap(output1);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
