package com.genuine.android.sweetspots;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

public class GameView extends View {

    // boardSize: 4-12
    int boardSize;
    int targetsPerRow;
    int targetsPerCol;
    int targetsPerZone;

    int[][] cellVal;  // 0=empty, 1=filler, 2=target
    int[][] savedCellVal;

    int[][] zoneVal;  // -1=undefined, 0, 1, 2, ...

    private ICellListener mCellListener;

    private static final int MARGIN = 2;

    private final Bitmap[] mBmpZone;
    private final Paint mBmpPaint;
    private final Bitmap mBmpFiller;
    private final Bitmap mBmpTarget;

    private final Rect mSrcRect = new Rect();
    private final Rect mDstRect = new Rect();
    private int mSxy;
    private int mOffsetX;
    private int mOffsetY;

    private long startTime;
    private long cumSeconds;

	public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        requestFocus();

        // Drawable mDrawableBg = getResources().getDrawable(R.drawable.lib_bg);
        Drawable mDrawableBg =
            ResourcesCompat.getDrawable(getResources(), R.drawable.lib_bg, null);
        setBackground(mDrawableBg);

        mBmpZone = new Bitmap[12];
        mBmpZone[0] = getResBitmap(R.drawable.lib_zone00);
        mBmpZone[1] = getResBitmap(R.drawable.lib_zone01);
        mBmpZone[2] = getResBitmap(R.drawable.lib_zone02);
        mBmpZone[3] = getResBitmap(R.drawable.lib_zone03);
        mBmpZone[4] = getResBitmap(R.drawable.lib_zone04);
        mBmpZone[5] = getResBitmap(R.drawable.lib_zone05);
        mBmpZone[6] = getResBitmap(R.drawable.lib_zone06);
        mBmpZone[7] = getResBitmap(R.drawable.lib_zone07);
        mBmpZone[8] = getResBitmap(R.drawable.lib_zone08);
        mBmpZone[9] = getResBitmap(R.drawable.lib_zone09);
        mBmpZone[10] = getResBitmap(R.drawable.lib_zone10);
        mBmpZone[11] = getResBitmap(R.drawable.lib_zone11);

        mBmpFiller = getResBitmap(R.drawable.lib_filler);
        mBmpTarget = getResBitmap(R.drawable.lib_target);

        if (mBmpFiller != null) {
            mSrcRect.set(0, 0, mBmpFiller.getWidth() - 1, mBmpFiller.getHeight() - 1);
        }

        mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public interface ICellListener {
        abstract void onCellSelected();
    }

    public void setCellListener(ICellListener cellListener) {
        mCellListener = cellListener;
    }

    //// Game state methods BEGIN

