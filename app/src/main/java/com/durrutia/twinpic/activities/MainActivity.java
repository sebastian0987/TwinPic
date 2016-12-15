package com.durrutia.twinpic.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.durrutia.twinpic.Database;
import com.durrutia.twinpic.R;
import com.durrutia.twinpic.activities.model.AdaptadorDatos;
import com.durrutia.twinpic.activities.model.GPSTracker;
import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.util.DeviceUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;

/**
 * Pagina principal.
 *
 * @author Diego P. Urrutia Astorga
 * @version 20151022
 */
@Slf4j
public class MainActivity extends ListActivity  {
    static final String ruta = "/sdcard/Android/data/dawn.disc.ucn.cl.pictwin/files/Pictures";
    String nombreFoto;
    Double latitud;
    Double longitud;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Borrar la baase de datos
        super.getApplicationContext().deleteDatabase(Database.NAME + ".db");

        //Inicializar base de datos
        FlowManager.init(new FlowConfig.Builder(this).build());

        //Se muestrasn las fotos cuando inicia la aplicacion
        //mostrarFotos();

        // Cuchillo con mantequilla !
        ButterKnife.bind(this);

        //toolbar donde se indica el titulo de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        //Boton para activar camara
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se obtienen las coordenadas
                getCordenadas();
                //Se accede a la camara
                dispatchTakePictureIntent();
            }
        });

    }

    //ocultar menu del toolbar
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            this.nombreFoto = timeStamp + ".png";

            File f = new File(this.ruta +File.separator+ this.nombreFoto);
            Uri photoURI = Uri.fromFile(f);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            //Se insertar un Pic la base de datos local
            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            //final String path = this.ruta +File.separator+ this.nombreFoto;
            insertarPic(DeviceUtils.getDeviceId(this)
                    ,String.valueOf(this.latitud)
                    ,String.valueOf(this.longitud)
                    ,fecha
                    ,0,0,0
                    ,this.nombreFoto);


            final String URL = "http://192.168.0.2";//"http://192.168.1.103";

            //Se envia un Pic al servidor
            postPic(URL);

            //Se solicita un Pic al servidor
            getPic(URL);

            //Se muestra un mensaje de espera mientras se cargan las imagenes
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Actualizando imagenes, porfavor espere...");
            progressDialog.show();

            //Se esperan 10 seg antes de cargar las imagenes
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    //Se despliegan las fotos y sus datos en el layout
                    mostrarFotos();
                }
            },10000);

        }
    }


    /*
    Metodo encargado de enviar un Pic al servidor
     */
    private void postPic(String url){

        url = url + "/insertar/pic";
        log.debug(url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();

        Bitmap bm = BitmapFactory.decodeFile(this.ruta+File.separator+this.nombreFoto);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b,Base64.DEFAULT);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("file", encodedImage);
            jsonBody.put("deviceId", DeviceUtils.getDeviceId(this));
            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            jsonBody.put("date",fecha);
            jsonBody.put("latitude",this.latitud);
            jsonBody.put("longitude",this.longitud);
            jsonBody.put("nombre",this.nombreFoto);

            final String mRequestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    log.debug(response.substring(0,2));
                    log.debug(response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
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

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
    Metodo encargado de recibir un Pic desde el servidor
     */
    private void getPic(String url){

        url = url + "/obtener/pic";

        final String path = this.ruta+File.separator;
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("deviceId", DeviceUtils.getDeviceId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //log.debug(response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            insertarPic(jsonObject.get("deviceId").toString()
                                    ,jsonObject.get("latitud").toString()
                                    ,jsonObject.get("longitud").toString()
                                    ,jsonObject.get("fecha").toString()
                                    ,0,0,0
                                    ,jsonObject.get("file").toString());
                            byte [] bytes = Base64.decode(jsonObject.get("imagen").toString(),0);
                            File file = new File(path+jsonObject.get("file"));
                            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                            fileOutputStream.write(bytes);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                log.debug("No se ha logrado establecer la conexion");
            }
        }) {
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
    Metodo encargado de insertas las fotos existentes en la base de datos desde un arreglo, este arreglo
    posteriormente es enviado a la clase adapter para ser insertadas en el layaout correspondiente
     */
    private void mostrarFotos() {

        List<Pic> picList = SQLite.select().from(Pic.class).queryList();
        final ArrayList<Pic> arrayLocal = new ArrayList<Pic>();
        final ArrayList<Pic> arrayRemoto = new ArrayList<Pic>();

        if (!picList.isEmpty()) {
            for (int j = picList.size()-1; j >= 0; j=j-2) {
                log.debug("valor en la base de datos:   " + picList.get(j).getPathFoto() + "  numero: "+String.valueOf(picList.size()));
                arrayRemoto.add(picList.get(j));
                arrayLocal.add(picList.get(j-1));
            }


            final AdaptadorDatos adaptador = new AdaptadorDatos(this);
            ListView lista = (ListView) findViewById(R.id.listaPicTwin);
            adaptador.setDatosLocal(arrayLocal);
            adaptador.setDatosRemoto(arrayRemoto);
            adaptador.setPath(this.ruta);
            lista.setAdapter(adaptador);
            //adaptador.setDatos(array);
            //adaptador.notifyDataSetChanged();
        }else{
            log.debug("La base de datos esta vacia");
        }

    }

    private void insertarPic(String deviceId,String latitud,String longitud,String fecha,
                             int positive,int negative,int warnings,String filePath){
        File archivo = new File(this.ruta);
        File [] fichero = archivo.listFiles();

        Random numeroAleatorio = new Random();

        final Pic pic = Pic.builder()
                .deviceId(deviceId)
                .latitude(latitud)
                .longitude(longitud)
                .date(fecha)
                .pathFoto(filePath)
                .fotoRecibida(this.ruta
                        + File.separator
                        + fichero[numeroAleatorio.nextInt(fichero.length)].getName())
                .positive(positive)
                .negative(negative)
                .warning(warnings)
                .build();
        pic.save();


    }

    /*
    Metodo que inicializa la clase encargada de realizar la conexion con el gps, dicha clase
    tambien es la encargada de obtener la latitud y la longitud de la ubicacion actual
     */
    private void getCordenadas(){
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            this.latitud = gps.getLatitude();
            this.longitud = gps.getLongitude();
        }else{
            log.debug("Debe iniciar el gps para continuar");
        }
        gps.stopUsingGPS();
    }

}
