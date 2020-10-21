/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Cargos;
import AccesoDatos.Conexion;
import AccesoDatos.Entity_Main;
import AccesoDatos.Proyecto;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.ViewAdministrar_Proyecto;
import gestor_de_proyectos.interfaces.ViewCrear_Proyecto;
import gestor_de_proyectos.interfaces.ViewDetalles_Proyecto;
import gestor_de_proyectos.interfaces.ViewEditar_Proyecto;
import gestor_de_proyectos.interfaces.ViewMenu_Principal;
import gestor_de_proyectos.interfaces.ViewPago_de_personal_Historico;
import gestor_de_proyectos.interfaces.ViewPlanilla;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author josse
 */
public class ProyectoJpaController implements Serializable {
    
    ViewAdministrar_Proyecto view = new ViewAdministrar_Proyecto();
    ViewCrear_Proyecto CEproyecto = new ViewCrear_Proyecto();
    ViewMenu_Principal menu = new ViewMenu_Principal();
    ViewDetalles_Proyecto detalles = new ViewDetalles_Proyecto();
    ViewEditar_Proyecto editarPro = new ViewEditar_Proyecto();
    
    
    int fila = -1;
    int columna = -1;
    Proyecto _proyectos; 
    Conexion claseConnect = new Conexion();

    public ProyectoJpaController(EntityManagerFactory emf){
        this.emf = emf;}
    
