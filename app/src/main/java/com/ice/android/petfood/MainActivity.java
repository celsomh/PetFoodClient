package com.ice.android.petfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ICuadroDialogo, IRespuestaPostOperacion {

    private final String NOMBRE_SHARED_PREFERECES_DATOS_APP = "DatosApp";
    private final String ATRIBUTO_IP = "IP";
    private final String ATRIBUTO_COMIDA_CONTENEDOR = "ComidaContenedor";
    private final String ATRIBUTO_COMIDA_CONSUMIDA = "ComidaContenedor";

    private SharedPreferences sharedPreferencesDatosApp;
    private SharedPreferences.Editor editorDatosApp;

    private int opcionAEjecutar;
    private String tituloCuadroDialogo;
    private String mensajeCuadroDialogo;
    private OperacionAsyncTask operacionAsyncTask;

    private Button btnObtenerPeso;
    private Button btnMotorTime;
    private Button btnGetFoodEated;
    private Button btnEatingNow;
    private Button btnGiveFood;

    private TextView tvComidaConsumida;
    private TextView tvComidaContenedor;
    private TextView tvIP;
    private TextView tvComidaRecipiente;

    private ImageView imageViewEstaComiendo;

    private ProgressBar progressBarEstaComiendo;
    private ProgressBar progressBarDispensarPorTiempo;
    private ProgressBar progressBarDispensarPorPeso;

    private CuadroDialogo cuadroDialogo;

    private ScrollView scrollView;

    private ObjetoIce objetoIce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesDatosApp = getSharedPreferences(NOMBRE_SHARED_PREFERECES_DATOS_APP, Context.MODE_PRIVATE);
        String IP = sharedPreferencesDatosApp.getString(ATRIBUTO_IP, "localhost");
        objetoIce = new ObjetoIce(IP, "10000", "SensorControl");
        tvIP = findViewById(R.id.tv_ip);
        tvIP.setText(IP);

        editorDatosApp = sharedPreferencesDatosApp.edit();

        tvComidaContenedor = findViewById(R.id.tv_comida_en_contenedor);
        int comidaContenedor = sharedPreferencesDatosApp.getInt(ATRIBUTO_COMIDA_CONTENEDOR, 0);
        tvComidaContenedor.setText(String.valueOf(comidaContenedor));

        String comidaConsumida = sharedPreferencesDatosApp.getString(ATRIBUTO_COMIDA_CONSUMIDA, "0");
        tvComidaConsumida = findViewById(R.id.tv_comida_consumida);
        tvComidaConsumida.setText(comidaConsumida);

        scrollView = findViewById(R.id.id_scroll_view);

        progressBarDispensarPorPeso = findViewById(R.id.id_progress_bar_dispensar_por_peso);
        progressBarDispensarPorPeso.setVisibility(View.INVISIBLE);

        progressBarDispensarPorTiempo = findViewById(R.id.pb_dispensar_por_tiempo);
        progressBarDispensarPorTiempo.setVisibility(View.INVISIBLE);

        progressBarEstaComiendo = findViewById(R.id.id_progress_bar_esta_comiendo);
        progressBarEstaComiendo.setVisibility(View.INVISIBLE);

        imageViewEstaComiendo = findViewById(R.id.id_image_view_esta_comiendo);

        tvComidaRecipiente = findViewById(R.id.tv_comida_en_recipiente);

        btnObtenerPeso = findViewById(R.id.btn_get_weight);
        btnObtenerPeso.setOnClickListener(this);

        btnMotorTime = findViewById(R.id.btn_motor_time);
        btnMotorTime.setOnClickListener(this);

        btnGetFoodEated = findViewById(R.id.btn_get_food_eated);
        btnGetFoodEated.setOnClickListener(this);

        btnEatingNow = findViewById(R.id.btn_eating_now);
        btnEatingNow.setOnClickListener(this);

        btnGiveFood = findViewById(R.id.btn_give_food);
        btnGiveFood.setOnClickListener(this);

        cuadroDialogo = new CuadroDialogo(this, this);
        //Linea comentada para realizar pruebas
        //operacionAsyncTask = new OperacionAsyncTask(objetoIce, this, this);
    }

    @Override
    public void onClick(View v) {
        OperacionAsyncTask operacionAsyncTask;
        if (v.getId() == R.id.btn_get_weight) {
            operacionAsyncTask = new OperacionAsyncTask(objetoIce, this);
            operacionAsyncTask.execute(Opcion.GET_WEIGHT);

        } else if (v.getId() == R.id.btn_motor_time) {
            progressBarDispensarPorTiempo.setVisibility(View.VISIBLE);
            cuadroDialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
            tituloCuadroDialogo = "Dispensar por tiempo";
            mensajeCuadroDialogo = "Ingrese el tiempo en segundos";
            opcionAEjecutar = Opcion.MOTOR_TIME;
            cuadroDialogo.show();

        } else if (v.getId() == R.id.btn_get_food_eated) {
            int comidaConsumida = sharedPreferencesDatosApp.getInt(ATRIBUTO_COMIDA_CONSUMIDA, 0);
            tvComidaConsumida.setText(String.valueOf(comidaConsumida));
        } else if (v.getId() == R.id.btn_eating_now) {
            operacionAsyncTask = new OperacionAsyncTask(objetoIce, this);
            imageViewEstaComiendo.setVisibility(View.INVISIBLE);
            progressBarEstaComiendo.setVisibility(View.VISIBLE);
            operacionAsyncTask.execute(Opcion.EATING_NOW);
        } else if (v.getId() == R.id.btn_give_food) {
            progressBarDispensarPorPeso.setVisibility(View.VISIBLE);
            cuadroDialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
            scrollView.fullScroll(View.FOCUS_DOWN);
            tituloCuadroDialogo = "Dispensar por peso";
            mensajeCuadroDialogo = "Ingrese la cantidad en gramos";
            opcionAEjecutar = Opcion.GIVE_FOOD;
            cuadroDialogo.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.id_cambiar_ip) {
            opcionAEjecutar = Opcion.CAMBIAR_IP;
            cuadroDialogo.setInputType(InputType.TYPE_CLASS_TEXT);
            cuadroDialogo.setText(objetoIce.getNumHost());
            tituloCuadroDialogo = "Cambiar ip";
            mensajeCuadroDialogo = "Ingrese la nueva ip";
            cuadroDialogo.show();
            return true;
        } else if (item.getItemId() == R.id.id_ingresar_comida) {
            opcionAEjecutar = Opcion.INGRESAR_COMIDA;
            cuadroDialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
            tituloCuadroDialogo = "Ingresar comida al contenedor";
            mensajeCuadroDialogo = "Ingrese la cantidad en gramos";
            cuadroDialogo.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public String getTitulo() {
        return tituloCuadroDialogo;
    }

    @Override
    public String getMensaje() {
        return mensajeCuadroDialogo;
    }

    @Override
    public void setRespuesta(String respuesta) {
        if (opcionAEjecutar == Opcion.MOTOR_TIME) {
            operacionAsyncTask.execute(Opcion.MOTOR_TIME, Integer.parseInt(respuesta));
        } else if (opcionAEjecutar == Opcion.GIVE_FOOD) {
            operacionAsyncTask.execute(Opcion.GIVE_FOOD, Integer.parseInt(respuesta));
        } else if (opcionAEjecutar == Opcion.CAMBIAR_IP) {
            editorDatosApp.putString(ATRIBUTO_IP, respuesta);
            editorDatosApp.commit();
            //Linea comentada para realizar pruebas
            //objetoIce.setNumHost(respuesta);
            tvIP.setText(respuesta);
        } else if (opcionAEjecutar == Opcion.INGRESAR_COMIDA) {
            int comidaContendor = sharedPreferencesDatosApp.getInt(ATRIBUTO_COMIDA_CONTENEDOR, 0);
            if (comidaContendor > 0) {
                int comidaIngresada = Integer.parseInt(respuesta);
                int comidaContendorActualizada = comidaContendor + comidaIngresada;
                editorDatosApp.putInt(ATRIBUTO_COMIDA_CONTENEDOR, comidaContendorActualizada);
                editorDatosApp.commit();
                tvComidaContenedor.setText(String.valueOf(comidaContendorActualizada));
            } else {
                editorDatosApp.putInt(ATRIBUTO_COMIDA_CONTENEDOR, Integer.parseInt(respuesta));
                editorDatosApp.commit();
                tvComidaContenedor.setText(respuesta);
            }
        }
    }

    @Override
    public void getRespuesta(String respuesta) {
        if (opcionAEjecutar == Opcion.MOTOR_TIME || opcionAEjecutar == Opcion.GIVE_FOOD) {
            int comidaContenedor = sharedPreferencesDatosApp.getInt(ATRIBUTO_COMIDA_CONTENEDOR, 0);
            int comidaExpendida = Integer.parseInt(respuesta);
            int comidaContendorActualizada = comidaContenedor - comidaExpendida;
            //Si el sensor de peso arroja un resultado negativo
            if (comidaContenedor < 0)
                comidaContenedor = 0;
            editorDatosApp.putInt(ATRIBUTO_COMIDA_CONTENEDOR, comidaContendorActualizada);
            tvComidaContenedor.setText(String.valueOf(comidaContendorActualizada));
            int comidaConsumida = sharedPreferencesDatosApp.getInt(ATRIBUTO_COMIDA_CONSUMIDA, 0);
            comidaConsumida += comidaExpendida;
            editorDatosApp.putInt(ATRIBUTO_COMIDA_CONSUMIDA, comidaConsumida);
        }

        switch (opcionAEjecutar) {
            case Opcion.GET_WEIGHT:
                tvComidaRecipiente.setText(respuesta);
                break;
            case Opcion.GIVE_FOOD:
                progressBarDispensarPorPeso.setVisibility(View.INVISIBLE);
                break;
            case Opcion.MOTOR_TIME:
                progressBarDispensarPorTiempo.setVisibility(View.INVISIBLE);
                break;
            case Opcion.EATING_NOW:
                if (respuesta.equals("true"))
                    imageViewEstaComiendo.setImageResource(R.mipmap.yes_icon);
                else
                    imageViewEstaComiendo.setImageResource(R.mipmap.no_icon);
                progressBarEstaComiendo.setVisibility(View.INVISIBLE);
                imageViewEstaComiendo.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
