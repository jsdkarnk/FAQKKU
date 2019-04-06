package kku.coe25.faqkku;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FAQFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue mQueue;
    static ArrayList<String> questionList = new ArrayList<String>();
    static ArrayList<String> IDList = new ArrayList<String>();
    static ArrayList<List<String>> allanswerList = new ArrayList<List<String>>();
    static ArrayList<String>  answerList ;
    static ArrayList<String> ImgList = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_faq, container, false);
        mQueue = Volley.newRequestQueue(getContext());
        jsonParse();
        recyclerView = (RecyclerView) view.findViewById(R.id.faq_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        ((MainActivity) getActivity())
                .setActionBarTitle("คำถามที่พบบ่อยในช่วงนี้");

        return view;
    }

    private void jsonParse() {

        String url = "http://139.180.214.164:9000/alldata";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("KKUanswer");

                            for(int i = 0;i < jsonArray.length();i++){
                                JSONObject KKUanswer = jsonArray.getJSONObject(i);
                                JSONArray answer = KKUanswer.getJSONArray("answer");

                                String question = KKUanswer.getString("question");
                                questionList.add(question);
                                String idQ = KKUanswer.getString("_id");
                                IDList.add(idQ);
                                String img = KKUanswer.getString("img");
                                ImgList.add(img);
                                String answerIndex;
                                answerList = new ArrayList<>();
                                for(int j=0;j<answer.length();j++){
                                    answerIndex = answer.getJSONObject(j).getString("ans");
                                    answerList.add(answerIndex);
                                }
                                allanswerList.add(answerList);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("ANS",IDList.toString());
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_faq, parent , false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.tv_question.setText(questionList.get(position));

            /*File imgFile = new File("@drawable/tools.png");
            Log.d("IMG1234", String.valueOf(imgFile.exists()));
            if(imgFile.exists()){
                Log.d("IMG1234", String.valueOf(imgFile));
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.img_question.setImageBitmap(myBitmap);
            }*/

            Resources res = getResources();
            int resourceId = res.getIdentifier(
                    ImgList.get(position), "drawable", getActivity().getPackageName() );
            holder.img_question.setImageResource( resourceId );


        }

        @Override
        public int getItemCount() {
            Log.d("queISize", String.valueOf(questionList.size()));
            return 4;
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
