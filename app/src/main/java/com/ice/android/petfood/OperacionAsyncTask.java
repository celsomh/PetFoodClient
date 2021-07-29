package com.ice.android.petfood;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;

public class OperacionAsyncTask extends AsyncTask<Integer, String, String> {

    private SensorControlPrx sensor;
    IRespuestaPostOperacion iRespuestaPostOperacion;

    public OperacionAsyncTask(ObjetoIce objetoIce, IRespuestaPostOperacion iRespuestaPostOperacion) {
        objetoIce.prepararConexion();
        sensor = objetoIce.getSensor();
        this.iRespuestaPostOperacion = iRespuestaPostOperacion;
    }

    @Override
    protected String doInBackground(Integer... integers) {
        String respuesta = "";
        int metodo = integers[0];
        int dato;
        switch (metodo) {
            case Opcion.GET_WEIGHT:
                respuesta = String.valueOf(sensor.getWeight());
                break;
            case Opcion.MOTOR_TIME:
                dato = integers[1];
                int prePeso = sensor.getWeight();
                sensor.motorTime(String.valueOf(dato));
                int comidaExpendida = prePeso - sensor.getWeight();
                respuesta = String.valueOf(comidaExpendida);
                break;
            case Opcion.GIVE_FOOD:
                dato = integers[1];
                sensor.givefood(dato);
                respuesta = String.valueOf(dato);
                break;
            case Opcion.EATING_NOW:
                if (sensor.eatingNow())
                    respuesta = "true";
                else
                    respuesta = "false";
                break;
            default:
                break;
        }
        return respuesta;
    }

    @Override
    protected void onPostExecute(String respuesta) {
        iRespuestaPostOperacion.getRespuesta(respuesta);
    }
}
