package com.github.iojjj.rcbs;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.LineBackgroundSpan;
import android.text.style.ReplacementSpan;
import android.util.SparseIntArray;

/**
 * Span that will be placed between text parts to simulate spacing between them.
 */
class SpacingSpan extends ReplacementSpan implements LineBackgroundSpan {

    private final float mSeparatorWidth;
    private final SparseIntArray mLineCoordinates;
    private int mLineCounter;
    private int mSizeCounter;

    static SpacingSpan newInstance(float separatorWidth) {
        return new SpacingSpan(separatorWidth);
    }

    private SpacingSpan(float separatorWidth) {
        mSeparatorWidth = separatorWidth;
        mLineCoordinates = new SparseIntArray();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start,
                       @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
        mLineCounter = 0; // reset counter just in case
        final int coordinates = mLineCoordinates.get(mSizeCounter);
        final int lineStart = coordinates & 0xFFFF;
        final int lineEnd = (coordinates >> 16) & 0xFFFF;
        if (lineStart == start || lineEnd == end) {
            // don't draw empty space if it's placed in the beginning or in the end of line
            return 0;
        }
        return (int) mSeparatorWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start,
                     @IntRange(from = 0) int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        // increment must be called here, because getSize called multiple times
        mSizeCounter++;
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {
        mSizeCounter = 0; // reset counter just in case
        final int coordinates = start | (end << 16); // encode two integers into one to skip array or object creation
        mLineCoordinates.put(mLineCounter, coordinates);
        mLineCounter++;
    }
}
