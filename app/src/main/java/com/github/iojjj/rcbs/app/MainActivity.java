package com.github.iojjj.rcbs.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.iojjj.rcbs.RoundedCornersBackgroundSpan;
import com.github.iojjj.rcbs.TextAlignment;

public class MainActivity extends AppCompatActivity {


    private static final String[] COLORS = new String[]{
            "#F44336",
            "#4CAF50",
            "#FF5722",
            "#607D8B",
            "#673AB7"
    };

    private static final String[] ENGLISH_PARTS = new String[]{
            "Lorem ipsum dolor sit amet, omnis splendide cu vim, usu verear lucilius sensibus ut.",
            "Nam ad semper fabellas scriptorem, ad aperiam referrentur sea.",
            "Pri tation blandit id, usu et elitr scaevola pericula.",
            "Agam philosophia eu sit, qui ut quidam mediocritatem.",
            "His te choro utinam noster, everti elaboraret comprehensam ut vel.",
    };

    private static final String[] ARABIC_PARTS = new String[]{
            "عرض وجزر وأزيز حاملات ثم, كرسي أطراف الضغوط أما لم.",
            "لعدم الهجوم أما أم, بـ فاتّبع المبرمة لتقليعة جعل.",
            "رب لعملة بالرغم وفنلندا تم, حلّت تحرّك قائمة لمّ ان.",
            "كل فصل فاتّبع بالجانب, وترك يونيو تلك قد.",
            "تم اتفاق للسيطرة الشتاء، ومن, كل بحشد مارد تحرير تلك.",
    };

    private static final String[] HEBREW_PARTS = new String[]{
            "שפות הבהרה ואלקטרוניקה סדר ב.",
            "כתב ברוכים ויקימדיה מה.",
            "של תיבת עיצוב מונחונים בקר, הנדסת החופשית את היא.",
            "מונחונים בקר, הנדסת החופשית את היא.",
            "כדי וקשקש לויקיפדיה אם."
    };

    private TextView mEnglishTextView;
    private TextView mArabicTextView;
    private TextView mHebrewTextView;
    private TextView[] mAllTextViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEnglishTextView = (TextView) findViewById(R.id.text_english);
        mArabicTextView = (TextView) findViewById(R.id.text_arabic);
        mHebrewTextView = (TextView) findViewById(R.id.text_hebrew);
        mAllTextViews = new TextView[]{
                mEnglishTextView, mArabicTextView, mHebrewTextView
        };
        setTextByParts(ENGLISH_PARTS, mEnglishTextView, RoundedCornersBackgroundSpan.ALIGN_START);
        setTextByParts(ARABIC_PARTS, mArabicTextView, RoundedCornersBackgroundSpan.ALIGN_START);
        setTextByParts(HEBREW_PARTS, mHebrewTextView, RoundedCornersBackgroundSpan.ALIGN_START);
    }

    private void updateTextAlignment(@TextAlignment int alignment) {

        for (TextView textView : mAllTextViews) {
            if (alignment == RoundedCornersBackgroundSpan.ALIGN_START) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            } else if (alignment == RoundedCornersBackgroundSpan.ALIGN_END) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            } else if (alignment == RoundedCornersBackgroundSpan.ALIGN_CENTER) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
        setTextByParts(ENGLISH_PARTS, mEnglishTextView, alignment);
        setTextByParts(ARABIC_PARTS, mArabicTextView, alignment);
        setTextByParts(HEBREW_PARTS, mHebrewTextView, alignment);
    }

    /**
     * Set text using {@link com.github.iojjj.rcbs.RoundedCornersBackgroundSpan.Builder}.
     */
    private void setTextByParts(@NonNull String[] parts, @NonNull TextView textView, @TextAlignment int alignment) {
        final RoundedCornersBackgroundSpan.Builder builder = new RoundedCornersBackgroundSpan.Builder(this)
                .setTextPaddingRes(R.dimen.rcbs_padding)
                .setCornersRadiusRes(R.dimen.rcbs_radius);
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            final String color = COLORS[i];
            final SpannableString string = new SpannableString(part);
            if (!TextUtils.isEmpty(color)) {
                final ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
                string.setSpan(colorSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.addTextPart(string, Color.parseColor(color));
            } else {
                builder.addTextPart(string);
            }
        }
        final Spannable firstText = builder
                .setTextAlignment(alignment)
                .setPartsSpacingRes(R.dimen.rcbs_parts_spacing)
                .build();
        textView.setText(firstText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_left:
                updateTextAlignment(RoundedCornersBackgroundSpan.ALIGN_START);
                return true;
            case R.id.action_center:
                updateTextAlignment(RoundedCornersBackgroundSpan.ALIGN_CENTER);
                return true;
            case R.id.action_right:
                updateTextAlignment(RoundedCornersBackgroundSpan.ALIGN_END);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
