package com.example.pkm_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;


public class ListarActivity extends AppCompatActivity {

    //ListView que recoge la lista de Pokémon capturados
    ListView lista_pokemon;

    //ImageButton que vuelve a la ventana principal
    ImageButton boton_volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        //ListView
        lista_pokemon = findViewById(R.id.lista_pok);

        //ImageButton
        boton_volver = findViewById(R.id.boton_volver);

        //Se guardan los Pokémon
        ArrayAdapter<String> adapter;
        List<String> list = new ArrayList<String>();

        //Se realiza un SELECT que será el encargado de mostrar todos los datos de los Pokémon capturados
        Cursor cursor = MainActivity.baseDatos.rawQuery("SELECT * FROM Pokemon_Capturados;", null);

        //Aquí se controla si hay o no algún Pokémon en la lista y lo que mostrará en cada posibilidad
        if(cursor.getCount() == 0){
            list.add("No hay pokemons en la pokedex");
        } else {
            while (cursor.moveToNext()){
                list.add("-Nombre: " + cursor.getString(0) + "\n-Tipo: " + cursor.getString(1) + "\n-Coordenada X: " + cursor.getString(2) + "\n-Cordenada Y: " + cursor.getString(3));
            }
        }

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_fila, list);
        lista_pokemon.setAdapter(adapter);

        //Acción del botón
        Intent ventana_principal = new Intent(this, MainActivity.class);
        boton_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ventana_principal);
            }
        });

    }
}