package ivancardillo.spesa;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class GruppoActivity extends AppCompatActivity {
    public String nome, dataScadenza, oraScadenza, codiceGruppo, codiceAdmin, token;
    private Toolbar toolbar;
    private ArrayList<String> partecipanti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppo);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        nome = getIntent().getExtras().getString("nomeGruppo");
        dataScadenza = getIntent().getExtras().getString("scadenzaData");
        oraScadenza = getIntent().getExtras().getString("scadenzaData");
        codiceGruppo = getIntent().getExtras().getString("codiceGruppo");
        codiceAdmin = getIntent().getExtras().getString("codiceAdmin");
        partecipanti = getIntent().getStringArrayListExtra("partecipanti");
        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        token = (sharedPreferences.getString("token", ""));
        setTitle(nome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Toast.makeText(GruppoActivity.this, nome, Toast.LENGTH_SHORT).show();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (token.equals(codiceAdmin)) {
            ImageView immagine;
            immagine = (ImageView) findViewById(R.id.starAdmin);
            immagine.setVisibility(View.VISIBLE);
        }

    }


    private void refresh(String codiceGruppo) {


        final String url = "http://www.mishu.altervista.org/api/gruppo/" + codiceGruppo + "/" + token;
        final RequestQueue req = Volley.newRequestQueue(GruppoActivity.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray array = response.getJSONArray("gruppi");
                    for (int b = 0; b < array.length(); b++) {

                        String nome, data, orario, codiceGruppo, codiceAdmin;
                        ArrayList<String> partecipanti = new ArrayList<String>();
                        nome = array.getJSONObject(b).getString("nomeGruppo");
                        data = array.getJSONObject(b).getString("dataScadenza");
                        codiceGruppo = array.getJSONObject(b).getString("codiceGruppo");
                        codiceAdmin = array.getJSONObject(b).getString("adminGruppo");
                        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = sm.parse(data);
                        sm.applyPattern("dd/MM/yyyy");
                        String newDateString = sm.format(d);
                        orario = array.getJSONObject(b).getString("oraScadenza");
                        JSONArray array1 = array.getJSONObject(b).getJSONArray("partecipanti");
                        for (int c = 0; c < array1.length(); c++) {
                            partecipanti.add(array1.getString(c));
                        }
                    }


                } catch (JSONException e) {
                    Toast.makeText(GruppoActivity.this, "Errore interno", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GruppoActivity.this, "Errore di comunicazione", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gruppo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.infoGruppo:
                /*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(GruppoActivity.this, MainActivity.class);
                startActivity(i);*/
                /*Intent avanti = new Intent(GruppoActivity.this, InfoActivity.class);
                avanti.putExtra("codiceGruppo", codiceGruppo);
                startActivity(avanti);*/
                DialogFragment newFragment = new InfoActivity();
                newFragment.show(getFragmentManager(), "info");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
