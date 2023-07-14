package com.example.pkm_go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.*;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.hardware.*;
import android.location.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener {

    //Button para ir a la ventana de introducir, listar o capturar el Pokémon
    Button boton_introducirR, boton_listarR, boton_capturarR;

    //Atributos necesarios para la localización
    static final long TIEMPO_MIN = 10 * 1000; //10 SEGUNDOS
    static final long DISTANCIA_MIN = 5; // 5 METROS
    static final String[] E = {"Fuera de servicio", "Temporalmente no disponible ", "Disponible"};
    LocationManager manejador;
    String proveedor;

    //Base de datos donde se guardarán los datos
    static SQLiteDatabase baseDatos;

    //Atributo para el sonido al capturar el Pokémon
    MediaPlayer sonido;

    //En estos atributo se guardarán los datos de los Pokémon capturados
    String nombre_pokemon, tipo_pokemon, coordenadaX, coordenadaY;

    //Atributos necesarios para el sensor
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    boolean sensorActivado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sonido al cazar
        sonido = MediaPlayer.create(this, R.raw.cazar);

        //Buttons
        boton_introducirR = findViewById(R.id.boton_introducir);
        boton_listarR = findViewById(R.id.boton_listar);
        boton_capturarR = findViewById(R.id.boton_capturar);

        //Sensor
        boton_capturarR.setEnabled(false); //nuestro botón capturar está deshabilitado
        boton_capturarR.setAlpha(0.5f); //configuración de la opacidad
        sensorActivado = false; //el sensor también está deshabilitado

        //Creación de las dos bases de datos
        baseDatos = openOrCreateDatabase("Pokemon", Context.MODE_PRIVATE, null);
        //baseDatos.execSQL("DROP TABLE Pokemon;");
        baseDatos.execSQL("CREATE TABLE IF NOT EXISTS Pokemon (Nombre VARCHAR, Tipo VARCHAR, CoordenadasX VARCHAR, CoordenadasY VARCHAR);");
        //baseDatos.execSQL("DROP TABLE Pokemon_Capturados;");
        baseDatos.execSQL("CREATE TABLE IF NOT EXISTS Pokemon_Capturados (Nombre VARCHAR, Tipo VARCHAR, CoordenadasX VARCHAR, CoordenadasY VARCHAR);");

        //Aquí controlamos el tipo de sensor que queremos utilizar y si nuestro dispositivo tiene dicho sensor
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor == null){
            Toast.makeText(this, "EL MÓVIL NO TIENE SENSOR", Toast.LENGTH_SHORT).show();

        }

        //Si el sensor está activado entonces realizará las siguientes funciones
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorActivado == true){
                    if(sensorEvent.values[0] < -8 || sensorEvent.values[0] > 8){ //Se indica la sensibilidad del sensor a la hora de mover nuestro móvil
                        capturarPokemon(); //Se llama al método de capturarPokemon y se realiza la función
                        sensorActivado = false; //Se desactiva el sensor
                        boton_capturarR.setEnabled(false); //Nuestro botón capturar se deshabilita una vez capturado nuestro Pokémon
                        boton_capturarR.setAlpha(0.5f); //Configuración de la opacidad
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                //En este método no introducimos información porque es innecesaria
            }
        };
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        //Localización
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = manejador.getBestProvider(criterio, true);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location localizacion = manejador.getLastKnownLocation(proveedor);


        //Acciones de los botones
        Intent ventana_introducir = new Intent(this, IntroducirActivity.class);
        Intent ventana_listar = new Intent(this, ListarActivity.class);

        boton_introducirR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ventana_introducir);
            }
        });

        boton_listarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ventana_listar);
            }
        });

        boton_capturarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturarPokemon();
                boton_capturarR.setEnabled(false);
                boton_capturarR.setAlpha(0.5f);
            }
        });

    }

    //Métodos de los botones
    public void capturarPokemon() {
        sonido.start();
        //Insertamos el Pokémon capturado en la lista de Pokemon_Capturado
        baseDatos.execSQL("INSERT INTO Pokemon_Capturados VALUES('" + nombre_pokemon + "', '" + tipo_pokemon + "', '" + coordenadaX + "', '" + coordenadaY + "');");

        Toast.makeText(MainActivity.this, "HAS CAPTURADO EL POKEMON", Toast.LENGTH_SHORT).show();

        //Una vez insertado el Pokémon en la otra lista, tenemos que borrar ese Pokémon capturado de la lista donde estaba anteriormente
        baseDatos.execSQL("DELETE FROM Pokemon WHERE Nombre = '" + nombre_pokemon + "' AND Tipo = '" + tipo_pokemon + "';");

    }

    //Métodos de la localización
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manejador.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitudX = location.getLatitude(); //Recoge la latitud
        double longitudY = location.getLongitude(); //Recoge la longitud

        Cursor cursor = baseDatos.rawQuery("SELECT * FROM Pokemon;", null); //Hacemos un select para que muestre toda la información

        if (cursor.getCount() == 0) {

        } else {
            while (cursor.moveToNext()) {
                double restarX = Double.parseDouble(cursor.getString(2)) - latitudX;
                double restarY = Double.parseDouble(cursor.getString(3)) - longitudY;

                if (restarX < 0.000900 && restarX > -0.000900 && restarY < 0.000900 && restarY > -0.000900) {
                    Toast.makeText(this, "¡EL POKEMON ESTÁ AQUÍ!", Toast.LENGTH_SHORT).show(); //Toast que indique que el Pokémon está justo en tu posición
                    boton_capturarR.setEnabled(true); //El botón capturar se habilita para poder usarlo
                    boton_capturarR.setAlpha(1f); //Configuración de la opacidad
                    sensorActivado = true; //Se activa el sensor

                    //Se recoge con el cursor la información obtenida
                    nombre_pokemon = cursor.getString(0);
                    tipo_pokemon = cursor.getString(1);
                    coordenadaX = cursor.getString(2);
                    coordenadaY = cursor.getString(3);

                } else if (restarX < 0.002000 && restarX > -0.002000 && restarY < 0.002000 && restarY > -0.002000) {
                    Toast.makeText(this, "EL POKEMON ESTÁ CERCA", Toast.LENGTH_SHORT).show(); //Toast que indique que el Pokémon está cerca
                    boton_capturarR.setEnabled(false); //El botón capturar se deshabilita
                    boton_capturarR.setAlpha(0.5f); //Configuración de la opacidad
                    sensorActivado = false; //El sensor está desactivado

                } else {
                    boton_capturarR.setEnabled(false); //El botón capturar se deshabilita
                    boton_capturarR.setAlpha(0.5f); //Configuración de la opacidad
                    sensorActivado = false; //El sensor está desactivado
                }
            }
        }
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        log("Proveedor deshabilitado: " + proveedor + "\n");
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        log("Proveedor habilitado: " + proveedor + "\n");
    }


    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        log("Cambia estado proveedor: " + proveedor + ", estado="
                + E[Math.max(0, estado)] + ", extras=" + extras + "\n");
    }

    // Métodos para mostrar información
    private void log(String cadena) {

    }

    private void muestraLocaliz(Location localizacion) {
        if (localizacion == null)
            log("Localización desconocida\n");
        else
            log(localizacion.toString() + "\n");
    }

    private void muestraProveedores() {
        log("Proveedores de localización: \n ");
        List<String> proveedores = manejador.getAllProviders();
        for (String proveedor : proveedores) {
            muestraProveedor(proveedor);
        }
    }

    private void muestraProveedor(String proveedor) {
        LocationProvider info = manejador.getProvider(proveedor);
        log("LocationProvider[ " + "getName=" + info.getName()
                + ", isProviderEnabled="
                + manejador.isProviderEnabled(proveedor) + ", getAccuracy="
                + ", hasMonetaryCost=" + info.hasMonetaryCost()
                + ", requiresCell=" + info.requiresCell()
                + ", requiresNetwork=" + info.requiresNetwork()
                + ", requiresSatellite=" + info.requiresSatellite()
                + ", supportsAltitude=" + info.supportsAltitude()
                + ", supportsBearing=" + info.supportsBearing()
                + ", supportsSpeed=" + info.supportsSpeed() + " ]\n");
    }
}