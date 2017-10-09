package ivancardillo.spesa;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class Bacheca extends AppCompatActivity {
    public ArrayList<Gruppo> gruppi;
    ArrayList<Gruppo> multiselect_list = new ArrayList<>();
    android.view.ActionMode mActionMode;
    Menu context_menu;
    GruppiAdapter adapter;
    private ListView v;
    private Gruppo g;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private Boolean isFabOpen = false;
    private File directoryGruppo,dir;
    private FloatingActionButton fab, fab1, fab2;
    private FloatingActionMenu fam;
    SharedPreferences sharedPreferences;
    private String tokenUtente;
    TextView nomeUtente;

    boolean isMultiSelect = false;


    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacheca);
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        nomeUtente = (TextView) findViewById(R.id.nomeUtenteBacheca);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fam = (FloatingActionMenu) findViewById(R.id.fab);
        fab1.setOnClickListener(onButtonClick());
        fab2.setOnClickListener(onButtonClick());
        fam.setClosedOnTouchOutside(true );

        if (ContextCompat.checkSelfPermission(Bacheca.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(Bacheca.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 3);

        }


        nomeUtente.setText("Ciao " + sharedPreferences.getString("nome", "null") + "!");
        tokenUtente=sharedPreferences.getString("token", "null");

        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {

                } else {

                }
            }
        });

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.listaGruppi);

        gruppi = new ArrayList<Gruppo>();

        // Create adapter passing in the sample user data
        adapter = new GruppiAdapter(this, gruppi);
        // Attach the adapter to the recyclerview to populate items

        // Set layout manager to position the items
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        rvContacts.setLayoutManager(gridLayoutManager);
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.setAdapter(adapter);
        setSupportActionBar(toolbar);


        //creazione directory per il conseguente salvataggio delle foto in locale
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "appSpesa");
        if (!f.exists()) {
            f.mkdirs();
        }
        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "appSpesa", "gruppi");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        File f2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "appSpesa", "prodotti");
        if (!f2.exists()) {
            f2.mkdirs();
        }
        dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        directoryGruppo=new File(dir+File.separator+"appSpesa"+File.separator+"gruppi"+File.separator);
        refresh();

        rvContacts.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rvContacts,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        if (isMultiSelect)
                            multi_select(position);
                        else {
                            Intent avanti = new Intent(Bacheca.this, GruppoActivity.class);
                            Gruppo gruppo = gruppi.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("value", gruppo);
                            avanti.putExtras(bundle);
                            startActivity(avanti);
                        }
                    }

                    public void onLongClickItem(View v, int position) {
                        if (!isMultiSelect) {
                            multiselect_list = new ArrayList<Gruppo>();
                            isMultiSelect = true;

                            if (mActionMode == null) {
                                refreshLayout.setEnabled(false);
                                mActionMode = startActionMode(mActionModeCallback);
                                nomeUtente.setBackgroundResource(R.color.colorActionMode);
                            }
                        }

                        multi_select(position);

                    }
                }));

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                            refresh();
                            refreshLayout.setRefreshing(false);
                    }
                }
        );


    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(gruppi.get(position)))
                multiselect_list.remove(gruppi.get(position));
            else
                multiselect_list.add(gruppi.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            //refreshAdapter();

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Premi di nuovo per uscire", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fab1) {
                    Intent i = new Intent(Bacheca.this, JoinGruppo.class);
                    startActivityForResult(i,2);
                } else if (view == fab2) {
                    Intent i = new Intent(Bacheca.this, NuovoGruppo.class);
                    startActivityForResult(i,1);
                }
                fam.close(true);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                refresh();
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
        else
        {
            if (resultCode == RESULT_OK){
                refresh();
            }
        }
    }

    private android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_contextual_action_mode, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteGruppo:
                    refreshAdapter();
                    refreshLayout.setEnabled(true);

                    final Map<String, String> jsonParams = new HashMap<String, String>();
                    int l=0;
                    JSONArray jsonArray = new JSONArray();
                    for(Gruppo gr: multiselect_list)
                    {
                        try {
                            jsonArray.put(l,gr.getCodiceGruppo());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        l++;
                    }

                    JSONObject nuovo = new JSONObject();

                    try {
                        nuovo.put("gruppi", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String url = "http://www.mishu.altervista.org/api/gruppo/elimina/"+tokenUtente;
                    final RequestQueue req = Volley.newRequestQueue(Bacheca.this);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,nuovo, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String s = "";
                            try {
                                s = response.get("risposta").toString();
                            } catch (JSONException e) {
                                Toast.makeText(Bacheca.this, "Errore interno", Toast.LENGTH_SHORT).show();
                            }

                            if (s.compareTo("110") == 0) {
                                Toast.makeText(Bacheca.this, "Cancellazione effettuata", Toast.LENGTH_SHORT).show();

                            } else
                                Toast.makeText(Bacheca.this, "Cancellazione gruppo fallita!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Bacheca.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    req.add(jsonObjReq);


                    mActionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            refreshLayout.setEnabled(true);
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Gruppo>();
            nomeUtente.setBackgroundResource(R.color.colorPrimary);
            //refreshAdapter();
        }
    };
    public Bitmap getBitmapFromString(String str)
    {
        byte[] decodedString = Base64.decode(str, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public void refreshAdapter() {
        for(Gruppo gr:multiselect_list){
            if(gruppi.contains(gr))
            {

                int i=gruppi.indexOf(gr);
                gruppi.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
    }


    private void refresh() {

        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final String channel = (sharedPreferences.getString("token", ""));
        final String url = "http://www.mishu.altervista.org/api/gruppi/" + channel;
        final RequestQueue req = Volley.newRequestQueue(Bacheca.this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gruppo gruppo=null;
                String nome, data, orario, codiceGruppo, codiceAdmin;
                Bitmap image;
                ArrayList<String> partecipanti=null;
                ArrayList<Gruppo> gruppiRicevuti=new ArrayList<Gruppo>();
                try {
                    JSONArray array = response.getJSONArray("gruppi");
                    for (int b = 0; b < array.length(); b++) {


                        partecipanti = new ArrayList<String>();
                        nome = array.getJSONObject(b).getString("nomeGruppo");
                        data = array.getJSONObject(b).getString("dataScadenza");
                        codiceGruppo = array.getJSONObject(b).getString("codiceGruppo");
                        codiceAdmin = array.getJSONObject(b).getString("adminGruppo");
                        JSONArray array1 = array.getJSONObject(b).getJSONArray("partecipanti");
                        for (int c = 0; c < array1.length(); c++) {

                            partecipanti.add(array1.getString(c));

                        }
                        File creazione;
                        if(!array.getJSONObject(b).getString("image").equals(""))
                        {
                            image= decodeBase64(array.getJSONObject(b).getString("image"));
                            Bitmap bitmapNew;
                            OutputStream stream = null;

                            creazione=new File(dir+File.separator+"appSpesa"+File.separator+"gruppi"+File.separator+codiceGruppo+".jpg");
                            if(!creazione.exists())
                            {
                                try {
                                    stream = new FileOutputStream(creazione.getAbsolutePath());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                image.compress(Bitmap.CompressFormat.JPEG,100, stream);

                            }
                            else
                            {

                            }
                            gruppo = new Gruppo(nome.toUpperCase(), partecipanti, data, codiceAdmin, codiceGruppo,creazione.getAbsolutePath());
                        }
                        else
                        {
                            gruppo = new Gruppo(nome.toUpperCase(), partecipanti, data, codiceAdmin, codiceGruppo,"");
                        }

                        gruppiRicevuti.add(gruppo);


                        /*if(gruppi.contains(gruppo))
                        {
                            if(gruppi.get(gruppi.indexOf(gruppo)).getUtenti().equals(partecipanti))
                            {

                            }
                            else
                            {
                                gruppi.get(gruppi.indexOf(gruppo)).setUtenti(partecipanti);
                                adapter.notifyItemChanged(gruppi.indexOf(gruppo));
                            }
                        }
                        else
                        {
                            gruppi.add(gruppo);
                            adapter.notifyItemInserted(gruppi.size()-1);
                        }*/





                    }
                   ArrayList<Gruppo> tempAggiungi, tempElimina,tempModifica;

                    tempAggiungi=new ArrayList<>(gruppiRicevuti);
                    tempAggiungi.removeAll(gruppi);
                    tempElimina=new ArrayList<>(gruppi);
                    tempElimina.removeAll(gruppiRicevuti);
                    int []indice=new int [tempElimina.size()];
                    int a=0;
                    for (Gruppo gr:tempElimina)
                    {
                        indice[a]=gruppi.indexOf(gr);
                        gruppi.remove(indice[a]);
                        adapter.notifyItemRemoved(indice[a]);
                        a++;
                    }
                    for (Gruppo gr:tempAggiungi)
                    {
                        gruppi.add(gr);
                        adapter.notifyItemInserted(gruppi.size()-1);
                    }
                    tempModifica=new ArrayList<>(gruppi);
                    tempModifica.removeAll(tempAggiungi);
                    for (Gruppo gr:tempModifica)
                    {

                        if(!gr.getPartecipanti().equals(gruppiRicevuti.get(gruppiRicevuti.indexOf(gr)).getPartecipanti()))
                        {
                            gruppi.get(gruppi.indexOf(gr)).setUtenti(gruppiRicevuti.get(gruppiRicevuti.indexOf(gr)).getUtenti());
                            adapter.notifyItemChanged(gruppi.indexOf(gr));
                        }
                    }



                } catch (JSONException e) {
                    Toast.makeText(Bacheca.this, e.toString(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Bacheca.this, "Errore di comunicazione", Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_esci:
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(Bacheca.this, MainActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
