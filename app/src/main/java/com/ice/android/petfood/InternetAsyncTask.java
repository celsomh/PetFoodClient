package com.ice.android.petfood;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;

import java.util.Random;

public class InternetAsyncTask extends AsyncTask<Integer,String,String> {

    private File file;
    SensorControlPrx sensor;
    Context context;

    public InternetAsyncTask(ObjectIce objectIce, Context context){
        //sensor=objectIce.getSensor();
        this.context=context;
        file=new File();
    }

    @Override
    protected String doInBackground(Integer... integers) {
        String dataForPostExecute=null;
        int metodo=integers[0];
        int dato;
        switch (metodo){
            case Opcion.GET_WEIGHT:
                dataForPostExecute=String.valueOf(sensor.getWeight());
                break;
            case Opcion.MOTOR_TIME:
                dato=integers[1];
                int prePeso=sensor.getWeight();
                sensor.motorTime(String.valueOf(dato));
                int comidaExpendida=prePeso-sensor.getWeight();
                dataForPostExecute=String.valueOf(comidaExpendida);
                break;
            case Opcion.GET_FOOD_EATED:
                dataForPostExecute=file.readFromFile(NameFile.FILE_COMIDA_CONSUMIDA,context);
                break;
            case Opcion.GIVE_FOOD:
                dato=integers[1];
                sensor.givefood(dato);
                dataForPostExecute=String.valueOf(dato);
                break;
            case Opcion.EATING_NOW:
                if(sensor.eatingNow())
                    dataForPostExecute="true";
                else
                    dataForPostExecute="false";
                break;
            default:
                break;
        }
        return String.valueOf(metodo)+" "+dataForPostExecute;
    }

    @Override
    protected void onPostExecute(String string){
        int comidaContendor;
        int comidaExpendida;
        String[] parameters=string.split(" ");
        int metodo=Integer.parseInt(parameters[0]);
        String dato=parameters[1];

        if(metodo==Opcion.MOTOR_TIME || metodo==Opcion.GIVE_FOOD){
            String txtComidaContenedor=file.readFromFile(NameFile.FILE_CONTENEDOR,context);
            comidaContendor=Integer.parseInt(txtComidaContenedor);
            comidaExpendida=Integer.parseInt(dato);
            comidaContendor-=comidaExpendida;
            if (comidaContendor<0)
                comidaContendor=0;
            txtComidaContenedor=String.valueOf(comidaContendor);
            file.writeToFile(NameFile.FILE_CONTENEDOR,txtComidaContenedor,context);
            MainActivity.textViewComidaContenedor.setText(txtComidaContenedor);

            String txtComidaConsumida=file.readFromFile(NameFile.FILE_COMIDA_CONSUMIDA,context);
            int comidaConsumida=Integer.parseInt(txtComidaConsumida)+comidaExpendida;
            file.writeToFile(NameFile.FILE_COMIDA_CONSUMIDA,String.valueOf(comidaConsumida),context);
        }

        switch (metodo) {
            case Opcion.GET_WEIGHT:
                MainActivity.textViewComidaRecipiente.setText(dato);
                break;
            case Opcion.GIVE_FOOD:
                MainActivity.progressBarDispensarPorPeso.setVisibility(View.INVISIBLE);

                break;
            case Opcion.MOTOR_TIME:
                MainActivity.progressBarDispensarPorTiempo.setVisibility(View.INVISIBLE);
                break;

            case Opcion.GET_FOOD_EATED:
                MainActivity.textViewCuantoHaComido.setText(dato);
                break;

            case Opcion.EATING_NOW:
                if (dato.equals("true"))
                    MainActivity.imageViewEstaComiendo.setImageResource(R.mipmap.yes_icon);
                else
                    MainActivity.imageViewEstaComiendo.setImageResource(R.mipmap.no_icon);
                MainActivity.progressBarEstaComiendo.setVisibility(View.INVISIBLE);
                MainActivity.imageViewEstaComiendo.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

    }
}
