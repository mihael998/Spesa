package ivancardillo.spesa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button accedi = (Button) findViewById(R.id.accedi);
        Button registrati = (Button) findViewById(R.id.registrati);

        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avanti = new Intent(MainActivity.this, Accedi.class);
                startActivity(avanti);
            }
        });


        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avanti = new Intent(MainActivity.this, Registrati.class);
                startActivity(avanti);
            }
        });


    }
}
