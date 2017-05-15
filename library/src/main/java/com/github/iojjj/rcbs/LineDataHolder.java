package com.github.iojjj.rcbs;

import android.text.TextPaint;

/**
 * Holder that stores data about background that should be drawn on line.
 */
final class LineDataHolder {

    private final TextPaint mTextPaint;
    private final int mStartIntText;
    private final int mEndIntText;
    private final float mLeft, mRight, mTop, mBottom;
    private final BackgroundHolder mBgHolder;

    private LineDataHolder(Builder builder) {
        mTextPaint = builder.mTextPaint;
        mStartIntText = builder.mStartIntText;
        mEndIntText = builder.mEndIntText;
        mLeft = builder.mLeft;
        mRight = builder.mRight;
        mTop = builder.mTop;
        mBottom = builder.mBottom;
        mBgHolder = builder.mBgHolder;
    }

    TextPaint getTextPaint() {
        return mTextPaint;
    }

    int getStartIntText() {
        return mStartIntText;
    }

    int getEndIntText() {
        return mEndIntText;
    }

    float getLeft() {
        return mLeft;
    }

    float getRight() {
        return mRight;
    }

    float getTop() {
        return mTop;
    }

    float getBottom() {
        return mBottom;
    }

    BackgroundHolder getBgHolder() {
        return mBgHolder;
    }

    static class Builder {

        private TextPaint mTextPaint;
        private int mStartIntText;
        private int mEndIntText;
        private float mLeft, mRight, mTop, mBottom;
        private BackgroundHolder mBgHolder;

        Builder setTextPaint(TextPaint textPaint) {
            mTextPaint = textPaint;
            return this;
        }

        Builder setStartIntText(int startIntText) {
            mStartIntText = startIntText;
            return this;
        }

        Builder setEndIntText(int endIntText) {
            mEndIntText = endIntText;
            return this;
        }

        Builder setLeft(float left) {
            mLeft = left;
            return this;
        }

        Builder setRight(float right) {
            mRight = right;
            return this;
        }

        Builder setTop(float top) {
            mTop = top;
            return this;
        }

        Builder setBottom(float bottom) {
            mBottom = bottom;
            return this;
        }

        Builder setBgHolder(BackgroundHolder bgHolder) {
            mBgHolder = bgHolder;
            return this;
        }

        LineDataHolder build() {
            return new LineDataHolder(this);
        }
    }
}
