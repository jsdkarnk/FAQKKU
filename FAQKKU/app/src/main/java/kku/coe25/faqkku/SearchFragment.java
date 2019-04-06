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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment {

    EditText et_input ;
    ImageButton imgBut_search ;
    static ArrayList<String> questionList = new ArrayList<String>();
    static ArrayList<String> IDList = new ArrayList<String>();
    static ArrayList<List<String>> allanswerList = new ArrayList<List<String>>();
    static ArrayList<String>  answerList ;
    private RecyclerView recyclerView;
    private RequestQueue mQueue;
    static ArrayList<String> ImgList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((MainActivity) getActivity())
                .setActionBarTitle("ค้นหา");
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        et_input = view.findViewById(R.id.edt_input);
        imgBut_search = view.findViewById(R.id.imgbut_search);
        mQueue = Volley.newRequestQueue(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.srch_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);


        imgBut_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = String.valueOf(et_input.getText());
                jsonParse(input);
            }
        });


        return view;
    }

    private void jsonParse(final String input) {
        String url = "http://139.180.214.164:9000/cut";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            questionList.clear();
                            IDList.clear();
                            allanswerList.clear();
                            JSONArray jsonArray = response.getJSONArray("result");

                            for(int i = 0;i < jsonArray.length();i++){
                                JSONObject KKUanswer = jsonArray.getJSONObject(i);
                                JSONArray answer = KKUanswer.getJSONArray("answer");
                                String question = KKUanswer.getString("question");
                                questionList.add(question);
                                Log.d("answerSIZE1", String.valueOf(answer.length()));
                                String answerIndex;
                                answerList = new ArrayList<>();
                                for(int j=0;j<answer.length();j++){
                                    answerIndex = answer.getJSONObject(j).getString("ans");
                                    Log.d("answerValue", answerIndex);
                                    answerList.add(answerIndex);
                                }
                                allanswerList.add(answerList);
                                String idQ = KKUanswer.getString("qid");
                                IDList.add(idQ);
                               String img = KKUanswer.getString("image");
                               Log.d("ANSimg",img);
                              ImgList.add(img);



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("ANS",questionList.toString());
                        Log.d("ANS",IDList.toString());

                        recyclerView.setAdapter(new SearchFragment.RecyclerViewAdapter());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<String, String>();
                params2.put("sentence", input);
                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        mQueue.add(request);

    }
    public class RecyclerViewAdapter extends RecyclerView.Adapter<SearchFragment.ViewHolder>{

        @NonNull
        @Override
        public SearchFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_faq, parent , false);
            return new SearchFragment.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchFragment.ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.tv_question.setText(questionList.get(position));
            Resources res = getResources();
           int resourceId = res.getIdentifier(
                   ImgList.get(position), "drawable", getActivity().getPackageName() );
          holder.img_question.setImageResource( resourceId );


        }

        @Override
        public int getItemCount() {
            Log.d("queISize", String.valueOf(questionList.size()));
            return questionList.size();
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
                    intent.putExtra("question_list",questionList);
                    ArrayList ans = (ArrayList) allanswerList.get(index);
                    intent.putExtra("answers",ans);
                    intent.putExtra("id_que",IDList);
                    startActivity(intent);
                    Log.d("indK", String.valueOf(index));
                }
            });
        }
    }




}
