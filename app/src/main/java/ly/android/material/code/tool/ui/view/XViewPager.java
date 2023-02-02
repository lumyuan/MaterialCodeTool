package ly.android.material.code.tool.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class XViewPager extends ViewPager {

    private boolean noScroll = false;
    private boolean noScrollAnim = true;
    private boolean left = false;
    private boolean right = false;
    private boolean isScrolling = false;
    private int lastValue = -1;
    private ChangeViewCallback changeViewCallback = null;

    public XViewPager(Context context) {
        super(context);
        init();
    }

    public XViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSlide(boolean slide) {
        this.noScroll = slide;
    }

    /**
     * 设置是否能左右滑动
     * @param noScroll true 不能滑动
     */
    public void setScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    /**
     * 设置没有滑动动画
     * @param noAnim false 无动画
     */
    public void setScrollAnim(boolean noAnim){
        this.noScrollAnim = noAnim;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return !noScroll && super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return !noScroll && super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,noScrollAnim);
    }

    private void init() {
        setOnPageChangeListener(listener);
    }

    /**
     * listener ,to get move direction .
     */
    public  OnPageChangeListener listener = new OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == 1) {
                isScrolling = true;
            } else {
                isScrolling = false;
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (isScrolling) {
                if (lastValue > arg2) {
                    // 递减，向右侧滑动
                    right = true;
                    left = false;
                } else if (lastValue < arg2) {
                    // 递减，向右侧滑动
                    right = false;
                    left = true;
                } else if (lastValue == arg2) {
                    right = left = false;
                }
            }
            Log.i("meityitianViewPager",
                    "meityitianViewPager onPageScrolled  last :arg2  ,"
                            + lastValue + ":" + arg2);
            lastValue = arg2;
        }

        @Override
        public void onPageSelected(int arg0) {
            if(changeViewCallback!=null){
                changeViewCallback.getCurrentPageIndex(arg0);
            }
        }
    };

    /**
     * 得到是否向右侧滑动
     * @return true 为右滑动
     */
    public boolean getMoveRight(){
        return right;
    }

    /**
     * 得到是否向左侧滑动
     * @return true 为左做滑动
     */
    public boolean getMoveLeft(){
        return left;
    }

    /**
     *  滑动状态改变回调
     * @author zxy
     *
     */
    public interface ChangeViewCallback{
        /**
         * 切换视图 ？决定于left和right 。
         * @param left
         * @param right
         */
        public  void changeView(boolean left,boolean right);
        public  void  getCurrentPageIndex(int index);
    }

    /**
     * set ...
     * @param callback
     */
    public void  setChangeViewCallback(ChangeViewCallback callback){
        changeViewCallback = callback;
    }
}