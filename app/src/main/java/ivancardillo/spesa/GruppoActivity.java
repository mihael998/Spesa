package ivancardillo.spesa;

import android.Manifest;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import static ivancardillo.spesa.R.id.image;
import static ivancardillo.spesa.R.id.psw;

public class GruppoActivity extends AppCompatActivity {
    private ArrayList<Prodotto> prodotti;
    ImageView star;
    Gruppo gruppo;
    ProdottiAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private String token;
    private Toolbar toolbar;
    private EditText nomeProduct;
    private EditText note;
    ImageView camera, cameraDelete;
    ProgressBar loading;
    Prodotto nuovoProdotto;
    private ArrayList<String> partecipanti;
    private SharedPreferences sharedPreferences;
    private Bundle bundle;
    FloatingActionButton fab;
    private File dir;

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
        cameraDelete = (ImageView) findViewById(R.id.cameraDelete);
        setTitle(gruppo.getNome());
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshProdotti);
        RecyclerView rvProdotti = (RecyclerView) findViewById(R.id.listaProdotti);
        prodotti = new ArrayList<Prodotto>();
        adapter = new ProdottiAdapter(this, prodotti);
        nomeProduct = (EditText) findViewById(R.id.nomeProduct);
        note = (EditText) findViewById(R.id.note);
        fab = (FloatingActionButton) findViewById(R.id.send);
        LinearLayoutManager gridLayoutManager=new LinearLayoutManager(GruppoActivity.this);
        rvProdotti.setLayoutManager(gridLayoutManager);

        rvProdotti.setItemAnimator(new DefaultItemAnimator());
        rvProdotti.setAdapter(adapter);
        refresh();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                refreshLayout.setRefreshing(false);
            }
        });

        /*rvProdotti.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rvProdotti,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {

                    }

                    public void onLongClickItem(View v, int position) {

                    }
                }));*/
        cameraDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.setImageResource(R.drawable.ic_add_a_photo);
                File canc=new File(nuovoProdotto.getImgProdotto());
                canc.delete();
                nuovoProdotto.setImgProdotto("");
                cameraDelete.setVisibility(View.GONE);
                camera.setClickable(true);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        nuovoProdotto = new Prodotto();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                String picName = nuovoProdotto.getCodiceProdotto() + ".jpg";
                File newfile = new File(dir, picName);
                nuovoProdotto.setImgProdotto(newfile.getAbsolutePath());
                Toast.makeText(GruppoActivity.this,newfile.getAbsolutePath() , Toast.LENGTH_LONG).show();
                Uri picUri;
                if (Build.VERSION.SDK_INT >= 23) {
                    picUri = FileProvider.getUriForFile(GruppoActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            newfile);
                }
                else
                {
                    picUri = Uri.fromFile(newfile);
                }


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(takePictureIntent, 100);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nomeProduct.getText().toString().equals(""))
                    Toast.makeText(GruppoActivity.this, "Nome prodotto è un campo obbligatorio", Toast.LENGTH_SHORT).show();
                else {
                    nuovoProdotto.setNome(nomeProduct.getText().toString());
                    nuovoProdotto.setCodiceGruppo(gruppo.getCodiceGruppo());
                    nuovoProdotto.setCodiceRichiedente(sharedPreferences.getString("token", ""));
                    nuovoProdotto.setDataRichiesta();
                    if(note.getText().toString().compareTo("")!=0)
                    {
                        nuovoProdotto.setNote(note.getText().toString());
                    }
                    final Bitmap imageBitmap;
                    if(nuovoProdotto.getImgProdotto()!="")
                    {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = false;
                        imageBitmap = BitmapFactory.decodeFile(nuovoProdotto.getImgProdotto(), options);
                        jsonParams.put("image", getStringImage(imageBitmap));
                    }
                    else
                    {
                        imageBitmap=null;
                        jsonParams.put("image", "");
                    }


                    String url = "http://www.mishu.altervista.org/api/richiesta";
                    jsonParams.put("nomeProdotto", nuovoProdotto.getNome());
                    jsonParams.put("codiceProdotto", nuovoProdotto.getCodiceProdotto());
                    jsonParams.put("codiceGruppo", nuovoProdotto.getCodiceGruppo());
                    jsonParams.put("codiceRichiedente", nuovoProdotto.getCodiceRichiedente());
                    jsonParams.put("dataRichiesta", nuovoProdotto.getDataRichiesta());
                    jsonParams.put("note", nuovoProdotto.getNote());

                    RequestQueue req = Volley.newRequestQueue(GruppoActivity.this);
                    loading=(ProgressBar)findViewById(R.id.loadingPanel);
                    camera.setVisibility(View.GONE);
                    cameraDelete.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            loading.setVisibility(View.GONE);
                            camera.setImageResource(R.drawable.ic_add_a_photo);
                            camera.setVisibility(View.VISIBLE);
                            camera.setClickable(true);


                            String s = "";
                            try {
                                s = response.get("risposta").toString();
                                if(s.equals("100"))
                                {
                                    prodotti.add(nuovoProdotto);
                                    adapter.notifyItemInserted(prodotti.size()-1);
                                    nuovoProdotto=new Prodotto();
                                    nomeProduct.setText("");
                                    note.setText("");
                                    Toast.makeText(GruppoActivity.this, "Caricato", Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    Toast.makeText(GruppoActivity.this, s, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(GruppoActivity.this, "Errore Interno", Toast.LENGTH_SHORT).show();

                            }

                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {

                            loading.setVisibility(View.GONE);
                            camera.setVisibility(View.VISIBLE);
                            Toast.makeText(GruppoActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
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


        if (token.equals(gruppo.getCodiceAdmin())) {
            star.setVisibility(View.VISIBLE);
        }
    }
    public Bitmap getBitmapFromString(String str)
    {
        byte[] decodedString = Base64.decode(str, Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap imageBitmap;
            imageBitmap = getResizedBitmap(BitmapFactory.decodeFile(nuovoProdotto.getImgProdotto(), options),1080);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(nuovoProdotto.getImgProdotto());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            camera.setImageBitmap(imageBitmap);
            camera.setClickable(false);
            cameraDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            nuovoProdotto.setImgProdotto("");
        }

    }

    private void refresh() {
        String url="http://www.mishu.altervista.org/api/richieste/"+gruppo.getCodiceGruppo();
        RequestQueue req=Volley.newRequestQueue(GruppoActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray prodottiRisposta=null;
                Prodotto nuovo;
                try {
                    prodottiRisposta=response.getJSONArray("prodotti");
                    for(int a=0;a<prodottiRisposta.length();a++)
                    {
                        nuovo=new Prodotto(prodottiRisposta.getJSONObject(a).getString("richiesta"),prodottiRisposta.getJSONObject(a).getString("codice_richiedente"),gruppo.getCodiceGruppo(),
                                prodottiRisposta.getJSONObject(a).getString("note"),"",prodottiRisposta.getJSONObject(a).getString("codice_prodotto"),prodottiRisposta.getJSONObject(a).getString("data_creazione"),
                                prodottiRisposta.getJSONObject(a).getString("stato"));

                        if(prodotti.contains(nuovo))
                        {
                            prodotti.indexOf(nuovo);
                        }
                        else
                        {
                            prodotti.add(nuovo);
                            adapter.notifyItemChanged(prodotti.size()-1);
                        }

                    }
                } catch (JSONException e) {
                    Toast.makeText(GruppoActivity.this,"errore conversione",Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(GruppoActivity.this,"nope",Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GruppoActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        req.add(jsonObjectRequest);
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
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}


