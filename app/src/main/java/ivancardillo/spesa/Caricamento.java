package ivancardillo.spesa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Caricamento extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caricamento);
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        final String channel = (sharedPreferences.getString("token", ""));


        //Toast.makeText(Caricamento.this, "Accesso effettuato", Toast.LENGTH_SHORT).show();

        if (channel.equals("")) {
            Intent i = new Intent(Caricamento.this, MainActivity.class);
            startActivity(i);
        } else {
            final Intent avanti = new Intent(Caricamento.this, Bacheca.class);
            final String url = "http://www.mishu.altervista.org/api/utente/" + channel;
            final RequestQueue req = Volley.newRequestQueue(Caricamento.this);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nome", response.get("nome").toString());
                        editor.commit();
                        avanti.putExtra("Nome", response.get("nome").toString());
                        avanti.putExtra("Token", channel);
                        startActivity(avanti);
                    } catch (JSONException e) {
                        Toast.makeText(Caricamento.this, "Errore interno", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Caricamento.this, "Errore di comunicazione", Toast.LENGTH_SHORT).show();
                }
            }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            req.add(jsonObjReq);
        }


    }
}
