package com.ice.android.petfood;

import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class ObjectIce {

    ObjectPrx objPrx;
    String numHost;
    String numPort;
    String identify;
    Communicator communicator;
    SensorControlPrx sensor;

    public ObjectIce(String numHost, String numPort, String identify) {
        this.numHost = numHost;
        this.numPort = numPort;
        this.identify = identify;
        communicator= Util.initialize();

    }

    public void setNumPort(String numPort){
        this.numPort=numPort;
        prepararConexion();
    }

    public void setNumHost(String numHost){
        this.numHost=numHost;
        prepararConexion();
    }

    public void setIdentify(String identify){
        this.identify=identify;
        prepararConexion();
    }

    public void prepararConexion(){
        objPrx = communicator.stringToProxy(identify+":default -h "+numHost+" -p "+numPort);
        sensor = SensorControlPrx.checkedCast(objPrx);
    }

    public String getNumHost(){
        return numHost;
    }

    public SensorControlPrx getSensor(){
        return sensor;
    }

}
