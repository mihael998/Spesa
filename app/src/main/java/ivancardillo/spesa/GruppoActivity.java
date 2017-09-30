package ivancardillo.spesa;

import android.Manifest;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import static ivancardillo.spesa.R.id.psw;

public class GruppoActivity extends AppCompatActivity {
    private ArrayList<Prodotto> prodotti;
    ImageView star;
    Gruppo gruppo;
    ProdottiAdapter adapter;
    private String token;
    private Toolbar toolbar;
    private EditText nomeProduct;
    private EditText note;
    ImageView camera;
    Prodotto nuovoProdotto;
    private ArrayList<String> partecipanti;
    private SharedPreferences sharedPreferences;
    private Bundle bundle;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppo);
        star = (ImageView) findViewById(R.id.starAdmin);
        FloatingActionButton sendFab = (FloatingActionButton) findViewById(R.id.send);
        final Map<String, String> jsonParams = new HashMap<String, String>();
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        bundle = getIntent().getExtras();
        gruppo = (Gruppo) bundle.getSerializable("value");
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        token = (sharedPreferences.getString("token", ""));
        camera = (ImageView) findViewById(R.id.camera);
        setTitle(gruppo.getNome());
        RecyclerView rvProdotti = (RecyclerView) findViewById(R.id.listaProdotti);
        prodotti = new ArrayList<Prodotto>();
        adapter = new ProdottiAdapter(this, prodotti);
        nomeProduct = (EditText) findViewById(R.id.nomeProduct);
        note = (EditText) findViewById(R.id.note);
        fab = (FloatingActionButton) findViewById(R.id.send);
        rvProdotti.setLayoutManager(new LinearLayoutManager(this));
        rvProdotti.setItemAnimator(new DefaultItemAnimator());
        rvProdotti.setAdapter(adapter);

        prodotti.add(new Prodotto("Pasta", "", "", "", ""));
        prodotti.add(new Prodotto("Latte", "", "", "", ""));
        adapter.notifyDataSetChanged();

        /*rvProdotti.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rvProdotti,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {

                    }

                    public void onLongClickItem(View v, int position) {

                    }
                }));*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        nuovoProdotto = new Prodotto();

        if (ContextCompat.checkSelfPermission(GruppoActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(GruppoActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                String picName = nuovoProdotto.getCodiceProdotto() + ".jpg";
                File newfile = new File(dir, "Spesa/"+picName);
                nuovoProdotto.setImgProdotto(newfile.getAbsolutePath());
                Uri picUri = Uri.fromFile(newfile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(takePictureIntent, 100);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nomeProduct.getText().toString() == "")
                    Toast.makeText(GruppoActivity.this, "Nome prodotto è un campo obbligatorio", Toast.LENGTH_SHORT).show();
                else {
                    nuovoProdotto.setNome(nomeProduct.getText().toString());
                    nuovoProdotto.setCodiceGruppo(gruppo.getCodiceGruppo());
                    nuovoProdotto.setCodiceRichiedente(sharedPreferences.getString("token", ""));
                    nuovoProdotto.setDataRichiesta();
                    nuovoProdotto.setNote(note.getText().toString());

                    String url = "www.altervista.org/mishu";

                    jsonParams.put("nomeProdotto", nuovoProdotto.getNome());
                    jsonParams.put("codiceProdotto", nuovoProdotto.getCodiceProdotto());
                    jsonParams.put("codiceGruppo", nuovoProdotto.getCodiceGruppo());
                    jsonParams.put("codiceRichiedente", nuovoProdotto.getCodiceRichiedente());
                    jsonParams.put("dataRichiesta", nuovoProdotto.getDataRichiesta());
                    jsonParams.put("note", nuovoProdotto.getNote());

                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
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
                    */



                }
            }
        });


        if (token.equals(gruppo.getCodiceAdmin())) {
            star.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap imageBitmap = BitmapFactory.decodeFile(nuovoProdotto.getImgProdotto(), options);
            camera.setImageBitmap(imageBitmap);
        }

    }

    private void refresh() {
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
                DialogFragment newFragment = new InfoActivity();
                newFragment.show(getFragmentManager(), "info");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
