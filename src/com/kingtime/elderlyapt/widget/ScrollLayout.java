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
 * ���һ����л���Ļ�ؼ�
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
	 * �����Ƿ���Ի��� 2014-7-29
	 * Ĭ��Ϊ���ɻ���
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
		// Ϊÿһ�������������ǵ�λ��
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					// �˴���ȡ���Ŀ�Ⱦ�����onMeasure�����õ�ֵ
					final int childWidth = childView.getMeasuredWidth();
					// Ϊÿһ����View����
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
		// ��onlayout֮ǰִ�У���ȡView����Ĵ�С�������Ǳ����������������ʹ��
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		// Ϊÿһ�������������ǵĴ�СΪScrollLayout�Ĵ�С
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();// ��view�Ŀ�ȣ�������Ϊ������ĸ�view�Ŀ��
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// ��ȡҪ��������Ŀ��screen
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			// ��ȡ��Ļ�Ƶ�Ŀ��view����Ҫ�ƶ����پ���
			final int delta = whichScreen * getWidth() - getScrollX();
			// ʹ��Scroller�����������ù�����ø�ƽ��
			mScroller.startScroll(getScrollX(), 0, delta, 0, 300);

			mCurScreen = whichScreen;
			invalidate(); // �ػ����
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
//		case MotionEvent.ACTION_DOWN:// 1,��ֹ����2,��ȡ���һ���¼���xֵ
//			// System.out.println("������onTouchEvent");
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
//		case MotionEvent.ACTION_MOVE:// 1,��ȡ���һ���¼���xֵ2,������ָ��λ��
//			// System.out.println("���໬��onTouchEvent");
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
//		case MotionEvent.ACTION_UP:// 1,������ָ�ƶ����ٶȲ��ó�������Ҫ���ٶ�2,ѡ��ͬ����¹������ĸ� screen
//			// System.out.println("����ſ�onTouchEvent");
//			int velocityX = 0;
//			if (mVelocityTracker != null) {
//				mVelocityTracker.addMovement(event);
//				// ��������Ϊ����1�����ж��ٸ�����
//				// computeCurrentVelocity(int
//				// units, float
//				// maxVelocity)�����1000��Ϊ�˴���units��
//				// maxVelocity����Ϊ������ʾ������������ʴ���maxVelocityʱΪmaxVelocity
//				// С��maxVelocity��Ϊ�����������
//				mVelocityTracker.computeCurrentVelocity(1000);
//				velocityX = (int) mVelocityTracker.getXVelocity();
//			}
//			// ����ٶ�Ϊ�������ʾ���һ�������Ҫָ��mCurScreen����0�����ܻ�����Ȼ�Ͳ�׼ȷ��
//			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
//				// Fling enough to move left
//				Log.e(TAG, "snap left");
//				snapToScreen(mCurScreen - 1);
//			}
//			// ����ٶ�Ϊ�������ʾ��ָ���󻬶�����Ҫָ��mCurScreenС�����һ����view��id�����ܻ�����Ȼ�Ͳ�׼ȷ��
//			else if (velocityX < -SNAP_VELOCITY
//					&& mCurScreen < getChildCount() - 1) {
//				// Fling enough to move right
//				Log.e(TAG, "snap right");
//				snapToScreen(mCurScreen + 1);
//			}
//			// �ٶ�С�����ǹ涨�Ĵ���ٶȣ���ô���ý��������ָ������ʾ�������ʾ�ĸ�screen�������㣨�������м��㣩
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
//		case MotionEvent.ACTION_DOWN:// �жϹ����Ƿ�ֹͣ
//			// System.out.println("������onInterceptTouchEvent");
//			if (isPass) {
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:// �ж��Ƿ��ɹ�������
//			// System.out.println("���໬��onInterceptTouchEvent");
//			if (isPass) {
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_UP:// ��״̬����Ϊ����
//			// System.out.println("����ſ�onInterceptTouchEvent");
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
//			// System.out.println("������dispatchTouchEvent");
//			break;
//		case MotionEvent.ACTION_MOVE:
//			// System.out.println(Math.abs(event.getX() - mLastMotionX));
//			// System.out.println(Math.abs(event.getY() - mLastMotionY));
//			double tanNum = Math.atan(Math.abs(event.getY() - mLastMotionY)
//					/ Math.abs(event.getX() - mLastMotionX));
//			double retote = tanNum / 3.14 * 180;
//			// System.out.println("�Ƕ�:" + retote);
//			if (retote < 45) {
//				// System.out.println("---------���໬��dispatchTouchEvent");
//				isPass = true;
//			} else {
//				isPass = false;
//			}
//			onInterceptTouchEvent(event);
//			// System.out.println("***************" + isPass);
//			break;
//		case MotionEvent.ACTION_UP:
//			// System.out.println("����ſ�dispatchTouchEvent");
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