package ua.vlasov.rcbs.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.Random;

import static ua.vlasov.rcbs.RoundedCornersBackgroundSpan.DEFAULT_SEPARATOR;
import static ua.vlasov.rcbs.RoundedCornersBackgroundSpan.EntireTextBuilder;
import static ua.vlasov.rcbs.RoundedCornersBackgroundSpan.TextPartsBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView text1, text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        float radius = convertDpToPx(this, 2);
        final int padding = (int) convertDpToPx(this, 4);
        setTextByParts(radius, padding);
        setEntireText(radius, padding);
    }

    /**
     * Set text using {@link TextPartsBuilder}.
     * @param radius corner radius
     * @param padding text padding
     */
    private void setTextByParts(float radius, int padding) {
        String[] colors = new String[]{
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
        String[] parts = new String[]{
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
        final TextPartsBuilder textPartsBuilder =
                new TextPartsBuilder(this)
                        .setTextPadding(padding)
                        .setCornersRadius(radius);
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            final String color = colors[i];
            if (!TextUtils.isEmpty(color)) {
                final SpannableString string = new SpannableString(part);
                final ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
                string.setSpan(colorSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textPartsBuilder.addTextPart(string, Color.parseColor(color));
            } else {
                textPartsBuilder.addTextPart(part);
            }
        }
        final Spannable firstText = textPartsBuilder.build();
        text1.setText(firstText);
    }

    /**
     * Set text using {@link EntireTextBuilder}.
     * @param radius corner radius
     * @param padding text padding
     */
    private void setEntireText(float radius, int padding) {
        String[] colors = new String[]{
                "#F44336",
                "#448AFF",
                "#4CAF50",
                "#FFC107",
                "#FF5722",
                "#FF9800",
                "#607D8B",
                "#4CAF50",
                "#673AB7"
        };
        final Random random = new Random();
        final String text = "Be embittered." +
                DEFAULT_SEPARATOR +
                "Blessing is the only totality, the only guarantee of paradox." +
                DEFAULT_SEPARATOR +
                "One must shape the follower in order to experience the seeker of new advice." +
                DEFAULT_SEPARATOR +
                "When the teacher of faith captures the relativities of the spirit, the heaven will know source.";
        final EntireTextBuilder entireTextBuilder =
                new EntireTextBuilder(this, text)
                        .setTextPadding(padding)
                        .setCornersRadius(radius);
        final int separatorLength = DEFAULT_SEPARATOR.length() + 1;
        int index = 0, prev;
        do {
            prev = index;
            index = text.indexOf('.', index + 1);
            if (index > -1) {
                entireTextBuilder.addBackground(Color.parseColor(colors[random.nextInt(colors.length)]),
                        prev + (prev == 0 ? 0 : separatorLength), index + 1);
            }
        } while (index > -1);
        final Spanned secondText = entireTextBuilder.build();
        text2.setText(secondText);
    }

    private static float convertDpToPx(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }
}
