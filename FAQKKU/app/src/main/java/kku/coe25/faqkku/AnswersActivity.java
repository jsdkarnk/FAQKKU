package kku.coe25.faqkku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnswersActivity extends AppCompatActivity {

    private static final int REFRESH_SCREEN = 1;

    ArrayList<String> questionList = new ArrayList<String>();
    ArrayList<String> id_question = new ArrayList<String>();
    ArrayList<String> answerList = new ArrayList<>();
    ProgressBar mProgressbar;
    int index ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    private FirebaseAuth mAuth;
    ToggleButton fav_but;
    CardView cv_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        TextView tv_que = (TextView) findViewById(R.id.tv_que_input);
        TextView tv_ans = (TextView) findViewById(R.id.tv_ans_input);
        fav_but = (ToggleButton) findViewById(R.id.toggleButton_ans);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar_ans);
        mProgressbar.setVisibility(View.INVISIBLE);
        cv_content = findViewById(R.id.card_view);

        Bundle bundle = getIntent().getExtras();
        index = bundle.getInt("Class_id");
        questionList = bundle.getStringArrayList("question_list");
        answerList = bundle.getStringArrayList("answers");
        id_question = bundle.getStringArrayList("id_que");
        String que = questionList.get(index);
        tv_ans.setText("");
        for(int i = 0;i<answerList.size();i++){
            String ans = answerList.get(i);
            if(i == answerList.size()-1){
                tv_ans.append('\u2022' +" " + ans);
                Linkify.addLinks(tv_ans, Linkify.PHONE_NUMBERS);
            }else {
                tv_ans.append('\u2022' +" " + ans  + "\n\n");
                Linkify.addLinks(tv_ans, Linkify.PHONE_NUMBERS);
            }

        }

        tv_que.setText(que);
        getSupportActionBar().setTitle("ลองดูคำตอบของเราสิ!");

        Log.d("toANS",answerList.toString());
        String id = id_question.get(index);
        String uid = mAuth.getCurrentUser().getUid();
        checkFAV(uid,id);

        fav_but.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    Log.d("alarmFAV","FAV SET TO TRUE");
                   // Toast.makeText(getApplicationContext(), "This is my FAV!",
                     //       Toast.LENGTH_LONG).show();
                    String id = id_question.get(index);
                    String uid = mAuth.getCurrentUser().getUid();
                    myRef.child(uid).child("fav").child(id).setValue("true");

                }
                else
                {
                    Log.d("alarmFAV","FAV SET TO FALSE");
                    //Toast.makeText(getApplicationContext(), "This not my FAV!",
                     //       Toast.LENGTH_LONG).show();
                    String id = id_question.get(index);
                    String uid = mAuth.getCurrentUser().getUid();
                   myRef.child(uid).child("fav").child(id).removeValue();

                }
            }
        });

        mProgressbar.setVisibility(View.VISIBLE);
        cv_content.setVisibility(View.INVISIBLE);
        startScan();


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

    public void startScan() {
        new Thread() {
            public void run() {
                try{
                    Thread.sleep(2000);
                    hRefresh.sendEmptyMessage(REFRESH_SCREEN);
                }catch(Exception e){
                }
            }
        }.start();
    }

    public void checkFAV(String uid, final String id){

        myRef.child(uid).child("fav").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
                for (DataSnapshot contact : contactChildren) {
                    String qid = contact.getKey();
                    if(id.equals(qid)){
                        fav_but.setChecked(true);
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler hRefresh = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case REFRESH_SCREEN:
                    mProgressbar.setVisibility(View.INVISIBLE); // Hide ProgressBar
                    ShowText();
                    break;
                default:
                    break;
            }
        }
    };
    public void ShowText(){
        cv_content.setVisibility(View.VISIBLE);
    }
}
