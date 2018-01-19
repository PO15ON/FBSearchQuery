package com.example.ahmed.fbsearchquery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Login - Keyword";
    EditText searchQ, limit, groupLink;
    Button searchButton;
    TextView textView;

    String id, query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        limit = (EditText) findViewById(R.id.limit);
        searchQ = (EditText) findViewById(R.id.search_q);
        searchButton = (Button) findViewById(R.id.search_b);
        textView = (TextView) findViewById(R.id.text);
        groupLink = (EditText) findViewById(R.id.group_link);

        query = searchQ.getText().toString();

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
                parameters.putString("fields", "message");
                parameters.putString("limit", limit.getText().toString());
                request.setParameters(parameters);
                request.executeAsync();
            }
        });
    }

    void createJson(GraphResponse response) throws JSONException {

        JSONObject data = response.getJSONObject();

        if (data == null) {
            textView.setText("please make sure the group is public Or you are the admin.");
            return;
        }
        Log.i(TAG, "createJson: data = " + data);
        JSONArray jsonArray = data.getJSONArray("data");
        textView.setText("");
        int count = 0, androidCount = 0;
        Log.i(TAG, "createJson: jsonArray length = " + jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
//                Log.i(TAG, "createJson: jsonArray = " + jsonArray);
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String message = jsonObject.getString("message");
                count++;
//                Log.i(TAG, "onCompleted: message = " + message);
                if (message.toLowerCase().contains(query.toLowerCase())) {
                    textView.append(message + "\n.....................................................................................................\n");
                    androidCount++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "Found " + androidCount + " " + query + " posts of " + count + " total posts scanned", Toast.LENGTH_SHORT).show();

    }

    public class GetId extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchButton.setEnabled(false);
            if (groupLink.getText().toString() != "")
                textView.setText("Getting The group ID, Please Wait..");
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
            textView.setText("");
        }
    }


}
