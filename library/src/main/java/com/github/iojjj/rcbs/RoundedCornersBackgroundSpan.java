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
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.LineBackgroundSpan;
import android.text.style.MetricAffectingSpan;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LineBackgroundSpan that adds rounded rectangle backgrounds to text.
 */
public final class RoundedCornersBackgroundSpan implements LineBackgroundSpan {

    /**
     * Align text at start (left for LTR and right for RTL).
     */
    public static final int ALIGN_START = 0;

    /**
     * Align text at end (right for LTR and left for RTL).
     */
    public static final int ALIGN_END = 1;

    /**
     * Align text at center.
     */
    public static final int ALIGN_CENTER = 2;

    /**
     * Default separator between text parts.
     */
    private static final CharSequence DEFAULT_SEPARATOR = "   ";

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

    /**
     * Text alignment.
     */
    @TextAlignment
    private final int mTextAlignment;

    /**
     * Flag indicates that text is in RTL direction.
     */
    private final boolean mRtlText;

    /**
     * Separator between two parts.
     */
    private final CharSequence mSeparator;

    /**
     * List that holds data required to properly measure text on the line that contains multiple
     * {@link MetricAffectingSpan} effects.
     */
    private final List<LineDataHolder> mHoldersOnLine = new ArrayList<>();

