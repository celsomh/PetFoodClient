package com.ice.android.petfood;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnObtenerPeso;
    Button btnMotorTime;
    Button btnGetFoodEated;
    Button btnEatingNow;
    Button btnGiveFood;

    static String action;
    Communicator communicator;
    ObjectPrx objPrx;
    String nHost;
    String nPort;
    String identify;

    TextView textViewCuantoHaComido;
    TextView textViewComidaContenedor;
    EditText input;
    TextView textViewIp;
    TextView textViewComidaRestante;
    ImageView imageViewEstaComiendo;
    ProgressBar progressBarEstaComiendo;
    ProgressBar progressBarDispensarPorTiempo;
    ProgressBar progressBarDispensarPorPeso;
    ScrollView scrollView;

    String nameFileIP;
    String nameFileComidaContenedor;
    AlertDialog adNumber;
    AlertDialog.Builder builder;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCuantoHaComido=findViewById(R.id.id_text_view_cuanto_ha_comido);

        textViewComidaContenedor=findViewById(R.id.id_get_container_food);

        nameFileIP="Number_ip.txt";
        nameFileComidaContenedor="Comida_contenedor.txt";

        scrollView=findViewById(R.id.id_scroll_view);

        progressBarDispensarPorPeso=findViewById(R.id.id_progress_bar_dispensar_por_peso);
        progressBarDispensarPorPeso.setVisibility(View.INVISIBLE);

        progressBarDispensarPorTiempo=findViewById(R.id.id_progress_bar_dispensar_por_tiempo);
        progressBarDispensarPorTiempo.setProgress(0);
        progressBarDispensarPorTiempo.setVisibility(View.INVISIBLE);

        progressBarEstaComiendo=findViewById(R.id.id_progress_bar_esta_comiendo);
        progressBarEstaComiendo.setVisibility(View.INVISIBLE);

        imageViewEstaComiendo=findViewById(R.id.id_image_view_esta_comiendo);

        textViewIp=findViewById(R.id.id_text_view_ip);
        textViewComidaRestante=findViewById(R.id.id_text_view_comida_restante);

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

        communicator=Util.initialize();
        nPort="10000";
        String ip=readFromFile(nameFileIP,this);
        String comidaContenedor=readFromFile(nameFileComidaContenedor,this);

        if (ip!=null) {
            nHost = ip;
        }else{
            writeToFile(nameFileIP,"localhost",this);
            nHost="localhost";
        }

        if (comidaContenedor!=null){
            comidaContenedor+=" gr";
            textViewComidaContenedor.setText(comidaContenedor);
        }else{
            writeToFile(nameFileComidaContenedor,"0",this);
            textViewComidaContenedor.setText("0 gr");
        }

        identify="SensorControl";
        objPrx = communicator.stringToProxy(identify+":default -h "+nHost+" -p "+nPort);

        textViewIp.setText(nHost);

        //Builder-----------------------------------------------
        builder=new AlertDialog.Builder(this);

        input=new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String txt=input.getText().toString();
                String[] paramsForAsyncT;
                InternetAsyncTask AsyncT;
                if (!txt.isEmpty()) {
                    switch (action) {
                        case "motorTime":
                            progressBarDispensarPorTiempo.setVisibility(View.VISIBLE);
                            progressBarDispensarPorTiempo.setMax(Integer.parseInt(txt));
                            InternetAsyncTask AsyncT2;
                            AsyncT2 =new InternetAsyncTask();
                            AsyncT2.execute("motorTime");
                            paramsForAsyncT=new String[]{"chargeProgressBar",txt};
                            new InternetAsyncTask().execute(paramsForAsyncT);
                            break;
                        case "giveFood":
                            progressBarDispensarPorPeso.setVisibility(View.VISIBLE);
                            AsyncT = new InternetAsyncTask();
                            paramsForAsyncT = new String[]{"giveFood", txt};
                            AsyncT.execute(paramsForAsyncT);
                            break;
                        case "cambiar ip":
                            nHost=txt;
                            objPrx = communicator.stringToProxy(identify+":default -h "+nHost+" -p "+nPort);
                            writeToFile(nameFileIP,txt,MainActivity.this);
                            textViewIp.setText(nHost);
                            break;
                        case "getContainerFood":
                            String txtComidaContenedor=readFromFile(nameFileComidaContenedor,MainActivity.this);
                            int intComidaContendor=Integer.parseInt(txtComidaContenedor);
                            if (intComidaContendor>0){
                                int intComidaIngresada=Integer.parseInt(txt);
                                intComidaContendor+=intComidaIngresada;
                                txtComidaContenedor=String.valueOf(intComidaContendor);
                                writeToFile(nameFileComidaContenedor,txtComidaContenedor,MainActivity.this);
                                txtComidaContenedor+=" gr";
                                textViewComidaContenedor.setText(txtComidaContenedor);
                            }else{
                                writeToFile(nameFileComidaContenedor,txt,MainActivity.this);
                                txt+=" gr";
                                textViewComidaContenedor.setText(txt);
                            }

                            break;
                        default:
                            break;
                    }
                }

            }

        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        adNumber =builder.create();

    }

    private void writeToFile(String nameFile,String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(nameFile, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
        }
    }

    private String readFromFile(String nameFile, Context context) {

        String ret = null;

        try {
            InputStream inputStream = context.openFileInput(nameFile);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}

        return ret;
    }

    @Override
    public void onClick(View v) {
        InternetAsyncTask AsyncT;
        switch(v.getId()){
            case R.id.id_get_weight:
                AsyncT=new InternetAsyncTask();
                AsyncT.execute("getWeight");
                break;
            case R.id.id_motor_time:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText("");
                adNumber.setTitle("Dispensar por tiempo");
                adNumber.setMessage("Ingrese el tiempo en segundos");
                action="motorTime";
                adNumber.show(); //Muestra un cuadro de diálogo y se redirige según el botón que se presione (aceptar o cancelar)
                break;
            case R.id.id_get_food_eated:
                AsyncT=new InternetAsyncTask();
                AsyncT.execute();
                break;
            case R.id.id_eating_now:
                AsyncT=new InternetAsyncTask();
                imageViewEstaComiendo.setVisibility(View.INVISIBLE);
                progressBarEstaComiendo.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Realizando consulta...",Toast.LENGTH_SHORT).show();
                AsyncT.execute("eatingNow");
                break;
            case R.id.id_give_food:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                scrollView.fullScroll(View.FOCUS_DOWN);
                input.setText("");
                adNumber.setTitle("Dispensar por peso");
                adNumber.setMessage("Ingrese la cantidad en gramos");
                action="giveFood";
                adNumber.show(); //Muestra un cuadro de diálogo y se redirige según el botón que se presione (aceptar o cancelar)
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
            case R.id.id_opcion_cambiar_ip:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(nHost);
                action="cambiar ip";
                adNumber.setTitle("Cambiar ip");
                adNumber.setMessage("Ingrese la nueva ip");
                adNumber.show(); //Muestra un cuadro de diálogo y se redirige según el botón que se presione (aceptar o cancelar)
                return true;
            case R.id.id_opcion_ingresar_comida_al_contenedor:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText("");
                action="getContainerFood";
                adNumber.setTitle("Ingresar comida al contenedor");
                adNumber.setMessage("Ingrese la cantidad en gramos");
                adNumber.show(); //Muestra un cuadro de diálogo y se redirige según el botón que se presione (aceptar o cancelar)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class InternetAsyncTask extends AsyncTask<String,Integer,String> {
//        SensorControlPrx sensor = SensorControlPrx.checkedCast(objPrx);
        @Override
        protected String doInBackground(String... strings) {
            String params=null;
            switch (strings[0]){
                case "getWeight":
                    //params=String.valueOf(sensor.getWeight());
                    params="123";
                    action=strings[0];
                    break;
                case "motorTime":
 //                   int prePeso=sensor.getWeight();
//                    sensor.motorTime(strings[1]);
//                    int postPeso=sensor.getWeight();
 //                   int comidaExpendida=postPeso-prePeso;
//                    params=String.valueOf(comidaExpendida);

                    params="100";
                    action=strings[0];
                    break;
                case "chargeProgressBar":
                    int max = Integer.parseInt(strings[1]);
                    for (int i = 1; i <= max; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        publishProgress(i);
                    }
                    action=strings[0];
                    break;
                case "getFoodEated":
 //                   sensor.getFoodEated();
                    action=strings[0];
                    break;
                case "giveFood":
 //                   sensor.givefood(Integer.parseInt(strings[1]));
                    action=strings[0];
                    params=strings[1];
                    break;
                case "eatingNow":
//                   if(sensor.eatingNow())
//                        params="true"
//                    else
//                        params="true";
                    action=strings[0];
                    break;
                default:
                    break;
            }
            return params;
        }

        @Override
        protected void onProgressUpdate(Integer... integers){
            progressBarDispensarPorTiempo.setProgress(integers[0]);
        }

        @Override
        protected void onPostExecute(String string){
            String txtComidaContenedor;
            int intComidaContendor;
            int intComidaExpendida;
            switch (action) {
                case "getWeight":
                    textViewComidaRestante.setText(string+" gr");
                    break;
                case "giveFood":
                    txtComidaContenedor=readFromFile(nameFileComidaContenedor,MainActivity.this);
                    intComidaContendor=Integer.parseInt(txtComidaContenedor);
                    intComidaExpendida=Integer.parseInt(string);
                    intComidaContendor-=intComidaExpendida;
                    if (intComidaContendor<0){
                        intComidaContendor=0;
                    }
                    txtComidaContenedor=String.valueOf(intComidaContendor);
                    writeToFile(nameFileComidaContenedor,txtComidaContenedor,MainActivity.this);
                    txtComidaContenedor+=" gr";
                    textViewComidaContenedor.setText(txtComidaContenedor);
                    progressBarDispensarPorPeso.setVisibility(View.INVISIBLE);
                    break;
                case "motorTime":
                    txtComidaContenedor=readFromFile(nameFileComidaContenedor,MainActivity.this);
                    intComidaContendor=Integer.parseInt(txtComidaContenedor);
                    intComidaExpendida=Integer.parseInt(string);
                    intComidaContendor-=intComidaExpendida;
                    if (intComidaContendor<0){
                        intComidaContendor=0;
                    }
                    txtComidaContenedor=String.valueOf(intComidaContendor);
                    writeToFile(nameFileComidaContenedor,txtComidaContenedor,MainActivity.this);
                    txtComidaContenedor+=" gr";
                    textViewComidaContenedor.setText(txtComidaContenedor);
                    break;
                case "chargeProgressBar":
                    progressBarDispensarPorTiempo.setVisibility(View.INVISIBLE);
                    progressBarDispensarPorTiempo.setProgress(0);
                    break;
                case "getFoodEated":
                    textViewCuantoHaComido.setText(string);
                    Toast.makeText(MainActivity.this,"",Toast.LENGTH_LONG).show();
                    break;
                case "eatingNow":
                    if (string.equals("true"))
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
}
