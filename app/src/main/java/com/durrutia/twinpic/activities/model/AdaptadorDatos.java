package com.durrutia.twinpic.activities.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.durrutia.twinpic.R;
import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.domain.Pic_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by tatan on 01-11-16.
 */
@Slf4j
public class AdaptadorDatos extends BaseAdapter{
    private Activity activity;
    private ArrayList<Pic> datosLocal;
    private ArrayList<Pic> datosRemoto;
    private String path;

    public AdaptadorDatos(Activity activity){
        this.activity=activity;
    }

    public void setDatosLocal(ArrayList<Pic> datos){
        this.datosLocal=datos;
    }

    public void setDatosRemoto(ArrayList<Pic> datos){
        this.datosRemoto=datos;
    }

    public void setPath(String path){
        this.path = path;
    }
//
    @Override
    public int getCount() {
        return datosLocal.size();
    }

    @Override
    public Object getItem(int position) {
        return datosLocal.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inf.inflate(R.layout.separador, null);
            }

            Pic imagen = this.datosLocal.get(position);
            //log.debug("primera imagen: " + String.valueOf(position));
            final ImageButton imageButtonEnviada = (ImageButton) v.findViewById(R.id.imagenEnviada);
            imageButtonEnviada.setTransitionName(String.valueOf(imagen.getId()));
            Picasso.with(this.activity)
                    .load("file://"+ this.path + File.separator + imagen.getPathFoto())
                    .resize(300, 300)
                    .centerCrop()
                    .into(imageButtonEnviada);

            imagen = this.datosRemoto.get(position);
            //log.debug("segunda imagen: " + String.valueOf(position - 1));
            final ImageButton imageButtonRecibida = (ImageButton) v.findViewById(R.id.imagenRecibida);
            imageButtonRecibida.setTransitionName(String.valueOf(imagen.getId()));
            Picasso.with(this.activity)
                    .load("file://" + this.path + File.separator + imagen.getPathFoto())
                    .resize(300, 300)
                    .centerCrop()
                    .into(imageButtonRecibida);

            //Metodo que se activa al presionar una imagen
            imageButtonEnviada.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (imageButtonEnviada.getTransitionName() != null) {
                        //log.debug("PRIMER LOG:     " + imageButtonEnviada.getTransitionName());
                        final Pic imagenSegunId = SQLite.select().from(Pic.class).where(Pic_Table.id.is(Long.valueOf(imageButtonEnviada.getTransitionName()))).querySingle();
                        //log.debug("Latitud:     " + imagenSegunId.getLatitude());
                        //log.debug("Longitud:    " + imagenSegunId.getLongitude());
                        verImagen(imagenSegunId);
                    } else {
                        log.debug("El identificador es nulo");
                    }
                }
            });


        imageButtonRecibida.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageButtonRecibida.getTransitionName() != null) {
                    //log.debug("PRIMER LOG:     " + imageButtonRecibida.getTransitionName());
                    final Pic imagenSegunId = SQLite.select().from(Pic.class).where(Pic_Table.id.is(Long.valueOf(imageButtonRecibida.getTransitionName()))).querySingle();
                    //log.debug("Latitud:     " + imagenSegunId.getLatitude());
                    //log.debug("Longitud:    " + imagenSegunId.getLongitude());
                    verImagen(imagenSegunId);
                } else {
                    log.debug("El identificador es nulo");
                }
            }
        });
            return v;

    }

    /*
    Metodod encrgado de enviar los datos de la imagen a un nuevo Activity
     */
    public void verImagen(Pic pic){
        Intent intent = new Intent(this.activity, DatosImagen.class);
        intent
                .putExtra("fecha",pic.getDate().toString())
                .putExtra("latitud",pic.getLatitude().toString())
                .putExtra("longitud",pic.getLongitude().toString())
                .putExtra("path",this.path)
                .putExtra("deviceId",pic.getDeviceId())
                .putExtra("nombre",pic.getPathFoto().toString());
        activity.startActivity(intent);
    }

}
