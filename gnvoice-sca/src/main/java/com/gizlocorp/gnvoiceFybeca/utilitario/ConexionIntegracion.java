package com.gizlocorp.gnvoiceFybeca.utilitario;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;




public class ConexionIntegracion {
	
	//private static Logger log = Logger.getLogger(ConexionIntegracion.class.getName());
	
	@SuppressWarnings("finally")
	public static Connection getConnection()
    {
        Connection conexion=null;
      
        try
        {
            Class.forName("oracle.jdbc.OracleDriver");
            String servidor = "jdbc:oracle:thin:@localhost:1521:xe";
            String usuarioDB="fybeca";
            String passwordDB="fybeca";
            conexion= DriverManager.getConnection(servidor,usuarioDB,passwordDB);
        }
        catch(ClassNotFoundException ex)
        {
        	//log.error("Error1 en la Conexión con la BD "+ex.getMessage());
            conexion=null;
        }
        catch(SQLException ex)
        {
        	///log.error("Error2 en la Conexión con la BD "+ex.getMessage());
            conexion=null;
        }
        catch(Exception ex)
        {
        	//log.error("Error3 en la Conexión con la BD "+ex.getMessage());
            conexion=null;
        }
        finally
        {
            return conexion;
        }
    }
	
	public static void main(String[] args) {
//		 //Connection miConexion;
//	       // miConexion=ConexionIntegracion.getConnection();
//	        TrxElectronica objeto = null;
//	        //if(miConexion!=null)
//	        	objeto = new TrxElectronica();
//				objeto.setId(12L);
//				objeto.setCompania("Fybeca");
//				objeto.setTipoComprobante("Factura");
//				objeto.setNumeroComprobante("235689");
//				objeto.setXml("C:\\gnvoice\\recursos\\comprobantes\\factura\\autorizado\\01092014\\0109201401179077405800110010010000464055658032312.xml");
//				ConexionIntegracion.insert(objeto);
//	       // {
	            System.out.println("Conexión Realizada Correctamente");
//	       // }
	}
	


}
