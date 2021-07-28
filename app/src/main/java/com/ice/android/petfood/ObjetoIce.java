package com.ice.android.petfood;

import com.ice.android.petfood.slice.PetFoodSensors.SensorControlPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class ObjetoIce {

    private ObjectPrx objPrx;
    private String numHost;
    private String numPort;
    private String identify;
    private Communicator communicator;
    private SensorControlPrx sensor;

    public ObjetoIce(String numHost, String numPort, String identify) {
        this.numHost = numHost;
        this.numPort = numPort;
        this.identify = identify;
        communicator = Util.initialize();
    }

    public void setNumPort(String numPort) {
        this.numPort = numPort;
        prepararConexion();
    }

    public void setNumHost(String numHost) {
        this.numHost = numHost;
        prepararConexion();
    }

    public void setIdentify(String identify) {
        this.identify = identify;
        prepararConexion();
    }

    public void prepararConexion() {
        objPrx = communicator.stringToProxy(identify + ":default -h " + numHost + " -p " + numPort);
        sensor = SensorControlPrx.checkedCast(objPrx);
    }

    public String getNumHost() {
        return numHost;
    }

    public SensorControlPrx getSensor() {
        return sensor;
    }
}
