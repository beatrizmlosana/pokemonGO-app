package com.example.pkm_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class IntroducirActivity extends AppCompatActivity {

    //Button de enviar la información
    Button boton_enviar1;

    //ImageButton que vuelve a la ventana principal
    ImageButton boton_volver;

    //RadioGroup con sus RadioButton y el TextView
    RadioGroup radioGroup;
    RadioButton rb1, rb2, rb3, rb4;
    TextView tv;

    //Este string recoge la información del tipo de RadioButton que has seleccionado
    String tipo;

    //EditText
    EditText editTextNomb, editTextCXb, editTextCYb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir);

        //EditText
        editTextNomb = findViewById(R.id.editTextNom);
        editTextCXb = findViewById(R.id.editTextCX);
        editTextCYb = findViewById(R.id.editTextCY);

        //RadioGroup
        tv = (TextView) findViewById(R.id.textView);
        rb1 = (RadioButton) findViewById(R.id.rbAgua);
        rb2 = (RadioButton) findViewById(R.id.rbFuego);
        rb3 = (RadioButton) findViewById(R.id.rbPlanta);
        rb4 = (RadioButton) findViewById(R.id.rbElectrico);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        //Button e ImageButton
        boton_enviar1 = findViewById(R.id.boton_enviar);
        boton_volver = findViewById(R.id.boton_volver);

        //Acción que controla el RadioButton seleccionado
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.rbAgua:
                        tipo = "Agua";
                        break;
                    case R.id.rbFuego:
                        tipo = "Fuego";
                        break;
                    case R.id.rbPlanta:
                        tipo = "Tierra";
                        break;
                    case R.id.rbElectrico:
                        tipo = "Eléctrico";
                        break;
                }
            }
        });

        //Acciones de los botonones
        Intent ventana_principal = new Intent(this, MainActivity.class);

        boton_enviar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.baseDatos.execSQL("INSERT INTO Pokemon VALUES('" + editTextNomb.getText().toString() + "', '" + tipo + "', '" + editTextCXb.getText().toString() + "', '" + editTextCYb.getText().toString() + "');");
                startActivity(ventana_principal);
            }
        });

        boton_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ventana_principal);
            }
        });


    }
}