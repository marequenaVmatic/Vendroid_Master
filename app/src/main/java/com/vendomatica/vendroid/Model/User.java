package com.vendomatica.vendroid.Model;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Miguel Requena on 2017.
 */
public class User implements Serializable {

    public final static String TABLENAME = "tb_user";
    public final static String USERLOGIN = "userlogin";
    public final static String USERID = "user_id";
    public final static String PASSWORD = "password";
    public final static String NOMBRE = "nombre";
    public final static String APEPAT = "apepat";
    public final static String APEMAT = "apemat";
    public final static String TELEFONO = "telefono";
    public final static String MAIL = "mail";
    public final static String RUT = "rut";

    //public ICollection<ConfigPantalla> Pantallas { get; set; }

    public int userid=0;
    public String userlogin;
    public String password;
    public String nombre="";
    public String apepat="";
    public String apemat="";
    public String telefono="";
    public String mail="";
    public String rut;
    public ArrayList<ConfigPantalla> pantallas;

    public User()
    {
        this.userid=0;
        this.userlogin = "";
        this.password= "";
        this.nombre = "";
        this.apepat  = "";
        this.apemat= "";
        this.telefono = "";
        this.mail = "";
        this.rut = "";
        this.pantallas = new ArrayList<ConfigPantalla>();
    }
    public User(int muserid,String muserlongin,String mpassword,String mnombre,String mapepat,String mapemat,String mtelefono,String mmail,String mrut)
    {
        this.userid=muserid;
        this.userlogin = muserlongin;
        this.password= mpassword;
        this.nombre = mnombre;
        this.apepat  = mapepat;
        this.apemat= mapemat;
        this.telefono = mtelefono;
        this.mail = mmail;
        this.rut = mrut;
    }

    public User(JSONObject jsonObject)
    {
        try {
            this.userid = jsonObject.getInt("User_id");
            this.userlogin = jsonObject.getString("users");
            this.password = jsonObject.getString("Password");
            this.nombre = jsonObject.getString("Nombre");
            this.apepat = jsonObject.getString("ApePat");
            this.apemat = jsonObject.getString("ApeMat");
            this.telefono = jsonObject.getString("Telefono");
            this.mail = jsonObject.getString("Mail");
            this.rut = jsonObject.getString("Rut");
            JSONArray mPantallas  =jsonObject.getJSONArray("Pantallas");
            ArrayList<ConfigPantalla> lstPantallas = new ArrayList<ConfigPantalla>();
            for (int i = 0; i < mPantallas.length(); i++) {
                JSONObject jsonObject1 = mPantallas.getJSONObject(i);
                ConfigPantalla nPant = new ConfigPantalla();
                nPant.idTipoPantalla = jsonObject1.getInt("idTipoPantalla");
                nPant.DescTipoPantalla = jsonObject1.getString("DescTipoPantalla");
                nPant.NombreTipoPantalla = jsonObject1.getString("NombreTipoPantalla");
                nPant.idTipoTarea = jsonObject1.getInt("idTipoTarea");
                nPant.idSubTipoTarea = jsonObject1.getInt("idSubTipoTarea");
                nPant.idCampo = jsonObject1.getString("idCampo");
                nPant.NombreCampo = jsonObject1.getString("NombreCampo");
                nPant.orderCard = jsonObject1.getInt("orderCard");
                nPant.accionCampo = jsonObject1.getString("AccionCampo");
                lstPantallas.add(nPant);
            }
            this.setPantallas(lstPantallas);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setPantallas(ArrayList<ConfigPantalla> mPantallas){
        this.pantallas=mPantallas;
    }

    public boolean saveObject(User obj) {
        final File suspend_f=new File(Environment.getExternalStorageDirectory()+"/Vend", "");
        if ( !suspend_f.exists() )
        {
            suspend_f.mkdirs();
        }
        FileOutputStream fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;

        try {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Vend/Vendomatica.txt");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (Exception e) {
            keep = false;
        } finally {
            try {
                if (oos != null)   oos.close();
                if (fos != null)   fos.close();
                if (keep == false) suspend_f.delete();
            } catch (Exception e) { /* do nothing */ }
        }

        return keep;
    }

    public User getObject(User c) {

        final File suspend_f=new File(Environment.getExternalStorageDirectory()+"/Vend/", "Vendomatica.txt");

        User simpleClass= null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(suspend_f);
            is = new ObjectInputStream(fis);
            simpleClass = (User) is.readObject();
        } catch(Exception e) {
            String val= e.getMessage();
        } finally {
            try {
                if (fis != null)   fis.close();
                if (is != null)   is.close();
            } catch (Exception e) { }
        }

        return simpleClass;
    }

}
