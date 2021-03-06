package com.example.dandan.gesturepassword;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dandan on 2017/3/7.
 */

public class Gesture extends View {

    private boolean isCache = false;
    private int width;
    private List<Point> list = new ArrayList<>();
    private float x;
    private float a;
    private float b;
    private float r;
    private Paint bgPaint;
    private Paint linePaint;
    private Paint selectedPaint;
    private Paint errorPaint;
    private boolean checking = false;
    private List<Point> selectList = new ArrayList<>();
    private boolean isFinish = false;
    private Context context;
    private Vibrator vibrator;
    private float moveX;
    private float moveY;
    private boolean isTouch = true;
    private Set<Integer> set = new HashSet<>();
    private boolean isError = false;
    private boolean isSelectedSamePoint;

    public Gesture(Context context) {
        this(context, null);
    }

    public Gesture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Gesture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        int height = getHeight();
        if (width > height)
            width = height;

        if (!isCache)
            chche();
        drawToCanvas(canvas);
    }

    private void drawToCanvas(Canvas canvas) {
        for (int i = 0; i < list.size(); i++)
            canvas.drawCircle(list.get(i).x, list.get(i).y, r, bgPaint);


        if (checking) {
            if (selectList != null && selectList.size() > 1) {
                //两个以上
                for (int i = 0; i < selectList.size() - 1; i++) {
                    float firstX = selectList.get(i).x;
                    float firstY = selectList.get(i).y;
                    float secX = selectList.get(i + 1).x;
                    float secY = selectList.get(i + 1).y;
                    if(selectList.get(i).state == 0)
                        canvas.drawLine(firstX,firstY,secX,secY,linePaint);
                    else
                        canvas.drawLine(firstX, firstY, secX, secY, errorPaint);

                }
            }

            if(!isFinish) {
                //画移动线
                float lastX = selectList.get(selectList.size() - 1).x;
                float lastY = selectList.get(selectList.size() - 1).y;
                canvas.drawLine(lastX, lastY, moveX, moveY, linePaint);
            }
        }
        if(isError)
        {
            for (int i : set)
            {
                for(Point p : list)
                {
                    if(p.index == i)
                    {
                        canvas.drawCircle(p.x,p.y,r*1.5f,errorPaint);
                        break;
                    }
                }
            }
        }

    }

    private void initPaint() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#ffffff"));
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#ffffff"));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        //画笔的宽度
        linePaint.setStrokeWidth(r / 2);

        selectedPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#ffffff"));
        selectedPaint.setStyle(Paint.Style.FILL);
        selectedPaint.setAntiAlias(true);

        errorPaint = new Paint();
        errorPaint.setStyle(Paint.Style.FILL);
        errorPaint.setAntiAlias(true);
        errorPaint.setColor(Color.parseColor("#ff0000"));;
        errorPaint.setStrokeWidth(r);

    }

    private void chche() {
        x = width / 2;
        a = x / 3;
        b = x * 5 / 3;
        r = width / 50;
        list = new ArrayList();
        int i  = 1;
        list.add(new Point(a, a, i++));
        list.add(new Point(x, a, i++));
        list.add(new Point(b, a, i++));

        list.add(new Point(a, x, i++));
        list.add(new Point(x, x, i++));
        list.add(new Point(b, x, i++));

        list.add(new Point(a, b, i++));
        list.add(new Point(x, b, i++));
        list.add(new Point(b, b, i++));

        initPaint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isTouch) {
            return false;
        }

        Point p = null;
        float ex = event.getX();
        float ey = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                p = checkSelectPoint(ex, ey);
                if (p != null) {
                    checking = true;
                }
                moveX = ex;
                moveY = ey;
                break;
            case MotionEvent.ACTION_MOVE:
                p = checkSelectPoint(ex, ey);
                moveX = ex;
                moveY = ey;
                if (p != null) {
                    checking = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(checking) {
                    isFinish = true;
                    isTouch = false;
                }
                break;
        }
        if (!isFinish && checking && p != null && selectList.size() < 200) {

            if (selectList.size() < 1) {//第一次不用判断上一个点
                selectList.add(p);
                set.add(p.index);
                z();
            } else if (selectList.get(selectList.size() - 1).x != p.x || selectList.get(selectList.size() - 1).y != p.y) {//判断不是上一个点
                //能否多次连同一个点
                if(isSelectedSamePoint)
                {
                    selectList.add(p);
                    set.add(p.index);
                    z();
                }
                else {
                    boolean isSelected = false;
                    for (int i = 0; i < selectList.size(); i++) {
                        if (selectList.get(i).index == p.index) {
                            isSelected = true;
                            break;
                        }
                    }
                    if (!isSelected) {
                        selectList.add(p);
                        set.add(p.index);
                        z();
                    }
                }
            }
        }

        if (isFinish) {
            if (mCompleteListener != null)
                mCompleteListener.onComplete(selectList);
        }

        invalidate();
        return true;
    }

    private Point checkSelectPoint(float x, float y) {
        for (int i = 0; i < list.size(); i++) {
            Point p = list.get(i);
            if (MathUtil.checkInRound(p.x, p.y, width / 9f, (int) x, (int) y)) {
                return p;
            }
        }
        return null;
    }

    private void z() {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(20);
    }


    private OnCompleteListener mCompleteListener;

    /**
     * @param mCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public void reset() {
        isFinish = false;
        isTouch = true;
        checking = false;
        selectList.clear();
        set.clear();
        invalidate();




        
        isError = false;
    }

    public void setError() {
        isError = true;
        for(Point p : selectList)
            p.state = 2;
    }

    public void setSelectedSamePoint(boolean isSelectedSamePoint) {
        this.isSelectedSamePoint = isSelectedSamePoint;
        reset();
    }

    public interface OnCompleteListener {
        public void onComplete(List<Point> password);
    }


}
