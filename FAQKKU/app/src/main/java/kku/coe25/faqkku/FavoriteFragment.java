package kku.coe25.faqkku;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {


    private RecyclerView recyclerView;
    private RequestQueue mQueue;
    static ArrayList<String> questionList = new ArrayList<String>();
    static ArrayList<String> ImgList = new ArrayList<String>();
    static ArrayList<String> IDList = new ArrayList<String>();
    static ArrayList<String> fav_IDList = new ArrayList<String>();
    static ArrayList<List<String>> allanswerList = new ArrayList<List<String>>();
    static ArrayList<String>  answerList ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    private FirebaseAuth mAuth;
    ArrayList<String> favlist = new ArrayList<>();
    ArrayList<String> fav_questionList = new ArrayList<>();
    static ArrayList<List<String>> fav_answerList = new ArrayList<List<String>>();
    static ArrayList<String> fav_ImgList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity())
                .setActionBarTitle("ชื่นชอบ");

        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mQueue = Volley.newRequestQueue(getContext());
        mAuth = FirebaseAuth.getInstance();
        jsonParse();
        getFAV();
        recyclerView = (RecyclerView) view.findViewById(R.id.fav_list);

        return view;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<FavoriteFragment.ViewHolder>{

        @NonNull
        @Override
        public FavoriteFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_faq, parent , false);
            return new FavoriteFragment.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteFragment.ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.tv_question.setText(fav_questionList.get(position));
            Resources res = getResources();
            int resourceId = res.getIdentifier(
                    fav_ImgList.get(position), "drawable", getActivity().getPackageName() );
            holder.img_question.setImageResource( resourceId );


        }

        @Override
        public int getItemCount() {
            Log.d("queISize", String.valueOf(fav_questionList.size()));
            return fav_questionList.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_question;
        ImageView img_question;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_question = (TextView) itemView.findViewById(R.id.tv_faq);
            img_question = (ImageView) itemView.findViewById(R.id.img_faq);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    Intent intent = new Intent(getContext(),AnswersActivity.class);
                    intent.putExtra("Class_id",index);
                    intent.putExtra("question_list",fav_questionList);
                    ArrayList ans = (ArrayList) fav_answerList.get(index);
                    intent.putExtra("answers",ans);
                    intent.putExtra("id_que",fav_IDList);
                    startActivity(intent);
                    Log.d("indK", String.valueOf(index));
                }
            });
        }
    }

    private void jsonParse() {
        String url = "http://139.180.214.164:9000/alldata";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            IDList.clear();
                            JSONArray jsonArray = response.getJSONArray("KKUanswer");

                            for(int i = 0;i < jsonArray.length();i++){
                                JSONObject KKUanswer = jsonArray.getJSONObject(i);
                                JSONArray answer = KKUanswer.getJSONArray("answer");

                                String question = KKUanswer.getString("question");
                                questionList.add(question);
                                String idQ = KKUanswer.getString("_id");
                                IDList.add(idQ);

                                String answerIndex;
                                answerList = new ArrayList<>();
                                for(int j=0;j<answer.length();j++){
                                    answerIndex = answer.getJSONObject(j).getString("ans");
                                    answerList.add(answerIndex);
                                }
                                allanswerList.add(answerList);

                                String img = KKUanswer.getString("img");
                                ImgList.add(img);



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("ANS",IDList.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    public void getFAV(){

        String uid = mAuth.getCurrentUser().getUid();
        myRef.child(uid).child("fav").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favlist.clear();
                Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
                for (DataSnapshot contact : contactChildren) {
                    String qid = contact.getKey();
                    favlist.add(qid);
                }
                Log.d("FIND",favlist.toString());
                setFAV();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    public void setFAV(){
        fav_questionList.clear();
        fav_answerList.clear();
        fav_ImgList.clear();
        fav_IDList.clear();
        Log.d("FINDJa1", String.valueOf(fav_questionList.size()));
       for(int i=0;i<IDList.size();i++){
           for(int j=0;j<favlist.size();j++){
               if(favlist.get(j).equals(IDList.get(i))){
                   fav_questionList.add(questionList.get(i));
                   fav_answerList.add(allanswerList.get(i));
                   fav_ImgList.add(ImgList.get(i));
                   fav_IDList.add(IDList.get(i));
               }
           }
       }
        //Log.d("FINDJa2", String.valueOf(fav_questionList.toString()));
        Log.d("FINDJa333333", String.valueOf(favlist.size()));
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new FavoriteFragment.RecyclerViewAdapter());
    }


}
