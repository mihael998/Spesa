package ivancardillo.spesa;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinGruppo extends AppCompatActivity {
    EditText codiceInserito;
    Button joinGruppo;
    ImageView scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_gruppo);
        codiceInserito = (EditText) findViewById(R.id.codiceGr);
        joinGruppo = (Button) findViewById(R.id.joinGruppo);
        scan = (ImageView) findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(JoinGruppo.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        joinGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codiceInserito.getText().toString().equals("")) {

                } else {
                    SharedPreferences sharedPreferences;
                    sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    final String channel = (sharedPreferences.getString("token", ""));
                    final Map<String, String> jsonParams = new HashMap<String, String>();
                    final String url = "http://www.mishu.altervista.org/api/gruppo/join/" + codiceInserito.getText().toString();
                    final RequestQueue req = Volley.newRequestQueue(JoinGruppo.this);
                    jsonParams.put("token", channel);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String s = "";
                            try {
                                s = response.get("risposta").toString();
                            } catch (JSONException e) {
                                Toast.makeText(JoinGruppo.this, "Errore interno", Toast.LENGTH_SHORT).show();
                            }

                            if (s.compareTo("010") == 0) {
                                Toast.makeText(JoinGruppo.this, "Aggiunta gruppo riuscita!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent();
                                setResult(Activity.RESULT_OK, i);
                                finish();
                            } else
                                Toast.makeText(JoinGruppo.this, "Fai già parte di questo gruppo. Scemo!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(JoinGruppo.this, error.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null)
                Toast.makeText(JoinGruppo.this, "Scan interrotto", Toast.LENGTH_SHORT).show();
            else
                codiceInserito.setText(result.getContents());

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
