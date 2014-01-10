package fr.prodev73.maison.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

/**
 * Created by nberlioz on 03/01/14.
 */
public class ImageViewSvg extends ImageView implements View.OnTouchListener{

	private Context context;
	private int currentResource = 0;
	protected float mScale = 1;
	private SVG svg = null;
	private static final int INVALID_POINTER_ID = -1;
	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private float mLastGestureX;
	private float mLastGestureY;
	private int mActivePointerId = INVALID_POINTER_ID;

	private ScaleGestureDetector mScaleDetector;

	public ImageViewSvg(Context context) {
		super(context);
		commonInit(context);
	}

	public ImageViewSvg(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonInit(context);
	}

	public ImageViewSvg(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		commonInit(context);
	}

	private void commonInit(Context context) {
		this.context = context;
		this.setOnTouchListener(this);
		mScaleDetector = new ScaleGestureDetector(getContext(),
				new ScaleListener());
	}

	public void drawSvg(int resId) {
		this.currentResource = resId;
		try {
			svg = SVG.getFromResource(context, currentResource);
		} catch (SVGParseException e) {
			e.printStackTrace();
		}
		paintSvg();
	}

	private void paintSvg() {
		if (svg == null) {
			return;
		}
		int width = (int) (svg.getDocumentWidth() * mScale);
		int height = (int) (svg.getDocumentHeight() * mScale);

		Bitmap newBM = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas bmcanvas = new Canvas(newBM);
		// Clear background to white
		bmcanvas.drawRGB(255, 255, 255);
		// Render our document onto our canvas
		svg.renderToCanvas(bmcanvas);
		setImageBitmap(newBM);
	}

	@Override
	public boolean onTouch(View view, MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (!mScaleDetector.isInProgress()) {
				final float x = ev.getX();
				final float y = ev.getY();

				mLastTouchX = x;
				mLastTouchY = y;
				mActivePointerId = ev.getPointerId(0);
			}
			break;
		}
		case MotionEvent.ACTION_POINTER_1_DOWN: {
			if (mScaleDetector.isInProgress()) {
				final float gx = mScaleDetector.getFocusX();
				final float gy = mScaleDetector.getFocusY();
				mLastGestureX = gx;
				mLastGestureY = gy;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!mScaleDetector.isInProgress()) {
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);

				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();

				mLastTouchX = x;
				mLastTouchY = y;
			} else {
				final float gx = mScaleDetector.getFocusX();
				final float gy = mScaleDetector.getFocusY();

				final float gdx = gx - mLastGestureX;
				final float gdy = gy - mLastGestureY;

				mPosX += gdx;
				mPosY += gdy;

				invalidate();

				mLastGestureX = gx;
				mLastGestureY = gy;
			}

			break;
		}
		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				Log.d("DEBUG", "mActivePointerId");
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}

		return true;
	}

	public void restore()
	{
		mScale=1;
		mPosX=0;
		mPosY=0;
		this.invalidate();
	}
	@Override
	public void onDraw(Canvas canvas) {

		canvas.save();

		canvas.translate(mPosX, mPosX);
		if (mScaleDetector.isInProgress()) {
			canvas.scale(mScale, mScale, mScaleDetector.getFocusX(),
					mScaleDetector.getFocusY());
		} else {
			canvas.scale(mScale, mScale);
		}
		super.onDraw(canvas);
		canvas.restore();
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScale *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScale = Math.max(0.1f, Math.min(mScale, 10.0f));

			invalidate();
			return true;
		}
	}

	public boolean isOriginal() {
		return (mScale==1) && (mPosX==0) && (mPosY==0);
	}
}
