package ivancardillo.spesa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registrati extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrati);
        final String url = "http://www.mishu.altervista.org/api/utente/registrazione";
        final Map<String, String> jsonParams = new HashMap<String, String>();
        final RequestQueue req = Volley.newRequestQueue(Registrati.this);
        Button registrati = (Button) findViewById(R.id.regManda);
        final EditText nome = (EditText) findViewById(R.id.nomeUtenteReg);
        final EditText psw = (EditText) findViewById(R.id.pswReg);
        final EditText email = (EditText) findViewById(R.id.email);

        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome.getText().toString().compareTo("") == 0 || psw.getText().toString().compareTo("") == 0 || email.getText().toString().compareTo("") == 0)
                    Toast.makeText(Registrati.this, "Inserisci valori corretti", Toast.LENGTH_SHORT).show();
                else {
                    if (psw.length() < 6)
                        Toast.makeText(Registrati.this, "Password troppo corta (min. 6)", Toast.LENGTH_SHORT).show();
                    else {
                        jsonParams.put("nomeUtente", nome.getText().toString());
                        jsonParams.put("password", psw.getText().toString());
                        jsonParams.put("mail", email.getText().toString());

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String s = "";
                                try {
                                    s = response.get("risposta").toString();
                                } catch (JSONException e) {
                                    Toast.makeText(Registrati.this, "Errore interno", Toast.LENGTH_SHORT).show();
                                }
                                if (s.compareTo("100") == 0) {
                                    Toast.makeText(Registrati.this, "Registrazione riuscita!", Toast.LENGTH_SHORT).show();
                                    Intent avanti = new Intent(Registrati.this, Accedi.class);
                                    startActivity(avanti);
                                } else
                                    Toast.makeText(Registrati.this, "Nome utente già in uso!", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Registrati.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };

                        req.add(jsonObjReq);
                        ;
                    }
                }
            }
        });
    }
}
