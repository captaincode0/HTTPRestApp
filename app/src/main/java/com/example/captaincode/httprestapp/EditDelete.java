package com.example.captaincode.httprestapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EditDelete extends AppCompatActivity {
    private EntityTaller taller;
    private EditText etdesc, ethours, etplace;
    private DatePicker dpdi, dpde;
    private Button btnedit, btndel;
    private boolean action;

    private class ThreadData extends AsyncTask<String, Void, Void> {
        private InputStream inputdata;
        private String text = "";
        private String error = "";
        private ProgressDialog dialog = new ProgressDialog(EditDelete.this);
        private EntityTaller taller;
        private boolean action; // true: delete, false: edit

        public ThreadData(EntityTaller taller, boolean action){
            this.taller = taller;
            this.action = action;
        }

        @Override
        protected void onPreExecute(){
            dialog.setMessage("Sending data...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                for (String url : urls) {
                    //create a list to add value pairs
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    //add an option
                    if(this.action) {
                        pairs.add(new BasicNameValuePair("id", String.valueOf(this.taller.getId())));
                        pairs.add(new BasicNameValuePair("opcion", "3"));
                    }
                    else{
                        pairs.add(new BasicNameValuePair("id", String.valueOf(this.taller.getId())));
                        pairs.add(new BasicNameValuePair("descripcion", this.taller.getDescription()));
                        pairs.add(new BasicNameValuePair("horas", String.valueOf(this.taller.getHours())));
                        pairs.add(new BasicNameValuePair("lugar", this.taller.getPlace()));
                        pairs.add(new BasicNameValuePair("fechai", this.taller.getDatei()));
                        pairs.add(new BasicNameValuePair("fechaf", this.taller.getDatef()));
                        pairs.add(new BasicNameValuePair("opcion", "2"));
                    }

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
            Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), viewtallerres.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);

        String[] datei, datee;
        this.taller = new EntityTaller();

        this.etdesc = (EditText) findViewById(R.id.etDescripcion);
        this.ethours = (EditText) findViewById(R.id.etHoras);
        this.etplace = (EditText) findViewById(R.id.etLugar);
        this.dpdi = (DatePicker) findViewById(R.id.dtFI);
        this.dpde = (DatePicker) findViewById(R.id.dtFF);
        this.btnedit = (Button) findViewById(R.id.btneditar);
        this.btndel = (Button) findViewById(R.id.btneliminar);

        Bundle bundle = getIntent().getExtras();

        this.taller.setId(Integer.parseInt(bundle.getString("id")));
        this.etdesc.setText(bundle.getString("descripcion"));
        this.ethours.setText(bundle.getString("horas"));
        this.etplace.setText(bundle.getString("lugar"));
        datei = bundle.getString("fechai").split("/");
        datee = bundle.getString("fechaf").split("/");

        this.dpdi.updateDate(Integer.parseInt(datei[2]), Integer.parseInt(datei[1]), Integer.parseInt(datei[0]));
        this.dpde.updateDate(Integer.parseInt(datee[2]), Integer.parseInt(datee[1]), Integer.parseInt(datee[0]));

        this.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taller.setDescription(etdesc.getText().toString());
                taller.setHours(Integer.parseInt(ethours.getText().toString()));
                taller.setPlace(etplace.getText().toString());
                taller.setDatei(String.valueOf(dpdi.getDayOfMonth()) + "/" + String.valueOf(dpdi.getMonth()) + "/" + String.valueOf(dpdi.getYear()));
                taller.setDatef(String.valueOf(dpde.getDayOfMonth()) + "/" + String.valueOf(dpde.getMonth()) + "/" + String.valueOf(dpde.getYear()));

                ThreadData threadData = new ThreadData(taller, false);
                threadData.execute("http://192.168.0.2/testandroid/processor.php");
            }
        });

        this.btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadData threadData = new ThreadData(taller, true);
                threadData.execute("http://192.168.0.2/testandroid/processor.php");
            }
        });
    }
}
