package com.durrutia.twinpic.activities.model;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.durrutia.twinpic.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by tatan on 16-11-16.
 */

@Slf4j
public class DatosImagen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datosfoto);

        TextView textFecha = (TextView) findViewById(R.id.textFecha);
        TextView textLatitud = (TextView) findViewById(R.id.textLatitud);
        TextView textLongitud = (TextView) findViewById(R.id.textLongitud);
        ImageView imagen = (ImageView) findViewById(R.id.imagen);

        final Bundle bundle = getIntent().getExtras();

        textFecha.setText("Fecha:   "+bundle.getString("fecha"));
        textLatitud.setText("Latitud:   "+bundle.getString("latitud"));
        textLongitud.setText("Longitud:    "+bundle.getString("longitud"));

        final String url = "http://192.168.0.2";//"http://192.168.1.103";
        getParams(url,bundle.getString("deviceId"),bundle.getString("nombre"));

        Picasso.with(this)
                .load("file://"+bundle.getString("path")+ File.separator+bundle.getString("nombre"))
                .resize(600,600)
                .centerCrop()
                .into(imagen);

        final ImageButton imagePositive = (ImageButton) findViewById(R.id.imageButtonPositive);
        Picasso.with(this)
                .load(R.drawable.positive)
                .resize(50, 50)
                .centerCrop()
                .into(imagePositive);
        imagePositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParams(url,bundle.getString("deviceId"),bundle.getString("nombre"),"positive");
            }
        });

        final ImageButton imageNegative = (ImageButton) findViewById(R.id.imageButtonNegative);
        Picasso.with(this)
                .load(R.drawable.negative)
                .resize(50, 50)
                .centerCrop()
                .into(imageNegative);
        imageNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParams(url,bundle.getString("deviceId"),bundle.getString("nombre"),"negative");

            }
        });

    }

    /*
    Metodo que realiza la conexion con el servidor, mas especificamente realiza la conexion
    con el encargado de sumarle +1 al positive o al negative segun corresponda
     */
    private void addParams(String url, String device, String nombre, final String tipo){

        url = url + "/insertar/"+tipo;
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("deviceId", device);
            jsonBody.put("nombre", nombre);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String mRequestBody = jsonBody.toString();

        final TextView textPositive = (TextView) findViewById(R.id.textPositive);
        final TextView textNegaTive = (TextView) findViewById(R.id.textNegative);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        log.debug(tipo+"->"+response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            textPositive.setText(jsonObject.getString("positive"));
                            textNegaTive.setText(jsonObject.getString("negative"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                log.debug("No se ha logrado establecer la conexion");
            }

        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }


    /*
    Metodo que obtienes el estado actual del positive y del negative
     */
    private void getParams(String url,String device, String nombre){
        url = url + "/obtener/parametros";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("deviceId", device);
            jsonBody.put("nombre", nombre);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String mRequestBody = jsonBody.toString();

        final TextView textPositive = (TextView) findViewById(R.id.textPositive);
        final TextView textNegaTive = (TextView) findViewById(R.id.textNegative);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        log.debug("OBTENERPARAMS->"+response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            textPositive.setText(jsonObject.getString("positive"));
                            textNegaTive.setText(jsonObject.getString("negative"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                log.debug("No se ha logrado establecer la conexion");
            }

        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }

    public void finalizar(View view) {
        finish();
    }
}
