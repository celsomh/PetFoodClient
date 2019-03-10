package com.ice.android.petfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class CuadroDialogo implements DialogInterface.OnClickListener {

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private EditText editTextInput;
    private int opcion;
    private InternetAsyncTask internetAsyncTask;
    private File file;
    private Context context;

    public CuadroDialogo(Context context) {
        this.context=context;
        builder = new AlertDialog.Builder(context);
        editTextInput = new EditText(context);
        builder.setView(editTextInput);
        readyBuilder();
        alertDialog = builder.create();
        file=new File();
    }

    public void setInputType(int inputType) {
        this.editTextInput.setInputType(inputType);
    }

    public void setText(String text) {
        editTextInput.setText(text);
    }

    private void readyBuilder() {
        builder.setPositiveButton("Aceptar", this);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                editTextInput.setText("");
            }
        });
    }

    public void setTitle(String title) {
        alertDialog.setTitle(title);
    }

    public void setMessage(String message) {
        alertDialog.setMessage(message);
    }

    public void show(int opcion, InternetAsyncTask internetAsyncTask) {
        this.opcion=opcion;
        this.internetAsyncTask=internetAsyncTask;
        alertDialog.show();
    }

    public void show(int opcion){
        this.opcion=opcion;
        alertDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String txt = editTextInput.getText().toString();
        if (!txt.isEmpty()) {
            switch (opcion) {
                case Opcion.MOTOR_TIME:
                    MainActivity.progressBarDispensarPorTiempo.setVisibility(View.VISIBLE);
                    internetAsyncTask.execute(Opcion.MOTOR_TIME,Integer.parseInt(txt));
                    break;
                case Opcion.GIVE_FOOD:
                    internetAsyncTask.execute(Opcion.GIVE_FOOD,Integer.parseInt(txt));
                    MainActivity.progressBarDispensarPorPeso.setVisibility(View.VISIBLE);
                    break;
                case Opcion.CAMBIAR_IP:
                    file.writeToFile(NameFile.FILE_IP, txt, context);
                    MainActivity.objectIce.setNumHost(txt);
                    MainActivity.textViewIp.setText(txt);
                    break;
                case Opcion.INGRESAR_COMIDA:
                    String txtComidaContenedor = file.readFromFile(NameFile.FILE_CONTENEDOR, context);
                    int intComidaContendor = Integer.parseInt(txtComidaContenedor);
                    if (intComidaContendor > 0) {
                        int intComidaIngresada = Integer.parseInt(txt);
                        intComidaContendor += intComidaIngresada;
                        txtComidaContenedor = String.valueOf(intComidaContendor);
                        file.writeToFile(NameFile.FILE_CONTENEDOR, txtComidaContenedor, context);
                        MainActivity.textViewComidaContenedor.setText(txtComidaContenedor);
                    } else {
                        file.writeToFile(NameFile.FILE_CONTENEDOR, txt, context);
                        MainActivity.textViewComidaContenedor.setText(txt);
                    }
                    break;
                default:
                    break;
            }
        }
        editTextInput.setText("");
    }
}