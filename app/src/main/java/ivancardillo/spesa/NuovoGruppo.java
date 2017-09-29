package ivancardillo.spesa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Calendar;


import java.util.Calendar;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class NuovoGruppo extends AppCompatActivity {
    Calendar myCalendar;
    Button pulsanteOrarioScadenza;
    Button pulsanteDataScadenza;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ImageButton buttonElimina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_gruppo);

        pulsanteDataScadenza = (Button) findViewById(R.id.pulsanteDataScadenza);
        pulsanteOrarioScadenza = (Button) findViewById(R.id.pulsanteOrarioScadenza);
        ImageButton button = (ImageButton) findViewById(R.id.creaGruppo);
        buttonElimina = (ImageButton) findViewById(R.id.annullaCreazione);
        final EditText nomeDelGruppo = (EditText) findViewById(R.id.nomeGr);
        final Map<String, String> jsonParams = new HashMap<String, String>();

        buttonElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nomeDelGruppo.getText().toString().compareTo("") != 0 && pulsanteDataScadenza.getText().length() == 10 && pulsanteOrarioScadenza.getText().length() == 5) {


                    final String string2Qr = UUID.randomUUID().toString().substring(0, 8);
                    SharedPreferences sharedPreferences;
                    sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    final String channel = (sharedPreferences.getString("token", ""));
                    final String nomeUtente = (sharedPreferences.getString("nome", ""));

                    final String url = "http://www.mishu.altervista.org/api/gruppo/crea";
                    final RequestQueue req = Volley.newRequestQueue(NuovoGruppo.this);
                    jsonParams.put("token", channel);
                    jsonParams.put("nomeGruppo", nomeDelGruppo.getText().toString());
                    jsonParams.put("scadenzaDataGruppo", pulsanteDataScadenza.getText().toString());
                    jsonParams.put("scadenzaOrarioGruppo", pulsanteOrarioScadenza.getText().toString());
                    jsonParams.put("codiceGruppo", string2Qr);

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String s = "";
                            try {
                                s = response.get("risposta").toString();
                            } catch (JSONException e) {
                                Toast.makeText(NuovoGruppo.this, "Errore interno", Toast.LENGTH_SHORT).show();
                            }

                            if (s.compareTo("110") == 0) {
                                ArrayList<String>z=new ArrayList<String>();
                                z.add(nomeUtente);
                                Gruppo gr=new Gruppo(nomeDelGruppo.getText().toString().toUpperCase(),z,pulsanteOrarioScadenza.getText().toString(), pulsanteDataScadenza.getText().toString(),channel,string2Qr);
                                Toast.makeText(NuovoGruppo.this, "Creazione Gruppo Riuscita!", Toast.LENGTH_SHORT).show();
                                Intent _result = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("value", gr);
                                _result.putExtras(bundle);
                                setResult(Activity.RESULT_OK, _result);
                                finish();
                            } else
                                Toast.makeText(NuovoGruppo.this, "Pubblicazione gruppo fallita!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(NuovoGruppo.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    req.add(jsonObjReq);


                } else
                    Toast.makeText(NuovoGruppo.this, "Inserisci valori corretti", Toast.LENGTH_SHORT).show();

            }
        });


    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NuovoGruppo.this);

        // set title
        alertDialogBuilder.setTitle("Attenzione");

        // set dialog message
        alertDialogBuilder
                .setMessage("Continuando interromperai la creazione del gruppo. Vuoi continuare")
                .setCancelable(false)
                .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        NuovoGruppo.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

            return;
        }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hA, mA;
            hA = "" + hourOfDay;
            mA = "" + minute;
            if (hA.length() == 1) {
                hA = "0" + hA;
            }
            if (mA.length() == 1) {
                mA = "0" + mA;
            }
            Button pulsante = (Button) getActivity().findViewById(R.id.pulsanteOrarioScadenza);
            pulsante.setBackgroundResource(R.drawable.rounded_shape);

            pulsante.setText(hA + ":" + mA);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String monthA, dayA;
            dayA = "" + day;
            monthA = "" + (month + 1);
            if (monthA.length() == 1) {
                monthA = "0" + (month + 1);
            }
            if ((day + "").length() == 1) {
                dayA = "0" + day;
            }
            Button pulsante = (Button) getActivity().findViewById(R.id.pulsanteDataScadenza);
            pulsante.setBackgroundResource(R.drawable.rounded_shape);
            pulsante.setText(dayA + "/" + (monthA) + "/" + year);
        }
    }


}
