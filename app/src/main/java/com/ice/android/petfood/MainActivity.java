package com.ice.android.petfood;

import android.os.Build;
import android.support.annotation.RequiresApi;
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

import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnObtenerPeso;
    Button btnMotorTime;
    Button btnGetFoodEated;
    Button btnEatingNow;
    Button btnGiveFood;

    static TextView textViewCuantoHaComido;
    static TextView textViewComidaContenedor;
    static TextView textViewIp;
    static TextView textViewComidaRecipiente;

    static ImageView imageViewEstaComiendo;

    static ProgressBar progressBarEstaComiendo;
    static ProgressBar progressBarDispensarPorTiempo;
    static ProgressBar progressBarDispensarPorPeso;

    private CuadroDialogo cuadroDialogo;

    private ScrollView scrollView;

    static ObjectIce objectIce;

    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file=new File();

        objectIce=new ObjectIce("localhost","10000","SensorControl");
        String ip=file.readFromFile(NameFile.FILE_IP,this);
        if (ip!=null)
            objectIce.setNumHost(ip);

        textViewComidaContenedor=findViewById(R.id.id_get_container_food);

        String comidaContenedor=file.readFromFile(NameFile.FILE_CONTENEDOR,this);
        if (comidaContenedor!=null)
            textViewComidaContenedor.setText(comidaContenedor);

        String comidaConsumida=file.readFromFile(NameFile.FILE_COMIDA_CONSUMIDA,this);
        if(comidaConsumida==null)
            file.writeToFile(NameFile.FILE_COMIDA_CONSUMIDA,"0",this);

        textViewCuantoHaComido=findViewById(R.id.id_text_view_cuanto_ha_comido);

        textViewIp=findViewById(R.id.id_text_view_ip);
        textViewIp.setText(objectIce.getNumHost());

        scrollView=findViewById(R.id.id_scroll_view);

        progressBarDispensarPorPeso=findViewById(R.id.id_progress_bar_dispensar_por_peso);
        progressBarDispensarPorPeso.setVisibility(View.INVISIBLE);

        progressBarDispensarPorTiempo=findViewById(R.id.id_progress_bar_dispensar_por_tiempo);
        progressBarDispensarPorTiempo.setVisibility(View.INVISIBLE);

        progressBarEstaComiendo=findViewById(R.id.id_progress_bar_esta_comiendo);
        progressBarEstaComiendo.setVisibility(View.INVISIBLE);

        imageViewEstaComiendo=findViewById(R.id.id_image_view_esta_comiendo);

        textViewComidaRecipiente=findViewById(R.id.id_text_view_comida_recipiente);

        btnObtenerPeso=findViewById(R.id.id_get_weight);
        btnObtenerPeso.setOnClickListener(this);

        btnMotorTime=findViewById(R.id.id_motor_time);
        btnMotorTime.setOnClickListener(this);

        btnGetFoodEated=findViewById(R.id.id_get_food_eated);
        btnGetFoodEated.setOnClickListener(this);

        btnEatingNow=findViewById(R.id.id_eating_now);
        btnEatingNow.setOnClickListener(this);

        btnGiveFood=findViewById(R.id.id_give_food);
        btnGiveFood.setOnClickListener(this);

        cuadroDialogo=new CuadroDialogo(this);
    }

    @Override
    public void onClick(View v) {
        InternetAsyncTask internetAsyncTask;
        switch(v.getId()){
            case R.id.id_get_weight:
                internetAsyncTask=new InternetAsyncTask(objectIce,this);
                internetAsyncTask.execute(Opcion.GET_WEIGHT);
                break;
            case R.id.id_motor_time:
                cuadroDialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
                cuadroDialogo.setTitle("Dispensar por tiempo");
                cuadroDialogo.setMessage("Ingrese el tiempo en segundos");
                cuadroDialogo.show(Opcion.MOTOR_TIME,new InternetAsyncTask(objectIce, this));
                break;
            case R.id.id_get_food_eated:
                internetAsyncTask=new InternetAsyncTask(objectIce, this);
                internetAsyncTask.execute();
                break;
            case R.id.id_eating_now:
                internetAsyncTask=new InternetAsyncTask(objectIce, this);
                imageViewEstaComiendo.setVisibility(View.INVISIBLE);
                progressBarEstaComiendo.setVisibility(View.VISIBLE);
                internetAsyncTask.execute(Opcion.EATING_NOW);
                break;
            case R.id.id_give_food:
                cuadroDialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
                scrollView.fullScroll(View.FOCUS_DOWN);
                cuadroDialogo.setTitle("Dispensar por peso");
                cuadroDialogo.setMessage("Ingrese la cantidad en gramos");
                cuadroDialogo.show(Opcion.GIVE_FOOD, new InternetAsyncTask(objectIce,this));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_cambiar_ip:
                cuadroDialogo.setInputType(InputType.TYPE_CLASS_TEXT);
                cuadroDialogo.setText(objectIce.getNumHost());
                cuadroDialogo.setTitle("Cambiar ip");
                cuadroDialogo.setMessage("Ingrese la nueva ip");
                cuadroDialogo.show(Opcion.CAMBIAR_IP);
                return true;
            case R.id.id_ingresar_comida:
                cuadroDialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
                cuadroDialogo.setTitle("Ingresar comida al contenedor");
                cuadroDialogo.setMessage("Ingrese la cantidad en gramos");
                cuadroDialogo.show(Opcion.INGRESAR_COMIDA);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
