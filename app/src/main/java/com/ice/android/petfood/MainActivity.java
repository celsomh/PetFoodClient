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

        communicator=Util.initialize();
        nPort="10000";
        nHost="";
        identify="SimpleMotor";
        objPrx = communicator.stringToProxy(identify+":tcp -h "+nHost+" -p "+nPort);

    }

    @Override
    public void onClick(View v) {
        InternetAsyncTask AsyncT=new InternetAsyncTask();
        switch(v.getId()){
            case R.id.id_get_weight:
                AsyncT.execute("getWeight");
            case R.id.id_on_motor:
                AsyncT.execute("motorTime");
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
                    break;
                case "motorTime":
                    sensor.motorTime("2");
                    break;
            }
            return params;
        }

        @Override
        protected void onPostExecute(String string){
            if (string==null){
                Toast.makeText(MainActivity.this,"No se obtuvo ningun resultado",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"Resultado: "+string,Toast.LENGTH_SHORT).show();
            }

        }
    }
}
