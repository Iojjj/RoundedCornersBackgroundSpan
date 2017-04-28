package com.github.iojjj.rcbs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.LineBackgroundSpan;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LineBackgroundSpan that adds rounded rectangle backgrounds to text.
 */
public final class RoundedCornersBackgroundSpan implements LineBackgroundSpan {

    /**
     * Default separator between text parts.
     */
    public static final CharSequence DEFAULT_SEPARATOR = "   ";

    /**
     * Rectangle used for drawing background.
     */
    private final RectF mRectangle = new RectF();

    /**
     * Paint used for drawing background.
     */
    private final Paint mPaint = new Paint();

    /**
     * List of backgrounds to draw.
     */
    private final List<BackgroundHolder> mBackgroundHolders = new ArrayList<>();

    /**
     * Corners radius of background.
     */
    private final float mRadius;

    /**
     * Text padding.
     */
    private final float mPadding;

    private RoundedCornersBackgroundSpan(float radius, float padding) {
        mPaint.setAntiAlias(true);
        mRadius = radius;
        mPadding = padding;
    }

    private RoundedCornersBackgroundSpan(@NonNull TextPartsBuilder textPartsBuilder) {
        this(textPartsBuilder.mRadius, textPartsBuilder.mPadding);
        for (final Pair<CharSequence, BackgroundHolder> textPart : textPartsBuilder.mTextParts) {
            if (textPart.second != null) {
                mBackgroundHolders.add(textPart.second);
            }
        }
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        for (BackgroundHolder backgroundHolder : mBackgroundHolders) {
            if (start > backgroundHolder.mEnd || end < backgroundHolder.mStart)
                continue;
            final CharSequence part = text.subSequence(start, end);
            final int trimmedLength = TextUtils.getTrimmedLength(part);
            final String trimmedText = part.toString().trim();
            // skip empty parts
            if (TextUtils.isEmpty(trimmedText)) {
                continue;
            }
            // do not add background to lines that ends with spaces
            if (trimmedLength != part.length()) {
                final int trimmedLengthStart = getTrimmedLengthStart(part);
                final int trimmedLengthEnd = getTrimmedLengthEnd(part, trimmedLengthStart);
                start = start + trimmedLengthStart;
                end = end - trimmedLengthEnd;
            }
            final int startInText = start < backgroundHolder.mStart ? backgroundHolder.mStart : start;
            final int endInText = end > backgroundHolder.mEnd ? backgroundHolder.mEnd : end;
            // skip empty parts
            if (startInText == endInText) {
                continue;
            }
            float l = p.measureText(text, start, startInText);
            float r;
            try {
                r = l + p.measureText(text, startInText, endInText);
            } catch (IndexOutOfBoundsException e) {
                // skip drawing
                continue;
            }
            mRectangle.set(l - mPadding, top - mPadding, r + mPadding, baseline + p.descent() + mPadding);
            mPaint.setColor(backgroundHolder.mBgColor);
            c.drawRoundRect(mRectangle, mRadius, mRadius, mPaint);
        }
    }

    /**
     * Get number of space characters from the beginning of text.
     *
     * @param text any text
     * @return number of space characters from text beginning
     */
    private int getTrimmedLengthStart(@NonNull CharSequence text) {
        int len = text.length();

        int start = 0;
        while (start < len && text.charAt(start) <= ' ') {
            start++;
        }
        return start;
    }

    /**
     * Get number of space characters from the end of text.
     *
     * @param text  any text
     * @param start number of space characters from beginning of text
     * @return number of space characters from the end of text
     */
    private int getTrimmedLengthEnd(@NonNull CharSequence text, int start) {
        int end = text.length();
        while (end > start && text.charAt(end - 1) <= ' ') {
            end--;
        }

        return text.length() - end;
    }

    /**
     * Background model.
     */
    private static final class BackgroundHolder {

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

        BackgroundHolder(int bgColor, int start, int end) {
            mBgColor = bgColor;
            mStart = start;
            mEnd = end;
        }
    }

    /**
     * Builder for creating RoundedCornersBackgroundSpan by text parts.
     */
    public static class TextPartsBuilder {

