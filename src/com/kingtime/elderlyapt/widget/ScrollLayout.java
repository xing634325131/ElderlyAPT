package com.kingtime.elderlyapt.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 左右滑动切换屏幕控件
 * 
 * @author Yao.GUET date: 2011-05-04
 * @modify xp
 */
public class ScrollLayout extends ViewGroup {

	private static final String TAG = "ScrollLayout";
	private VelocityTracker mVelocityTracker;
	private static final int SNAP_VELOCITY = 400;
	private Scroller mScroller;
	private int mCurScreen;
	private int mDefaultScreen = 0;
	private float mLastMotionX;
	private float mLastMotionY;

	private boolean isPass = false;

	/**
	 * 设置是否可以滑动 2014-7-29
	 * 默认为不可滑动
	 * @author xingpeng
	 */
	private boolean canScroll = false;

	public void setCanScroll(boolean canScroll) {
		this.canScroll = canScroll;
	}

	private OnViewChangeListener mOnViewChangeListener;

	public ScrollLayout(Context context) {
		super(context);
		init(context);
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mCurScreen = mDefaultScreen;
		mScroller = new Scroller(context);
		 
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 为每一个孩子设置它们的位置
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					// 此处获取到的宽度就是在onMeasure中设置的值
					final int childWidth = childView.getMeasuredWidth();
					// 为每一个子View布局
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 在onlayout之前执行，获取View申请的大小，把它们保存下来，方面后面使用
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		// 为每一个孩子设置它们的大小为ScrollLayout的大小
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();// 子view的宽度，此例中为他适配的父view的宽度
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// 获取要滚动到的目标screen
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			// 获取屏幕移到目的view还需要移动多少距离
			final int delta = whichScreen * getWidth() - getScrollX();
			// 使用Scroller辅助滚动，让滚动变得更平滑
			mScroller.startScroll(getScrollX(), 0, delta, 0, 300);

			mCurScreen = whichScreen;
			invalidate(); // 重绘界面
			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);
			}
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		Log.i("function1", String.valueOf(canScroll));
//		if (!canScroll) {
			return super.onTouchEvent(event);
//		}
//		final int action = event.getAction();
//		final float x = event.getX();
//		final float y = event.getY();
//
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:// 1,终止滚动2,获取最后一次事件的x值
//			// System.out.println("父类点击onTouchEvent");
//			Log.i("", "onTouchEvent  ACTION_DOWN");
//			if (mVelocityTracker == null) {
//				mVelocityTracker = VelocityTracker.obtain();
//				mVelocityTracker.addMovement(event);
//			}
//			if (!mScroller.isFinished()) {
//				mScroller.abortAnimation();
//			}
//			mLastMotionX = x;
//			mLastMotionY = y;
//			break;
//
//		case MotionEvent.ACTION_MOVE:// 1,获取最后一次事件的x值2,滚动到指定位置
//			// System.out.println("父类滑动onTouchEvent");
//			int deltaX = (int) (mLastMotionX - x);
//			if (IsCanMove(deltaX)) {
//				if (mVelocityTracker != null) {
//					mVelocityTracker.addMovement(event);
//				}
//				mLastMotionX = x;
//				scrollBy(deltaX, 0);
//			}
//
//			break;
//		case MotionEvent.ACTION_UP:// 1,计算手指移动的速度并得出我们需要的速度2,选择不同情况下滚动到哪个 screen
//			// System.out.println("父类放开onTouchEvent");
//			int velocityX = 0;
//			if (mVelocityTracker != null) {
//				mVelocityTracker.addMovement(event);
//				// 设置属性为计算1秒运行多少个像素
//				// computeCurrentVelocity(int
//				// units, float
//				// maxVelocity)上面的1000即为此处的units。
//				// maxVelocity必须为正，表示当计算出的速率大于maxVelocity时为maxVelocity
//				// 小于maxVelocity就为计算出的速率
//				mVelocityTracker.computeCurrentVelocity(1000);
//				velocityX = (int) mVelocityTracker.getXVelocity();
//			}
//			// 如果速度为正，则表示向右滑动。需要指定mCurScreen大于0，才能滑，不然就不准确啦
//			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
//				// Fling enough to move left
//				Log.e(TAG, "snap left");
//				snapToScreen(mCurScreen - 1);
//			}
//			// 如果速度为负，则表示手指向左滑动。需要指定mCurScreen小于最后一个子view的id，才能滑，不然就不准确啦
//			else if (velocityX < -SNAP_VELOCITY
//					&& mCurScreen < getChildCount() - 1) {
//				// Fling enough to move right
//				Log.e(TAG, "snap right");
//				snapToScreen(mCurScreen + 1);
//			}
//			// 速度小于我们规定的达标速度，那么就让界面跟着手指滑动显示。最后显示哪个screen再做计算（方法中有计算）
//			else {
//				snapToDestination();
//			}
//
//			if (mVelocityTracker != null) {
//				mVelocityTracker.recycle();
//				mVelocityTracker = null;
//			}
//			// mTouchState = TOUCH_STATE_REST;
//			break;
//		}
//		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

//		Log.i("function2", String.valueOf(canScroll));
//		if (!canScroll) {
//			return onInterceptTouchEvent(event);
//		}
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:// 判断滚动是否停止
//			// System.out.println("父类点击onInterceptTouchEvent");
//			if (isPass) {
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:// 判断是否达成滚动条件
//			// System.out.println("父类滑动onInterceptTouchEvent");
//			if (isPass) {
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_UP:// 把状态调整为空闲
//			// System.out.println("父类放开onInterceptTouchEvent");
//			break;
//		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
//
//		Log.i("function3", String.valueOf(canScroll));
//		if (!canScroll) {
//			return super.dispatchTouchEvent(event);
//		}
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			mLastMotionX = event.getX();
//			mLastMotionY = event.getY();
//			// System.out.println("父类点击dispatchTouchEvent");
//			break;
//		case MotionEvent.ACTION_MOVE:
//			// System.out.println(Math.abs(event.getX() - mLastMotionX));
//			// System.out.println(Math.abs(event.getY() - mLastMotionY));
//			double tanNum = Math.atan(Math.abs(event.getY() - mLastMotionY)
//					/ Math.abs(event.getX() - mLastMotionX));
//			double retote = tanNum / 3.14 * 180;
//			// System.out.println("角度:" + retote);
//			if (retote < 45) {
//				// System.out.println("---------父类滑动dispatchTouchEvent");
//				isPass = true;
//			} else {
//				isPass = false;
//			}
//			onInterceptTouchEvent(event);
//			// System.out.println("***************" + isPass);
//			break;
//		case MotionEvent.ACTION_UP:
//			// System.out.println("父类放开dispatchTouchEvent");
//			break;
//		}
		return super.dispatchTouchEvent(event);
	}

	private boolean IsCanMove(int deltaX) {
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}
		return true;
	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}
}