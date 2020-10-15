/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Conexion;
import AccesoDatos.Usuarios;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.ViewLogin;
import gestor_de_proyectos.interfaces.ViewMenu_Principal;
import gestor_de_proyectos.interfaces.ViewNuevo_Editar_Usr;
import gestor_de_proyectos.interfaces.ViewUsuarios;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author josse
 */
public class UsuariosJpaController implements Serializable {
    
    ViewLogin view = new ViewLogin();
    Usuarios _usuario;
    Conexion claseConnect = new Conexion();
    ViewMenu_Principal menu = new ViewMenu_Principal();
    ViewUsuarios viewUser = new ViewUsuarios();

    public UsuariosJpaController(EntityManagerFactory emf, ViewLogin view) {
        this.emf = emf;
        this.view = view;
        this.view.btnIngresar.addActionListener(al);
        this.view.btnRecuperar.addActionListener(al);
    }
    
    public void iniciarForm(){
        view.setTitle("Login");
        view.setLocationRelativeTo(null);
    }  
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(usuarios);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getUserId()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuarios usuarios) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            usuarios = em.merge(usuarios);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = usuarios.getUserId();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getUserId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuarios findUsuarios(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public String verificarContra(String usuario){
        String contra=null;
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call verificar(?,?)}");
            cs.setString(1,usuario);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            while (rset.next()){
                contra = rset.getString("USER_CONTRASENA");
            }
            claseConnect.CerrarConexionBD();
            return contra;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return contra;
    }
    
    public ArrayList<Usuarios> correo (String clave){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call buscarUsuario(?,?)}");
            cs.setString(1,clave);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Usuarios> Datos = new ArrayList<Usuarios>();
            
            while (rset.next()){
                _usuario = new Usuarios();
                _usuario.setUserId(rset.getBigDecimal("USER_ID"));
                _usuario.setUserNombres(rset.getString("USER_NOMBRES"));
                _usuario.setUserClave(rset.getString("USER_CLAVE"));
                _usuario.setUserCorreo(rset.getString("USER_CORREO"));
                _usuario.setUserEstado(new BigInteger(Integer.valueOf(rset.getInt("USER_ESTADO")).toString()));
                _usuario.setUserContrasena(rset.getString("USER_CONTRASENA"));
                
                Datos.add(_usuario);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
    
    public void validandoUsuario(ArrayList<Usuarios> obj){
        if (obj.size() == 1) {
            int cont = 0;
            for (Object valor : obj){
                if (obj.get(cont).getUserEstado().toString().equals("1")) {
                    String destinatario = "josselynescobar464@gmail.com";
                    //String destinatario =  obj.get(cont).getUserCorreo(); //A quien le quieres escribir.
                    String asunto = "Recuperar contraseña de Gestor de Proyectos";
                    String cuerpo = "Ha seleccionado recuperar contraseña. Su usuario es: " + obj.get(cont).getUserClave() + " y su contraseña es: " + obj.get(cont).getUserContrasena();
                    enviarConGMail(destinatario, asunto, cuerpo);
                }
                else{
                    JOptionPane.showMessageDialog(view, "No puede realizar esta acción porque el usuario está inactivo");
                }
            }
            
        }
    }
    
    public boolean validandoEstado(ArrayList<Usuarios> obj){
        boolean estado = false;
        if (obj.size() == 1) {
            int cont = 0;
            for (Object valor : obj){
                if (obj.get(cont).getUserEstado().toString().equals("1")) {
                   estado = true;
                }
                else{
                    estado = false;
                }
            }
        }
        else {
            estado = false;
        }
        return estado;
    }
    
    public void enviarConGMail(String destinatario, String asunto, String cuerpo) {
        // Esto es lo que va delante de @gmail.com en tu cuenta de correo. Es el remitente también.
        String remitente = "universo.desarrollo01";  //Para la dirección nomcuenta@gmail.com
        String clave = "universodesa";
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
        props.put("mail.smtp.user", remitente);
        props.put("mail.smtp.clave", clave);    //La clave de la cuenta
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(remitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));   //Se podrían añadir varios de la misma manera
            message.setSubject(asunto);
            message.setText(cuerpo);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", remitente, clave);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            JOptionPane.showMessageDialog(view, "Se ha enviado un correo a la direccion: "+ destinatario);
        }
        catch (MessagingException me) {
            JOptionPane.showMessageDialog(view, me.getMessage());   //Si se produce un error
        }
    }
    
    ActionListener al = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == view.btnIngresar) {
                if (view.txtUsuario.getText().trim().toString().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "El usuario está vacío.");
                }
                else if (view.txtContra.getText().trim().toString().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "La contraseña está vacía.");
                }
                else {
                    if (validandoEstado(correo(view.txtUsuario.getText().trim())) == true) {
                        String met = verificarContra(view.txtUsuario.getText().trim().toString());
                        if (met != null) {
                            if (!view.txtContra.getText().trim().toString().equals(met)) {
                                JOptionPane.showMessageDialog(view, "La contraseña es incorrecta");
                            }
                            else{
                                menu.setTitle("Menú principal");
                                menu.setVisible(true);
                                menu.setLocationRelativeTo(null);
                                view.dispose();
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(view, "El usuario no existe.");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(view, "El usuario es inactivo");
                    }
                }
            }
            else if (ae.getSource()== view.btnRecuperar) {
                if (view.txtUsuario.getText().trim().toString().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "El usuario está vacío.");
                }
                else{
                    if (correo(view.txtUsuario.getText().trim()).size() > 0) {
                        validandoUsuario(correo(view.txtUsuario.getText().trim()));
                    }
                    else{
                        JOptionPane.showMessageDialog(view, "Este usuario existe");
                    }
                }
            }
        }
    };
    
    
    //lo de Marco
    ViewNuevo_Editar_Usr viewCrearEditar= new ViewNuevo_Editar_Usr();
    int fila;
    int col;
    public UsuariosJpaController(EntityManagerFactory emf, ViewUsuarios viewUs) {
        this.emf = emf;
        this.viewUser=viewUs;        
        this.viewUser.btnBuscar.addActionListener(al2);        
        this.viewUser.btnNuevo.addActionListener(al2);
        this.viewCrearEditar.btnCancelar.addActionListener(al2);
        this.viewCrearEditar.btnGuardar.addActionListener(al2);
        this.viewUser.btnBuscar.addActionListener(al2);
        this.viewUser.btnTodos.addActionListener(al2);
        this.viewUser.dgvUsuarios.addMouseListener( new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource()==viewUs.dgvUsuarios) {
                    leerTabla();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {           
            }

            @Override
            public void mouseExited(MouseEvent e) {                
            }
        });
    }
    public void leerTabla()
    {
        fila=viewUser.dgvUsuarios.getSelectedRow();
        col= viewUser.dgvUsuarios.getSelectedColumn();
        if (col == 6) {
            _usuario = new Usuarios();
            _usuario.setUserId(BigDecimal.valueOf(Double.parseDouble(viewUser.dgvUsuarios.getValueAt(fila, 0).toString())));
            _usuario.setUserNombres(viewUser.dgvUsuarios.getValueAt(fila, 1).toString());
            _usuario.setUserClave(viewUser.dgvUsuarios.getValueAt(fila, 2).toString());
            _usuario.setUserCorreo(viewUser.dgvUsuarios.getValueAt(fila, 3).toString());
            if (viewUser.dgvUsuarios.getValueAt(fila, 4).toString().equalsIgnoreCase("Activo")) {
                _usuario.setUserEstado(new BigInteger(String.valueOf("1")));
            }else{
                _usuario.setUserEstado(new BigInteger(String.valueOf("0")));
            }
            _usuario.setUserContrasena(viewUser.dgvUsuarios.getValueAt(fila, 5).toString());
            
            viewCrearEditar.txtNombUsr.setText(_usuario.getUserNombres());
            viewCrearEditar.txtClave.setText(_usuario.getUserClave());
            viewCrearEditar.txtCorreo.setText(_usuario.getUserCorreo());
            viewCrearEditar.txtContra.setText(_usuario.getUserContrasena());
            viewCrearEditar.cmbEstado.setSelectedIndex(_usuario.getUserEstado().intValue());
            
            viewCrearEditar.txtClave.setEditable(false);
            viewCrearEditar.txtClave.setEnabled(false);
            
            viewCrearEditar.setLocationRelativeTo(null);
            viewCrearEditar.setVisible(true);
        }        
    }
    
    public void iniciarFormUs(){
        viewUser.setTitle("Usuarios");
        List<Usuarios> lista = findUsuariosEntities();
        llenarTabla(lista);
        viewUser.setLocationRelativeTo(null);   
        viewUser.cmbEstado.addItem("Inactivo");
        viewUser.cmbEstado.addItem("Activo");        
    }    
    
    
    public void llenarTabla (List<Usuarios> _ls)
    {
        if (_ls.size()>0) {            
            Object Datos[] = new Object[7];
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.addColumn("ID");
            modelo.addColumn("NOMBRE");
            modelo.addColumn("CLAVE");
            modelo.addColumn("CORREO");
            modelo.addColumn("ESTADO");
            modelo.addColumn("CONTRASEÑA");
            modelo.addColumn("");
            
            int count =0;
            for (Object valor :_ls) {
                Datos[0] = _ls.get(count).getUserId();
                Datos[1] = _ls.get(count).getUserNombres();
                Datos[2] = _ls.get(count).getUserClave();
                Datos[3] = _ls.get(count).getUserCorreo();
                if (Double.parseDouble(_ls.get(count).getUserEstado().toString())==1) {
                    Datos[4] = "Activo";
                }
                else
                    Datos[4] = "Inactivo";
                Datos[5] = _ls.get(count).getUserContrasena();
                Datos[6] = "Modificar";
                
                count +=1;
                modelo.addRow(Datos);
            }
            viewUser.dgvUsuarios.setModel(modelo);
        }
    }        
    public void limpiarTXT ()
    {
        viewCrearEditar.txtNombUsr.setText(null);
        viewCrearEditar.txtClave.setText(null);
        viewCrearEditar.txtCorreo.setText(null);
        viewCrearEditar.txtContra.setText(null);
        viewCrearEditar.cmbEstado.setSelectedItem(0);
    }
    ActionListener al2 = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource()==viewUser.btnNuevo) {
                _usuario=null;                
                limpiarTXT();
                viewCrearEditar.txtClave.setEditable(true);
                viewCrearEditar.txtClave.setEnabled(true);
                viewCrearEditar.setLocationRelativeTo(null);
                viewCrearEditar.setVisible(true);
            }
            else if (e.getSource()==viewCrearEditar.btnCancelar) {
                _usuario=null;
                viewCrearEditar.dispose();
                viewCrearEditar.txtClave.setEditable(true);
                viewCrearEditar.txtClave.setEnabled(true);
            }
            else if (e.getSource()==viewCrearEditar.btnGuardar) {
                if (!viewCrearEditar.txtNombUsr.getText().trim().isEmpty() || !viewCrearEditar.txtClave.getText().trim().isEmpty() ||
                        viewCrearEditar.txtCorreo.getText().trim().isEmpty() ||viewCrearEditar.txtContra.getText().trim().isEmpty()) {                    
                    if (_usuario==null) {                        
                        ArrayList<Usuarios> list=null;
                        list= new ArrayList<Usuarios>();
                        list=BuscarPorClave(viewCrearEditar.txtClave.getText().trim());
                        if (list==null) {                            
                            viewCrearEditar.txtClave.setEditable(true);
                            viewCrearEditar.txtClave.setEnabled(true);
                            _usuario= new Usuarios();
                            _usuario.setUserNombres(viewCrearEditar.txtNombUsr.getText());
                            _usuario.setUserClave(viewCrearEditar.txtClave.getText().toUpperCase());
                            _usuario.setUserCorreo(viewCrearEditar.txtCorreo.getText());
                            _usuario.setUserContrasena(viewCrearEditar.txtContra.getText());
                            if (viewCrearEditar.cmbEstado.getSelectedIndex()==1) {
                                _usuario.setUserEstado(new BigInteger(String.valueOf("1")));
                            }
                            else
                            {
                                _usuario.setUserEstado(new BigInteger(String.valueOf("1")));
                            }
                            try {
                                create(_usuario);
                                llenarTabla(findUsuariosEntities());
                                _usuario=null;
                                limpiarTXT();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(viewCrearEditar,"Ocurrió un error al guardar el usuario, por favor vuelva a intentarlo");
                            }
                        }else{
                            JOptionPane.showMessageDialog(viewCrearEditar, "Este alias ya existe, escoja otro por favor");                            
                        }
                    }else{                        
                        viewCrearEditar.txtClave.setEditable(false);
                        viewCrearEditar.txtClave.setEnabled(false);
                        _usuario.setUserNombres(viewCrearEditar.txtNombUsr.getText());
                        _usuario.setUserClave(viewCrearEditar.txtClave.getText().toUpperCase());
                        _usuario.setUserCorreo(viewCrearEditar.txtCorreo.getText());
                        _usuario.setUserContrasena(viewCrearEditar.txtContra.getText());                        
                        if (viewCrearEditar.cmbEstado.getSelectedIndex()==1) {
                            _usuario.setUserEstado(new BigInteger(String.valueOf("1")));
                        }
                        else
                        {
                            _usuario.setUserEstado(new BigInteger(String.valueOf("0")));
                        }
                        try {                            
                            edit(_usuario);
                            llenarTabla(findUsuariosEntities());
                            _usuario=null;
                            limpiarTXT();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(viewCrearEditar,"Ocurrió un error al editar el usuario, por favor vuelva a intentarlo");
                        }
                    }                    
                }
            }
            else if (e.getSource()==viewUser.btnTodos) {
                llenarTabla(findUsuariosEntities());                
            }
            else if (e.getSource()==viewUser.btnBuscar) {
                int _index = viewUser.cmbEstado.getSelectedIndex();
                ArrayList<Usuarios> list;
                list = BuscarPorEstado(_index);
                if (list!=null) {
                    llenarTabla(list);
                }else {
                    JOptionPane.showMessageDialog(view,"No existen usuarios relacionados");
                }
            }
        }        
    };
    //-----------------------llamar a procedimientos-----------------------------
    public ArrayList<Usuarios> BuscarPorClave(String s)
    {
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findUsrClave(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Usuarios> Datos = new ArrayList<Usuarios>();
            Usuarios usu;
            if (!rset.next()) {
                return null;
            }
            else{
                while (rset.next()) {
                    usu = new Usuarios();                    
                    usu.setUserId(new BigDecimal(rset.getString("USER_ID")));
                    usu.setUserNombres(rset.getString("US.USER_NOMBRES"));
                    usu.setUserClave(rset.getString("US.USER_CLAVE"));
                    Datos.add(usu);
                }
                claseConnect.CerrarConexionBD();
                return Datos;            
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error al buscar alias(Clave)");
            return null;
        }        
    }
    public ArrayList<Usuarios> BuscarPorEstado(int index)
    {
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findUsrEstado(?,?)}");
            cs.setString(1, String.valueOf( index));
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Usuarios> Datos = new ArrayList<Usuarios>();
            Usuarios usu;  
            if (!rset.next()) {
                return null;
            }
            else{
                while (rset.next()) {
                    usu = new Usuarios();
                    usu.setUserId(new BigDecimal(rset.getString("USER_ID")));
                    usu.setUserNombres(rset.getString("USER_NOMBRES"));
                    usu.setUserClave(rset.getString("USER_CLAVE"));
                    usu.setUserCorreo(rset.getString("USER_CORREO"));
                    usu.setUserEstado(new BigInteger(rset.getString("USER_ESTADO")));
                    usu.setUserContrasena(rset.getString("USER_CONTRASENA"));                
                    Datos.add(usu);
                }                           
                claseConnect.CerrarConexionBD();
                return Datos;            
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error: "+ex.toString());
        }
        return null;
    }
}
