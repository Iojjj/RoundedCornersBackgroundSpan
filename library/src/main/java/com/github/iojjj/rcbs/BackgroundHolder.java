package com.github.iojjj.rcbs;

/**
 * Holder that stores info about background color and position in text.
 */
final class BackgroundHolder {

    /**
     * Background color.
     */
    private int mBgColor;

    /**
     * Start offset of background.
     */
    private int mStart;

    /**
     * End offset of background.
     */
    private int mEnd;

    BackgroundHolder(int bgColor) {
        mBgColor = bgColor;
    }

    int getBgColor() {
        return mBgColor;
    }

    int getStart() {
        return mStart;
    }

    int getEnd() {
        return mEnd;
    }

    BackgroundHolder setStart(int start) {
        mStart = start;
        return this;
    }

    BackgroundHolder setEnd(int end) {
        mEnd = end;
        return this;
    }
}