    public ProyectoJpaController(EntityManagerFactory emf, ViewAdministrar_Proyecto view) {
        this.emf = emf;
        this.view = view;
        this.view.btnBuscar.addActionListener(al);
        this.view.btnTodos.addActionListener(al);
        this.view.btnNuevo.addActionListener(al);
        this.CEproyecto.btnCrear.addActionListener(al);
        this.CEproyecto.btnCancelar.addActionListener(al);
        this.editarPro.btnEditar.addActionListener(al);
        this.editarPro.btnCancelar.addActionListener(al);
        this.view.dgtProyectos.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getSource() == view.dgtProyectos) {
                    leerTabla();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        
        });
    }
    
    public void iniciarForm(){
        view.setTitle("Administrar Proyectos");
        List<Proyecto> ls = findProyectoEntities();
        agregarATabla(ls);
        llenarComboAnios();
        view.setLocationRelativeTo(null);
    }
    
    public void iniciarFormCrearEditar(){
        view.setTitle("Crear/Editar Proyecto");
        view.setLocationRelativeTo(null);
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void agregarATabla(List<Proyecto> obj){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Proyecto");
        model.addColumn("Fecha inicio");
        model.addColumn("Estado");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        
        if (obj.size() > 0) {
            SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
            Object Datos[] = new Object[9];
            
            
            int cont = 0;
            for (Object valor : obj){
                Datos[0] = obj.get(cont).getProyId();
                Datos[1] = obj.get(cont).getProyNombre();
                Datos[2] = objSDF.format(obj.get(cont).getProyFecha());
                if (obj.get(cont).getProyEstado().toString().equals("0")) {
                    Datos[3] = "Inactivo";
                }
                else if(obj.get(cont).getProyEstado().toString().equals("1")){
                    Datos[3] = "Activo";
                }else{
                    Datos[3] = "Finalizado";
                }
                Datos[4] = "Detalles";
                Datos[5] = "Modificar";
                Datos[6] = "Eliminar";
                Datos[7] = "Pagos";
                Datos[8] = "Historial Pago";
                
                cont = cont +1 ;
                model.addRow(Datos);
            }  
        }
        view.dgtProyectos.setModel(model);
    }
    
    public void agregarATabla(ArrayList<Proyecto> obj){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Proyecto");
        model.addColumn("Fecha inicio");
        model.addColumn("Estado");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        if (obj.size() > 0) {
            SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
            Object Datos[] = new Object[9];
            
            int cont = 0;
            for (Object valor : obj){
                Datos[0] = obj.get(cont).getProyId();
                Datos[1] = obj.get(cont).getProyNombre();
                Datos[2] = objSDF.format(obj.get(cont).getProyFecha());
                if (obj.get(cont).getProyEstado().toString().equals("0")) {
                    Datos[3] = "Inactivo";
                }
                else if(obj.get(cont).getProyEstado().toString().equals("1")){
                    Datos[3] = "Activo";
                }else{
                    Datos[3] = "Finalizado";
                }
                Datos[4] = "Detalles";
                Datos[5] = "Modificar";
                Datos[6] = "Eliminar";
                Datos[7] = "Pagos";
                Datos[8] = "Historial Pago";
                
                cont = cont +1 ;
                model.addRow(Datos);
            }
            
            
        }
        view.dgtProyectos.setModel(model);
    }
    
    public void llenarComboAnios(){
        List<Integer> lstAnios = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = year; i > 2014; i--) {
            lstAnios.add(i);
        }
        view.cmbAnios.setModel(new DefaultComboBoxModel(lstAnios.toArray(new Integer[lstAnios.size()])));
    }
    
    public void leerTabla(){
        IngresoEgresoJpaController ctrlInEg = new IngresoEgresoJpaController(Entity_Main.getInstance(), detalles);
        fila = view.dgtProyectos.getSelectedRow();
        columna = view.dgtProyectos.getSelectedColumn();
        if (columna == 4) {
            int id = Integer.parseInt(view.dgtProyectos.getValueAt(fila, 0).toString());
            String nombre = view.dgtProyectos.getValueAt(fila, 1).toString();
            ctrlInEg.iniciarForm(id, nombre);
            detalles.setVisible(true);
            detalles.setLocationRelativeTo(null);
        }
        else if (columna == 5) {
            _proyectos = new Proyecto();
            _proyectos = obtenerObjeto(fila);
            editarPro.jTextField1.setText(_proyectos.getProyId().toString());
            editarPro.txtProyecto.setText(_proyectos.getProyNombre());
            editarPro.cmbEstado.setSelectedIndex(Integer.parseInt(_proyectos.getProyEstado().toString()));
            editarPro.spnFecha.setValue(_proyectos.getProyFecha());
            editarPro.jTextField1.setVisible(false);
            editarPro.setTitle("Editar proyecto");
            editarPro.setVisible(true);
            editarPro.setLocationRelativeTo(null);
        }
        else if (columna == 6) {
            int resp = JOptionPane.showConfirmDialog(null, "¿Realmente desea eliminar este proyecto?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp == 0) {
                try {
                    destroy(obtenerObjeto(fila).getProyId());
                    JOptionPane.showMessageDialog(view, "Proyecto eliminado correctamente.");
                    List<Proyecto> ls = findProyectoEntities();
                    agregarATabla(ls);

                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(view, "El proyecto que intenta eliminar no existe.");
                }
            }
        }
        else if (columna == 7) {            
            ///--------------------------------------------------------------------------------
            ///--------------------------------------------------------------------------------
            ViewPlanilla pl = new ViewPlanilla();
            EmpleadosJpaController ctrl = new EmpleadosJpaController(emf, pl);
            int id = Integer.parseInt(view.dgtProyectos.getValueAt(fila, 0).toString());
            String nombre = view.dgtProyectos.getValueAt(fila, 1).toString();
            ctrl.iniciarFormPlanilla(id, nombre);
            ///--------------------------------------------------------------------------------
            ///--------------------------------------------------------------------------------            
        }
        else if (columna == 8) {
            GastoPersonalJpaController ctrl;
            int id = Integer.parseInt(view.dgtProyectos.getValueAt(fila, 0).toString());
            String nombre = view.dgtProyectos.getValueAt(fila, 1).toString();
            ViewPago_de_personal_Historico vista = new ViewPago_de_personal_Historico();
            GastoPersonalJpaController control = new GastoPersonalJpaController(Entity_Main.getInstance(), vista);
            control.iniciarPagoH(nombre, id);
            
        }
    }
    
    public Proyecto obtenerObjeto(int fila){
        SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
        Date fecha = null;
        String status;
        _proyectos = new Proyecto();
        _proyectos.setProyId(BigDecimal.valueOf(Double.parseDouble(view.dgtProyectos.getValueAt(fila, 0).toString())));
        _proyectos.setProyNombre(view.dgtProyectos.getValueAt(fila, 1).toString());
        try {
            fecha = objSDF.parse(view.dgtProyectos.getValueAt(fila, 2).toString());
            _proyectos.setProyFecha(fecha);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(view, e.getMessage());
        }
        if (view.dgtProyectos.getValueAt(fila, 3).toString().equals("Activo")) {
            status = "1";
            _proyectos.setProyEstado(new BigInteger(String.valueOf(status)));
        }
        else if (view.dgtProyectos.getValueAt(fila, 3).toString().equals("Inactivo")) {
            status = "0";
            _proyectos.setProyEstado(new BigInteger(String.valueOf(status)));
        }
        else {
            status = "2";
            _proyectos.setProyEstado(new BigInteger(String.valueOf(status)));
        }
        return _proyectos;
    }

    public void create(Proyecto proyecto) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(proyecto);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProyecto(proyecto.getProyId()) != null) {
                throw new PreexistingEntityException("Proyecto " + proyecto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proyecto proyecto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            proyecto = em.merge(proyecto);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = proyecto.getProyId();
                if (findProyecto(id) == null) {
                    throw new NonexistentEntityException("The proyecto with id " + id + " no longer exists.");
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
            Proyecto proyecto;
            try {
                proyecto = em.getReference(Proyecto.class, id);
                proyecto.getProyId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proyecto with id " + id + " no longer exists.", enfe);
            }
            em.remove(proyecto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proyecto> findProyectoEntities() {
        return findProyectoEntities(true, -1, -1);
    }

    public List<Proyecto> findProyectoEntities(int maxResults, int firstResult) {
        return findProyectoEntities(false, maxResults, firstResult);
    }

    private List<Proyecto> findProyectoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proyecto.class));
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

    public Proyecto findProyecto(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proyecto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProyectoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proyecto> rt = cq.from(Proyecto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public ArrayList<Proyecto> filtros(String estado, String anio, String texto){
        int status;
        if (estado.equals("Activo")) {
            status = 1;
        }else if (estado.equals("Inactivo")) {
            status = 0;
        }else{
            status = 2;
        }
        
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call buscarEstadoAnioTexto(?,?,?,?)}");
            cs.setInt(1,status);
            cs.setInt(2, Integer.parseInt(anio));
            cs.setString(3, texto);
            cs.registerOutParameter(4, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(4);
            ArrayList<Proyecto> Datos = new ArrayList<Proyecto>();
            
            while (rset.next()){
                _proyectos = new Proyecto();
                _proyectos.setProyId(rset.getBigDecimal("PROY_ID"));
                _proyectos.setProyNombre(rset.getString("PROY_NOMBRE"));
                _proyectos.setProyFecha(rset.getDate("PROY_FECHA"));
                _proyectos.setProyEstado(new BigInteger(Integer.valueOf(rset.getInt("PROY_ESTADO")).toString()));
                
                Datos.add(_proyectos);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Proyecto> filtros(String estado, String anio){
        int status;
        if (estado.equals("Activo")) {
            status = 1;
        }else if (estado.equals("Inactivo")) {
            status = 0;
        }else{
            status = 2;
        }
        
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call buscarEstadoAnio(?,?,?)}");
            cs.setInt(1,status);
            cs.setInt(2, Integer.parseInt(anio));
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<Proyecto> Datos = new ArrayList<Proyecto>();
            
            while (rset.next()){
                _proyectos = new Proyecto();
                _proyectos.setProyId(rset.getBigDecimal("PROY_ID"));
                _proyectos.setProyNombre(rset.getString("PROY_NOMBRE"));
                _proyectos.setProyFecha(rset.getDate("PROY_FECHA"));
                _proyectos.setProyEstado(new BigInteger(Integer.valueOf(rset.getInt("PROY_ESTADO")).toString()));
                
                Datos.add(_proyectos);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Proyecto> filtrosAnioTexto(String anio, String texto){
        
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call buscarAnioTexto(?,?,?)}");
            cs.setInt(1, Integer.parseInt(anio));
            cs.setString(2, texto);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<Proyecto> Datos = new ArrayList<Proyecto>();
            
            while (rset.next()){
                _proyectos = new Proyecto();
                _proyectos.setProyId(rset.getBigDecimal("PROY_ID"));
                _proyectos.setProyNombre(rset.getString("PROY_NOMBRE"));
                _proyectos.setProyFecha(rset.getDate("PROY_FECHA"));
                _proyectos.setProyEstado(new BigInteger(Integer.valueOf(rset.getInt("PROY_ESTADO")).toString()));
                
                Datos.add(_proyectos);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Proyecto> filtros(String anio){
        
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call buscarAnio(?,?)}");
            cs.setInt(1, Integer.parseInt(anio));
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Proyecto> Datos = new ArrayList<Proyecto>();
            
            while (rset.next()){
                _proyectos = new Proyecto();
                _proyectos.setProyId(rset.getBigDecimal("PROY_ID"));
                _proyectos.setProyNombre(rset.getString("PROY_NOMBRE"));
                _proyectos.setProyFecha(rset.getDate("PROY_FECHA"));
                _proyectos.setProyEstado(new BigInteger(Integer.valueOf(rset.getInt("PROY_ESTADO")).toString()));
                
                Datos.add(_proyectos);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
    
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == view.btnTodos) {
                List<Proyecto> ls = findProyectoEntities();
                agregarATabla(ls);
            }
            else if (ae.getSource() == view.btnBuscar){
                busqueda();
            }
            else if(ae.getSource() == view.btnNuevo){
                CEproyecto.setTitle("Crear Proyecto");
                CEproyecto.setVisible(true);
                CEproyecto.setLocationRelativeTo(null);
            }
            else if (ae.getSource() == CEproyecto.btnCrear) {
                try {
                    formCrear();
                } catch (ParseException ex) {
                    Logger.getLogger(ProyectoJpaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (ae.getSource() == CEproyecto.btnCancelar) {
                CEproyecto.dispose();
            }
            else if (ae.getSource() == editarPro.btnEditar){
                try {
                    formEditar();
                } catch (ParseException ex) {
                    Logger.getLogger(ProyectoJpaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (ae.getSource() == editarPro.btnCancelar) {
                editarPro.dispose();
            }
        }
    };
    
    
    public void busqueda(){
        if (view.cmbEstados.getSelectedIndex()>0) {
            //selecciono un estado
            if (view.cmbAnios.getSelectedIndex()>=0) {
                //selecciono un año
                if(!view.txtBuscar.getText().trim().toString().isEmpty()){
                    //escribio nombre
                    ArrayList<Proyecto> list = filtros(view.cmbEstados.getSelectedItem().toString(), view.cmbAnios.getSelectedItem().toString(), view.txtBuscar.getText().trim());
                    agregarATabla(list);
                }
                else{
                    //no escribio nombre
                    ArrayList<Proyecto> list = filtros(view.cmbEstados.getSelectedItem().toString(), view.cmbAnios.getSelectedItem().toString());
                    agregarATabla(list);
                }
            }
        }
        else{
            //no selecciono un estado
            if (view.cmbAnios.getSelectedIndex()>=0) {
                //selecciono un año
                if(!view.txtBuscar.getText().trim().toString().isEmpty()){
                    //escribio nombre
                    ArrayList<Proyecto> list = filtrosAnioTexto(view.cmbAnios.getSelectedItem().toString(), view.txtBuscar.getText().trim());
                    agregarATabla(list);
                }
                else{
                    //no escribio nombre
                    ArrayList<Proyecto> list = filtros(view.cmbAnios.getSelectedItem().toString());
                    agregarATabla(list);
                }
            }
        }
    }
    
    public void formCrear() throws ParseException{
        if (CEproyecto.txtProyecto.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(CEproyecto, "Ingrese el nombre del proyecto");
        }
        else if (CEproyecto.cmbEstado.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(CEproyecto, "Seleccione un estado para el proyecto");
        }
        else{
            if (BuscarNombre(CEproyecto.txtProyecto.getText().trim()).size() > 0) {
                JOptionPane.showMessageDialog(CEproyecto, "El proyecto ya existe, ingrese un nombre diferente");
            }
            else{
                String status;
                if (CEproyecto.cmbEstado.getSelectedItem().equals("Activo")) {
                    status = "1";
                }
                else if (CEproyecto.cmbEstado.getSelectedItem().equals("Inactivo")) {
                     status = "0";
                }
                else{
                    status = "2";
                }

                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                String spinnerValue = formater.format(CEproyecto.spnFecha.getValue());
                Date date = formater.parse(spinnerValue);

                _proyectos = new Proyecto();
                _proyectos.setProyNombre(CEproyecto.txtProyecto.getText().trim().toString());
                _proyectos.setProyEstado(new BigInteger(String.valueOf(status)));
                _proyectos.setProyFecha(date);

                try {
                    create(_proyectos);
                    List<Proyecto> ls = findProyectoEntities();
                    agregarATabla(ls);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CEproyecto, "Asegúrese que los campos estén correctos");
                }
            }
        }
    }
    
    public void formEditar() throws ParseException{
        if (editarPro.txtProyecto.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(editarPro, "Ingrese el nombre del proyecto");
        }
        else if (editarPro.cmbEstado.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(editarPro, "Seleccione un estado para el proyecto");
        }
        else{
            BigDecimal d = BigDecimal.valueOf(Double.parseDouble(editarPro.jTextField1.getText().trim().toString()));
            BigDecimal d2 = d.setScale(0, BigDecimal.ROUND_HALF_UP); // yields 34
                      
            int b = Integer.parseInt(d2.toString());
                
            if (BuscarNombre(editarPro.txtProyecto.getText().trim(), b).size() > 0) {
                JOptionPane.showMessageDialog(editarPro, "El proyecto ya existe, ingrese un nombre diferente");
            }
            else {
                String status;
                if (editarPro.cmbEstado.getSelectedItem().equals("Activo")) {
                    status = "1";
                }
                else if (editarPro.cmbEstado.getSelectedItem().equals("Inactivo")) {
                     status = "0";
                }
                else{
                    status = "2";
                }
            
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                String spinnerValue = formater.format(editarPro.spnFecha.getValue());
                Date date = formater.parse(spinnerValue);

                _proyectos = new Proyecto();
                _proyectos.setProyId(BigDecimal.valueOf(Double.parseDouble(editarPro.jTextField1.getText().trim().toString())));
                _proyectos.setProyNombre(editarPro.txtProyecto.getText().trim().toString());
                _proyectos.setProyEstado(new BigInteger(String.valueOf(status)));
                _proyectos.setProyFecha(date);

                try {
                    edit(_proyectos);
                    List<Proyecto> ls = findProyectoEntities();
                    agregarATabla(ls);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editarPro, "Asegúrese que los campos estén correctos");
                }
            }
        }
    }
    
    public ArrayList<Proyecto> BuscarNombre(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call buscarTexto(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Proyecto> Datos = new ArrayList<Proyecto>();
            
            while (rset.next()){
                _proyectos = new Proyecto();
                _proyectos.setProyId(rset.getBigDecimal("PROY_ID"));
                _proyectos.setProyNombre(rset.getString("PROY_NOMBRE"));
                _proyectos.setProyFecha(rset.getDate("PROY_FECHA"));
                _proyectos.setProyEstado(new BigInteger(Integer.valueOf(rset.getInt("PROY_ESTADO")).toString()));
                
                Datos.add(_proyectos);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Proyecto> BuscarNombre(String s, int id) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call buscarTextoId(?,?,?)}");
            cs.setString(1, s);
            cs.setInt(2, id);
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<Proyecto> Datos = new ArrayList<Proyecto>();
            
            while (rset.next()){
                _proyectos = new Proyecto();
                _proyectos.setProyId(rset.getBigDecimal("PROY_ID"));
                _proyectos.setProyNombre(rset.getString("PROY_NOMBRE"));
                _proyectos.setProyFecha(rset.getDate("PROY_FECHA"));
                _proyectos.setProyEstado(new BigInteger(Integer.valueOf(rset.getInt("PROY_ESTADO")).toString()));
                
                Datos.add(_proyectos);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
        return null;
    }
}
