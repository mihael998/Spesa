package ivancardillo.spesa;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private FloatingActionButton fab, fab1, fab2;
    private FloatingActionMenu fam;
    SharedPreferences sharedPreferences;
    private String tokenUtente;
    TextView nomeUtente;
    boolean isMultiSelect = false;
    //private Animation fab_open,fab_close,rotate_forward,rotate_backward;


    boolean doubleBackToExitPressedOnce = false;

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
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.setAdapter(adapter);
        setSupportActionBar(toolbar);
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
                Bundle bundle = data.getExtras();
                Gruppo nuovo=(Gruppo)bundle.getSerializable("value");
                gruppi.add(nuovo);
                adapter.notifyItemInserted(gruppi.size()-1);
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
                                Toast.makeText(Bacheca.this, "Pubblicazione gruppo fallita!", Toast.LENGTH_SHORT).show();
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

                try {
                    JSONArray array = response.getJSONArray("gruppi");
                    gruppi.clear();
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
                        Gruppo gruppo;
                        gruppo = new Gruppo(nome.toUpperCase(), partecipanti, orario.substring(0, 5), newDateString, codiceAdmin, codiceGruppo);


                            gruppi.add(gruppo);




                    }
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    Toast.makeText(Bacheca.this, "Errore interno", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
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


}
