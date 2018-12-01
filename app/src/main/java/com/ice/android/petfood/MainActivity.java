package com.ice.android.petfood;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnObtenerPeso;
    Button btnMotorTime;
    static String action;
    Communicator communicator;
    ObjectPrx objPrx;

    String nHost;
    String nPort;
    String identify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnObtenerPeso=findViewById(R.id.id_get_weight);
        btnObtenerPeso.setOnClickListener(this);

        btnMotorTime=findViewById(R.id.id_motor_time);
        btnMotorTime.setOnClickListener(this);

        communicator=Util.initialize();
        nPort="10000";
        nHost="192.168.101.46";
        identify="SensorControl";
        objPrx = communicator.stringToProxy(identify+":default -h "+nHost+" -p "+nPort);

    }
    /*

    void givefood(int weight);
    int getContainerFood();
    int getFoodEated();
    bool eatingNow();

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
                AsyncT=new InternetAsyncTask();
                AsyncT.execute("motorTime");
                break;
            default:
                break;
        }

    }

    class InternetAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            SensorControlPrx sensor = SensorControlPrx.checkedCast(objPrx);
            String params=null;
            switch (strings[0]){
                case "getWeight":
                    params=String.valueOf(sensor.getWeight());
                    action=strings[0];
                    break;
            }
            return params;
        }

        @Override
        protected void onPostExecute(String string){
            if (string==null){
                Toast.makeText(MainActivity.this,"No se realizó ninguna acción",Toast.LENGTH_SHORT).show();
            }else{
                switch (action) {
                    case "getWeight":
                        Toast.makeText(MainActivity.this,"Peso actual: "+string,Toast.LENGTH_SHORT).show();
                        break;
                    case "giveFood":
                        Toast.makeText(MainActivity.this,"Comida dispensada"+string,Toast.LENGTH_SHORT).show();
                        break;
                    case "motorTime":
                        Toast.makeText(MainActivity.this,"Funcionamiento de motor finalizado"+string,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }

        }
    }
}
