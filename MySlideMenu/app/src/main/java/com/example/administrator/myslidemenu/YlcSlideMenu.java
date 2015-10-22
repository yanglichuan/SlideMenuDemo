package com.example.administrator.myslidemenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class YlcSlideMenu extends RelativeLayout {
    private Scroller mScroller;
    private ScrollerCompat scrollCompat;

    public YlcSlideMenu(Context context) {
        super(context);
    }

    public YlcSlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollCompat = ScrollerCompat.create(context);
        mScroller = new Scroller(context);
    }

    public YlcSlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int getPx(int idp) {
        DisplayMetrics ccc = new DisplayMetrics();
        ((Activity) getContext())
                .getWindowManager().getDefaultDisplay().getMetrics(ccc);
        return (int) (ccc.density * (float) idp + 0.10f);
    }

    private ViewGroup menu;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        menu = (ViewGroup) findViewById(R.id.menu);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        menu.layout(r, t, r + menu.getMeasuredWidth(), b);
    }

    int iRecordX = 0;

    private boolean isTouchPointInView(View view, float x, float y) {
        int[] locations = new int[2];

        view.getLocationInWindow(locations);

        int left = locations[0];
        int top = locations[1];
        int right = locations[0] + view.getMeasuredWidth();
        int bottom = locations[1] + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    private boolean bOpen = false;

    private boolean bSlideOk = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iRecordX = (int) event.getX();

                if (!bOpen) {
                    if (iRecordX > (getRight() - 100)) {
                        bSlideOk = true;
                    }
                } else {
                    if (iRecordX > getMinX()) {
                        bSlideOk = true;
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int deltaX = moveX - iRecordX;

                if (bSlideOk) {
                    menu.setX(menu.getX() + deltaX);
                }


                iRecordX = moveX;

                if (bSlideOk) {
                    int currentScrollX = (int) menu.getX();
                    if (currentScrollX <= YlcSlideMenu.this.getRight() - menu.getWidth()) {
                        menu.setX(YlcSlideMenu.this.getRight() - menu.getWidth());
                    } else if (currentScrollX >= YlcSlideMenu.this.getRight()) {
                        menu.setX(YlcSlideMenu.this.getRight());
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                if (bSlideOk) {
                    int currentScrollX = (int) menu.getX();
                    if (!bOpen && currentScrollX <= getOpenX()) {
                        createValueAnimator(currentScrollX, getMinX());
                        bOpen = true;
                    } else if (!bOpen && currentScrollX > getCloseX()) {
                        createValueAnimator(currentScrollX, getMaxX());
                        bOpen = false;
                    } else if (bOpen && currentScrollX > getCloseX()) {
                        createValueAnimator(currentScrollX, getMaxX());
                        bOpen = false;
                    } else if (bOpen && currentScrollX <= getOpenX()) {
                        createValueAnimator(currentScrollX, getMinX());
                        bOpen = true;
                    }
                    bSlideOk = false;
                }
                break;
        }
        return true;
    }


    private int getMinX() {
        return YlcSlideMenu.this.getRight() - menu.getMeasuredWidth();
    }

    private int getMaxX() {
        return YlcSlideMenu.this.getRight();
    }

    private int getOpenX() {
        return YlcSlideMenu.this.getRight() - menu.getMeasuredWidth() / 10;
    }

    private int getCloseX() {
        return YlcSlideMenu.this.getRight() - menu.getMeasuredWidth() + menu.getMeasuredWidth() / 10;
    }


    private void createValueAnimator(int from, int to) {
        final int distance = 1000;
        int dis = Math.abs(from - to);
        int xx = (int) ((float) dis / (float) distance);


        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
//        valueAnimator.setDuration(xx*300);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                menu.setX((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

//    @Override
//    public void computeScroll() {
//        super.computeScroll();
//        if(mScroller.computeScrollOffset()){
//            menu.scrollTo(0,mScroller.getCurrX());
//            menu.invalidate();
//        }
//    }
}
