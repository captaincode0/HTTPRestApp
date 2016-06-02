package com.example.captaincode.httprestapp;

import android.app.ProgressDialog;
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
import java.net.URI;
import java.util.ArrayList;

public class Talleres extends AppCompatActivity implements View.OnClickListener{

    private EditText etDescripcion, etHoras, etLugar;
    private DatePicker dtFI, dtFF;
    private Button btnguardar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talleres);

        etDescripcion = (EditText)findViewById(R.id.etDescripcion);
        etHoras = (EditText)findViewById(R.id.etHoras);
        etLugar = (EditText)findViewById(R.id.etLugar);
        dtFI = (DatePicker) findViewById(R.id.dtFI);
        dtFF = (DatePicker) findViewById(R.id.dtFF);
        btnguardar = (Button) findViewById(R.id.btnguardar);
        btnguardar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String descripcion = etDescripcion.getText().toString();
        String horas = String.valueOf(etHoras.getText().toString());
        String lugar = etLugar.getText().toString();
        String fechai = dtFI.getDayOfMonth()+"/"+dtFI.getMonth()+"/"+dtFI.getYear();
        String fechaf = dtFF.getDayOfMonth()+"/"+dtFF.getMonth()+"/"+dtFF.getYear();

        hiloDatos objHilo = new hiloDatos(descripcion,horas,lugar,fechai,fechaf);
        objHilo.execute("http://192.168.0.2/testandroid/processor.php");
    }

    private class hiloDatos extends AsyncTask<String, Void, Void> {
        private InputStream datosEntrada;
        private String text = "";
        private String error = "";

        private String Descripcion;
        private String Horas;
        private String Lugar;
        private String FI;
        private String FF;

        private ProgressDialog dialog = new ProgressDialog(Talleres.this);

        hiloDatos(String descripcion, String horas, String lugar,String fi,String ff){
            this.Descripcion = descripcion;
            this.Horas = horas;
            this.Lugar = lugar;
            this.FI = fi;
            this.FF = ff;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Enviando datos al servidor...");
            dialog.show();
        }
        //Boolean result = false;
        @Override
        protected Void doInBackground(String... urls) {
            for(String url1 : urls){
                try {
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("descripcion", Descripcion));
                    pairs.add(new BasicNameValuePair("horas", Horas));
                    pairs.add(new BasicNameValuePair("lugar", Lugar));
                    pairs.add(new BasicNameValuePair("fechai", FI));
                    pairs.add(new BasicNameValuePair("fechaf", FF));
                    pairs.add(new BasicNameValuePair("opcion", "0"));
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(pairs));
                    HttpResponse response = client.execute(post);
                    datosEntrada = response.getEntity().getContent();
                } catch (ClientProtocolException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                } catch (IOException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(datosEntrada ,"iso-8859-1"),8);
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        text += line + "\n";
                    }
                } catch (UnsupportedEncodingException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                } catch (IOException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            text = text.trim();
            Toast.makeText(Talleres.this, text, Toast.LENGTH_LONG).show();
        }
    }
}
