package org.looa.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 加载视图。三个点变化颜色的加载视图，类似微信小程序。
 * 我们推荐使用warp_content
 * <p>
 * Created by ran on 2017/3/8.
 */

public class LoadView extends View {

    private Context context;

    private Paint normalPaint;
    private Paint selectedPaint;

    private int normalColor;
    private int selectedColor;
    private int distance;
    private int count;
    private int radius;

    private int paddingTop;
    private int paddingLeft;
    private int paddingBottom;
    private int paddingRight;
    private int gravity;

    private Timer timer;
    private int curPosition;
    private boolean isCancel;
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            LoadView.this.invalidate();
        }
    };

    public LoadView(Context context) {
        this(context, null);
    }

    public LoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        initData();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadView);
        normalColor = ta.getColor(R.styleable.LoadView_normal_color, Color.LTGRAY);
        selectedColor = ta.getColor(R.styleable.LoadView_selected_color, Color.DKGRAY);
        distance = ta.getDimensionPixelSize(R.styleable.LoadView_distance, (int) getPx(20));
        radius = ta.getDimensionPixelSize(R.styleable.LoadView_radius, (int) getPx(4));
        count = ta.getInteger(R.styleable.LoadView_count, 3);
        gravity = ta.getInteger(R.styleable.LoadView_gravity, 0);
        ta.recycle();
    }

    private void initData() {
        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalPaint.setColor(normalColor);

        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColor);

        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingRight = getPaddingRight();

        setClickable(true);
        setFocusableInTouchMode(true);
    }

    private float getPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    public void show() {
        setVisibility(VISIBLE);
        start();
    }

    /**
     * 默认延迟2000ms停止动画
     */
    public void hide() {
        hide(2000);
    }

    public void hide(long delayMillis) {
        if (delayMillis > 0) {
            android.os.Handler handler = LoadView.this.getHandler();
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                        setVisibility(GONE);
                    }
                }, delayMillis);
            }
        } else {
            stop();
            setVisibility(GONE);
        }
    }

    private void start() {
        requestFocus();
        requestFocusFromTouch();
        if (timer != null) timer.cancel();
        curPosition = -1;
        isCancel = false;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isCancel) {
                    timer.cancel();
                    timer = null;
                    return;
                }
                curPosition = (curPosition + 1) % count;
                android.os.Handler handler = LoadView.this.getHandler();
                if (handler != null) {
                    handler.post(refreshRunnable);
                } else {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        }, 0, 400);
    }

    private void stop() {
        isCancel = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < count; i++) {
            canvas.drawCircle(paddingLeft + radius + i * distance, paddingTop + radius, radius, i == curPosition ? selectedPaint : normalPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);

        if (gravity == 0) {
            paddingLeft = paddingRight = (measuredWidth - ((count - 1) * distance + 2 * radius)) / 2;
            paddingTop = paddingBottom = (measuredHeight - 2 * radius) / 2;
        } else if (gravity == -1) {//center vertical
            paddingTop = paddingBottom = (measuredHeight - 2 * radius) / 2;
        } else if (gravity == 1) {//center horizontal
            paddingLeft = paddingRight = (measuredWidth - ((count - 1) * distance + 2 * radius)) / 2;
        }
    }


    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int result = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.AT_MOST) result = paddingTop + paddingBottom + radius * 2;
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int result = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.AT_MOST)
            result = paddingLeft + paddingRight + radius * 2 + (count - 1) * distance;
        return result;
    }
}
