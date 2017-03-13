package com.vendomatica.vendroid.Model;

import java.io.Serializable;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class TicketsCerrado implements Serializable {

    public final static String TABLENAME = "tb_tickets";
    public final static String BITACORA = "id_bitacora";
    public final static String IDDOC = "id_doc";
    public final static String IDWKF = "id_wkf";
    public final static String IDLINEA = "id_linea";
    public final static String IDESTADO = "id_estado";
    public final static String IDUSERESTADO = "id_usuario_estado";
    public final static String IDRESOLUCION = "id_resolucion";
    public final static String FECHAESTADO = "fecha_estado";
    public final static String CODCLIENTE = "CodCliente";
    public final static String SERIEMAQUINA = "SerieMaquina";
    public final static String DIRECCION = "Direccion";
    public final static String UBICACION = "Ubicacion";
    public final static String FECHA = "Fecha";
    public final static String IDUSER = "ID_User";
    public final static String RAZONSOCIAL = "RazonSocial";
    public final static String CIUDAD = "Ciudad";
    public final static String COMUNA = "Comuna";
    public final static String TIPOMAQUINA = "TipoMaquina";
    public final static String TIPONEGOCIO = "TipoNegocio";
    public final static String MODELOMAQUINA = "ModeloMaquina";
    public final static String LLAVEMAQUINA = "LlaveMaquina";
    public final static String CODFALLA = "CodFalla";
    public final static String CONTACTOLLAMADA = "ContactoLlamada";
    public final static String CONTACTOFONO = "ContactoFono";
    public final static String CONTACTOEMAIL = "ContactoEmail";
    public final static String DEVOLUCION = "Devolucion";
    public final static String DEVMONTO = "DevolucionMonto";
    public final static String OBSERVA = "Observacion";

    public final static String IDINCIDENTE = "ID_IncidenciaCliente";
    public final static String RUTAABAST = "RutaAbastecimiento";
    public final static String RUTATECNICA = "RutaTecnica";
    public final static String SERIEPRINT = "SeriePrint";
    public final static String ESTADOINICIAL = "EstadoInicial";
    public final static String TIPOLLAMADA = "TipoLlamada";
    public final static String LLAVEPUERTA = "LlavePuertaMaquina";
    public final static String SERIEBILLETERO = "SerieBilletero";
    public final static String SERIEMONEDERO = "SerieMonedero";

    public final static String SERIELECTOR = "SerieLectorChip";
    public final static String CODESTADOCLIENTE = "CodEstadoCliente";
    public final static String RUTATECNICAINICIAL = "RutaTecnicaInicial";
    public final static String RUT = "Rut";
    public final static String EJECUTIVOHISTORICO = "Ejecutivo_historico";
    public final static String LATITUD = "latitud";
    public final static String LONGITUD= "longitud";
    public final static String GARANTIA = "Garantia";
    public final static String IDCLIENTEINTERNO = "id_cliente_interno";
    public final static String DIAGNOSTICO = "DiagnosticoNV3";
    public final static String NROTARJETABIP = "NroTarjetaBip";

    public final static String CONSULTABIP = "ConsultaBip";
    public final static String ORIGENLLAMADA = "OrigenLlamada";
    public final static String TIPODEV = "TipoDevolucion";
    public final static String FALLA = "falla";


    public int id_bitacora;
    public int  id_wkf;
    public int  id_doc;
    public int  id_linea;
    public int id_estado;
    public int id_usuario_estado;
    public String id_resolucion;
    public String fecha_estado;
    public String CodCliente;
    public String SerieMaquina;
    public String Direccion;
    public String Ubicacion;
    public String Fecha;
    public int ID_User;
    public String RazonSocial;
    public String Ciudad;
    public String Comuna;
    public String TipoMaquina;
    public String TipoNegocio;
    public String ModeloMaquina;
    public String LlaveMaquina;
    public String CodFalla;
    public String ContactoLlamada;
    public String ContactoFono;
    public String ContactoEmail;
    public String Devolucion;
    public String DevolucionMonto;
    public String Observacion;

    public int ID_IncidenciaCliente;
    public String RutaAbastecimiento;
    public String RutaTecnica;
    public String SeriePrint;
    public String EstadoInicial;
    public String TipoLlamada;
    public String LlavePuertaMaquina;
    public String SerieBilletero;
    public String SerieMonedero;

    public String SerieLectorChip;
    public String CodEstadoCliente;
    public String RutaTecnicaInicial;
    public String Rut;
    public String Ejecutivo_historico;
    public String latitud;
    public String longitud;
    public String Garantia;
    public String id_cliente_interno;
    public String DiagnosticoNV3;
    public String NroTarjetaBip;

    public String ConsultaBip;
    public String OrigenLlamada;
    public String TipoDevolucion;
    public String Falla;
    public String estadonew;
    public String clasificacionnew;
    public String fallanew;
    public String observa;


    public TicketsCerrado()
    {
        this.id_bitacora =0 ;
        this.id_wkf =0 ;
        this.id_doc =0;
        this.id_linea = 0;
        this.id_estado = 0;
        this.id_usuario_estado = 0;
        this.id_resolucion = "";
        this.fecha_estado = "";
        this.CodCliente = "";
        this.SerieMaquina = "";
        this.Direccion = "";
        this.Ubicacion = "";
        this.Fecha  ="" ;
        this.ID_User = 0;
        this.RazonSocial = "";
        this.Ciudad = "";
        this.Comuna = "";
        this.TipoMaquina = "";
        this.TipoNegocio = "";
        this.ModeloMaquina = "";
        this.LlaveMaquina = "";
        this.CodFalla = "";
        this.ContactoLlamada =" ";
        this.ContactoFono= "";
        this.ContactoEmail ="";
        this.Devolucion = "";
        this.DevolucionMonto ="";
        this.Observacion ="";

        this.ID_IncidenciaCliente=0;
        this.RutaAbastecimiento="";
        this.RutaTecnica="";
        this.SeriePrint="";
        this.EstadoInicial="";
        this.TipoLlamada="";
        this.LlavePuertaMaquina = "";
        this.SerieBilletero="";
        this.SerieMonedero="";

        this.SerieLectorChip="";
        this.CodEstadoCliente="";
        this.RutaTecnicaInicial="";
        this.Rut="";
        this.Ejecutivo_historico="";
        this.latitud="";
        this.longitud="";
        this.Garantia="";
        this.id_cliente_interno="";
        this.DiagnosticoNV3="";
        this.NroTarjetaBip="";

        this.ConsultaBip="";
        this.OrigenLlamada="";
        this.TipoDevolucion="";
        this.Falla ="";
        this.estadonew="";
        this.clasificacionnew="";
        this.fallanew="";
        this.observa="";


    }
    public TicketsCerrado(int id_bitacora, int idwkf, int  id_doc, int  id_linea, int id_estado, int id_usuario_estado, String id_resolucion, String fecha_estado, String CodCliente, String SerieMaquina, String Direccion, String Ubicacion, String Fecha, int ID_User, String RazonSocial, String Ciudad, String Comuna, String TipoMaquina, String TipoNegocio, String ModeloMaquina, String LlaveMaquina, String CodFalla, String ContactoLlamada, String ContactoFono, String ContactoEmail, String Devolucion, String DevolucionMonto, String Observacion, int ID_IncidenciaCliente, String RutaAbastecimiento, String RutaTecnica, String SeriePrint, String EstadoInicial, String TipoLlamada, String LlavePuertaMaquina, String SerieBilletero, String SerieMonedero, String SerieLectorChip, String CodEstadoCliente, String RutaTecnicaInicial, String Rut, String Ejecutivo_historico, String latitud, String longitud, String Garantia, String id_cliente_interno, String DiagnosticoNV3, String NroTarjetaBip, String ConsultaBip, String OrigenLlamada, String TipoDevolucion, String Falla,String estadonew, String clasificacionnew, String fallanew, String observa)
    {
        this.id_bitacora =id_bitacora  ;
        this.id_wkf=idwkf;
        this.id_doc =id_doc;
        this.id_linea = id_linea;
        this.id_estado = id_estado;
        this.id_usuario_estado = id_usuario_estado;
        this.id_resolucion = id_resolucion;
        this.fecha_estado = fecha_estado;
        this.CodCliente = CodCliente;
        this.SerieMaquina = SerieMaquina;
        this.Direccion = Direccion;
        this.Ubicacion = Ubicacion;
        this.Fecha  =Fecha ;
        this.ID_User = ID_User;
        this.RazonSocial = RazonSocial;
        this.Ciudad = Ciudad;
        this.Comuna = Comuna;
        this.TipoMaquina = TipoMaquina;
        this.TipoNegocio = TipoNegocio;
        this.ModeloMaquina = ModeloMaquina;
        this.LlaveMaquina = LlaveMaquina;
        this.CodFalla = CodFalla;
        this.ContactoLlamada =ContactoLlamada;
        this.ContactoFono= ContactoFono;
        this.ContactoEmail =ContactoEmail;
        this.Devolucion = Devolucion;
        this.DevolucionMonto =DevolucionMonto;
        this.Observacion =Observacion;

        this.ID_IncidenciaCliente=ID_IncidenciaCliente;
        this.RutaAbastecimiento=RutaAbastecimiento;
        this.RutaTecnica=RutaTecnica;
        this.SeriePrint=SeriePrint;
        this.EstadoInicial=EstadoInicial;
        this.TipoLlamada=TipoLlamada;
        this.LlavePuertaMaquina = LlavePuertaMaquina;
        this.SerieBilletero=SerieBilletero;
        this.SerieMonedero=SerieMonedero;

        this.SerieLectorChip=SerieLectorChip;
        this.CodEstadoCliente=CodEstadoCliente;
        this.RutaTecnicaInicial=RutaTecnicaInicial;
        this.Rut=Rut;
        this.Ejecutivo_historico=Ejecutivo_historico;
        this.latitud=latitud;
        this.longitud=longitud;
        this.Garantia=Garantia;
        this.id_cliente_interno=id_cliente_interno;
        this.DiagnosticoNV3=DiagnosticoNV3;
        this.NroTarjetaBip=NroTarjetaBip;

        this.ConsultaBip=ConsultaBip;
        this.OrigenLlamada=OrigenLlamada;
        this.TipoDevolucion=TipoDevolucion;
        this.Falla=Falla;

        this.estadonew=estadonew;
        this.clasificacionnew=clasificacionnew;
        this.fallanew=fallanew;
        this.observa=observa;

    }
}
