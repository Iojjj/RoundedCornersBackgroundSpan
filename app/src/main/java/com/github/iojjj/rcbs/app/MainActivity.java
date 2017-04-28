package com.github.iojjj.rcbs.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.SparseArray;
import android.widget.TextView;

import com.github.iojjj.rcbs.RoundedCornersBackgroundSpan;

public class MainActivity extends AppCompatActivity {

    private TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.text1);
        float radius = convertDpToPx(this, 2);
        final int padding = (int) convertDpToPx(this, 4);
        setTextByParts(radius, padding);
    }

    /**
     * Set text using {@link com.github.iojjj.rcbs.RoundedCornersBackgroundSpan.Builder}.
     *
     * @param radius  corner radius
     * @param padding text padding
     */
    private void setTextByParts(float radius, int padding) {
        final String[] colors = new String[]{
                "#F44336",
                null,
                "#4CAF50",
                null,
                "#FF5722",
                null,
                "#607D8B",
                null,
                "#673AB7"
        };
        final String[] parts = new String[]{
                "The mast grows passion like a swashbuckling mainland.",
                "Golden, big gulls quirky endure a sunny, jolly sailor.",
                "The misty freebooter heavily hails the wave.",
                "Ooh, cold urchin!",
                "All furners loot dark, coal-black seas.",
                "Yo-ho-ho, greed!",
                "Dead, gutless scabbards heavily hail a swashbuckling, undead sailor.",
                "Yo-ho-ho! Pieces o' fight are forever golden.",
                "Woodchucks are the landlubbers of the rough passion."
        };
        final RoundedCornersBackgroundSpan.Builder builder = new RoundedCornersBackgroundSpan.Builder(this)
                .setTextPadding(padding)
                .setCornersRadius(radius);
        final SparseArray<Object[]> spans = new SparseArray<>();
        spans.append(0, new Object[] { new ScaleXSpan(1.3f), new TypefaceSpan("monospace") });
        spans.append(1, new Object[] { new RelativeSizeSpan(0.8f), new TypefaceSpan("serif") });
        spans.append(2, new Object[] { new StyleSpan(Typeface.BOLD), new TypefaceSpan("sans-serif") });
        spans.append(3, new Object[] { new SubscriptSpan() });
        spans.append(4, new Object[] { new SuperscriptSpan() });
        spans.append(5, new Object[] { new TextAppearanceSpan(this, android.R.style.TextAppearance_Small) });
        spans.append(6, new Object[] { new TypefaceSpan("monospace") });
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            final String color = colors[i];
            final SpannableString string = new SpannableString(part);
            final Object[] spanObjects = spans.get(i);
            if (spanObjects != null) {
                for (Object object : spanObjects) {
                    string.setSpan(object, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if (!TextUtils.isEmpty(color)) {
                final ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
                string.setSpan(colorSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.addTextPart(string, Color.parseColor(color));
            } else {
                builder.addTextPart(string);
            }
        }
        final Spannable firstText = builder.build();
        text1.setText(firstText);
    }

    private static float convertDpToPx(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }
}
