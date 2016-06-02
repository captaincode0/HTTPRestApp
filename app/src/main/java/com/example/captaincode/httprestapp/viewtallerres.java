package com.example.captaincode.httprestapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class viewtallerres extends AppCompatActivity {
    private ListView lvdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtallerres);
        this.lvdata = (ListView) findViewById(R.id.listView);

        this.lvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                String[] data = value.split("\n");

                Intent intent = new Intent(getApplicationContext(), EditDelete.class);
                intent.putExtra("id", data[0]);
                intent.putExtra("descripcion", data[1]);
                intent.putExtra("lugar", data[2]);
                intent.putExtra("fechai", data[3]);
                intent.putExtra("fechaf", data[4]);
                intent.putExtra("horas", data[5]);

                startActivity(intent);
            }
        });

        ThreadData threadData = new ThreadData();
        threadData.execute("http://192.168.0.2/testandroid/processor.php");
    }

    private class ThreadData extends AsyncTask<String, Void, Void> {
        private InputStream inputdata;
        private String text = "";
        private String error = "";
        private String text0;

        private ArrayList<EntityTaller> myList;
        //create an async process dialog
        private ProgressDialog dialog;


        public ThreadData(){
            myList = new ArrayList<EntityTaller>();
            dialog = new ProgressDialog(viewtallerres.this);
        }

        @Override
        protected void onPreExecute(){
            /*dialog.setMessage("Sending data...");
            dialog.show();*/
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                for (String url : urls) {
                    //create a list to add value pairs
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    //add an option
                    pairs.add(new BasicNameValuePair("opcion", "1"));
                    //define a new client
                    HttpClient client = new DefaultHttpClient();
                    //create a new http client
                    HttpPost httpPost = new HttpPost(url);
                    //encoding the post request
                    httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                    //get the response in a HttpResponse object
                    HttpResponse response = client.execute(httpPost);
                    inputdata = response.getEntity().getContent();
                }
            }
            catch(ClientProtocolException e){
                error += "\nClientProtocolException: "+e.getMessage();
            }
            catch(IOException e){
                error += "\nIOException: "+e.getMessage();
            }

            BufferedReader reader;

            try {
                reader = new BufferedReader(new InputStreamReader(inputdata, "iso-8859-1"), 8);

                String line = null;

                while((line = reader.readLine()) != null) {
                    text += line+"\n";
                }
            }
            catch(UnsupportedEncodingException e){
                error += "\nUnsupportedEncodingException: "+e.getMessage();
            }
            catch(IOException e){
                error += "\nIOException: "+e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0){
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            text = text.trim();
            text0 = text;

            try {
                ArrayList<String> ll = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(text0);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    EntityTaller entity = new EntityTaller();
                    entity.setId(jsonObject.getInt("id"));
                    entity.setDescription(jsonObject.getString("descripcion"));
                    entity.setHours(jsonObject.getInt("horas"));
                    entity.setDatei(jsonObject.getString("fechai"));
                    entity.setDatef(jsonObject.getString("fechaf"));
                    entity.setPlace(jsonObject.getString("lugar"));
                    myList.add(entity);
                }

                for(EntityTaller tt : myList){
                    String data = String.valueOf(tt.getId())+"\n"+tt.getDescription()+"\n"+tt.getPlace()+"\n"+tt.getDatei()+"\n"+tt.getDatef()+"\n"+tt.getHours();
                    ll.add(data);
                }

                ArrayAdapter dataadapter = new ArrayAdapter<>(
                        getBaseContext(),
                        android.R.layout.simple_list_item_1,
                        ll
                );
                lvdata.setAdapter(dataadapter);
            }
            catch(JSONException ex){
                error += "\nJSONException: "+ex.getMessage();
            }
        }
    }
}
