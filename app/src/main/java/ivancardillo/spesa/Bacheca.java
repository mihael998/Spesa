package ivancardillo.spesa;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Bacheca extends AppCompatActivity {
    public ArrayList<Gruppo> gruppi;
    GruppiAdapter adapter;
    private ListView v;
    private Gruppo g;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private FloatingActionMenu fam;
    SharedPreferences sharedPreferences;
    TextView nomeUtente;
    //private Animation fab_open,fab_close,rotate_forward,rotate_backward;

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

        nomeUtente.setText("Ciao " + sharedPreferences.getString("nome", "null") + "!");

        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {

                } else {

                }
            }
        });
        /*fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab_open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);*/

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
                        Intent avanti = new Intent(Bacheca.this, GruppoActivity.class);
                        avanti.putExtra("codiceGruppo", gruppi.get(position).getCodiceGruppo());
                        avanti.putExtra("codiceAdmin", gruppi.get(position).getCodiceAdmin());
                        avanti.putExtra("nomeGruppo", gruppi.get(position).getNome());
                        avanti.putExtra("scadenzaOra", gruppi.get(position).getScadenzaOra());
                        avanti.putExtra("scadenzaData", gruppi.get(position).getScadenzaData());
                        avanti.putStringArrayListExtra("partecipanti", gruppi.get(position).getUtenti());
                        startActivity(avanti);
                    }

                    public void onLongClickItem(View v, int position) {
                        Toast.makeText(Bacheca.this, "Long Click", Toast.LENGTH_SHORT).show();
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

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fab1) {
                    Intent i = new Intent(Bacheca.this, JoinGruppo.class);
                    startActivity(i);
                } else if (view == fab2) {
                    Intent i = new Intent(Bacheca.this, NuovoGruppo.class);
                    startActivity(i);
                }
                fam.close(true);
            }
        };
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



    /*@Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                //animateFAB();
                break;
            case R.id.fab1:

                Log.d("Raj", "Fab 1");
                break;
            case R.id.fab2:

                Log.d("Raj", "Fab 2");
                break;
        }
    }*/

    /*public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }*/

}
