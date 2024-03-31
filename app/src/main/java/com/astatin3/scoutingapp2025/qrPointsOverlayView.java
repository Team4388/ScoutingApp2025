package com.astatin3.scoutingapp2025;

// From https://github.com/dlazaro66/QRCodeReaderView/blob/master/samples/src/main/java/com/example/qr_readerexample/PointsOverlayView.java

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class qrPointsOverlayView extends View {

        PointF[] points;
        private Paint paint;

        public qrPointsOverlayView(Context context) {
            super(context);
            init();
        }

        public qrPointsOverlayView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public qrPointsOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
        }

        public void setPoints(PointF[] points) {
            this.points = points;
            invalidate();
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (points != null) {
                for (PointF pointF : points) {
                    canvas.drawCircle(pointF.x, pointF.y, 10, paint);
                }
            }
        }
}
