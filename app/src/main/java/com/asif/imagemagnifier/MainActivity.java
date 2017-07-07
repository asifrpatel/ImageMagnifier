package com.asif.imagemagnifier;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private int MODE;
    private int OFFSET;
    private int containerHeight;
    private int containerWidth;
    private boolean isImageSet = false;
    private Bitmap originalBitmap;
    private BrushView brush;
    public TouchImageView touchImageView;
    public Bitmap bitmapMaster;
    public Bitmap scaledBitmap;

    public MainActivity() {
        this.OFFSET = 250;
        this.MODE = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.originalBitmap != null) {
            this.originalBitmap.recycle();
            this.originalBitmap = null;
        }

        if (this.bitmapMaster != null) {
            this.bitmapMaster.recycle();
            this.bitmapMaster = null;
        }
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initViews();
    }

    private void initViews() {
        touchImageView = (TouchImageView) findViewById(R.id.touchimageview);
        brush = (BrushView) findViewById(R.id.brushview);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        this.containerWidth = point.x;
        this.containerHeight = point.y;

        if (this.originalBitmap != null) {
            this.originalBitmap.recycle();
            this.originalBitmap = null;
        }
        this.originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample5);
        this.touchImageView.resetZoom();

        if (this.bitmapMaster != null) {
            this.bitmapMaster.recycle();
            this.bitmapMaster = null;
        }
        this.originalBitmap = resizeBitmapByCanvas();
        this.bitmapMaster = Bitmap.createBitmap(this.originalBitmap.getWidth(), this.originalBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(this.bitmapMaster);
        canvas.drawBitmap(this.originalBitmap, 0.0f, 0.0f, null);
        this.touchImageView.setImageBitmap(this.bitmapMaster);

        updateBrush((float) (point.x / 2), (float) (point.y / 2));
        this.touchImageView.setOnTouchListener(new MyOnTouchListener());

        this.touchImageView.setPan(false);
        this.brush.invalidate();

    }



    public Bitmap resizeBitmapByCanvas() {
        float width;
        float height;
        float originalWidth = (float) this.originalBitmap.getWidth();
        float originalHeight = (float) this.originalBitmap.getHeight();
        if (originalWidth > originalHeight) {
            width = (float) this.containerWidth;
            height = (((float) this.containerWidth) * originalHeight) / originalWidth;
        } else {
            height = (float) this.containerHeight;
            width = (((float) this.containerHeight) * originalWidth) / originalHeight;
        }
        if (width > originalWidth || height > originalHeight) {
            return this.originalBitmap;
        }
        Bitmap background = Bitmap.createBitmap((int) width, (int) height, Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        float scale = width / originalWidth;
        float yTranslation = (height - (originalHeight * scale)) / 2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(0.0f, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(this.originalBitmap, transformation, paint);
        return background;
    }

    public float getImageViewZoom() {
        return this.touchImageView.getCurrentZoom();
    }

    public PointF getImageViewTranslation() {
        return this.touchImageView.getTransForm();
    }

    public void updateBrush(float x, float y) {
        this.brush.offset = (float) this.OFFSET;
        this.brush.centerx = x;
        this.brush.centery = y;
        this.brush.invalidate();
    }

    class MyOnTouchListener implements View.OnTouchListener {
        MyOnTouchListener() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (!(event.getPointerCount() == 1)) {
                touchImageView.onTouchEvent(event);
                MODE = 2;
            } else if (action == MotionEvent.ACTION_DOWN) {
                touchImageView.onTouchEvent(event);
                MODE = 1;
                try {
                    if (!isImageSet) {
                        scaledBitmap = Bitmap.createScaledBitmap(bitmapMaster, (int) touchImageView.getImageWidth(),
                                (int) touchImageView.getImageHeight(), false);
                        isImageSet = true;
                    }

                    brush.isTouch = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Runtime.getRuntime().gc();
                    System.gc();
                    if (scaledBitmap != null) {
                        scaledBitmap.recycle();
                        scaledBitmap = null;
                    }
                }
                updateBrush(event.getX(), event.getY());
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (MODE == 1) {
                    updateBrush(event.getX(), event.getY());
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                isImageSet = false;
                if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
                    scaledBitmap.recycle();
                    scaledBitmap = null;
                }
                // To visible Magnifier when touch to image
                // brush.isTouch = false;
                // updateBrush(event.getX(), event.getY());
                Runtime.getRuntime().gc();
                System.gc();
                MODE = 0;
            }
            return true;
        }
    }
}
