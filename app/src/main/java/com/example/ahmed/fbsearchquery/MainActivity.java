package com.example.ahmed.fbsearchquery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // TODO: 1/30/2018 when click on a post opens it (in app Or in facebook app)
    // TODO: 1/30/2018 notifications

    private static final String TAG = "Login - Keyword";
    EditText searchQ, limit, groupLink;
    Button searchButton;
    RecyclerView recyclerView;
    String id, query;
    ContentAdapter contentAdapter;
    String[] messages, dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        limit = findViewById(R.id.limit);
        searchQ = findViewById(R.id.search_q);
        searchButton = findViewById(R.id.search_b);
        groupLink = findViewById(R.id.group_link);
        query = searchQ.getText().toString();


        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        contentAdapter = new ContentAdapter();
        recyclerView.setAdapter(contentAdapter);


        searchQ.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = null;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                query = searchQ.getText().toString();
                new GetId().execute(groupLink.getText().toString());
            }
        });

        groupLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                id = null;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                // Get group ID
                new GetId().execute(groupLink.getText().toString());

                Log.i(TAG, "afterTextChanged: groupLink = " + groupLink.getText().toString());
                Log.i(TAG, "afterTextChanged: id = " + id);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.i(TAG, "onClick: id = " + id);
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + id + "/feed/",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                // Insert your code here
                                Log.i(TAG, "onCompleted: response = " + response);
                                try {
                                    createJson(response);
                                    Log.i(TAG, "onCompleted: completed");
                                } catch (Exception e) {
                                    Log.i(TAG, "onCompleted: Error " + e);
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("limit", limit.getText().toString());
                parameters.putString("fields", "created_time,message");
                request.setParameters(parameters);
                request.executeAsync();

            }
        });
    }

    void createJson(GraphResponse response) throws JSONException {

        JSONObject data = response.getJSONObject();

        if (data == null) {
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
            return;
        }
//        Log.i(TAG, "createJson: data = " + data);
        JSONArray jsonArray = data.getJSONArray("data");
        int count = 0, androidCount = 0;
//        Log.i(TAG, "createJson: jsonArray length = " + jsonArray.length());

//        messages = new String[jsonArray.length()];
//        dates = new String[jsonArray.length()];
        ArrayList<String> queryMessages = new ArrayList<>();
        ArrayList<String> queryDates = new ArrayList<>();

        int j = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
//                Log.i(TAG, "createJson: jsonArray = " + jsonArray);
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Log.i(TAG, "createJson: jsonObject = " + jsonObject.toString());
                String message = jsonObject.getString("message");

                String updatedTime, Time;

                Time = jsonObject.getString("created_time").replace("T", " ");
                updatedTime = Time.substring(0, Time.lastIndexOf("+"));

                count++;
                Log.i(TAG, "createJson: query = " + query);
//                Log.i(TAG, "onCompleted: message = " + message);
                if (message.toLowerCase().contains(query.toLowerCase())) {

                    queryMessages.add(message);
                    queryDates.add(updatedTime);

//                    Log.i(TAG, "createJson: update = " + updatedTime);

                    androidCount++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        messages = new String[queryMessages.size()];
        dates = new String[queryDates.size()];

        for(int i=0; i<queryMessages.size(); i++) {
            messages[i] = queryMessages.get(i);
            dates[i] = queryDates.get(i);
        }

        contentAdapter.setData(messages, dates);
        Toast.makeText(this, "Found " + androidCount + " " + query + " posts of " + count + " total posts scanned", Toast.LENGTH_SHORT).show();

    }

    public class GetId extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchButton.setEnabled(false);
            if (groupLink.getText().toString() != "")
                Toast.makeText(MainActivity.this, R.string.loading_message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(strings[0]).get();
                Log.i(TAG, "volleyFunction: doc = " + doc.toString());
                Log.i(TAG, "doInBackground: length = " + doc.toString().length());
                Elements meta = doc.getElementsByAttributeValueStarting("content", "fb://group");
                Element contentElement = meta.first();
                String attr = contentElement.attr("content");
                id = attr.substring(attr.lastIndexOf("/") + 1);
                Log.i(TAG, "volleyFunction: meta = " + meta);
                Log.i(TAG, "doInBackground: content = " + contentElement);
                Log.i(TAG, "doInBackground: attr = " + attr);
                Log.i(TAG, "doInBackground: id = " + id);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: Error = " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchButton.setEnabled(true);
        }
    }


}
