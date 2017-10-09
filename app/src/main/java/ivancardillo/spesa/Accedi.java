package ivancardillo.spesa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Accedi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accedi);
        final String url = "http://www.mishu.altervista.org/api/utente/accedi";
        final Map<String, String> jsonParams = new HashMap<String, String>();
        final RequestQueue req = Volley.newRequestQueue(Accedi.this);
        Button accedi = (Button) findViewById(R.id.accediManda);
        final EditText nome = (EditText) findViewById(R.id.nomeUtente);
        final EditText psw = (EditText) findViewById(R.id.psw);
        
        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome.getText().toString().compareTo("") == 0 || psw.getText().toString().compareTo("") == 0)
                    Toast.makeText(Accedi.this, "Inserisci valori corretti", Toast.LENGTH_LONG).show();
                else {
                    jsonParams.put("nomeUtente", nome.getText().toString());
                    jsonParams.put("password", psw.getText().toString());
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String s = "";
                            String token = "";
                            try {
                                s = response.get("risposta").toString();

                            } catch (JSONException e) {
                                Toast.makeText(Accedi.this, "Errore Interno", Toast.LENGTH_SHORT).show();

                            }
                            if (s.compareTo("000") == 0) {
                                Toast.makeText(Accedi.this, "Accesso effettuato", Toast.LENGTH_SHORT).show();
                                Intent avanti = new Intent(Accedi.this, Bacheca.class);
                                try {
                                    token = response.get("token").toString();

                                } catch (JSONException e) {
                                    Toast.makeText(Accedi.this, "Errore Interno", Toast.LENGTH_SHORT).show();
                                }

                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

                                if (sharedPreferences.getString("token", "0") != "0") {
                                    startActivity(avanti);
                                } else {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token", token);
                                    editor.putString("nome", nome.getText().toString());
                                    editor.commit();
                                    startActivity(avanti);
                                }
                            } else
                                Toast.makeText(Accedi.this, "Nome utente e/o password scorretti!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Accedi.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
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
        });
    }
}
