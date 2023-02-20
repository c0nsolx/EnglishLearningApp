package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GrammarTestActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseFirestore firebaseFirestore;

    TextView totalQuestionsTextView, goBack;
    TextView questionTextView;
    AppCompatButton ansA, ansB, ansC, ansD;
    Button submitBtn;

    int score=0;
    int totalQuestion = 1;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    ArrayList<GrammarQuizModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_test_activity);

        goBack = findViewById(R.id.back);
        totalQuestionsTextView = findViewById(R.id.total_questions);
        questionTextView = findViewById(R.id.question);
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

        totalQuestionsTextView.setText("Total questions : "+totalQuestion);





        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseFirestore= FirebaseFirestore.getInstance();

        firebaseFirestore.collection("grammartest")
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

    @Override
    public void onClick(View view) {

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Drawable d = getResources().getDrawable(R.drawable.button_background_style);
                ansA.setBackgroundDrawable(d);
                ansB.setBackgroundDrawable(d);
                ansC.setBackgroundDrawable(d);
                ansD.setBackgroundDrawable(d);
                AppCompatButton clickedButton = (AppCompatButton) view;

                if(clickedButton.getId()==R.id.submit_btn){
                    if(selectedAnswer.equals(arrayList.get(currentQuestionIndex).answer)){
                        score++;
                    }
                    currentQuestionIndex++;

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNewQuestion();
                        }
                    }, 200);



                }else{
                    //choices button clicked
                    selectedAnswer = clickedButton.getText().toString();
                    clickedButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selection_bg));

                }
            }
        },500);


    }

    void loadNewQuestion(){


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
        String passStatus = "";
        if(score > totalQuestion*0.60){
            passStatus = "Passed";
        }else{
            passStatus = "Failed";
        }

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score is "+ score+" out of "+ totalQuestion)
               /* .setPositiveButton("Restart",(dialogInterface, i) -> restartQuiz() )*/
                .setPositiveButton("View wrong answers",(dialogInterface, i) -> wrongAnswersPage() )
                .setCancelable(false)
                .show();


    }

   /* void restartQuiz(){
        score = 0;
        currentQuestionIndex =0;
        loadNewQuestion();
    }*/

    void wrongAnswersPage(){
        Intent intent = new Intent(getApplicationContext(), GrammarWrongAnswers.class);
        startActivity(intent);
    }

}
