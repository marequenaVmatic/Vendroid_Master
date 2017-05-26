package com.vendomatica.vendroid.net;
/*
This file has connection function between android app and backend online webservice.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.vendomatica.vendroid.Model.LogFile;
import com.vendomatica.vendroid.Model.PlantillaTipoTarea;
import com.vendomatica.vendroid.Model.TablasMaestras;
import com.vendomatica.vendroid.Model.Tarea;
import com.vendomatica.vendroid.Model.TareaDetalle;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.Model.logEvents;
import com.vendomatica.vendroid.db.DBManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.httpclient.HttpStatus;

public class NetworkManager {
    protected final static String UTF8 = "utf-8";
    protected Map<String, Object> _reqParams = null;

    private NetworkManager() {
        _reqParams = new LinkedHashMap<String, Object>();
    }
    private static NetworkManager s_instance = null;


    public static NetworkManager getManager() {
        if (s_instance == null) {
            s_instance = new NetworkManager();
        }
        synchronized (s_instance) {
            return s_instance;
        }
    }
    private final static String SERVER_URL = " ";
    //produccion
    private final static String DOMAIN = "http://api.vendomatica.cl:61/";


    //local desarrollo
    //private final static String DOMAIN = "http://192.168.254.208/";
    //private final static String DOMAIN = "http://192.168.254.127/";

    ////servidor desarrollo
    //private final static String DOMAIN = "http://api-desa.vendomatica.cl:68/";

    protected final static String URL_ACTIVE 		    = DOMAIN + "apivendo/tickets/GetActive";
    protected final static String URL_LOGIN 		    = DOMAIN + "apivendo/pdg/AuthPdg/";
    protected final static String URL_GETTAREAS 		    = DOMAIN + "apivendo/tickets/GetTickets";
    protected final static String  URL_GETTABLAS            = DOMAIN + "apivendo/tickets/GetTabla";
    protected final static String  URL_GETPLANTILLAS            = DOMAIN + "apivendo/tickets/getplantilla";
    protected final static String  URL_POSTTAREA            = DOMAIN + "apivendo/tickets/UpdateTarea";
    protected final static String   URL_LOGEVENT             = DOMAIN + "apivendo/tickets/UploadLog";
    public String URL_UPLOADMROFILE     	    = DOMAIN + "apivendo/tickets/UploadMROImage";

    protected final static String URL_GETTICKETS 		    = DOMAIN + "/apivendo/tickets/GetTickets";
    protected final static String URL_GETESTADOS 		    = DOMAIN + "apivendo/tickets/GetEstados";
    protected final static String URL_GETCLASIFICACION	    = DOMAIN + "apivendo/tickets/GetClasificacion";
    protected final static String URL_GETFALLAS     	    = DOMAIN + "apivendo/tickets/GetFallas";
    protected final static String URL_GETSAVEBITACORA     	    = DOMAIN + "apivendo/tickets/saveBitacora";



    protected final static String URL_UPLOAD_FILE 		    = DOMAIN + "uploadfile.aspx";

    protected final static String   URL_LOGFILE    = DOMAIN + "logevent.aspx";




    private String filePath = "";
    private DownloadThread dThread;

    //This function receive the report information about the userid from the online database throughout the webservice
    //The report information is saved at Common.getInstance().arrReports variables.



    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    //This function receive the login information about the userid from the online database throughout the webservice when the user login to the app.
    //The login information is saved at LoginUser class
    public User login(String strUserID, String strPassword) {
        String myResult;
        try {
            User userdat = new User();

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost =new HttpPost(URL_LOGIN);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("userid", strUserID);
            jsonObject.accumulate("pwd", strPassword);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httppost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httppost);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                JSONObject jsonresul = new JSONObject(result);
                userdat = new User(jsonresul);
                return userdat;
                //DBManager.getManager().insertUser(Usuario);
            }
            return userdat;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    //This function receive the login information about the userid from the online database throughout the webservice when the user login to the app.
    //The login information is saved at LoginUser class
    public User loginLocal(String strUserID, String strPassword) {
        String myResult;
            User userdat = new User();
            userdat = DBManager.getManager().getUserLogin(strUserID,strPassword);
            return userdat;
    }

    public boolean UploadTarea(Tarea task) {
        String myResult;
        try {
            User userdat = new User();

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost =new HttpPost(URL_POSTTAREA);

            String json = "";
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("idTarea",task.idTarea);
            jsonObject.accumulate("idTipoTarea",task.idTipoTarea);
            jsonObject.accumulate("idSubTipoTarea",task.idSubTipoTarea);
            jsonObject.accumulate("FechaInicio",formato.format(task.FechaInicio.getTime()));
            jsonObject.accumulate("HoraInicio",formato.format(task.HoraInicio.getTime()));
            jsonObject.accumulate("FechaFin",task.FechaFin);
            jsonObject.accumulate("FechaInfo",task.FechaInfo);
            jsonObject.accumulate("TiempoEstimado",task.TiempoEstimado);
            jsonObject.accumulate("idUsuario",task.idUsuario);
            jsonObject.accumulate("id_doc",task.id_doc);
            jsonObject.accumulate("idResolucion",task.idResolucion);
            jsonObject.accumulate("FechaHoraSinc",task.FechaHoraSinc);
            jsonObject.accumulate("NoRealizado",task.NoRealizado);
            jsonObject.accumulate("DescNoRealizado",task.DescNoRealizado);
            jsonObject.accumulate("Comentario",task.comentario);
            JSONObject jsonObjectDets = new JSONObject();
            if(task.wkf)
                for (TareaDetalle DT : task.Detalle) {
                    JSONObject jsonObjectDet = new JSONObject();
                    if(DT.idCampo.equals("CodFalla") || DT.idCampo.equals("id_wkf") || DT.idCampo.equals("id_estado")
                            || DT.idCampo.equals("id_usuario") || DT.idCampo.equals("id_doc") || DT.idCampo.equals("id_resolucion") ||  DT.idCampo.equals("id_bitacora")) {
                        jsonObjectDet.accumulate("idTarea", task.idTarea);
                        jsonObjectDet.accumulate("idCampo", DT.idCampo);
                        jsonObjectDet.accumulate("ValorCampo", DT.ValorCampo);
                        jsonObject.accumulate("Detalle", jsonObjectDet);
                    }
                }
            else
                for (TareaDetalle DT : task.Detalle) {
                    JSONObject jsonObjectDet = new JSONObject();
                        jsonObjectDet.accumulate("idTarea", task.idTarea);
                        jsonObjectDet.accumulate("idCampo", DT.idCampo);
                        jsonObjectDet.accumulate("ValorCampo", DT.ValorCampo);
                        jsonObject.accumulate("Detalle", jsonObjectDet);
                }
            //jsonObject.accumulate("Detalle",jsonObjectDets);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();


            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httppost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httppost);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                JSONObject jsonresul = new JSONObject(result);
                DBManager.getManager().setUpdateTareaSincro(task);

                //DBManager.getManager().insertUser(Usuario);
            }
            return true;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Load tareas information of Tareas class
    public int loadTareas(ArrayList<Tarea> arrTasks,User muser)
    {
        String myResult;
        try {
            arrTasks.addAll(getTareasService(muser.userid));
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean getActive() {
        boolean active = false;
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(URL_ACTIVE);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpget);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

                if(result.equals("true"))
                    active=true;
                //listTickets = jsonresul;
            }

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        }
        return active;
    }

    public ArrayList<Tarea> getTareasService(int mUserID) {
        ArrayList<Tarea> listTareas = new ArrayList<Tarea>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("iduser",String.valueOf(mUserID)));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            HttpGet httpget = new HttpGet(URL_GETTAREAS+ "?" + paramsString);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpget);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

                JSONArray json = new JSONArray(result);
                for(int i=0; i<json.length(); i++) {
                    JSONObject valor = json.getJSONObject(i);
                    Tarea mtarea = new Tarea(valor);
                    listTareas.add(mtarea);
                }
                //listTickets = jsonresul;
            }

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listTareas;
    }


    //Load tablas gneerales of Tareas class
    public int loadTablas(ArrayList<TablasMaestras> arrTablas)
    {
        String myResult;
        try {
            arrTablas.addAll(getTablas());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }



    public ArrayList<TablasMaestras> getTablas() {
        ArrayList<TablasMaestras> listTareas = new ArrayList<TablasMaestras>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(URL_GETTABLAS);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpget);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

                JSONArray json = new JSONArray(result);
                for(int i=0; i<json.length(); i++) {
                    JSONObject valor = json.getJSONObject(i);
                    TablasMaestras mtabla = new TablasMaestras(valor);
                    listTareas.add(mtabla);
                }
                //listTickets = jsonresul;
            }

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listTareas;
    }

    //Load plantillas information of Tareas class
    public int loadPlantillas(ArrayList<PlantillaTipoTarea> arrPlantillas)
    {
        String myResult;
        try {
            arrPlantillas.addAll(getPlnatillas());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList<PlantillaTipoTarea> getPlnatillas() {
        ArrayList<PlantillaTipoTarea> listPlantillas = new ArrayList<PlantillaTipoTarea>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(URL_GETPLANTILLAS);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpget);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

                JSONArray json = new JSONArray(result);
                for(int i=0; i<json.length(); i++) {
                    JSONObject valor = json.getJSONObject(i);
                    PlantillaTipoTarea mtabla = new PlantillaTipoTarea(valor);
                    listPlantillas.add(mtabla);
                }
                //listTickets = jsonresul;
            }

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listPlantillas;
    }


    public List<Tickets> getTicket(String strUserID) {
        List<Tickets> listTickets = new ArrayList<Tickets>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("iduser",strUserID ));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            HttpGet httpget = new HttpGet(URL_GETTICKETS+ "?" + paramsString);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpget);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

                    JSONArray json = new JSONArray(result);
                for(int i=0; i<json.length(); i++) {
                    JSONObject valor = json.getJSONObject(i);
                    Tickets newTicket = new Tickets(valor.getInt("id_bitacora"),valor.getInt("id_wkf"),valor.getInt("id_doc"),valor.getInt("id_linea"),valor.getInt("id_estado"),valor.getInt("id_usuario_estado"),valor.getString("id_resolucion"),valor.getString("fecha_estado"),valor.getString("CodCliente"),valor.getString("SerieMaquina"),valor.getString("Direccion"),valor.getString("Ubicacion"),valor.getString("Fecha"),valor.getInt("ID_User"),valor.getString("RazonSocial"),valor.getString("Ciudad"),valor.getString("Comuna"),valor.getString("TipoMaquina"),valor.getString("TipoNegocio"),valor.getString("ModeloMaquina"),valor.getString("LlaveMaquina"),valor.getString("CodFalla"),valor.getString("ContactoLlamada"),valor.getString("ContactoFono"),valor.getString("ContactoEmail"),valor.getString("Devolucion"),valor.getString("DevolucionMonto"),valor.getString("Observacion"),valor.getInt("ID_IncidenciaCliente"),valor.getString("RutaAbastecimiento"),valor.getString("RutaTecnica"),valor.getString("SeriePrint"),valor.getString("EstadoInicial"),valor.getString("TipoLlamada"),valor.getString("LlavePuertaMaquina"),valor.getString("SerieBilletero"),valor.getString("SerieMonedero"),valor.getString("SerieLectorChip"),valor.getString("CodEstadoCliente"),valor.getString("RutaTecnicaInicial"),valor.getString("Rut"),valor.getString("Ejecutivo_historico"),valor.getString("latitud"),valor.getString("longitud"),valor.getString("Garantia"),valor.getString("id_cliente_interno"),valor.getString("DiagnosticoNV3"),valor.getString("NroTarjetaBip"),valor.getString("ConsultaBip"),valor.getString("OrigenLlamada"),valor.getString("TipoDevolucion"),valor.getString("Falla"));
                    listTickets.add(newTicket);
                }
                //listTickets = jsonresul;
            }

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listTickets;
    }




//    //save bitacora webservice
//    public int saveBitacora(String codFalla, int id_wkf, int id_estado, int id_usuario, int id_doc, int id_resolucion, int bp1, int bp2, String comentarios, int id_archivo, String id_bitacora, String latitud, String longitud) {
//
//        try {
//
//            InputStream inputStream = null;
//            String result = "";
//            // create HttpClient
//            HttpClient httpclient = new DefaultHttpClient();
//
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//            nameValuePairs.add(new BasicNameValuePair("codFalla",String.valueOf(codFalla)));
//            nameValuePairs.add(new BasicNameValuePair("id_wkf",String.valueOf(id_wkf)));
//            nameValuePairs.add(new BasicNameValuePair("id_estado",String.valueOf(id_estado)));
//            nameValuePairs.add(new BasicNameValuePair("id_usuario",String.valueOf(id_usuario)));
//            nameValuePairs.add(new BasicNameValuePair("id_doc",String.valueOf(id_doc)));
//            nameValuePairs.add(new BasicNameValuePair("id_resolucion",String.valueOf(id_resolucion)));
//            nameValuePairs.add(new BasicNameValuePair("bp1",String.valueOf(bp1)));
//            nameValuePairs.add(new BasicNameValuePair("bp2",String.valueOf(bp2)));
//            nameValuePairs.add(new BasicNameValuePair("comentarios",String.valueOf(comentarios)));
//            nameValuePairs.add(new BasicNameValuePair("id_archivo",String.valueOf(id_archivo)));
//            nameValuePairs.add(new BasicNameValuePair("id_bitacora",String.valueOf(id_bitacora)));
//            nameValuePairs.add(new BasicNameValuePair("latitud",String.valueOf(latitud)));
//            nameValuePairs.add(new BasicNameValuePair("longitud",String.valueOf(longitud)));
//            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
//            HttpGet httpget = new HttpGet(URL_GETSAVEBITACORA+ "?" + paramsString);
//
//            // make POST request to the given URL
//            HttpResponse httpResponse = httpclient.execute(httpget);
//
//            // receive response as inputStream
//            inputStream = httpResponse.getEntity().getContent();
//
//            // convert inputstream to string
//            if(inputStream != null) {
//                result = convertInputStreamToString(inputStream);
//
//                if(Integer.parseInt(result)>0){
//                    for(int i=0; i<Common.getInstance().arrTickets.size(); i++) {
//                        if(Common.getInstance().arrTickets.get(i).id_bitacora==Integer.parseInt(id_bitacora)){
//                            Common.getInstance().arrTicketsCerrados.clear();;
//                            Common.getInstance().arrTicketsCerrados.addAll(DBManager.getManager().getAllticketsCerrados());
//                            Tickets tk = Common.getInstance().arrTickets.get(i);
//                            tk.Observacion = comentarios;
//                            Common.getInstance().arrTicketsCerrados.add(tk);
//                            DBManager.getManager().deleteAllticketsCerrados();
//                            DBManager.getManager().insertTicketsCerrados(Common.getInstance().arrTicketsCerrados);
//                            Common.getInstance().arrTickets.remove(Common.getInstance().arrTickets.get(i));
//                            DBManager.getManager().deleteAlltickets();
//                            DBManager.getManager().insertTickets(Common.getInstance().arrTickets);
//                        }
//                    }
//
//                }
//
//                //listTickets = jsonresul;
//            }
//
//        } catch (MalformedURLException e) {
//            //
//        } catch (IOException e) {
//            e.printStackTrace();
//            //
//        }
//        return 1;
//    }
    //This function Load Array Public from the online database throught the webservice.
    public int loadPublicArray(ArrayList<Tickets> arrTasks)
    {
        String myResult;
        try {
            //arrTasks.addAll(getTicket(Common.getInstance().getLoginUser().getUserId()));
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }




    //This function download the image about the each completetasked data and save the file path of the android sdcard from the online database throught the webservice.
    void download(String path) {
        String[] path_array = path.split("/");
        String FileName = path_array[path_array.length - 1];

        String save_path = Environment.getExternalStorageDirectory() + "/staffapp";

        File dir = new File(save_path);
        if (!dir.exists())
            dir.mkdir();
        save_path += "/" + FileName;
        filePath = save_path;
        if (new File(save_path).exists() == false) {
            //loading.setVisibility(View.VISIBLE);
            dThread = new DownloadThread(path, save_path);
            dThread.start();
        }
    }
    // This is the thread in order to download the image file.
    class DownloadThread extends Thread {
        String ServerUrl;
        String LocalPath;

        DownloadThread(String serverPath, String localPath) {
            ServerUrl = serverPath;
            LocalPath = localPath;
        }

        @Override
        public void run() {
            URL videourl;
            int Read;
            try {
                videourl = new URL(ServerUrl);
                HttpURLConnection conn = (HttpURLConnection) videourl
                        .openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];
                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                FileOutputStream fos = new FileOutputStream(file);
                for (;;) {
                    Read = is.read(tmpByte);
                    if (Read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, Read);
                }
                is.close();
                fos.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("ERROR1", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR2", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    //This function uploads the image file that the user took in the app to the web server throughout the webservice.
    public void HttpFileUpload(String urlString, String fileName, String filepath)
    {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";

        try {
            URL url = new URL(URL_UPLOAD_FILE);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size
            FileInputStream fileInputStream = new FileInputStream(filepath);

            int bytesAvailable = fileInputStream.available();
            int bufferSize = bytesAvailable;
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = bytesAvailable;
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();

            Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.i("Response", s);
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

    }


    //This function uploads the image file that the user took in the app to the web server throughout the webservice.
    public boolean HttpFileUploadPhoto(String urlString, String fileName, String filepath)
    {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";

        try {
            URL url = new URL(URL_UPLOAD_FILE);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size
            FileInputStream fileInputStream = new FileInputStream(filepath);

            int bytesAvailable = fileInputStream.available();
            int bufferSize = bytesAvailable;
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = bytesAvailable;
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();

            Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.i("Response", s);
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
            return false;
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
            return false;
        }
        return true;
    }

    public boolean uploadFileMro(String fileName, String filepath) {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_UPLOADMROFILE);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
/**/
            ExifInterface ei = new ExifInterface(filepath);

            OutputStream os = new FileOutputStream(filepath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 45, os);
            os.flush();
            os.close();

            FileInputStream fileInputStream = new FileInputStream(filepath);

            int bytesAvailable = fileInputStream.available();
            int bufferSize = bytesAvailable;
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = bytesAvailable;
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();

            Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.i("Response", s);
            dos.close();



        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
            return false;
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
            return false;
        }
        return true;
    }

    //This function uploads the log file data to the webserver
    public boolean postLogFile(LogFile log) {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        try
        {
            Log.e(Tag, "Starting Http File Sending to URL");
            Log.e(Tag, "Post Log File: " + log.getTaskID() + ", " + log.getCaptureFile() + ", " + log.getFilePath());
            URL url = new URL(URL_LOGFILE);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"taskid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(log.getTaskID()));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"capture_file\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(log.getCaptureFile());
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file_type\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(log.getFileType());
            dos.writeBytes(lineEnd);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file_name\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(log.getFilePath());
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"estMaq\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("111");
            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            File f = new File(log.getFilePath());
            if(!f.exists()) {
                Log.e(Tag, "Log File does not exist!");
            } else {
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + log.getCaptureFile() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                Log.e(Tag, "Headers are written");

                FileInputStream fileInputStream = new FileInputStream(f);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = bytesAvailable;
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();

                dos.flush();
            }

            Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF8");

            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            int ch;
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                return strRet.equals("success");
            }catch (JSONException e){

            }
            dos.close();
        }
        catch (MalformedURLException ex)
        {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        }

        catch (IOException ioe)
        {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;
    }
    //This function uploads the log file data to the webserver
    public boolean postLogFile1(LogFile log) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_LOGFILE);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("taskid").append("=").append(String.valueOf(log.getTaskID())).append("&");
            buffer.append("capture_file").append("=").append(log.getCaptureFile()).append("&");
            buffer.append("file_name").append("=").append(log.getFilePath());

            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            int status = http.getResponseCode();
            InputStream in;
            if(status >= HttpStatus.SC_BAD_REQUEST)
                in = http.getErrorStream();
            else
                in = http.getInputStream();
            InputStreamReader tmp = new InputStreamReader(in, "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                return strRet.equals("success");
            } catch (JSONException e) {

            }
            //dos.close();
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;
    }
    //This function uploads the logevent data to the webserver
    public boolean UploadLogEvent(logEvents event) {
        String myResult;
        try {
            User userdat = new User();

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost =new HttpPost(URL_LOGEVENT);

            String json = "";

            // 3. build jsonObject
            SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String formattedDate = datetime.format(event.fecha);

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("userid", event.userid);
            jsonObject.accumulate("idTarea", event.idTarea);
            jsonObject.accumulate("fecha",formattedDate );
            jsonObject.accumulate("descripcion", event.description);
            jsonObject.accumulate("latitud", event.latitude);
            jsonObject.accumulate("longitud", event.longitude);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httppost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httppost);
            int status = httpResponse.getStatusLine().getStatusCode();
            InputStream in;
            //if(status >= HttpStatus.SC_BAD_REQUEST)
            //    in = httpResponse.getErrorStream();
            //else
            //    in = httpResponse.getInputStream();
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                if(Integer.parseInt(result)>0)
                    DBManager.getManager().setUpdatelogEvents(event);

                return true;
                //DBManager.getManager().insertUser(Usuario);
            }
            return true;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean uploadPhoto(){
        List<String> item = new ArrayList<String>();

        //Defino la ruta donde busco los ficheros
        File f = new File(Environment.getExternalStorageDirectory() + "/staffapp/");
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();

        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        if(files!=null)
            for (int i = 0; i < files.length; i++)

            {
                File file = files[i];
                if(HttpFileUploadPhoto(URL_UPLOAD_FILE, file.getName(), file.getAbsolutePath())){
                    file.delete();
                }else{
                    Date dd = new Date();
                    if(file.lastModified()<(dd.getTime()-259200000)){
                        file.delete();
                    }
                }

            }

        return true;
    }


    //get function JsonObject from the return data.
    protected JSONObject getResponseData(String strUrl, Map<String, Object> params, boolean bIsAbsoluteUri) {
        try {
            return new JSONObject(getServerResponse(strUrl, params, bIsAbsoluteUri));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected JSONObject getResponseData(String strUrl, Map<String, Object> params) {
        return getResponseData(strUrl, params, false);
    }
    protected JSONArray getResponseArray(String strUrl, Map<String, Object> params) {
        try {
            return new JSONArray(getServerResponse(strUrl, params));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //get the reponse from the request.
    protected String getServerResponse(String strUrl, Map<String, Object> params, boolean bIsAbsoluteUri) {
        try {
            URL url;
            if(bIsAbsoluteUri) {
                url = new URL(strUrl);
            } else {
                url = new URL(SERVER_URL + strUrl);
            }

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String inputLine;
            String repString = "";

            while ((inputLine = in.readLine()) != null) {
                repString = repString + inputLine;
            }
            in.close();
            return repString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //get the reponse from the request.
    protected String getServerResponse(String strUrl, Map<String, Object> params) {
        return getServerResponse(strUrl, params, false);
    }

}
