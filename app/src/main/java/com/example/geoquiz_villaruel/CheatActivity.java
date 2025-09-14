package com.example.geoquiz_villaruel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CheatActivity extends AppCompatActivity {

    private static final String TAG = "CheatActivity";
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String EXTRA_REMAINING_TOKENS =
            "com.bignerdranch.android.geoquiz.remaining_tokens";

    private boolean mAnswerIsTrue;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    private TextView mCheatTokensText;
    private int mRemainingTokens;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int mRemainingCheatTokens) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getRemainingTokens(Intent result) {
        return result.getIntExtra(EXTRA_REMAINING_TOKENS, 0);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mRemainingTokens = getIntent().getIntExtra(EXTRA_REMAINING_TOKENS, 3);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        TextView apiLevelTextView = findViewById(R.id.api_level_text_view);
        apiLevelTextView.setText("API Level " + Build.VERSION.SDK_INT);

        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mCheatTokensText = findViewById(R.id.cheat_tokens_text);

        updateTokensText();

        if (savedInstanceState != null) {
            boolean wasAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false);
            if (wasAnswerShown) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
            }
        }

        if (mRemainingTokens <= 0) {
            mShowAnswerButton.setEnabled(false);
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        if (isAnswerShown && mRemainingTokens > 0) {
            mRemainingTokens--; // use one token
            updateTokensText();
        }
        if (mRemainingTokens <= 0) {
            mShowAnswerButton.setEnabled(false);
        }
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_REMAINING_TOKENS, mRemainingTokens);
        setResult(RESULT_OK, data);
    }

    private void updateTokensText() {
        mCheatTokensText.setText("Cheat tokens left: " + mRemainingTokens);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, mAnswerTextView.getVisibility() == View.VISIBLE && mAnswerTextView.getText().length() > 0);
    }

    }
