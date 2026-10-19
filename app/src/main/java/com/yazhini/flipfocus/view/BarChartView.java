package com.yazhini.flipfocus.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yazhini.flipfocus.util.TimeUtils;

public class BarChartView extends View {

    private static final int DAYS = 7;

    // Created ONCE and reused, never created inside onDraw
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint todayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF barRect = new RectF();
    private final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);

    private final String[] labels = new String[DAYS];
    private final String[] valueLabels = new String[DAYS];
    private final long[] values = new long[DAYS];

    private long maxValue = 0;
    private float progress = 1f;   // 0 = bars hidden, 1 = bars fully grown
    private float dp = 1f;         // 1 dp in pixels on this phone

    public BarChartView(Context context) {
        super(context);
        init(context);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        dp = metrics.density;

        // Use the theme's normal text color, so the chart also works in dark mode
        int textColor = new TextView(context).getCurrentTextColor();

        barPaint.setColor(Color.parseColor("#7986CB"));
        todayPaint.setColor(Color.parseColor("#3F51B5"));

        axisPaint.setColor(textColor);
        axisPaint.setAlpha(80);
        axisPaint.setStrokeWidth(dp);

        labelPaint.setColor(textColor);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, metrics));

        valuePaint.setColor(textColor);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setFakeBoldText(true);
        valuePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, metrics));

        for (int i = 0; i < DAYS; i++) {
            labels[i] = "";
            valueLabels[i] = "";
        }

        animator.setDuration(700);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> {
            progress = a.getAnimatedFraction();
            invalidate();   // "please redraw me"
        });
    }

    public void setData(String[] newLabels, long[] newValues) {
        if (newLabels.length != DAYS || newValues.length != DAYS) {
            throw new IllegalArgumentException("Exactly " + DAYS + " values are needed");
        }
        maxValue = 0;
        StringBuilder description = new StringBuilder("Focus time for the last 7 days. ");
        for (int i = 0; i < DAYS; i++) {
            labels[i] = newLabels[i];
            values[i] = newValues[i];
            valueLabels[i] = TimeUtils.formatShort(newValues[i]);   // text prepared here, not in onDraw
            if (newValues[i] > maxValue) {
                maxValue = newValues[i];
            }
            description.append(newLabels[i]).append(": ").append(valueLabels[i]).append(". ");
        }
        setContentDescription(description.toString());   // for TalkBack (screen reader)

        animator.cancel();
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left = getPaddingLeft();
        float right = getWidth() - getPaddingRight();
        float top = getPaddingTop() + valuePaint.getTextSize() + 8 * dp;
        float baseline = getHeight() - getPaddingBottom() - labelPaint.getTextSize() - 12 * dp;

        float chartHeight = baseline - top;
        float slotWidth = (right - left) / DAYS;
        float barWidth = slotWidth * 0.6f;
        float corner = 4 * dp;

        canvas.drawLine(left, baseline, right, baseline, axisPaint);

        for (int i = 0; i < DAYS; i++) {
            float centerX = left + slotWidth * i + slotWidth / 2f;

            float barHeight = 0f;
            if (maxValue > 0 && values[i] > 0) {
                barHeight = chartHeight * values[i] / maxValue;
                barHeight = Math.max(barHeight, 3 * dp);   // tiny values stay visible
                barHeight *= progress;                      // animation
            }

            barRect.set(centerX - barWidth / 2f, baseline - barHeight,
                    centerX + barWidth / 2f, baseline);
            canvas.drawRoundRect(barRect, corner, corner, i == DAYS - 1 ? todayPaint : barPaint);

            if (values[i] > 0) {
                canvas.drawText(valueLabels[i], centerX, baseline - barHeight - 4 * dp, valuePaint);
            }
            canvas.drawText(labels[i], centerX, baseline + labelPaint.getTextSize() + 4 * dp, labelPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.cancel();   // a running animator would keep this view alive (memory leak)
        super.onDetachedFromWindow();
    }
}