    void initGameState(int bSize, int rowTargets, int colTargets, int zoneTargets, int[][] zVal) {

        // Log.i("INFO: GameView", "Within initGameState()");

        boardSize = bSize;
        targetsPerRow = rowTargets;
        targetsPerCol = colTargets;
        targetsPerZone = zoneTargets;

        cellVal = new int[boardSize][boardSize];
        savedCellVal = new int[boardSize][boardSize];

        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                cellVal[i][j] = 0;
                savedCellVal[i][j] = 0;
            }
        }

        zoneVal = zVal;

        startTime = System.currentTimeMillis();
    }

    boolean noTargetsAround(int x, int y) {
        boolean noAdjTargets = true;
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if ((i >= 0 && i < boardSize) && (j >= 0 && j < boardSize) && !(i == x && j == y)) {
                    if (cellVal[i][j] == 2) {
                        noAdjTargets = false;
                        break;
                    }
                }
            }
        }
        return noAdjTargets;
    }

    boolean noAdjacentTargets() {
        boolean noAdjTargets = true;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cellVal[i][j] == 2 && !noTargetsAround(i,j)) {
                    noAdjTargets = false;
                    break;
                }
            }
        }
        return noAdjTargets;
    }

    int getTargetCountInRow(int rowId) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            if (cellVal[i][rowId] == 2) {
                count++;
            }
        }
        return count;
    }

    int getTargetCountInCol(int colId) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            if (cellVal[colId][i] == 2) {
                count++;
            }
        }
        return count;
    }

    int getTargetCountInZone(int zId) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (zoneVal[i][j] == zId && cellVal[i][j] == 2)
                    count++;
            }
        }
        return count;
    }

    boolean allRowsValid() {
        boolean isValid = true;
        for (int i = 0; i < boardSize; i++) {
            if (getTargetCountInRow(i) != targetsPerRow) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    boolean allColsValid() {
        boolean isValid = true;
        for (int i = 0; i < boardSize; i++) {
            if (getTargetCountInCol(i) != targetsPerCol) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    boolean allZonesValid() {
        boolean isValid = true;
        for (int i = 0; i < boardSize; i++) {
            if (getTargetCountInZone(i) != targetsPerZone) {
                isValid = false;
                break;
            }
        }
        return isValid;
    } 

    void saveGame() {
        savedCellVal = new int[boardSize][boardSize];
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                savedCellVal[i][j] = cellVal[i][j];
            }
        }
    }

    void restoreGame() {
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                cellVal[i][j] = savedCellVal[i][j];
            }
        }

        // justRestored = true;
        invalidate();
    }

    void resetStartTime() {
        startTime = System.currentTimeMillis();
    }

    long getTimeTaken() {
        long duration = (System.currentTimeMillis() - startTime) / 1000;
        return cumSeconds + duration;
    }

    //// Game state methods END

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Log.i("INFO: GameView", "Within onDraw()");

        int sxy = mSxy;
        int offsetX = mOffsetX;
        int offSetY = mOffsetY;

        for (int j = 0, y = offSetY; j < boardSize; j++, y += sxy) {
            for (int i = 0, x = offsetX; i < boardSize; i++, x += sxy) {

                mDstRect.offsetTo(MARGIN+x, MARGIN+y);

                int val, zVal;

                val = cellVal[i][j];

                zVal = zoneVal[i][j];
                if (mBmpZone[zVal] != null) {
                    canvas.drawBitmap(mBmpZone[zVal], mSrcRect, mDstRect, mBmpPaint);
                }

                switch(val) {
                    case 0:
                        break;
                    case 1:
                        if (mBmpFiller != null) {
                            canvas.drawBitmap(mBmpFiller, mSrcRect, mDstRect, mBmpPaint);
                        }
                        break;
                    case 2:
                        if (mBmpTarget != null) {
                            canvas.drawBitmap(mBmpTarget, mSrcRect, mDstRect, mBmpPaint);
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Log.i("INFO: GameView", "Within onMeasure()");

        // Keep the view squared
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Log.i("INFO: GameView", "Within onSizeChanged()");

        int sx = (w - 2 * MARGIN) / boardSize;
        int sy = (h - 2 * MARGIN) / boardSize;
        int min = sx < sy ? sx : sy;

        mSxy = min;
        mOffsetX = (w - boardSize * min) / 2;
        mOffsetY = (h - boardSize * min) / 2;
        mDstRect.set(MARGIN, MARGIN, min - MARGIN, min - MARGIN);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Log.i("INFO: GameView", "Within onTouchEvent()");

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        else if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int sxy = mSxy;
            x = (x - MARGIN) / sxy;
            y = (y - MARGIN) / sxy;

            if(isEnabled() && (x >= 0 && x < boardSize) && (y >= 0 && y < boardSize)) {
                int val = cellVal[x][y];

                switch(val) {
                    case 0:
                        val = 1;
                        break;
                    case 1:
                        val = 2;
                        break;
                    case 2:
                        val = 0;
                        break;
                }

                cellVal[x][y] = val;

                if (mCellListener != null) {
                    mCellListener.onCellSelected();
                }

                invalidate();
            }
            return true;
        }
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        // Log.i("INFO: GameView", "Within onSaveInstanceState()");

        Bundle b = new Bundle();

        Parcelable s = super.onSaveInstanceState();
        b.putParcelable("par_super_state", s);

        long duration = (System.currentTimeMillis() - startTime) / 1000;
        cumSeconds += duration;
        b.putLong("par_cum_seconds", cumSeconds);

        b.putBoolean("par_enabled", isEnabled());

        b.putInt("par_board_size", boardSize);
        b.putInt("par_targets_per_row", targetsPerRow);
        b.putInt("par_targets_per_col", targetsPerCol);
        b.putInt("par_targets_per_zone", targetsPerZone);

        int[] cellData = new int[boardSize*boardSize];
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                cellData[j*boardSize+i] = cellVal[i][j];
            }
        }
        b.putIntArray("par_cell_data", cellData);

        int[] savedCellData = new int[boardSize*boardSize];
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                savedCellData[j*boardSize+i] = savedCellVal[i][j];
            }
        }
        b.putIntArray("par_saved_cell_data", savedCellData);

        int[] zoneData = new int[boardSize*boardSize];
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                zoneData[j*boardSize+i] = zoneVal[i][j];
            }
        }
        b.putIntArray("par_zone_data", zoneData);

        return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        // Log.i("INFO: GameView", "Within onRestoreInstanceState()");

        if (!(state instanceof Bundle)) {
            // Not supposed to happen.
            super.onRestoreInstanceState(state);
            return;
        }

        Bundle b = (Bundle) state;
        Parcelable superState = b.getParcelable("par_super_state");

        cumSeconds = b.getLong("par_cum_seconds", 0);

        setEnabled(b.getBoolean("par_enabled", true));

        boardSize = b.getInt("par_board_size", 0);
        targetsPerRow = b.getInt("par_targets_per_row", 0);
        targetsPerCol = b.getInt("par_targets_per_col", 0);
        targetsPerZone = b.getInt("par_targets_per_zone", 0);

        int[] cellData = b.getIntArray("par_cell_data");
        if (cellData != null && cellData.length == boardSize*boardSize) {
            for (int j = 0; j < boardSize; j++) {
                for (int i = 0; i < boardSize; i++) {
                    cellVal[i][j] = cellData[j*boardSize+i];
                }
            }
        }

        int[] savedCellData = b.getIntArray("par_saved_cell_data");
        if (savedCellData != null && savedCellData.length == boardSize*boardSize) {
            for (int j = 0; j < boardSize; j++) {
                for (int i = 0; i < boardSize; i++) {
                    savedCellVal[i][j] = savedCellData[j*boardSize+i];
                }
            }
        }

        int[] zoneData = b.getIntArray("par_zone_data");
        if (zoneData != null && zoneData.length == boardSize*boardSize) {
            for (int j = 0; j < boardSize; j++) {
                for (int i = 0; i < boardSize; i++) {
                    zoneVal[i][j] = zoneData[j*boardSize+i];
                }
            }
        }

        super.onRestoreInstanceState(superState);
    }

    private Bitmap getResBitmap(int bmpResId) {
        Options opts = new Options();
        opts.inDither = false;

        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, bmpResId, opts);

        if (bmp == null && isInEditMode()) {
            // BitmapFactory.decodeResource doesn't work from the rendering
            // library in Eclipse's Graphical Layout Editor. Use this workaround instead.

            // Drawable d = res.getDrawable(bmpResId);
            Drawable d =
                ResourcesCompat.getDrawable(getResources(), R.drawable.lib_bg, null);
            if (d != null) {
                int w = d.getIntrinsicWidth();
                int h = d.getIntrinsicHeight();
                bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
                Canvas c = new Canvas(bmp);
                d.setBounds(0, 0, w - 1, h - 1);
                d.draw(c);
            }
        }

        return bmp;
    }
}


