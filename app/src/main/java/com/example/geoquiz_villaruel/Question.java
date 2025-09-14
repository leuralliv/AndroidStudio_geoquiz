package com.example.geoquiz_villaruel;

import android.widget.Button;
import android.widget.TextView;

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAnswered, isAnswered;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mAnswered = false;
    }

    public int getTextResId() {
        return mTextResId;
    }
    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }
    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }
    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) { isAnswered = answered; }
}
