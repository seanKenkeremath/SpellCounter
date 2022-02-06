package com.kenkeremath.mtgcounter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;

import com.kenkeremath.mtgcounter.R;

/**
 * Legacy code, forked from a library + adapted
 */

public class LineColorPickerView extends View {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    int[] colors = new int[1];

    private Paint paint;
    private Paint highlightPaint;
    private RectF rect = new RectF();

    // indicate if nothing selected
    boolean isColorSelected = false;

    private int selectedColor = colors[0];

    private OnColorChangedListener onColorChanged;

    private int mCellWidth;
    private int mCellHeight;
    private int mRowLength;
    private int mHighlightColor;
    private float mHighlightStrokeWidth;

    private int mOrientation = HORIZONTAL;

    private int mRows;

    public LineColorPickerView(Context context) {
        this(context, null, 0, 0);
    }

    public LineColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public LineColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LineColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);

        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LineColorPickerView, 0, 0);

        try {
            mOrientation = a.getInteger(R.styleable.LineColorPickerView_orientation, HORIZONTAL);
            mRows = a.getInteger(R.styleable.LineColorPickerView_rows, 1);
            mHighlightColor = a.getColor(R.styleable.LineColorPickerView_highlightColor, Color.BLACK);
            mHighlightStrokeWidth = a.getDimension(R.styleable.LineColorPickerView_highlightStrokeWidth, 8f);

            if (!isInEditMode()) {
                final int colorsArrayResId = a.getResourceId(R.styleable.LineColorPickerView_colors, -1);

                if (colorsArrayResId > 0) {
                    final int[] colors = context.getResources().getIntArray(colorsArrayResId);
                    setColors(colors);
                }
            }

            final int selected = a.getInteger(R.styleable.LineColorPickerView_selectedColorIndex, -1);

            if (selected != -1) {
                final int[] currentColors = getColors();

                final int currentColorsLength = currentColors != null ? currentColors.length : 0;

                if (selected < currentColorsLength) {
                    setSelectedColorPosition(selected);
                }
            }

        } finally {
            a.recycle();
        }

        paint = new Paint();
        paint.setStyle(Style.FILL);

        highlightPaint = new Paint();
        highlightPaint.setStyle(Style.STROKE);
        highlightPaint.setStrokeWidth(mHighlightStrokeWidth);
        highlightPaint.setColor(mHighlightColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOrientation == HORIZONTAL) {
            drawHorizontalPicker(canvas);
        } else {
            drawVerticalPicker(canvas);
        }

    }

    //TODO: make vertical support rows
    private void drawVerticalPicker(Canvas canvas) {
        rect.left = 0;
        rect.top = 0;
        rect.right = canvas.getWidth();
        rect.bottom = 0;

        // 8%
        int margin = Math.round(canvas.getWidth() * 0.08f);

        for (int i = 0; i < colors.length; i++) {

            paint.setColor(colors[i]);

            rect.top = rect.bottom;
            rect.bottom += mCellHeight;

            if (isColorSelected && colors[i] == selectedColor) {
                rect.left = 0;
                rect.right = canvas.getWidth();
            } else {
                rect.left = margin;
                rect.right = canvas.getWidth() - margin;
            }

            canvas.drawRect(rect, paint);
        }

    }

    private void drawHorizontalPicker(Canvas canvas) {
        rect.left = 0;
        rect.top = 0;
        rect.right = 0;
        rect.bottom = canvas.getHeight();

        for (int i = 0; i < colors.length; i++) {

            int row = i / mRowLength;

            paint.setColor(colors[i]);

            if (i % mRowLength == 0) {
                rect.left = 0;
                rect.right = 0;
            }

            rect.left = rect.right;
            rect.right += mCellWidth;

            rect.top = row * mCellHeight;
            rect.bottom = rect.top + mCellHeight;

            canvas.drawRect(rect, paint);

            if (isColorSelected && colors[i] == selectedColor) {
                //stroke is centered on boundary. need to inset to get full stroke width inside rect
                float insetAmount = mHighlightStrokeWidth / 2;
                rect.inset(insetAmount, insetAmount);
                canvas.drawRect(rect, highlightPaint);
                rect.inset(-insetAmount, -insetAmount);
            }
        }
    }

    private void onColorChanged(int color) {
        if (onColorChanged != null) {
            onColorChanged.onColorChanged(color);
        }
    }

    private boolean isClick = false;
    private int screenW;
    private int screenH;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int actionId = event.getAction();

        int newColor;

        switch (actionId) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                break;
            case MotionEvent.ACTION_UP:
                newColor = getColorAtXY(event.getX(), event.getY());

                setSelectedColor(newColor);

                if (isClick) {
                    performClick();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                newColor = getColorAtXY(event.getX(), event.getY());

                setSelectedColor(newColor);

                break;
            case MotionEvent.ACTION_CANCEL:
                isClick = false;
                break;

            case MotionEvent.ACTION_OUTSIDE:
                isClick = false;
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * Return color at x,y coordinate of view.
     */
    private int getColorAtXY(float x, float y) {


        //TODO: better solution for this?
        //prevent index out of bounds exceptions
        if (x > screenW) {
            x = screenW - 1;
        }

        if (y > screenH) {
            y = screenH - 1;
        }

        if (y < 0) {
            y = 1f;
        }

        if (x < 0) {
            x = 1f;
        }

        int xIndex = (int) (x / mCellWidth);
        int yIndex = (int) (y / mCellHeight);

        if (mOrientation == HORIZONTAL) {

            int colorIndex = yIndex * mRowLength + xIndex;

            return colors[colorIndex];

        } else {

            int colorIndex = xIndex * mRowLength + yIndex;

            return colors[colorIndex];
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        // end

        ss.selectedColor = this.selectedColor;
        ss.isColorSelected = this.isColorSelected;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // begin boilerplate code so parent classes can restore state
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        // end

        this.selectedColor = ss.selectedColor;
        this.isColorSelected = ss.isColorSelected;
    }

    static class SavedState extends BaseSavedState {
        int selectedColor;
        boolean isColorSelected;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedColor = in.readInt();
            this.isColorSelected = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selectedColor);
            out.writeInt(this.isColorSelected ? 1 : 0);
        }

        // required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        screenW = w;
        screenH = h;

        recalcCellSize();

        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(ViewOutlineProvider.BOUNDS);
    }

    // @Override
    // protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
    // int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    // this.setMeasuredDimension(parentWidth, parentHeight);
    // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // }

    /**
     * Return currently selected color.
     */
    public int getColor() {
        return selectedColor;
    }

    /**
     * Set selected color as color value from palette.
     */
    public void setSelectedColor(int color) {

        // not from current palette
        if (!containsColor(colors, color)) {
            return;
        }

        // do we need to re-draw view?
        if (!isColorSelected || selectedColor != color) {
            this.selectedColor = color;

            isColorSelected = true;

            invalidate();

            onColorChanged(color);
        }
    }

    /**
     * Set selected color as index from palete
     */
    public void setSelectedColorPosition(int position) {
        setSelectedColor(colors[position]);
    }

    /**
     * Set picker palette
     */
    public void setColors(int[] colors) {
        // TODO: selected color can be NOT in set of colors
        // FIXME: colors can be null
        this.colors = colors;

        if (!containsColor(colors, selectedColor)) {
            selectedColor = colors[0];
        }

        recalcCellSize();

        invalidate();
    }

    private void recalcCellSize() {
        if (mOrientation == HORIZONTAL) {
            mCellWidth = Math.round(screenW / ((colors.length * 1f) / mRows));
            mCellHeight = screenH / mRows;
        } else {
            mCellHeight = Math.round(screenH / ((colors.length * 1f) / mRows));
            mCellWidth = screenW / mRows;
        }
        mRowLength = (int) Math.ceil(colors.length / mRows * 1f);
    }

    /**
     * Return current picker palete
     */
    public int[] getColors() {
        return colors;
    }

    /**
     * Return true if palette contains this color
     */
    private boolean containsColor(int[] colors, int c) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == c)
                return true;

        }

        return false;
    }

    /**
     * Set onColorChanged listener
     *
     * @param l
     */
    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.onColorChanged = l;
    }

    public interface OnColorChangedListener {
        void onColorChanged(int c);
    }
}