        private final Context mContext;
        private float mRadius;
        private float mPadding;
        private CharSequence mSeparator = DEFAULT_SEPARATOR;
        private final List<Pair<CharSequence, BackgroundHolder>> mTextParts = new ArrayList<>();

        /**
         * Constructor.
         *
         * @param context instance of Context
         */
        public TextPartsBuilder(@NonNull Context context) {
            mContext = context.getApplicationContext();
        }

        /**
         * Set corners radius.
         *
         * @param radius corners radius in pixels.
         */
        public TextPartsBuilder setCornersRadius(float radius) {
            mRadius = radius;
            return this;
        }

        /**
         * Set text padding.
         *
         * @param padding text padding in pixels
         */
        public TextPartsBuilder setTextPadding(float padding) {
            mPadding = padding;
            return this;
        }

        /**
         * Set text padding from resources.
         *
         * @param paddingRes dimen ID of padding
         */
        public TextPartsBuilder setTextPaddingRes(@DimenRes int paddingRes) {
            return setTextPadding(mContext.getResources().getDimension(paddingRes));
        }

        /**
         * Set corners radius from resources.
         *
         * @param radiusRes dimen ID of radius
         */
        public TextPartsBuilder setCornersRadiusRes(@DimenRes int radiusRes) {
            return setCornersRadius(mContext.getResources().getDimension(radiusRes));
        }

        /**
         * Set separator between text parts.
         *
         * @param separator any separator
         */
        public TextPartsBuilder setSeparator(@NonNull CharSequence separator) {
            mSeparator = separator;
            return this;
        }

        /**
         * Add text part with background.
         *
         * @param textPart text part
         * @param bgColor  background color
         */
        public TextPartsBuilder addTextPart(@NonNull CharSequence textPart, @ColorInt int bgColor) {
            BackgroundHolder backgroundHolder = new BackgroundHolder(bgColor, 0, 0);
            Pair<CharSequence, BackgroundHolder> pair = Pair.create(textPart, backgroundHolder);
            mTextParts.add(pair);
            return this;
        }

        /**
         * Add text part with background from resources.
         *
         * @param textRes    string ID of text part
         * @param bgColorRes color ID of background
         */
        public TextPartsBuilder addTextPart(@StringRes int textRes, @ColorRes int bgColorRes) {
            return addTextPart(mContext.getText(textRes), getColor(mContext, bgColorRes));
        }

        /**
         * Add text part without background.
         *
         * @param textPart text part
         */
        public TextPartsBuilder addTextPart(@NonNull CharSequence textPart) {
            Pair<CharSequence, BackgroundHolder> pair = Pair.create(textPart, null);
            mTextParts.add(pair);
            return this;
        }

        /**
         * Add text part without background from resources.
         *
         * @param textRes string ID of text part
         */
        public TextPartsBuilder addTextPart(@StringRes int textRes) {
            return addTextPart(mContext.getText(textRes));
        }

        /**
         * Create a spanned string that contains RoundedCornersBackgroundSpan.
         *
         * @return spanned string
         */
        public Spannable build() {
            boolean first = true;
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (final Pair<CharSequence, BackgroundHolder> stringPart : mTextParts) {
                if (first) {
                    first = false;
                } else {
                    builder.append(mSeparator);
                }
                if (stringPart.second != null) {
                    stringPart.second.mStart = builder.length();
                }
                builder.append(stringPart.first);
                if (stringPart.second != null) {
                    stringPart.second.mEnd = builder.length();
                }
            }
            RoundedCornersBackgroundSpan span = new RoundedCornersBackgroundSpan(this);
            builder.setSpan(span, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }
    }

    /**
     * Returns a color associated with a particular resource ID
     * <p>
     * Starting in {@link android.os.Build.VERSION_CODES#M}, the returned
     * color will be styled for the specified Context's theme.
     *
     * @param id The desired resource identifier, as generated by the aapt
     *           tool. This integer encodes the package, type, and resource
     *           entry. The value 0 is an invalid identifier.
     * @return A single color value in the form 0xAARRGGBB.
     * @throws android.content.res.Resources.NotFoundException if the given ID
     *                                                         does not exist.
     */
    private static int getColor(@NonNull Context context, @ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }
}
