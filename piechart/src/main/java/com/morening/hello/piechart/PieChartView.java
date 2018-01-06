package com.morening.hello.piechart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieChartView extends View {
    private Context mContext = null;

    private static final int CONSTANTS_BLANK_DEGREE = 2;
    private static final float CONSTANTS_SCALED_09 = 0.9f;

    private float mSum = 0f;
    private List<DataBean> mChartDatas = null;
    private int mCenterTextColor = Color.BLACK;
    private int mCenterTextSize = 0;
    private String mPostfix = null;
    private Rect mCenterTextRect = null;
    private CalculateSweepAsyncTask mCalculateSweepTask = null;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mCenterTextSize = Utils.sp2px(mContext, 16);
    }

    /*
     * show the pie chart with given data list
     * Any listener MUST BE set after this method invoked
     * Because this method will init all listeners
     *
     * @param datas List<DataBean>
     */
    public void show(List<DataBean> datas){
        if (datas == null || datas.size() == 0){
            return;
        }

        mOnSegmentTwiceClickListener = null;
        mOnCenterTextTwiceClickListener = null;
        mOnShowStateListener = null;
        mSegmentTouched = false;
        mSegmentSelectedColor = Color.TRANSPARENT;
        mCTextTouched = false;

        mCalculateSweepTask = new CalculateSweepAsyncTask();
        mCalculateSweepTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, datas);
    }

    /*
     * cancel the showing operation including calculate sweep task
     */
    public void cancel(){
        if (mCalculateSweepTask != null){
            if (mCalculateSweepTask.cancel(true)){
                mCalculateSweepTask = null;
            }
        }
    }

    /*
     * set center text color
     *
     * @param color the text color
     */
    public void setCenterTextColor(int color){
        mCenterTextColor = color;
    }

    /*
     * set center text size
     *
     * @param size the text size
     */
    public void setCenterTextSize(int size){
        mCenterTextSize = size;
    }

    /*
     * set center text postfix
     *
     * @param postfix the postfix like "kb", "MB", "GB", "kg" or others
     */
    public void setCenterTextPostfix(String postfix){
        mPostfix = postfix;
    }

    private boolean mSegmentTouched = false;
    private int mSegmentSelectedColor = Color.TRANSPARENT;
    private int mSegmentLastSelectedColor = Color.TRANSPARENT;
    private long mSegmentSelectedLastTime = 0L;

    private boolean mCTextTouched = false;
    private long mCTextTouchedLastTime = 0L;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingStart() - getPaddingEnd();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);

        /*draw the outer arc*/
        int scaled_width = (int) (width * CONSTANTS_SCALED_09);
        int scaled_height = (int) (height * CONSTANTS_SCALED_09);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        DataBean selectedData = null;
        float startAngle = 0f;
        for (DataBean data: mChartDatas) {
            paint.setColor(data.getColor());
            if (mSegmentSelectedColor == data.getColor()) {
                selectedData = data;
                canvas.drawArc(-width / 2, -height / 2,
                        width / 2, height / 2,
                        startAngle - CONSTANTS_BLANK_DEGREE, data.getSweep() + 2 * CONSTANTS_BLANK_DEGREE,
                        true, paint);
            } else {
                canvas.drawArc(-scaled_width / 2, -scaled_height / 2,
                        scaled_width / 2, scaled_height / 2,
                        startAngle, data.getSweep(), true, paint);
            }
            startAngle += data.getSweep() + CONSTANTS_BLANK_DEGREE;
        }

        /*draw the inner arc*/
        paint.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, scaled_width / 4, paint);
        startAngle = 0f;
        for (DataBean data: mChartDatas) {
            paint.setColor(data.getColor());
            paint.setAlpha(100);
            canvas.drawArc(-scaled_width / 4, -scaled_height / 4,
                    scaled_width / 4, scaled_height / 4,
                    startAngle + CONSTANTS_BLANK_DEGREE, data.getSweep() - 2 * CONSTANTS_BLANK_DEGREE,
                    true, paint);
            startAngle += data.getSweep() + CONSTANTS_BLANK_DEGREE;
        }

        /*draw the center white circle*/
        paint.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, (float) (scaled_width / 4.5), paint);

        /*draw the center text*/
        paint.setColor(mCenterTextColor);
        if (mCTextTouched){
            paint.setTextSize(mCenterTextSize+Utils.sp2px(mContext, 5));
        } else {
            paint.setTextSize(mCenterTextSize);
        }
        String format = null;
        if (TextUtils.isEmpty(mPostfix)){
            format = "%d";
        } else {
            format = "%d "+mPostfix;
        }
        String cText = null;
        if (mSegmentTouched){
            cText = String.format(format, (int)selectedData.getData());
        } else {
            cText = String.format(format, (int)mSum);
        }
        Rect rect = new Rect();
        paint.getTextBounds(cText, 0, cText.length(), rect);
        int cTextWidth = rect.width();
        int cTextHeight = rect.height();
        canvas.drawText(cText, - cTextWidth/2, cTextHeight/2, paint);

        /*get the center text range*/
        if (mCenterTextRect == null){
            mCenterTextRect = new Rect();
        }
        mCenterTextRect.left = (width - cTextWidth)/2;
        mCenterTextRect.top = (height - cTextHeight)/2;
        mCenterTextRect.right = mCenterTextRect.left + cTextWidth;
        mCenterTextRect.bottom = mCenterTextRect.top + cTextHeight;

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        event.getPointerCoords(0, coords);
        float x = coords.getAxisValue(MotionEvent.AXIS_X);
        float y = coords.getAxisValue(MotionEvent.AXIS_Y);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*get drawing cache*/
                setDrawingCacheEnabled(true);
                buildDrawingCache();
                Bitmap bitmap = getDrawingCache();

                /*get the selected color*/
                mSegmentSelectedColor = bitmap.getPixel((int) x, (int) y);
                setDrawingCacheEnabled(false);
                bitmap.recycle();

                /*check the selected color*/
                mSegmentTouched = false;
                for (DataBean data: mChartDatas) {
                    if (mSegmentSelectedColor == data.getColor()) {
                        mSegmentTouched = true;
                        break;
                    }
                }

                /*check if twice clicked or not*/
                if (mSegmentTouched) {
                    int tempColor = mSegmentLastSelectedColor;
                    mSegmentLastSelectedColor = mSegmentSelectedColor;
                    long tempTime = mSegmentSelectedLastTime;
                    mSegmentSelectedLastTime = System.currentTimeMillis();
                    if (mSegmentSelectedLastTime - tempTime <= 1000 && mSegmentLastSelectedColor == tempColor) {
                        if (mOnSegmentTwiceClickListener != null){
                            int index = 0;
                            for (;index<mChartDatas.size(); index++){
                                if (mChartDatas.get(index).getColor() == mSegmentLastSelectedColor){
                                    break;
                                }
                            }
                            mOnSegmentTwiceClickListener.onTwiceClicked(mChartDatas.get(index));
                        }
                    }
                }

                /*check if in the range of center text*/
                if (x >= mCenterTextRect.left && x <= mCenterTextRect.right
                        && y >= mCenterTextRect.top && y <= mCenterTextRect.bottom){
                    mCTextTouched = true;
                    long temp = mCTextTouchedLastTime;
                    mCTextTouchedLastTime = System.currentTimeMillis();
                    if (mCTextTouchedLastTime - temp <= 1000){
                        if (mOnCenterTextTwiceClickListener != null){
                            mOnCenterTextTwiceClickListener.onTwiceClicked(null);
                        }
                    }
                }

                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                mSegmentTouched = false;
                mSegmentSelectedColor = Color.TRANSPARENT;
                mCTextTouched = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    final class CalculateSweepAsyncTask extends AsyncTask<List<DataBean>, Integer, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mOnShowStateListener != null){
                mOnShowStateListener.onStart();
            }
        }

        @Override
        protected Void doInBackground(List<DataBean>[] datas) {
            if (isCancelled()){
                return null;
            }

            mChartDatas = new ArrayList<>(datas[0]);

            mSum = 0f;
            for (DataBean data: mChartDatas) {
                mSum += data.getData();
            }

            for (DataBean data: mChartDatas) {
                data.setSweep(data.getData() / mSum * (360f - datas[0].size() * CONSTANTS_BLANK_DEGREE));
            }

            for (DataBean data: mChartDatas){
                if (data.getColor() == Color.TRANSPARENT){
                    data.setColor(getColorRandom());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void NULL) {
            super.onPostExecute(NULL);
            if (isCancelled()){
                return;
            }

            if (mOnShowStateListener != null){
                mOnShowStateListener.onShow();
            }
            invalidate();
            if (mOnShowStateListener != null){
                mOnShowStateListener.onFinished();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mOnShowStateListener != null){
                mOnShowStateListener.onCancelled();
            }
        }
    }

    private int getColorRandom() {

        int r = new Random().nextInt(255);
        int g = new Random().nextInt(255);
        int b = new Random().nextInt(255);

        return Color.rgb(r, g, b);

    }

    private OnSegmentTwiceClickListener mOnSegmentTwiceClickListener = null;
    public void setOnSegmentTwiceClickListener(OnSegmentTwiceClickListener listener){
        mOnSegmentTwiceClickListener = listener;
    }
    public interface OnSegmentTwiceClickListener {
        void onTwiceClicked(DataBean data);
    }

    private OnCenterTextTwiceClickListener mOnCenterTextTwiceClickListener = null;
    public void setOnCenterTextTwiceClickListener(OnCenterTextTwiceClickListener listener){
        mOnCenterTextTwiceClickListener = listener;
    }
    public interface OnCenterTextTwiceClickListener {
        void onTwiceClicked(DataBean data);
    }

    private OnShowStateListener mOnShowStateListener = null;
    public void setOnShowStateListener(OnShowStateListener listener){
        mOnShowStateListener = listener;
    }
    public interface OnShowStateListener{
        void onStart();
        void onCancelled();
        void onShow();
        void onFinished();
    }
}
