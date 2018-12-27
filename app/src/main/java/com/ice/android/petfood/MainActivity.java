package com.ice.android.petfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

    EditText input;
    TextView textViewIp;
    TextView textViewComidaRestante;
    ImageView imageViewEstaComiendo;
    ProgressBar progressBarEstaComiendo;

    String nHost;
    String nPort;
    String identify;
    AlertDialog adNumber;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        String ip=readFromFile(this);

        if (ip!=null) {
            nHost = ip;
        }else{
            writeToFile("localhost",this);
            nHost="localhost";
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
                            AsyncT = new InternetAsyncTask();
                            paramsForAsyncT = new String[]{"motorTime", txt};
                            AsyncT.execute(paramsForAsyncT);
                            break;
                        case "giveFood":
                            AsyncT = new InternetAsyncTask();
                            Toast.makeText(getApplicationContext(), "Dispensando comida...", Toast.LENGTH_SHORT).show();
                            paramsForAsyncT = new String[]{"giveFood", txt};
                            AsyncT.execute(paramsForAsyncT);
                            break;
                        case "cambiar ip":
                            nHost=txt;
                            objPrx = communicator.stringToProxy(identify+":default -h "+nHost+" -p "+nPort);
                            writeToFile(txt,MainActivity.this);
                            textViewIp.setText(nHost);
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

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("Number_ip.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
        }
    }

    private String readFromFile(Context context) {

        String ret = null;

        try {
            InputStream inputStream = context.openFileInput("Number_ip.txt");

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

    /* Métodos Ice
        *void motorTime(string time);
        *void givefood(int weight);
        int getContainerFood();
        *int getFoodEated();
        *bool eatingNow();
        *int getWeight();
    */
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
                adNumber.setMessage("Ingrese el numero de segundos");
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class InternetAsyncTask extends AsyncTask<String,Void,String> {
        //SensorControlPrx sensor = SensorControlPrx.checkedCast(objPrx);
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
//                    sensor.motorTime(strings[1]);
                    action=strings[0];
                    break;
                case "getFoodEated":
 //                   sensor.getFoodEated();
                    action=strings[0];
                    break;
                case "giveFood":
 //                   sensor.givefood(Integer.parseInt(strings[1]));
                    action=strings[0];
                    break;
                case "eatingNow":
                    try {
                        Thread.sleep(5000);
                    } catch(InterruptedException e) {}
 //                   if(sensor.eatingNow())
//                        params="true"
//                    else
                    Random dado=new Random();
                    if (dado.nextInt(2)==1)
                        params="false";
                    else
                        params="true";
                    action=strings[0];
                    break;
                default:
                    break;
            }
            return params;
        }
        //TODO: Guardar datos en JSON. Los datos deben ser dia, cantidad expendida, cantidad de comida en recipiente y cantidad de comida en contenedor
        @Override
        protected void onPostExecute(String string){
            if (string==null){
                Toast.makeText(MainActivity.this,"No se realizó ninguna acción",Toast.LENGTH_LONG).show();
            }else{
                switch (action) {
                    case "getWeight":
                        textViewComidaRestante.setText(string+" gr");
                        break;
                    case "giveFood":
                        Toast.makeText(MainActivity.this,"Comida dispensada "+"("+string+")",Toast.LENGTH_LONG).show();
                        break;
                    case "motorTime":
                        Toast.makeText(MainActivity.this,"Funcionamiento de motor finalizado ("+string+")",Toast.LENGTH_LONG).show();
                        break;
                    case "getFoodEated":
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
}
