package com.example.geoquiz_villaruel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final String KEY_ANSWERED_QUESTIONS = "answered_questions";
    private static final String KEY_CORRECT_SCORE = "correct_score";
    private static final String KEY_CHEATED_QUESTIONS = "cheated_questions";
    private static final int REQUEST_CODE_CHEAT = 0;
    private int mRemainingCheatTokens = 3;
    private Button trueButton, falseButton, mCheatButton;
    private ImageButton nextButton, prevButton;
    private TextView mQuestionTextView;
    private double correct = 0;
    private boolean mIsCheater;
    private boolean[] mCheatedQuestions;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize cheated questions array
        mCheatedQuestions = new boolean[mQuestionBank.length];

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            correct = savedInstanceState.getDouble(KEY_CORRECT_SCORE, 0);

            // Restore answered states for each question
            boolean[] answeredStates = savedInstanceState.getBooleanArray(KEY_ANSWERED_QUESTIONS);
            if (answeredStates != null) {
                for (int i = 0; i < mQuestionBank.length && i < answeredStates.length; i++) {
                    mQuestionBank[i].setAnswered(answeredStates[i]);
                }
            }

            // Restore cheated questions
            boolean[] cheatedStates = savedInstanceState.getBooleanArray(KEY_CHEATED_QUESTIONS);
            if (cheatedStates != null) {
                mCheatedQuestions = cheatedStates;
            }

        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        trueButton = findViewById(R.id.true_button);
        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        falseButton = findViewById(R.id.false_button);
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, mRemainingCheatTokens);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        nextButton = (ImageButton) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numOfQuestions = mQuestionBank.length;
//                int nextCheatedQuestion = findNextCheatedQuestion();

//                if (mCurrentIndex == numOfQuestions - 1) {
//                    if (nextCheatedQuestion != -1) {
//                        mCurrentIndex = nextCheatedQuestion;
//                        Toast.makeText(QuizActivity.this, "Please re-answer the questions you cheated on", Toast.LENGTH_LONG).show();
//                    }
                if (mCurrentIndex == numOfQuestions - 1) {
                        Toast.makeText(QuizActivity.this, "Score: " + (correct/numOfQuestions)*100 + "%", Toast.LENGTH_LONG).show();
//                    }

                } else {
                    mIsCheater = false;
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                }
                updateQuestion();
            }
        });

        prevButton = (ImageButton) findViewById(R.id.previous_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mQuestionBank[mCurrentIndex].isAnswered() == mIsCheater) {
                    trueButton.setEnabled(false);
                    falseButton.setEnabled(false);
                }
                if (mCurrentIndex  != 0) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                } else {
                    Toast.makeText(QuizActivity.this, "No previous question", Toast.LENGTH_SHORT)
                            .show();
                }
                updateQuestion();
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            mIsCheater = CheatActivity.wasAnswerShown(data);

            // If user cheated, mark this question as cheated and disable buttons
            if (mIsCheater) {
                mCheatedQuestions[mCurrentIndex] = true;
            }
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        boolean answered = mQuestionBank[mCurrentIndex].isAnswered();
        boolean cheatedOnThisQuestion = mCheatedQuestions[mCurrentIndex];

        trueButton.setEnabled(!answered);
        falseButton.setEnabled(!answered);

        if (cheatedOnThisQuestion) {
            mIsCheater = true;
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;

        } else {
            if (userPressedTrue == answerIsTrue && !userPressedTrue == !answerIsTrue) {
                messageResId = R.string.correct_toast;
                correct++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
            // If answered without cheating, remove from cheated list
            mCheatedQuestions[mCurrentIndex] = false;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

        mQuestionBank[mCurrentIndex].setAnswered(true);
        trueButton.setEnabled(false);
        falseButton.setEnabled(false);
    }

//    private int findNextCheatedQuestion() {
//        for (int i = 0; i < mQuestionBank.length; i++) {
//            if (mCheatedQuestions[i]) {
//
//                // Reset the question so it can be answered again
//                mQuestionBank[i].setAnswered(false);
//                mCheatedQuestions[i] = false; // Reset the cheated state
//                mIsCheater = false;
//                return i;
//            }
//        }
//        return -1; // No cheated questions found
//    }

    private boolean allQuestionsAnsweredWithoutCheating() {
        for (int i = 0; i < mQuestionBank.length; i++) {
            if (!mQuestionBank[i].isAnswered() || mCheatedQuestions[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        savedInstanceState.putDouble(KEY_CORRECT_SCORE, correct);

        // Save answered states for each question
        boolean[] answeredStates = new boolean[mQuestionBank.length];
        for (int i = 0; i < mQuestionBank.length; i++) {
            answeredStates[i] = mQuestionBank[i].isAnswered();
        }
        savedInstanceState.putBooleanArray(KEY_ANSWERED_QUESTIONS, answeredStates);

        // Save cheated questions
        savedInstanceState.putBooleanArray(KEY_CHEATED_QUESTIONS, mCheatedQuestions);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}