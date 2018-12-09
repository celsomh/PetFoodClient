package com.ice.android.petfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

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
    AlertDialog ad;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        nHost="localhost";
        identify="SensorControl";
        objPrx = communicator.stringToProxy(identify+":default -h "+nHost+" -p "+nPort);

        //Builder-----------------------------------------------
        builder=new AlertDialog.Builder(this);

        EditText input=new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String txt=input.getText().toString();
                String[] paramsForAsyncT;
                InternetAsyncTask AsyncT;
                switch (action) {
                    case "motorTime":
                        AsyncT = new InternetAsyncTask();
                        paramsForAsyncT = new String[]{"motorTime", txt};
                        AsyncT.execute(paramsForAsyncT);
                        break;
                    case "giveFood":
                        AsyncT = new InternetAsyncTask();
                        Toast.makeText(getApplicationContext(),"Dispensando comida...",Toast.LENGTH_SHORT).show();
                        paramsForAsyncT = new String[]{"giveFood",txt};
                        AsyncT.execute(paramsForAsyncT);
                        break;
                    default:
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        ad =builder.create();

    }
    /* Métodos Ice
        *void motorTime(string time);
        *void givefood(int weight);
        int getContainerFood();
        int getFoodEated();
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
                ad.setTitle("Dispensar por tiempo");
                ad.setMessage("Ingrese la cantidad de tiempo (segundos) que desee dispensar");
                action="motorTime";
                ad.show(); //Muestra un cuadro de diálogo y se redirige según el botón que se presione (aceptar o cancelar)
                break;
            case R.id.id_get_food_eated:
                AsyncT=new InternetAsyncTask();
                AsyncT.execute();
                break;
            case R.id.id_eating_now:
                AsyncT=new InternetAsyncTask();
                Toast.makeText(getApplicationContext(),"Realizando consulta...",Toast.LENGTH_SHORT).show();
                AsyncT.execute("eatingNow");
                break;
            case R.id.id_give_food:
                ad.setTitle("Dispensar por peso");
                ad.setMessage("Ingrese la cantidad (gramos) que desee dispensar");
                action="giveFood";
                ad.show(); //Muestra un cuadro de diálogo y se redirige según el botón que se presione (aceptar o cancelar)
                break;
            default:
                break;
        }

    }

    class InternetAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
//            SensorControlPrx sensor = SensorControlPrx.checkedCast(objPrx);
            String params=null;
            switch (strings[0]){
                case "getWeight":
//                    params=String.valueOf(sensor.getWeight());
                    action=strings[0];
                    break;
                case "motorTime":
//                    sensor.motorTime("5");
                    action=strings[0];
                    params=strings[1];
                    break;
                case "getFoodEated":
//                    sensor.getFoodEated();
                    action=strings[0];
                    break;
                case "giveFood":
//                    sensor.givefood(300);
                    action=strings[0];
                    params=strings[1];
                    break;
                case "eatingNow":
//                    if(sensor.eatingNow())
                        params="true";
//                    else
                        params="false";
                    action=strings[0];
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
                        Toast.makeText(MainActivity.this,"Peso actual: "+string,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(MainActivity.this,"La mascota está comiendo",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"La mascota no está comiendo",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }

            }

        }
    }
}
