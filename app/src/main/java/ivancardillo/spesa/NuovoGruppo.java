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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
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
    ImageView scattaFoto,scegliFoto,thumbnailGruppo,eliminaFoto;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ImageButton buttonElimina;
    private File dir,fileFinale;
    private String string2Qr,absolutePathImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_gruppo);

        ImageButton button = (ImageButton) findViewById(R.id.creaGruppo);
        buttonElimina = (ImageButton) findViewById(R.id.annullaCreazione);
        scattaFoto=(ImageView)findViewById(R.id.pulsanteScattafoto);
        thumbnailGruppo=(ImageView)findViewById(R.id.thumbnailNuovoGruppo);
        absolutePathImage="";
        eliminaFoto=(ImageView)findViewById(R.id.pulsanteCancellafoto);
        scegliFoto=(ImageView)findViewById(R.id.pulsanteScegliFoto);
        final EditText nomeDelGruppo = (EditText) findViewById(R.id.nomeGr);
        final Map<String, String> jsonParams = new HashMap<String, String>();


        string2Qr = UUID.randomUUID().toString().substring(0, 8);
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        fileFinale=new File(dir+File.separator+"appSpesa"+File.separator+"gruppi"+File.separator+string2Qr+".jpg");



        buttonElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scattaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                absolutePathImage=fileFinale.getAbsolutePath();
                Uri picUri;
                if (Build.VERSION.SDK_INT >= 23) {
                    picUri = FileProvider.getUriForFile(NuovoGruppo.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            fileFinale);
                }
                else
                {
                    picUri = Uri.fromFile(fileFinale);
                }


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(takePictureIntent, 100);
            }
        });
        scegliFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
            }
        });
        eliminaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File canc=new File(absolutePathImage);
                canc.delete();
                absolutePathImage="";
                thumbnailGruppo.setImageResource(R.drawable.ic_photo_select);
                eliminaFoto.setVisibility(View.GONE);
                scattaFoto.setVisibility(View.VISIBLE);
                scegliFoto.setVisibility(View.VISIBLE);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nomeDelGruppo.getText().toString().compareTo("") != 0) {



                    SharedPreferences sharedPreferences;
                    sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    final String channel = (sharedPreferences.getString("token", ""));
                    final String nomeUtente = (sharedPreferences.getString("nome", ""));

                    final String url = "http://www.mishu.altervista.org/api/gruppo/crea";
                    final RequestQueue req = Volley.newRequestQueue(NuovoGruppo.this);
                    jsonParams.put("token", channel);
                    jsonParams.put("nomeGruppo", nomeDelGruppo.getText().toString());
                    jsonParams.put("scadenzaDataGruppo", new SimpleDateFormat("dd/MM/yyyy").format(android.icu.util.Calendar.getInstance().getTime()));
                    jsonParams.put("codiceGruppo", string2Qr);
                    final Bitmap imageBitmap;
                    if(!absolutePathImage.equals(""))
                    {
                        BitmapFactory.Options options= new BitmapFactory.Options();
                        options.inJustDecodeBounds=false;
                        imageBitmap = BitmapFactory.decodeFile(absolutePathImage, options);
                        jsonParams.put("image", encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG,80));
                    }
                    else
                    {
                        jsonParams.put("image", "");
                    }

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
                                Toast.makeText(NuovoGruppo.this, "Creazione Gruppo Riuscita!", Toast.LENGTH_SHORT).show();
                                Intent _result = new Intent();
                                setResult(Activity.RESULT_OK, _result);
                                finish();
                            } else
                                Toast.makeText(NuovoGruppo.this, "Pubblicazione gruppo fallita!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(NuovoGruppo.this, error.toString(), Toast.LENGTH_LONG).show();
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
    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
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
                        if(!absolutePathImage.equals(""))
                        {
                            File canc=new File(absolutePathImage);
                            canc.delete();
                        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap imageBitmap;
            imageBitmap = getResizedBitmap(BitmapFactory.decodeFile(absolutePathImage, options),1080);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(absolutePathImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            thumbnailGruppo.setImageBitmap(imageBitmap);
            thumbnailGruppo.setImageTintList(null);
            eliminaFoto.setVisibility(View.VISIBLE);
            scattaFoto.setVisibility(View.GONE);
            scegliFoto.setVisibility(View.GONE);

        }
        else if(requestCode == 100 && resultCode == RESULT_CANCELED)
        {

        }
        else if(requestCode == 3&&resultCode == RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(selectedImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap imageBitmap;
            imageBitmap = getResizedBitmap(bitmap,1080);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(fileFinale.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            absolutePathImage=fileFinale.getAbsolutePath();

            thumbnailGruppo.setImageBitmap(imageBitmap);

            thumbnailGruppo.setImageTintList(null);
            eliminaFoto.setVisibility(View.VISIBLE);
            scattaFoto.setVisibility(View.GONE);
            scegliFoto.setVisibility(View.GONE);
        }
        else
        {

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

    /*public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }*/


    /*public static class TimePickerFragment extends DialogFragment
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
    }*/


}
