package com.ice.android.petfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class CuadroDialogo implements DialogInterface.OnClickListener {

    private AlertDialog alertDialog;
    private AlertDialog.Builder builderAlertDialog;
    private EditText etEntradaTexto;
    private ICuadroDialogo iCuadroDialogo;

    public CuadroDialogo(ICuadroDialogo iCuadroDialogo, Context context) {
        this.iCuadroDialogo = iCuadroDialogo;
        builderAlertDialog = new AlertDialog.Builder(context);
        etEntradaTexto = new EditText(context);
        builderAlertDialog.setView(etEntradaTexto);
        readyBuilder();
        alertDialog = builderAlertDialog.create();
    }

    public void setInputType(int inputType) {
        this.etEntradaTexto.setInputType(inputType);
    }

    public void setText(String text) {
        etEntradaTexto.setText(text);
    }

    private void readyBuilder() {
        builderAlertDialog.setPositiveButton("Aceptar", this);
        builderAlertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                etEntradaTexto.setText("");
            }
        });
    }

    public void show() {
        alertDialog.setTitle(iCuadroDialogo.getTitulo());
        alertDialog.setMessage(iCuadroDialogo.getMensaje());
        alertDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String respuesta = etEntradaTexto.getText().toString();
        if (!respuesta.isEmpty()) {
            iCuadroDialogo.setRespuesta(respuesta);
        }
        etEntradaTexto.setText("");
    }
}