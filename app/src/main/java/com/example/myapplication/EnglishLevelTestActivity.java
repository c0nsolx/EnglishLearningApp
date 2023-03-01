package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EnglishLevelTestActivity extends AppCompatActivity implements View.OnClickListener{

    int bestScore = 0;

    FirebaseFirestore firebaseFirestore;

    TextView totalQuestionsTextView, goBack;
    TextView questionTextView;
    AppCompatButton ansA, ansB, ansC, ansD;
    AppCompatButton submitBtn, nextBtn;


    int score = 0;
    int totalQuestion = 1;
    int currentQuestionIndex = 0;
    String level;
    AppCompatButton rightAnswer, wrongAnswer, selectedAnswer;


    ArrayList<GrammarQuizModel> arrayList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_test_activity);

        goBack = findViewById(R.id.back);
        totalQuestionsTextView = findViewById(R.id.total_questions);
        questionTextView = findViewById(R.id.question);
        nextBtn = findViewById(R.id.next_btn);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        totalQuestionsTextView.setText("Total questions : " + totalQuestion);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseFirestore= FirebaseFirestore.getInstance();

        firebaseFirestore.collection("englishleveltest")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot: task.getResult()) {
                                String question = documentSnapshot.get("question").toString();
                                List<String> choices = (List<String>) documentSnapshot.get("choices");
                                String answer = documentSnapshot.get("answer").toString();
                                arrayList.add(new GrammarQuizModel(question,choices,answer));

                            }

                            loadNewQuestion();

                            totalQuestion = arrayList.size();
                            totalQuestionsTextView.setText("Total question " + totalQuestion);

                        }

                    }
                });



    }


    public void onClick(View view) {



        if (ansA.getText().equals(arrayList.get(currentQuestionIndex).answer)){
            rightAnswer = ansA;
        } else if (ansB.getText().equals(arrayList.get(currentQuestionIndex).answer)) {
            rightAnswer = ansB;

        } else if (ansC.getText().equals(arrayList.get(currentQuestionIndex).answer)) {
            rightAnswer = ansC;

        } else if (ansD.getText().equals(arrayList.get(currentQuestionIndex).answer)) {
            rightAnswer = ansD;
        }





        (new Handler()).postDelayed(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                Drawable bg_style = getResources().getDrawable(R.drawable.button_background_style);
                Drawable bg_select = getResources().getDrawable(R.drawable.button_selection_bg);
                ansA.setBackgroundDrawable(bg_style);
                ansB.setBackgroundDrawable(bg_style);
                ansC.setBackgroundDrawable(bg_style);
                ansD.setBackgroundDrawable(bg_style);
                AppCompatButton clickedButton = (AppCompatButton) view;

                if(clickedButton.getId()==R.id.submit_btn){

                    if (selectedAnswer == null){
                        Toast.makeText(EnglishLevelTestActivity.this, "Please select any option", Toast.LENGTH_SHORT).show();
                    } else {


                        if(selectedAnswer.getText().toString().equals(arrayList.get(currentQuestionIndex).answer)){
                            rightAnswer.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selection_right));
                            rightAnswer.setTextColor(Color.GREEN);
                        } else{
                            wrongAnswer.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selection_wrong));
                            rightAnswer.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selection_right));
                            wrongAnswer.setTextColor(Color.RED);
                        }
                        ansA.setEnabled(false);
                        ansB.setEnabled(false);
                        ansC.setEnabled(false);
                        ansD.setEnabled(false);

                    }

                }
                else if(clickedButton.getId() == R.id.next_btn){



                        clickedButton.setBackgroundDrawable(bg_select);
                        if (selectedAnswer.getText().toString().equals(arrayList.get(currentQuestionIndex).answer)){
                            score++;
                        }
                        submitBtn.setBackgroundDrawable(bg_style);
                        currentQuestionIndex++;
                        loadNewQuestion();
                        clickedButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_style_mirror));



                } else {
                    //choices button clicked
                    submitBtn.setBackgroundDrawable(bg_style);
                    selectedAnswer = clickedButton;
                    clickedButton.setBackgroundDrawable(bg_select);

                    if (!selectedAnswer.getText().toString().equals(arrayList.get(currentQuestionIndex).answer)){
                        wrongAnswer = selectedAnswer;
                    }

                    nextBtn.setEnabled(true);
                }
            }
        },200);



    }



    @SuppressLint("UseCompatLoadingForDrawables")
    void loadNewQuestion(){

        ansA.setEnabled(true);
        ansB.setEnabled(true);
        ansC.setEnabled(true);
        ansD.setEnabled(true);

        ansA.setTextColor(Color.WHITE);
        ansB.setTextColor(Color.WHITE);
        ansC.setTextColor(Color.WHITE);
        ansD.setTextColor(Color.WHITE);

        nextBtn.setEnabled(false);


        selectedAnswer = null;




        if(currentQuestionIndex == totalQuestion ){
            finishQuiz();
            return;
        }

        questionTextView.setText(arrayList.get(currentQuestionIndex).question);
        ansA.setText(arrayList.get(currentQuestionIndex).choices.get(0));
        ansB.setText(arrayList.get(currentQuestionIndex).choices.get(1));
        ansC.setText(arrayList.get(currentQuestionIndex).choices.get(2));
        ansD.setText(arrayList.get(currentQuestionIndex).choices.get(3));

    }

    void finishQuiz(){
        String passStatus = "Result";

        float x = (float) score / totalQuestion;

        if (x < 0.5){
            level = "beginner";

        } else if (x >= 0.5 && (float)totalQuestion / score < 0.8) {
            level = "intermediate";
        } else if (x >= 0.8) {
            level = "advanced";
        }

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score is "+ score+" out of "+ totalQuestion + " so your enlish grammar level is approximately " + level + " so we recommend you to pick the course for your english level")
                .setPositiveButton("Click to pick course",(dialogInterface, i) -> goBack() )
                .setCancelable(false)
                .show();


    }

   /* void restartQuiz(){
        score = 0;
        currentQuestionIndex =0;
        loadNewQuestion();
    }*/

    void goBack(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}