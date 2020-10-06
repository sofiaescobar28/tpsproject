/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Manuel
 */
public class Conexion {
      
    public Connection con = null;
//    PreparedStatement statement=null;
    ResultSet resul=null;
    public Conexion(){}
    
    public void AbrirConexionBD() {
         try {
           Class.forName("oracle.jdbc.OracleDriver");
        String BaseDeDatos = "jdbc:oracle:thin:@localhost:1521:XE";
        con= DriverManager.getConnection(BaseDeDatos,"USUARIO_TPS","prodigy700"); 
         } catch (SQLException  ex) {
            JOptionPane.showMessageDialog(null,"Sucedio un error al abrir conexión." + ex);
        }catch (ClassNotFoundException ex) {
                  JOptionPane.showMessageDialog(null,"Sucedio un error al abrir conexión." + ex);
             }

    }
     public void CerrarConexionBD() {
        try {
         con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Sucedio un error al cerrar conexión." + ex);
//            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