    private RoundedCornersBackgroundSpan(@NonNull Builder builder) {
        mPaint.setAntiAlias(true);
        mRadius = builder.mRadius;
        mPadding = builder.mPadding;
        mSeparator = builder.mSeparator;
        mTextAlignment = builder.mTextAlignment;
        for (final Pair<CharSequence, BackgroundHolder> textPart : builder.mTextParts) {
            if (textPart.second != null) {
                mBackgroundHolders.add(textPart.second);
            }
        }
        final char firstChar = builder.mTextParts.get(0).first.charAt(0);
        mRtlText = firstChar >= 0x5D0 && firstChar <= 0x6ff;
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {
        mHoldersOnLine.clear();
        for (BackgroundHolder backgroundHolder : mBackgroundHolders) {
            if (start > backgroundHolder.getEnd() || end < backgroundHolder.getStart()) {
                continue;
            }
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
            final int startInText = start < backgroundHolder.getStart() ? backgroundHolder.getStart() : start;
            final int endInText = end > backgroundHolder.getEnd() ? backgroundHolder.getEnd() : end;
            // skip empty parts
            if (startInText == endInText) {
                continue;
            }
            updateHoldersOnLine(p, left, right, top, baseline, text, backgroundHolder,
                    startInText, endInText);
        }
        drawBackgrounds(c, left, right);
    }

    /**
     * Update list of holders on line.
     * @param backgroundHolder holder of text part
     * @param startInText start position in text
     * @param endInText end position in text
     */
    private void updateHoldersOnLine(@NonNull Paint p, int left, int right, int top, int baseline,
                                     @NonNull CharSequence text, @NonNull BackgroundHolder backgroundHolder,
                                     int startInText, int endInText) {
        final float separatorWidth = p.measureText(mSeparator, 0, mSeparator.length());
        final TextPaint textPaint = getTextPaint(p, text, startInText, endInText);
        final float prevTextWidth = getPrevTextWidth(text, separatorWidth);
        float curTextWidth;
        try {
            curTextWidth = textPaint.measureText(text, startInText, endInText);
        } catch (IndexOutOfBoundsException e) {
            // skip drawing. This crashes on Android 4.3 (potentially on all 4.x) devices
            // without `continue` it will draw an empty rectangle with rounded corners (if padding has been set)
            return;
        }
        float l = left;
        float r = right;
        if (mRtlText) {
            r -= prevTextWidth;
            l = r - curTextWidth;
        } else {
            l += prevTextWidth;
            r = l + curTextWidth;
        }
        final float rectLeft = l - mPadding;
        final float rectTop = top - mPadding;
        final float rectRight = r + mPadding;
        final float rectBottom = baseline + p.descent() + mPadding;
        final LineDataHolder lineDataHolder = new LineDataHolder.Builder()
                .setTextPaint(textPaint)
                .setStartIntText(startInText)
                .setEndIntText(endInText)
                .setLeft(rectLeft)
                .setRight(rectRight)
                .setTop(rectTop)
                .setBottom(rectBottom)
                .setBgHolder(backgroundHolder)
                .build();
        mHoldersOnLine.add(lineDataHolder);
    }

    /**
     * Get width of previous text on line.
     * @param text some text
     * @param separatorWidth width of separator
     * @return width of previous text on line
     */
    private float getPrevTextWidth(CharSequence text, float separatorWidth) {
        float prevTextWidth = 0;
        if (!mHoldersOnLine.isEmpty()) {
            for (LineDataHolder holder : mHoldersOnLine) {
                prevTextWidth += holder.getTextPaint().measureText(text, holder.getStartIntText(), holder.getEndIntText());
            }
            prevTextWidth += mHoldersOnLine.size() * separatorWidth;
        }
        return prevTextWidth;
    }

    /**
     * Get new text paint.
     * @param p init paint object
     * @param text some text
     * @param startInText start position in text
     * @param endInText end position in text
     * @return new instance of TextPaint
     */
    @NonNull
    private TextPaint getTextPaint(Paint p, CharSequence text, int startInText, int endInText) {
        final TextPaint textPaint = new TextPaint(p);
        if (text instanceof SpannedString) {
            SpannedString spanned = (SpannedString) text;
            final MetricAffectingSpan[] spans = spanned.getSpans(startInText, endInText, MetricAffectingSpan.class);
            if (spans.length > 0) {
                for (MetricAffectingSpan span : spans) {
                    span.updateMeasureState(textPaint);
                }
            }
        }
        return textPaint;
    }

    /**
     * Calculate alignment fix value.
     * @return alignment fix value
     */
    private float calculateAlignmentFix(int left, int right) {
        if (mHoldersOnLine.isEmpty()) {
            return 0;
        }
        float fLeft = left;
        float fRight = right;
        final float mostLeft;
        final float mostRight;
        if (mRtlText) {
            mostLeft = mHoldersOnLine.get(mHoldersOnLine.size() - 1).getLeft();
            mostRight = mHoldersOnLine.get(0).getRight();
        } else {
            mostLeft = mHoldersOnLine.get(0).getLeft();
            mostRight = mHoldersOnLine.get(mHoldersOnLine.size() - 1).getRight();
        }
        if (mostLeft < left) {
            fLeft = mostLeft;
        }
        if (mostRight > fRight) {
            fRight = mostRight;
        }
        final float width = fRight - fLeft;
        final float consumedWidth = mostRight - mostLeft;
        if (mTextAlignment == ALIGN_CENTER) {
            return (width - consumedWidth + mPadding) / 2;
        } else if (mTextAlignment == ALIGN_END) {
            return width - consumedWidth + mPadding;
        }
        return 0;
    }

    /**
     * Draw backgrounds.
     */
    private void drawBackgrounds(@NonNull Canvas c, int left, int right) {
        final float alignmentFix = calculateAlignmentFix(left, right);
        for (LineDataHolder lineDataHolder : mHoldersOnLine) {
            final BackgroundHolder backgroundHolder = lineDataHolder.getBgHolder();
            float rectLeft = lineDataHolder.getLeft();
            float rectRight = lineDataHolder.getRight();
            if (mRtlText) {
                rectLeft -= alignmentFix;
                rectRight -= alignmentFix;
            } else {
                rectLeft += alignmentFix;
                rectRight += alignmentFix;
            }
            final float rectTop = lineDataHolder.getTop();
            final float rectBottom = lineDataHolder.getBottom();
            // skip transparent backgrounds
            if (backgroundHolder.getBgColor() != 0) {
                mRectangle.set(rectLeft, rectTop, rectRight, rectBottom);
                mPaint.setColor(backgroundHolder.getBgColor());
                c.drawRoundRect(mRectangle, mRadius, mRadius, mPaint);
            }
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
     * Builder for creating RoundedCornersBackgroundSpan by text parts.
     */
    public static class Builder {

        private final Context mContext;
        private float mRadius;
        private float mPadding;
        private CharSequence mSeparator = DEFAULT_SEPARATOR;
        private final List<Pair<CharSequence, BackgroundHolder>> mTextParts = new ArrayList<>();
        @TextAlignment
        private int mTextAlignment = ALIGN_START;

        /**
         * Constructor.
         *
         * @param context instance of Context
         */
        public Builder(@NonNull Context context) {
            mContext = context.getApplicationContext();
        }

        /**
         * Set corners radius.
         *
         * @param radius corners radius in pixels.
         */
        public Builder setCornersRadius(float radius) {
            mRadius = radius;
            return this;
        }

        /**
         * Set text padding.
         *
         * @param padding text padding in pixels
         */
        public Builder setTextPadding(float padding) {
            mPadding = padding;
            return this;
        }

        /**
         * Set text padding from resources.
         *
         * @param paddingRes dimen ID of padding
         */
        public Builder setTextPaddingRes(@DimenRes int paddingRes) {
            return setTextPadding(mContext.getResources().getDimension(paddingRes));
        }

        /**
         * Set corners radius from resources.
         *
         * @param radiusRes dimen ID of radius
         */
        public Builder setCornersRadiusRes(@DimenRes int radiusRes) {
            return setCornersRadius(mContext.getResources().getDimension(radiusRes));
        }

        /**
         * Set separator between text parts.
         *
         * @param separator any separator
         */
        public Builder setSeparator(@NonNull CharSequence separator) {
            mSeparator = separator;
            return this;
        }

        /**
         * Add text part with background.
         *
         * @param textPart text part
         * @param bgColor  background color
         */
        public Builder addTextPart(@NonNull CharSequence textPart, @ColorInt int bgColor) {
            final BackgroundHolder backgroundHolder = new BackgroundHolder(bgColor, 0, 0);
            final Pair<CharSequence, BackgroundHolder> pair = Pair.create(textPart, backgroundHolder);
            mTextParts.add(pair);
            return this;
        }

        /**
         * Add text part with background from resources.
         *
         * @param textRes    string ID of text part
         * @param bgColorRes color ID of background
         */
        public Builder addTextPart(@StringRes int textRes, @ColorRes int bgColorRes) {
            return addTextPart(mContext.getText(textRes), getColor(mContext, bgColorRes));
        }

        /**
         * Add text part without background.
         *
         * @param textPart text part
         */
        public Builder addTextPart(@NonNull CharSequence textPart) {
            return this.addTextPart(textPart, 0);
        }

        /**
         * Add text part without background from resources.
         *
         * @param textRes string ID of text part
         */
        public Builder addTextPart(@StringRes int textRes) {
            return addTextPart(mContext.getText(textRes));
        }

        public Builder setTextAlignment(@TextAlignment int textAlignment) {
            mTextAlignment = textAlignment;
            return this;
        }

        /**
         * Create a spanned string that contains RoundedCornersBackgroundSpan.
         *
         * @return spanned string
         */
        public Spannable build() {
            if (mTextParts.isEmpty()) {
                throw new IllegalArgumentException("You must specify at least one text part.");
            }
            boolean first = true;
            final SpannableStringBuilder builder = new SpannableStringBuilder();
            for (final Pair<CharSequence, BackgroundHolder> stringPart : mTextParts) {
                if (first) {
                    first = false;
                } else {
                    builder.append(mSeparator);
                }
                if (stringPart.second != null) {
                    stringPart.second.setStart(builder.length());
                }
                builder.append(stringPart.first);
                if (stringPart.second != null) {
                    stringPart.second.setEnd(builder.length());
                }
            }
            final RoundedCornersBackgroundSpan span = new RoundedCornersBackgroundSpan(this);
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
