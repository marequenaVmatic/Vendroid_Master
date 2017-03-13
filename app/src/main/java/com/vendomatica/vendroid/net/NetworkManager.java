package com.vendomatica.vendroid.net;
/*
This file has connection function between android app and backend online webservice.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.Category;
import com.vendomatica.vendroid.Model.Clasificacion;
import com.vendomatica.vendroid.Model.CommentError;
import com.vendomatica.vendroid.Model.CompleteDetailCounter;
import com.vendomatica.vendroid.Model.CompleteTask;
import com.vendomatica.vendroid.Model.CompltedTinTask;
import com.vendomatica.vendroid.Model.DetailCounter;
import com.vendomatica.vendroid.Model.Estado;
import com.vendomatica.vendroid.Model.Falla;
import com.vendomatica.vendroid.Model.LogEvent;
import com.vendomatica.vendroid.Model.LogFile;
import com.vendomatica.vendroid.Model.LoginUser;
import com.vendomatica.vendroid.Model.MachineCounter;
import com.vendomatica.vendroid.Model.Producto;
import com.vendomatica.vendroid.Model.Producto_RutaAbastecimento;
import com.vendomatica.vendroid.Model.Report;
import com.vendomatica.vendroid.Model.TaskInfo;
import com.vendomatica.vendroid.Model.TaskType;
import com.vendomatica.vendroid.Model.Tickets;
import com.vendomatica.vendroid.Model.TinTask;
import com.vendomatica.vendroid.Model.User;
import com.vendomatica.vendroid.db.DBManager;

//import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
    //private final static String DOMAIN = "http://201.236.65.58:6030/";


    //local desarrollo
    //private final static String DOMAIN = "http://192.168.254.208/";
    private final static String DOMAIN = "http://192.168.254.157/";

    ////servidor desarrollo
    //private final static String DOMAIN = "http://api-desa.vendomatica.cl:68/";

    protected final static String URL_LOGIN 		    = DOMAIN + "apivendo/pdg/AuthPdg/";
    protected final static String URL_GETTICKETS 		    = DOMAIN + "/apivendo/tickets/GetTickets";
    protected final static String URL_GETESTADOS 		    = DOMAIN + "apivendo/tickets/GetEstados";
    protected final static String URL_GETCLASIFICACION	    = DOMAIN + "apivendo/tickets/GetClasificacion";
    protected final static String URL_GETFALLAS     	    = DOMAIN + "apivendo/tickets/GetFallas";
    protected final static String URL_GETSAVEBITACORA     	    = DOMAIN + "apivendo/tickets/saveBitacora";
    public String URL_UPLOADMROFILE     	    = DOMAIN + "apivendo/tickets/UploadMROImage";
    protected final static String URL_UPLOAD 		    = DOMAIN + "posttask.aspx";
    protected final static String URL_UPLOAD_DETAILCOUNTER 		    = DOMAIN + "detailcounter.aspx";
    protected final static String URL_UPLOAD_TIN 		    = DOMAIN + "posttintask.aspx";
    protected final static String URL_UPLOAD_FILE 		    = DOMAIN + "uploadfile.aspx";
    protected final static String URL_LOADTASKS 	= DOMAIN + "task.aspx";
    protected final static String URL_CATEGORY      = DOMAIN + "category.aspx";
    protected final static String URL_PRODUCTO      = DOMAIN + "producto.aspx";
    protected final static String URL_LOGEVENT      = DOMAIN + "logevent.aspx";
    protected final static String URL_LOGFILE      = DOMAIN + "logfile.aspx";
    protected final static String URL_MACHINE      = DOMAIN + "machine.aspx";
    protected final static String URL_POSTNEWTASK = DOMAIN + "postnewtask.aspx";
    protected final static String URL_REPORT = DOMAIN + "report.aspx";
    protected final static String URL_DAYLY = DOMAIN + "dayly.aspx";

    /*
    protected final static String URL_LOGIN 		    = "http://192.168.1.180:8070/login.aspx";
    protected final static String URL_UPLOAD 		    = "http://192.168.1.180:8070/posttask.aspx";
    protected final static String URL_UPLOAD_FILE 		    = "http://192.168.1.180:8070/uploadfile.aspx";
    protected final static String URL_LOADTASKS 	= "http://192.168.1.180:8070/task.aspx";
    protected final static String URL_CATEGORY      ="http://192.168.1.180:8070/category.aspx";
*/
    private String filePath = "";
    private DownloadThread dThread;

    //This function receive the report information about the userid from the online database throughout the webservice
    //The report information is saved at Common.getInstance().arrReports variables.
    public int report(String strUserID) {
        String myResult;
        try {
            URL url = new URL(URL_REPORT);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(strUserID);
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONArray arrJson = new JSONArray(myResult.toString());
            Common.getInstance().arrReports.clear();
            for (int i = 0; i < arrJson.length(); i++) {
                int a = 0;
                JSONObject objItem = arrJson.getJSONObject(i);
                Report info = new Report(objItem.getString("NUS"), objItem.getString("Quantity"));
                Common.getInstance().arrReports.add(info);
            }
            return 1;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


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
    public LoginUser login(String strUserID, String strPassword) {
        String myResult;
        try {
            LoginUser userdat = new LoginUser();

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

                userdat.setUserId(jsonresul.getString("User_id"));
                userdat.setFirstName(jsonresul.getString("Nombre"));
                userdat.setLastName(jsonresul.getString("ApePat"));

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


    //get estados webservice
    public List<Estado> getEstado(int idestado, int idwkf) {
        List<Estado> list = new ArrayList<Estado>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("idEstado",String.valueOf(idestado)));
            nameValuePairs.add(new BasicNameValuePair("idwkf",String.valueOf(idwkf)));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            HttpGet httpget = new HttpGet(URL_GETESTADOS+ "?" + paramsString);

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
                    Estado newObj = new Estado(valor.getInt("id_estado"),valor.getString("Estado"));
                    list.add(newObj);
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
        return list;
    }

    //get clasifcacion webservice
    public List<Clasificacion> getClasificacion() {
        List<Clasificacion> list = new ArrayList<Clasificacion>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(URL_GETCLASIFICACION);

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
                    Clasificacion newObj = new Clasificacion(valor.getString("CodClasificacionFalla"),valor.getString("ClasificacionFalla"));
                    list.add(newObj);
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
        return list;
    }

    //get fallas webservice
    public List<Falla> getFalla(String idClasifcacion) {
        List<Falla> list = new ArrayList<Falla>();
        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("idClasificacion",String.valueOf(idClasifcacion)));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            HttpGet httpget = new HttpGet(URL_GETFALLAS+ "?" + paramsString);

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
                    Falla newObj = new Falla(valor.getString("CodFalla"),valor.getString("Nombre"));
                    list.add(newObj);
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
        return list;
    }

    //save bitacora webservice
    public int saveBitacora(String codFalla, int id_wkf, int id_estado, int id_usuario, int id_doc, int id_resolucion, int bp1, int bp2, String comentarios, int id_archivo, String id_bitacora, String latitud, String longitud) {

        try {

            InputStream inputStream = null;
            String result = "";
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("codFalla",String.valueOf(codFalla)));
            nameValuePairs.add(new BasicNameValuePair("id_wkf",String.valueOf(id_wkf)));
            nameValuePairs.add(new BasicNameValuePair("id_estado",String.valueOf(id_estado)));
            nameValuePairs.add(new BasicNameValuePair("id_usuario",String.valueOf(id_usuario)));
            nameValuePairs.add(new BasicNameValuePair("id_doc",String.valueOf(id_doc)));
            nameValuePairs.add(new BasicNameValuePair("id_resolucion",String.valueOf(id_resolucion)));
            nameValuePairs.add(new BasicNameValuePair("bp1",String.valueOf(bp1)));
            nameValuePairs.add(new BasicNameValuePair("bp2",String.valueOf(bp2)));
            nameValuePairs.add(new BasicNameValuePair("comentarios",String.valueOf(comentarios)));
            nameValuePairs.add(new BasicNameValuePair("id_archivo",String.valueOf(id_archivo)));
            nameValuePairs.add(new BasicNameValuePair("id_bitacora",String.valueOf(id_bitacora)));
            nameValuePairs.add(new BasicNameValuePair("latitud",String.valueOf(latitud)));
            nameValuePairs.add(new BasicNameValuePair("longitud",String.valueOf(longitud)));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            HttpGet httpget = new HttpGet(URL_GETSAVEBITACORA+ "?" + paramsString);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpget);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

                if(Integer.parseInt(result)>0){
                    for(int i=0; i<Common.getInstance().arrTickets.size(); i++) {
                        if(Common.getInstance().arrTickets.get(i).id_bitacora==Integer.parseInt(id_bitacora)){
                            Common.getInstance().arrTicketsCerrados.clear();;
                            Common.getInstance().arrTicketsCerrados.addAll(DBManager.getManager().getAllticketsCerrados());
                            Tickets tk = Common.getInstance().arrTickets.get(i);
                            tk.Observacion = comentarios;
                            Common.getInstance().arrTicketsCerrados.add(tk);
                            DBManager.getManager().deleteAllticketsCerrados();
                            DBManager.getManager().insertTicketsCerrados(Common.getInstance().arrTicketsCerrados);
                            Common.getInstance().arrTickets.remove(Common.getInstance().arrTickets.get(i));
                            DBManager.getManager().deleteAlltickets();
                            DBManager.getManager().insertTickets(Common.getInstance().arrTickets);
                        }
                    }

                }

                //listTickets = jsonresul;
            }

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        }
        return 1;
    }
    //This function Load Array Public from the online database throught the webservice.
    public int loadPublicArray(ArrayList<Tickets> arrTasks)
    {
        String myResult;
        try {
            arrTasks.addAll(getTicket(Common.getInstance().getLoginUser().getUserId()));
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //This function Load Array Public from the online database throught the webservice.
    public int loadEstado(ArrayList<Estado> arrEstado,int idestado,int idwkf)
    {
        String myResult;
        try {
            arrEstado.addAll(getEstado(idestado,idwkf));
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //This function Load Clasificacion Array Public from the online database throught the webservice.
    public int loadClasificacion(ArrayList<Clasificacion> arrClasificacion)
    {
        String myResult;
        try {
            arrClasificacion.addAll(getClasificacion());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //This function Load fallas Array Public from the online database throught the webservice.
    public int loadFalla(ArrayList<Falla> arrFalla,String idClasificion)
    {
        String myResult;
        try {
            arrFalla.addAll(getFalla(idClasificion));
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    //This function receive the MachineCounter informations from the online database throughout the webservice.
    public void loadMachine(ArrayList<MachineCounter> arrMachines)
    {
        String myResult;
        try {
            URL url = new URL(URL_MACHINE);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(Common.getInstance().getLoginUser().getUserId());
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONObject retVal = new JSONObject(myResult.toString());
            String strRet = retVal.getString("result");
            if(strRet.equals("success"))
            {
                JSONObject obj;
                JSONArray arrJsonMachine = retVal.getJSONArray("machine");
                for (int i = 0; i < arrJsonMachine.length(); i++) {
                    obj = arrJsonMachine.getJSONObject(i);
                    MachineCounter machine = new MachineCounter(obj.getString("TaskBusinessKey"), obj.getString("CodContador"), obj.getString("StartValue"), obj.getString("EndValue"), obj.getString("StartDate"), obj.getString("EndDate"));
                    arrMachines.add(machine);
                }
            }
            return ;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } catch (JSONException e) {

        }
        return;
    }
    //This function receive the Category, Producto, Producto_RutaAbastecimento, TaskType informations from the online database throughout the webservice.
    public void loadCategory(ArrayList<Category> arrCategory, ArrayList<Producto> arrPro, ArrayList<Producto_RutaAbastecimento> arrPro_Ruta, ArrayList<User> arrusers, ArrayList<TaskType> arrTypes)
    {
        String myResult;
        try {
            URL url = new URL(URL_CATEGORY);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(Common.getInstance().getLoginUser().getUserId());
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONObject retVal = new JSONObject(myResult.toString());
            String strRet = retVal.getString("result");
            if(strRet.equals("success"))
            {
                JSONArray arrJsonCategory = retVal.getJSONArray("category");
                JSONObject obj;
                for (int i = 0; i < arrJsonCategory.length(); i++) {
                    obj = arrJsonCategory.getJSONObject(i);
                    Category info = new Category(obj.getString("category"));
                    arrCategory.add(info);
                }
                JSONArray arrJsonProducto = retVal.getJSONArray("producto");
                arrPro.clear();
                for (int i = 0; i < arrJsonProducto.length(); i++) {
                    obj = arrJsonProducto.getJSONObject(i);
                    Producto info = new Producto(obj.getString("CUS"), obj.getString("NUS"),obj.getString("MAXP"));
                    arrPro.add(info);
                }
                arrPro_Ruta.clear();
                JSONArray arrJsonProducto_Ruta = retVal.getJSONArray("producto_ruta");
                for (int i = 0; i < arrJsonProducto_Ruta.length(); i++) {
                    obj = arrJsonProducto_Ruta.getJSONObject(i);
                    Producto_RutaAbastecimento info = new Producto_RutaAbastecimento(obj.getString("TaskType"), obj.getString("TaskBusinessKey"), obj.getString("RutaAbastecimiento"), obj.getString("CUS"));
                    arrPro_Ruta.add(info);
                }
                JSONArray arrJsonUser = retVal.getJSONArray("user");
                for (int i = 0; i < arrJsonUser.length(); i++) {
                    obj = arrJsonUser.getJSONObject(i);
                    User info = new User(obj.getString("userid"), obj.getString("password"), obj.getString("first_name"), obj.getString("last_name"));
                    arrusers.add(info);
                }
                JSONArray arrJsonType = retVal.getJSONArray("tasktype");
                for (int i = 0; i < arrJsonType.length(); i++) {
                    obj = arrJsonType.getJSONObject(i);
                    TaskType type = new TaskType(obj.getString("type"), obj.getString("name"));
                    arrTypes.add(type);
                }/*
                JSONArray arrJsonMachine = retVal.getJSONArray("machine");
                for (int i = 0; i < arrJsonMachine.length(); i++) {
                    obj = arrJsonMachine.getJSONObject(i);
                    MachineCounter machine = new MachineCounter(obj.getString("TaskBusinessKey"), obj.getString("CodContador"), obj.getString("StartValue"), obj.getString("EndValue"), obj.getString("StartDate"), obj.getString("EndDate"));
                    arrMachines.add(machine);
                }*/
            }
            return ;
        } catch (MalformedURLException e) {
            //
            String vvv = e.getMessage();
        } catch (IOException e) {
            //
            String vvv = e.getMessage();
        } catch (JSONException e) {
            String vvv = e.getMessage();
        }
        return;
    }

    //This function receives the all pending task and completedtask, TaskDetail information from the online database throught the webservice.
    public int loadTasks(ArrayList<TaskInfo> arrTasks, ArrayList<CompleteTask> arrCompletedTasks, ArrayList<CompltedTinTask> arrTintasks, ArrayList<CompleteDetailCounter> arrDetails, ArrayList<CommentError> arrErrors)
    {
        String myResult;
        try {
            URL url = new URL(URL_LOADTASKS);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(String.valueOf(Common.getInstance().getLoginUser().getUserId()));
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();


            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONObject retVal = new JSONObject(myResult.toString());
            String strRet = retVal.getString("result");
            if(strRet.equals("success"))
            {
                JSONArray arrJsonDetail = retVal.getJSONArray("detail");
                JSONObject obj;
                for (int i = 0; i < arrJsonDetail.length(); i++) {
                    obj = arrJsonDetail.getJSONObject(i);
                    CompleteDetailCounter info = new CompleteDetailCounter(obj.getString("Taskid"), obj.getString("CodCounter"), obj.getString("Quantity"));
                    arrDetails.add(info);
                }
                JSONArray arrJsonTin = retVal.getJSONArray("tin");
                for (int i = 0; i < arrJsonTin.length(); i++) {
                    obj = arrJsonTin.getJSONObject(i);

                    CompltedTinTask info = new CompltedTinTask(Common.getInstance().getLoginUser().getUserId(), Integer.parseInt(obj.getString("Taskid")), obj.getString("TaskType"), obj.getString("RutaAbastecimiento"), obj.getString("CUS"), obj.getString("NUS"), obj.getString("Quantity"));

                    DBManager.getManager().insertCompleteTinTask(info);
                    //arrTintasks.add(info);
                }
                JSONArray arrJson = retVal.getJSONArray("task");
                for (int i = 0; i < arrJson.length(); i++) {
                    obj = arrJson.getJSONObject(i);

                    TaskInfo info = new TaskInfo(obj.getString("userid"), Integer.parseInt(obj.getString("TaskID")), obj.getString("date"), obj.getString("TaskType"), obj.getString("RutaAbastecimiento"), obj.getString("TaskBusinessKey"),  obj.getString("Customer"), obj.getString("Adress"), obj.getString("LocationDesc"), obj.getString("Model"), obj.getString("Latitude").replace(",", "."), obj.getString("Longitude").replace(",", "."), obj.getString("EPV"), obj.getString("MachineType"), "", obj.getString("Aux_valor1"), obj.getString("Aux_valor2"), obj.getString("Aux_valor3"), obj.getString("Aux_valor4"), obj.getString("Aux_valor5"), obj.getString("Aux_valor6"));
                    arrTasks.add(info);
                }
                JSONArray arrJsonError = retVal.getJSONArray("error");
                for (int i = 0; i < arrJsonError.length(); i++) {
                    obj = arrJsonError.getJSONObject(i);

                    CommentError info = new CommentError(obj.getString("ID"), obj.getString("Error"));
                    arrErrors.add(info);
                }

                JSONArray arrJson1 = retVal.getJSONArray("complete");
                for (int j = 0; j < arrJson1.length(); j++) {
                    obj = arrJson1.getJSONObject(j);
                    String filePathSignature = "";
                    String filePath1 = "";
                    String filePath2 = "";
                    String filePath3 = "";
                    String filePath4 = "";
                    String filePath5 = "";
                    //if(!obj.getString("Signature").equals("")){
                    //    download(Common.getInstance().server_host + obj.getString("Signature"));
                    //    filePathSignature = filePath;
                    //}
                    if(!obj.getString("image1").equals("")){
                        // download(Common.getInstance().server_host + obj.getString("image1"));
                        filePath1 = filePath;
                    }
                    if(!obj.getString("image2").equals("")){
                        // download(Common.getInstance().server_host + obj.getString("image2"));
                        filePath2 = filePath;
                    }
                    if(!obj.getString("image3").equals("")){
                        // download(Common.getInstance().server_host + obj.getString("image3"));
                        filePath3 = filePath;
                    }
                    if(!obj.getString("image4").equals("")){
                        // download(Common.getInstance().server_host + obj.getString("image4"));
                        filePath4 = filePath;
                    }
                    if(!obj.getString("image5").equals("")){
                        // download(Common.getInstance().server_host + obj.getString("image5"));
                        filePath5 = filePath;
                    }
                    CompleteTask info = new CompleteTask(Common.getInstance().getLoginUser().getUserId(), Integer.parseInt(obj.getString("TaskID")), obj.getString("date"), obj.getString("TaskType"), obj.getString("RutaAbastecimiento"), obj.getString("TaskBusinessKey"), obj.getString("Customer"), obj.getString("Adress"), obj.getString("LocationDesc"), obj.getString("Model"), obj.getString("Latitude"), obj.getString("Longitude"), obj.getString("EPV"), obj.getString("logLatitude"), obj.getString("logLongitude"), obj.getString("ActionDate"), filePath1, filePath2, filePath3, filePath4, filePath5, obj.getString("MachineType"), filePathSignature, obj.getString("NumeroGuia"), obj.getString("Glosa"), obj.getString("Aux_valor1"), obj.getString("Aux_valor2"), obj.getString("Aux_valor3"), obj.getString("Aux_valor4"), obj.getString("Aux_valor5"), obj.getInt("Completed"), obj.getString("Comment"), obj.getString("Aux_valor6"), (obj.getString("QuantityResumen").equals("") ? 0 : Integer.parseInt(obj.getString("QuantityResumen"))), obj.getString("tipo_error_captura"));
                    arrCompletedTasks.add(info);
                }
                return 0;
            }
            else
                return 1;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, os);
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
    public boolean postLogEvent(LogEvent event) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_LOGEVENT);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(event.userid).append("&");
            buffer.append("taskid").append("=").append(event.taskid).append("&");
            buffer.append("datetime").append("=").append(event.datetime).append("&");
            buffer.append("description").append("=").append(event.description).append("&");
            buffer.append("latitude").append("=").append(event.latitude).append("&");
            buffer.append("longitude").append("=").append(event.longitude).append("&");
            buffer.append("batteryPercent").append("=").append(event.batteryLevel).append("&");
            buffer.append("freespace").append("=").append(event.freespace).append("&");
            buffer.append("isChargingUSB").append("=").append(event.isChargingUSB).append("&");
            buffer.append("isChargingOther").append("=").append(event.isChargingOther);

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
    //This function uploads the Dayly data to the webserver
    public boolean postDayly(String userid) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_DAYLY);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(userid);
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
    //This function uploads the DetailCounter data to the webserver
    public boolean postDetailCounter(DetailCounter task) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_UPLOAD_DETAILCOUNTER);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("taskid").append("=").append(task.taskid).append("&");
            buffer.append("codcounter").append("=").append(task.CodCounter).append("&");
            buffer.append("quantity").append("=").append(task.quantity);
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
    //This function uploads the TinTask data to the webserver
    public boolean postTinTask(TinTask task) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_UPLOAD_TIN);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(task.userid).append("&");
            buffer.append("taskid").append("=").append(String.valueOf(task.taskid)).append("&");
            buffer.append("tasktype").append("=").append(task.tasktype).append("&");
            buffer.append("RutaAbastecimiento").append("=").append(task.RutaAbastecimiento).append("&");
            buffer.append("cus").append("=").append(task.cus).append("&");
            buffer.append("nus").append("=").append(task.nus).append("&");
            buffer.append("quantity").append("=").append(task.quantity);
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
    //This function uploads the NewTaskInfo data to the webserver
    public boolean postNewTask(TaskInfo task) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_POSTNEWTASK);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(task.userid).append("&");
            buffer.append("date").append("=").append(task.date).append("&");
            buffer.append("tasktype").append("=").append(task.taskType).append("&");
            buffer.append("RutaAbastecimiento").append("=").append(task.RutaAbastecimiento).append("&");
            buffer.append("TaskBusinessKey").append("=").append(task.TaskBusinessKey).append("&");
            buffer.append("Customer").append("=").append(task.Customer).append("&");
            buffer.append("Adress").append("=").append(String.valueOf(task.Adress)).append("&");
            buffer.append("LocationDesc").append("=").append(task.LocationDesc).append("&");
            buffer.append("Model").append("=").append(task.Model).append("&");
            buffer.append("latitude").append("=").append(task.latitude).append("&");
            buffer.append("longitude").append("=").append(task.longitude).append("&");
            buffer.append("epv").append("=").append(task.epv).append("&");
            buffer.append("MachineType").append("=").append(task.MachineType).append("&");
            buffer.append("Aux_valor1").append("=").append(task.Aux_valor1).append("&");
            buffer.append("Aux_valor2").append("=").append(task.Aux_valor2).append("&");
            buffer.append("Aux_valor3").append("=").append(task.Aux_valor3).append("&");
            buffer.append("Aux_valor4").append("=").append(task.Aux_valor4).append("&");
            buffer.append("Aux_valor5").append("=").append(task.Aux_valor5).append("&");
            buffer.append("Aux_valor6").append("=").append(task.Aux_valor6);
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(out, "UTF8");
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
                if (strRet.equals("success")) {
                    task.taskID = obj.getInt("taskid");
                    return true;
                }
                else
                    return false;
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

    //This function uploads the Completedtask data to the webserver
    public boolean postTask(int taskid, String date, String tasktype, String RutaAbastecimiento, String TaskBusinessKey, String Customer, String Adress, String LocationDesc, String Model, String latitude, String longitude, String epv, String logLatitude, String logLongitude, String ActionDate, String MachineType, String Signature, String NumeroGuia, String Aux_valor1, String Aux_valor2, String Aux_valor3, String Aux_valor4, String Aux_valor5, String Glosa, String[] arrPhoto, int count, int iCompleted, String strComment, String Aux_valor6, int QuantityResumen, String comment_notcap) {

        String fileNameSignature = "";
        String fileName1 = "";
        String fileName2 = "";
        String fileName3 = "";
        String fileName4 = "";
        String fileName5 = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";
        if(!Signature.equals("")){
            String[] path_array = Signature.split("/");
            fileNameSignature = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileNameSignature, Signature);
        }
        if (!arrPhoto[0].equals("")) {
            String[] path_array = arrPhoto[0].split("/");
            fileName1 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName1, arrPhoto[0]);
        }
        if (!arrPhoto[1].equals("")) {
            String[] path_array = arrPhoto[1].split("/");
            fileName2 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName2, arrPhoto[1]);
        }
        if (!arrPhoto[2].equals("")) {
            String[] path_array = arrPhoto[2].split("/");
            fileName3 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName3, arrPhoto[2]);
        }
        if (!arrPhoto[3].equals("")) {
            String[] path_array = arrPhoto[3].split("/");
            fileName4 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName4, arrPhoto[3]);
        }
        if (!arrPhoto[4].equals("")) {
            String[] path_array = arrPhoto[4].split("/");
            fileName5 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName5, arrPhoto[4]);
        }
        try {
            Log.e(Tag, "Starting Http File Sending to URL");
            URL url = new URL(URL_UPLOAD);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(Common.getInstance().getLoginUser().getUserId()).append("&");
            buffer.append("taskid").append("=").append(String.valueOf(taskid)).append("&");
            buffer.append("date").append("=").append(date).append("&");
            buffer.append("tasktype").append("=").append(tasktype).append("&");
            buffer.append("RutaAbastecimiento").append("=").append(RutaAbastecimiento).append("&");
            buffer.append("TaskBusinessKey").append("=").append(TaskBusinessKey).append("&");
            buffer.append("Customer").append("=").append(Customer).append("&");
            buffer.append("Adress").append("=").append(Adress).append("&");
            buffer.append("LocationDesc").append("=").append(LocationDesc).append("&");
            buffer.append("Model").append("=").append(Model).append("&");
            buffer.append("latitude").append("=").append(latitude).append("&");
            buffer.append("longitude").append("=").append(longitude).append("&");
            buffer.append("epv").append("=").append(epv).append("&");
            buffer.append("logLatitude").append("=").append(logLatitude).append("&");
            buffer.append("logLongitude").append("=").append(logLongitude).append("&");
            buffer.append("ActionDate").append("=").append(ActionDate).append("&");
            buffer.append("MachineType").append("=").append(MachineType).append("&");
            buffer.append("Signature").append("=").append(NumeroGuia).append("&");
            buffer.append("NumeroGuia").append("=").append(Glosa).append("&");
            buffer.append("Glosa").append("=").append(fileNameSignature).append("&");
            buffer.append("count").append("=").append(String.valueOf(count)).append("&");
            buffer.append("Aux_valor1").append("=").append(Aux_valor1).append("&");
            buffer.append("Aux_valor2").append("=").append(Aux_valor2).append("&");
            buffer.append("Aux_valor3").append("=").append(Aux_valor3).append("&");
            buffer.append("Aux_valor4").append("=").append(Aux_valor4).append("&");
            buffer.append("Aux_valor5").append("=").append(Aux_valor5).append("&");
            buffer.append("Aux_valor6").append("=").append(Aux_valor6).append("&");
            buffer.append("QuantityResumen").append("=").append(String.valueOf(QuantityResumen)).append("&");
            buffer.append("Completed").append("=").append("" + iCompleted).append("&");
            buffer.append("Comment").append("=").append(strComment == null ? "" : URLEncoder.encode(strComment, UTF8)).append("&");
            buffer.append("tipo_error_captura").append("=").append(comment_notcap).append("&");
            buffer.append("file1").append("=").append(fileName1).append("&");
            buffer.append("file2").append("=").append(fileName2).append("&");
            buffer.append("file3").append("=").append(fileName3).append("&");
            buffer.append("file4").append("=").append(fileName4).append("&");
            buffer.append("file5").append("=").append(fileName5);
            //OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            int status = http.getResponseCode();
            InputStream in;
            if (status >= HttpStatus.SC_BAD_REQUEST)
                in = http.getErrorStream();
            else
                in = http.getInputStream();
            InputStreamReader tmp = new InputStreamReader(in, "UTF8");
            Log.e(Tag, "File Sent, Response: " + String.valueOf(http.getResponseCode()));

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);

        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;
    }



    //This function uploads the Completetask data to the webserver
    public boolean postTask1(int taskid, String date, String tasktype, String RutaAbastecimiento, String TaskBusinessKey, String Customer, String Adress, String LocationDesc, String Model, String latitude, String longitude, String epv, String logLatitude, String logLongitude, String estMaq, String moned, String billeter, String tarjet, String nivAb, String higEx, String higIn, String atrSm, String atrSen, String atrIlu, String ActionDate, String MachineType, String[] arrPhoto, int count) {

        int Second = Integer.valueOf(new SimpleDateFormat("ss").format(new Date()));

        String fileName1 = "";
        String fileName2 = "";
        String fileName3 = "";
        String fileName4 = "";
        String fileName5 = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        if(!arrPhoto[0].equals("")){
            String[] path_array = arrPhoto[0].split("/");
            fileName1 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName1, arrPhoto[0]);
        }
        if(!arrPhoto[1].equals("")){
            String[] path_array = arrPhoto[1].split("/");
            fileName2 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName2, arrPhoto[1]);
        }
        if(!arrPhoto[2].equals("")){
            String[] path_array = arrPhoto[2].split("/");
            fileName3 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName3, arrPhoto[2]);
        }
        if(!arrPhoto[3].equals("")){
            String[] path_array = arrPhoto[3].split("/");
            fileName4 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName4, arrPhoto[3]);
        }
        if(!arrPhoto[4].equals("")){
            String[] path_array = arrPhoto[4].split("/");
            fileName5 = path_array[path_array.length - 1];
            //HttpFileUpload(URL_UPLOAD_FILE, fileName5, arrPhoto[4]);
        }

        try
        {
            Log.e(Tag,"Starting Http File Sending to URL");
            URL url = new URL(URL_UPLOAD);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

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
            dos.writeBytes("Content-Disposition: form-data; name=\"userid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(Common.getInstance().getLoginUser().getUserId()));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"taskid\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(taskid));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"date\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(date);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tasktype\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(tasktype);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"RutaAbastecimiento\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(RutaAbastecimiento);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"TaskBusinessKey\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(TaskBusinessKey);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Customer\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Customer);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Adress\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Adress);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"LocationDesc\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(LocationDesc);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Model\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Model);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"latitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(latitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"longitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(longitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"epv\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(epv);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"logLatitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(logLatitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"logLongitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(logLongitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"estMaq\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(estMaq);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"moned\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(moned);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"billeter\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(billeter);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tarjet\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(tarjet);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"nivAb\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(nivAb);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"higEx\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(higEx);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"higIn\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(higIn);
            dos.writeBytes(lineEnd)
            ;
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"atrSm\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(atrSm);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"atrSen\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(atrSen);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"atrIlu\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(atrIlu);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ActionDate\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(ActionDate);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"MachineType\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(MachineType);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"count\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(count));
            dos.writeBytes(lineEnd);

            if(!arrPhoto[0].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file1\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName1);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[1].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file2\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName2);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[2].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file3\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName3);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[3].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file4\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName4);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[4].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file5\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName5);
                dos.writeBytes(lineEnd);
            }
            if(!arrPhoto[0].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName1 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[0]);

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
            }

            if(!arrPhoto[1].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName2 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[1]);

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
            }

            if(!arrPhoto[2].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName3 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[2]);

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
            }

            if(!arrPhoto[3].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName4 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[3]);

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
            }

            if(!arrPhoto[4].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName5 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[4]);

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
            }
            dos.flush();

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
