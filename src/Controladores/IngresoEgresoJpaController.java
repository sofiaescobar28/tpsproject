/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AccesoDatos.Categorias;
import AccesoDatos.Conexion;
import AccesoDatos.Entity_Main;
import AccesoDatos.IngresoEgreso;
import AccesoDatos.Proyecto;
import AccesoDatos.UnidadesDeMedida;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import Reportes.entidades.EEgresoIngreso;
import Reportes.entidades.EIEResumen;
import Reportes.entidades.IEReumenDS;
import Reportes.entidades.IngresoEgresoDS;
import gestor_de_proyectos.interfaces.ViewAdministrar_Proyecto;
import gestor_de_proyectos.interfaces.ViewCategorias;
import gestor_de_proyectos.interfaces.ViewCrear_Registro;
import gestor_de_proyectos.interfaces.ViewDetalles_Proyecto;
import gestor_de_proyectos.interfaces.ViewEditar_Registro;
import gestor_de_proyectos.interfaces.ViewPlanilla;
import gestor_de_proyectos.interfaces.viewUnidades_de_medida;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author josse
 */
public class IngresoEgresoJpaController implements Serializable {
    
    ViewDetalles_Proyecto view = new ViewDetalles_Proyecto();
    Conexion claseConnect = new Conexion();
    IngresoEgreso _ingresoEgreso;
    int id_proy;
    int id_segundo;
    String nombre_proy;
    int fila = -1;
    int columna = -1;
    Categorias _categorias = new Categorias();
    Proyecto _proyecto = new Proyecto();
    UnidadesDeMedida _unidades = new UnidadesDeMedida();
    CategoriasJpaController ctrlCategorias = new CategoriasJpaController(Entity_Main.getInstance());
    UnidadesDeMedidaJpaController ctrlUnidades = new UnidadesDeMedidaJpaController(Entity_Main.getInstance());
    ProyectoJpaController ctrlPtoyecto = new ProyectoJpaController(Entity_Main.getInstance());
    ViewAdministrar_Proyecto viewProyecto = new ViewAdministrar_Proyecto();
    ViewPlanilla viewPlanilla = new ViewPlanilla();
    EmpleadosJpaController ctrlEmpleados = new EmpleadosJpaController(Entity_Main.getInstance(), viewPlanilla);
    ViewEditar_Registro viewEditRegistro = new ViewEditar_Registro();
    ViewCrear_Registro viewCreatRegistro = new ViewCrear_Registro();
    
    public IngresoEgresoJpaController(EntityManagerFactory emf){
        this.emf = emf;
    }

    //Constructor de detalle
    public IngresoEgresoJpaController(EntityManagerFactory emf, ViewDetalles_Proyecto view) {
        this.emf = emf;
        this.view = view;
        this.view.cmbFiltro.addActionListener(al);
        this.view.btnNuevo.addActionListener(al);
        
        this.view.btnReporte.addActionListener(al);
                
        this.viewEditRegistro.btnACategoria.addActionListener(al);
        this.viewEditRegistro.btnAUnidad.addActionListener(al);
        this.viewEditRegistro.btnCancelar.addActionListener(al);
        this.viewEditRegistro.cmbTipo.addActionListener(al);
        this.viewEditRegistro.btnGuardar.addActionListener(al);
        this.viewCreatRegistro.btnCancelar.addActionListener(al);
        this.viewCreatRegistro.cmbTipo.addActionListener(al);
        this.viewCreatRegistro.btnGuardar.addActionListener(al);
        this.viewCreatRegistro.btnACategoria.addActionListener(al);
        this.viewCreatRegistro.btnAUnidad.addActionListener(al);
        this.viewCreatRegistro.idRefrescar1.addActionListener(al);
        this.viewCreatRegistro.idRefrescar2.addActionListener(al);
        this.viewEditRegistro.btnRefrescarCate.addActionListener(al);
        this.viewEditRegistro.btnRefrescarUnidad.addActionListener(al);
        
    }
    
    public void iniciarForm(Integer id, String nombre){
        view.setTitle("Detalles de Proyecto");
        view.getContentPane().setBackground(new Color(153,168,178));
        id_proy = id;
        id_segundo = id;
        nombre_proy = nombre;
        view.cmbFiltro.setSelectedIndex(0);
        agregarATabla(registrosProyecto(id));
        double gasto = gastos(id);
        double ingreso = ingresos(id);
        view.lblNombre.setText(nombre);
        view.lblGasto.setText("Gastos: $" + gasto);
        view.lblIngreso.setText("Ingresos: $" + ingreso);
        view.lblBalance.setText("Balance: $" + String.valueOf(ingreso-gasto));
        view.setLocationRelativeTo(null);
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public ArrayList<IngresoEgreso> registrosProyecto(Integer id){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call registrosProyecto(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<IngresoEgreso> Datos = new ArrayList<IngresoEgreso>();
            
            while (rset.next()){
                _ingresoEgreso = new IngresoEgreso();
                _categorias = new Categorias();
                _unidades = new UnidadesDeMedida();
                _proyecto.setProyId(rset.getBigDecimal("PROY_ID"));
                _ingresoEgreso.setProyId(_proyecto);
                _ingresoEgreso.setIeId(rset.getBigDecimal("IE_ID"));
                _ingresoEgreso.setIeDescripcion(rset.getString("IE_DESCRIPCION"));
                _ingresoEgreso.setIeTipo(new BigInteger(Integer.valueOf(rset.getInt("IE_TIPO")).toString()));
                _ingresoEgreso.setIeFecha(rset.getDate("IE_FECHA"));
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(ctrlCategorias.findCategorias(rset.getBigDecimal("CAT_ID")).getCatNombre());
                _ingresoEgreso.setCatId(_categorias);
                _ingresoEgreso.setIeCantidad(rset.getDouble("IE_CANTIDAD"));
                _unidades.setUmId(rset.getBigDecimal("UM_ID"));
                _unidades.setUmNombre(ctrlUnidades.findUnidadesDeMedida(rset.getBigDecimal("UM_ID")).getUmNombre());
                _ingresoEgreso.setUmId(_unidades);
                _ingresoEgreso.setIeMonto(rset.getDouble("IE_MONTO"));
                Datos.add(_ingresoEgreso);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar registros del proyecto." + e);
        }
        return null;
    }
    
    public ArrayList<IngresoEgreso> registrosEgresos(Integer id){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call registrosGastos(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<IngresoEgreso> Datos = new ArrayList<IngresoEgreso>();
            
            while (rset.next()){
                _ingresoEgreso = new IngresoEgreso();
                _proyecto.setProyId(rset.getBigDecimal("PROY_ID"));
                _ingresoEgreso.setProyId(_proyecto);
                _ingresoEgreso.setIeId(rset.getBigDecimal("IE_ID"));
                _ingresoEgreso.setIeDescripcion(rset.getString("IE_DESCRIPCION"));
                _ingresoEgreso.setIeTipo(new BigInteger(Integer.valueOf(rset.getInt("IE_TIPO")).toString()));
                _ingresoEgreso.setIeFecha(rset.getDate("IE_FECHA"));
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(ctrlCategorias.findCategorias(rset.getBigDecimal("CAT_ID")).getCatNombre());
                _ingresoEgreso.setCatId(_categorias);
                _ingresoEgreso.setIeCantidad(rset.getDouble("IE_CANTIDAD"));
                _unidades.setUmId(rset.getBigDecimal("UM_ID"));
                _unidades.setUmNombre(ctrlUnidades.findUnidadesDeMedida(rset.getBigDecimal("UM_ID")).getUmNombre());
                _ingresoEgreso.setUmId(_unidades);
                _ingresoEgreso.setIeMonto(rset.getDouble("IE_MONTO"));
                Datos.add(_ingresoEgreso);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar registros del proyecto." + e);
        }
        return null;
    }
    
    public ArrayList<IngresoEgreso> registrosIngresos(Integer id){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call registrosIngresos(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<IngresoEgreso> Datos = new ArrayList<IngresoEgreso>();
            
            while (rset.next()){
                _ingresoEgreso = new IngresoEgreso();
                _proyecto.setProyId(rset.getBigDecimal("PROY_ID"));
                _ingresoEgreso.setProyId(_proyecto);
                _ingresoEgreso.setIeId(rset.getBigDecimal("IE_ID"));
                _ingresoEgreso.setIeDescripcion(rset.getString("IE_DESCRIPCION"));
                _ingresoEgreso.setIeTipo(new BigInteger(Integer.valueOf(rset.getInt("IE_TIPO")).toString()));
                _ingresoEgreso.setIeFecha(rset.getDate("IE_FECHA"));
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(ctrlCategorias.findCategorias(rset.getBigDecimal("CAT_ID")).getCatNombre());
                _ingresoEgreso.setCatId(_categorias);
                _ingresoEgreso.setIeCantidad(rset.getDouble("IE_CANTIDAD"));
                _unidades.setUmId(rset.getBigDecimal("UM_ID"));
                _unidades.setUmNombre(ctrlUnidades.findUnidadesDeMedida(rset.getBigDecimal("UM_ID")).getUmNombre());
                _ingresoEgreso.setUmId(_unidades);
                _ingresoEgreso.setIeMonto(rset.getDouble("IE_MONTO"));
                Datos.add(_ingresoEgreso);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar registros del proyecto." + e);
        }
        return null;
    }
    
    public ArrayList<IngresoEgreso> registrosPerdidas(Integer id){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call registrosPerdidas(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<IngresoEgreso> Datos = new ArrayList<IngresoEgreso>();
            
            while (rset.next()){
                _ingresoEgreso = new IngresoEgreso();
                _proyecto.setProyId(rset.getBigDecimal("PROY_ID"));
                _ingresoEgreso.setProyId(_proyecto);
                _ingresoEgreso.setIeId(rset.getBigDecimal("IE_ID"));
                _ingresoEgreso.setIeDescripcion(rset.getString("IE_DESCRIPCION"));
                _ingresoEgreso.setIeTipo(new BigInteger(Integer.valueOf(rset.getInt("IE_TIPO")).toString()));
                _ingresoEgreso.setIeFecha(rset.getDate("IE_FECHA"));
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(ctrlCategorias.findCategorias(rset.getBigDecimal("CAT_ID")).getCatNombre());
                _ingresoEgreso.setCatId(_categorias);
                _ingresoEgreso.setIeCantidad(rset.getDouble("IE_CANTIDAD"));
                _unidades.setUmId(rset.getBigDecimal("UM_ID"));
                _unidades.setUmNombre(ctrlUnidades.findUnidadesDeMedida(rset.getBigDecimal("UM_ID")).getUmNombre());
                _ingresoEgreso.setUmId(_unidades);
                _ingresoEgreso.setIeMonto(rset.getDouble("IE_MONTO"));
                Datos.add(_ingresoEgreso);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar registros del proyecto." + e);
        }
        return null;
    }
    
    public Double gastos(Integer id){
        double gastos = 0;
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call gasto(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.executeQuery();
            
            gastos = cs.getDouble(2);
            claseConnect.CerrarConexionBD();
            return gastos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al cargar los gastos." + e);
        }
        return null;
    }
    
    public Double ingresos(Integer id){
        double ingresos = 0;
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call ingreso(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.executeQuery();
            
            ingresos = cs.getDouble(2);
            claseConnect.CerrarConexionBD();
            return ingresos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al cargar los ingresos." + e);
        }
        return null;
    }
    
    public void agregarATabla(ArrayList<IngresoEgreso> obj){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Descripcion");
        model.addColumn("Tipo");
        model.addColumn("Fecha");
        model.addColumn("Categoría");
        model.addColumn("Cantidad");
        model.addColumn("Medida");
        model.addColumn("Monto");
        model.addColumn("");
        if (obj.size() > 0) {
            
            Collections.sort(obj, new Comparator<IngresoEgreso>() {
                @Override
                public int compare(IngresoEgreso o1, IngresoEgreso o2) {
                    return o1.getIeId().compareTo(o2.getIeId());
                }

            });
            
            SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
            Object Datos[] = new Object[9];
            
            
            int cont = 0;
            for (Object valor : obj){
                Datos[0] = obj.get(cont).getIeId();
                Datos[1] = obj.get(cont).getIeDescripcion();
                if (obj.get(cont).getIeTipo().toString().equals("0")) {
                    Datos[2] = "Ingreso";
                }
                else{
                    Datos[2] = "Egreso";
                }
                Datos[3] = objSDF.format(obj.get(cont).getIeFecha());
                Datos[4] = obj.get(cont).getCatId().getCatNombre();
                Datos[5] = obj.get(cont).getIeCantidad();
                Datos[6] = obj.get(cont).getUmId().getUmNombre();
                Datos[7] = obj.get(cont).getIeMonto();
                Datos[8] = "Modificar";
                
                cont = cont +1 ;
                model.addRow(Datos);
            }
        }
        view.jTable1.setModel(model);
    }
    
    public void leerTabla(IngresoEgreso _ie){
        viewEditRegistro.jTextField1.setText(_ie.getIeId().toString());
        viewEditRegistro.txtDescripcion.setText(_ie.getIeDescripcion());
        if (_ie.getIeTipo().toString().equals("0")) {
            viewEditRegistro.cmbTipo.setSelectedIndex(0);
        }
        else{
            viewEditRegistro.cmbTipo.setSelectedIndex(1);
        }
        if (_ie.getIeCalidad().toString().equals("0")) {
            viewEditRegistro.cmbCalidad.setSelectedIndex(0);
        }
        else if(_ie.getIeCalidad().toString().equals("1")){
            viewEditRegistro.cmbCalidad.setSelectedIndex(1);
        }
        else if(_ie.getIeCalidad().toString().equals("2")){
            viewEditRegistro.cmbCalidad.setSelectedIndex(2);
        }
        else{
            viewEditRegistro.cmbCalidad.setSelectedIndex(3);
        }
        viewEditRegistro.txtCantidad.setText(_ie.getIeCantidad().toString());
        viewEditRegistro.spnFecha.setValue(_ie.getIeFecha());
        viewEditRegistro.txtMonto.setText(_ie.getIeMonto().toString());
        viewEditRegistro.cmbCategoria.removeAllItems();
        obtCategoriasACombo(viewEditRegistro.cmbCategoria, Integer.parseInt(_ie.getIeTipo().toString()));
        viewEditRegistro.cmbCategoria.setSelectedItem(_ie.getCatId().getCatNombre());
        viewEditRegistro.cmbUnidad.removeAllItems();
        obtUnidadesACombo(viewEditRegistro.cmbUnidad);
        viewEditRegistro.cmbUnidad.setSelectedItem(_ie.getUmId().getUmNombre());
        viewEditRegistro.jTextField1.setVisible(false);
        viewEditRegistro.setTitle("Editar registro");
        viewEditRegistro.setVisible(true);
        viewEditRegistro.getContentPane().setBackground(new Color(153,168,178));
        viewEditRegistro.setLocationRelativeTo(null);
    }
    
    public void buscarCategoriaID(String categoria) {
        ArrayList<Categorias> lst = new ArrayList<Categorias>();
        lst = ctrlCategorias.findSearch(categoria);
        if (lst.size() > 0) {
            _categorias.setCatId(lst.get(0).getCatId());
            _categorias.setCatNombre(lst.get(0).getCatNombre());
            _categorias.setCatTipo(lst.get(0).getCatTipo());
        }
    }
    
    public void buscarProyectoID(String nombre) {
        ArrayList<Proyecto> lst = new ArrayList<Proyecto>();
        lst = ctrlPtoyecto.findSearch(nombre);
        if (lst.size() > 0) {
            _proyecto.setProyId(lst.get(0).getProyId());
            _proyecto.setProyNombre(lst.get(0).getProyNombre());
            _proyecto.setProyFecha(lst.get(0).getProyFecha());
            _proyecto.setProyEstado(lst.get(0).getProyEstado());
        }
    }
    
    public void buscarMedidaID(String medida) {
        ArrayList<UnidadesDeMedida> lst = new ArrayList<UnidadesDeMedida>();
        lst = ctrlUnidades.findUnidadSearch(medida);
        if (lst.size() > 0) {
            _unidades.setUmId(lst.get(0).getUmId());
            _unidades.setUmNombre(lst.get(0).getUmNombre());
        }
    }
    
    public String buscarCalidadID(int id){
        String calidad = "";
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call calidadID(?,?)}");
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.executeQuery();
            
            calidad = cs.getString(2);
            claseConnect.CerrarConexionBD();
            return calidad;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al cargar los datos. " + e);
        }
        return null;
    }
    
    public ArrayList<Categorias> obtenerCategorias(int tipo){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs = claseConnect.con.prepareCall("{call categoriasCombo(?,?)}");
            cs.setInt(1, tipo);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.executeQuery();
            
            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Categorias> Datos = new ArrayList<Categorias>();
            
            while (rset.next()){
                _categorias = new Categorias();
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(rset.getString("CAT_NOMBRE"));
                _categorias.setCatTipo((new BigInteger(Integer.valueOf(rset.getInt("CAT_TIPO")).toString())));
                Datos.add(_categorias);
            }
            claseConnect.CerrarConexionBD();
            return Datos;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar categorías." + e);
        }
        return null;
    }
    
    public void obtUnidadesACombo(JComboBox c) {
        List<UnidadesDeMedida> lst = ctrlUnidades.findUnidadesDeMedidaEntities();
        for (UnidadesDeMedida uni : lst) {
            c.addItem(uni.getUmNombre());
        }
    }
    
    public void obtCategoriasACombo(JComboBox c, int tipo) {
        ArrayList<Categorias> lst = obtenerCategorias(tipo);
        for (Categorias uni : lst) {
            c.addItem(uni.getCatNombre());
        }
    }

    public void create(IngresoEgreso ingresoEgreso) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categorias catId = ingresoEgreso.getCatId();
            if (catId != null) {
                catId = em.getReference(catId.getClass(), catId.getCatId());
                ingresoEgreso.setCatId(catId);
            }
            Proyecto proyId = ingresoEgreso.getProyId();
            if (proyId != null) {
                proyId = em.getReference(proyId.getClass(), proyId.getProyId());
                ingresoEgreso.setProyId(proyId);
            }
            UnidadesDeMedida umId = ingresoEgreso.getUmId();
            if (umId != null) {
                umId = em.getReference(umId.getClass(), umId.getUmId());
                ingresoEgreso.setUmId(umId);
            }
            em.persist(ingresoEgreso);
            if (catId != null) {
                catId.getIngresoEgresoList().add(ingresoEgreso);
                catId = em.merge(catId);
            }
            if (proyId != null) {
                proyId.getIngresoEgresoList().add(ingresoEgreso);
                proyId = em.merge(proyId);
            }
            if (umId != null) {
                umId.getIngresoEgresoList().add(ingresoEgreso);
                umId = em.merge(umId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findIngresoEgreso(ingresoEgreso.getIeId()) != null) {
                throw new PreexistingEntityException("IngresoEgreso " + ingresoEgreso + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(IngresoEgreso ingresoEgreso) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            IngresoEgreso persistentIngresoEgreso = em.find(IngresoEgreso.class, ingresoEgreso.getIeId());
            Categorias catIdOld = persistentIngresoEgreso.getCatId();
            Categorias catIdNew = ingresoEgreso.getCatId();
            Proyecto proyIdOld = persistentIngresoEgreso.getProyId();
            Proyecto proyIdNew = ingresoEgreso.getProyId();
            UnidadesDeMedida umIdOld = persistentIngresoEgreso.getUmId();
            UnidadesDeMedida umIdNew = ingresoEgreso.getUmId();
            if (catIdNew != null) {
                catIdNew = em.getReference(catIdNew.getClass(), catIdNew.getCatId());
                ingresoEgreso.setCatId(catIdNew);
            }
            if (proyIdNew != null) {
                proyIdNew = em.getReference(proyIdNew.getClass(), proyIdNew.getProyId());
                ingresoEgreso.setProyId(proyIdNew);
            }
            if (umIdNew != null) {
                umIdNew = em.getReference(umIdNew.getClass(), umIdNew.getUmId());
                ingresoEgreso.setUmId(umIdNew);
            }
            ingresoEgreso = em.merge(ingresoEgreso);
            if (catIdOld != null && !catIdOld.equals(catIdNew)) {
                catIdOld.getIngresoEgresoList().remove(ingresoEgreso);
                catIdOld = em.merge(catIdOld);
            }
            if (catIdNew != null && !catIdNew.equals(catIdOld)) {
                catIdNew.getIngresoEgresoList().add(ingresoEgreso);
                catIdNew = em.merge(catIdNew);
            }
            if (proyIdOld != null && !proyIdOld.equals(proyIdNew)) {
                proyIdOld.getIngresoEgresoList().remove(ingresoEgreso);
                proyIdOld = em.merge(proyIdOld);
            }
            if (proyIdNew != null && !proyIdNew.equals(proyIdOld)) {
                proyIdNew.getIngresoEgresoList().add(ingresoEgreso);
                proyIdNew = em.merge(proyIdNew);
            }
            if (umIdOld != null && !umIdOld.equals(umIdNew)) {
                umIdOld.getIngresoEgresoList().remove(ingresoEgreso);
                umIdOld = em.merge(umIdOld);
            }
            if (umIdNew != null && !umIdNew.equals(umIdOld)) {
                umIdNew.getIngresoEgresoList().add(ingresoEgreso);
                umIdNew = em.merge(umIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = ingresoEgreso.getIeId();
                if (findIngresoEgreso(id) == null) {
                    throw new NonexistentEntityException("The ingresoEgreso with id " + id + " no longer exists.");
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
            IngresoEgreso ingresoEgreso;
            try {
                ingresoEgreso = em.getReference(IngresoEgreso.class, id);
                ingresoEgreso.getIeId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ingresoEgreso with id " + id + " no longer exists.", enfe);
            }
            Categorias catId = ingresoEgreso.getCatId();
            if (catId != null) {
                catId.getIngresoEgresoList().remove(ingresoEgreso);
                catId = em.merge(catId);
            }
            Proyecto proyId = ingresoEgreso.getProyId();
            if (proyId != null) {
                proyId.getIngresoEgresoList().remove(ingresoEgreso);
                proyId = em.merge(proyId);
            }
            UnidadesDeMedida umId = ingresoEgreso.getUmId();
            if (umId != null) {
                umId.getIngresoEgresoList().remove(ingresoEgreso);
                umId = em.merge(umId);
            }
            em.remove(ingresoEgreso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<IngresoEgreso> findIngresoEgresoEntities() {
        return findIngresoEgresoEntities(true, -1, -1);
    }

    public List<IngresoEgreso> findIngresoEgresoEntities(int maxResults, int firstResult) {
        return findIngresoEgresoEntities(false, maxResults, firstResult);
    }

    private List<IngresoEgreso> findIngresoEgresoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(IngresoEgreso.class));
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

    public IngresoEgreso findIngresoEgreso(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(IngresoEgreso.class, id);
        } finally {
            em.close();
        }
    }

    public int getIngresoEgresoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<IngresoEgreso> rt = cq.from(IngresoEgreso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public void CrearRegistro(){
        viewCreatRegistro.cmbUnidad.removeAllItems();
        obtUnidadesACombo(viewCreatRegistro.cmbUnidad);
        viewCreatRegistro.cmbCategoria.removeAllItems();
        obtCategoriasACombo(viewCreatRegistro.cmbCategoria, 0);
        viewCreatRegistro.cmbTipo.setSelectedIndex(0);
        viewCreatRegistro.setTitle("Nuevo Registro");
        viewCreatRegistro.setVisible(true);
        viewCreatRegistro.getContentPane().setBackground(new Color(153,168,178));
        viewCreatRegistro.setLocationRelativeTo(null);
    }
    
    public int buscarProyectoIDFiltro(String nombre) {
        ArrayList<Proyecto> lst = new ArrayList<Proyecto>();
        lst = ctrlPtoyecto.findSearch(nombre);
        int idSearch = 0;
        if (lst.size() > 0) {
            idSearch = Integer.parseInt(lst.get(0).getProyId().toString());
        }
        return idSearch;
    }
    
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == view.cmbFiltro) {
                if (view.cmbFiltro.getSelectedIndex() == 0) {
                    agregarATabla(registrosProyecto(buscarProyectoIDFiltro(view.lblNombre.getText())));
                }
                else if (view.cmbFiltro.getSelectedIndex() == 1) {
                    agregarATabla(registrosEgresos(buscarProyectoIDFiltro(view.lblNombre.getText())));
                }
                else if (view.cmbFiltro.getSelectedIndex() == 2) {
                    agregarATabla(registrosIngresos(buscarProyectoIDFiltro(view.lblNombre.getText())));
                }
                else if (view.cmbFiltro.getSelectedIndex() == 3) {
                    agregarATabla(registrosPerdidas(buscarProyectoIDFiltro(view.lblNombre.getText())));
                }
            }
            else if(ae.getSource() == viewEditRegistro.btnCancelar){
                viewEditRegistro.dispose();
            }
            else if(ae.getSource() == viewEditRegistro.btnGuardar){
                try {
                    formEditar();
                } catch (ParseException ex) {
                    Logger.getLogger(ProyectoJpaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (ae.getSource() == viewEditRegistro.cmbTipo) {
                if (viewEditRegistro.cmbTipo.getSelectedIndex() == 0) {
                    viewEditRegistro.cmbCategoria.removeAllItems();
                    obtCategoriasACombo(viewEditRegistro.cmbCategoria, 0);
                }
                else{
                    viewEditRegistro.cmbCategoria.removeAllItems();
                    obtCategoriasACombo(viewEditRegistro.cmbCategoria, 1);
                }
            }
            else if(ae.getSource() == viewCreatRegistro.btnGuardar){
                try {
                    formCrear();
                } catch (ParseException ex) {
                    Logger.getLogger(ProyectoJpaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (ae.getSource() == viewCreatRegistro.cmbTipo) {
                if (viewCreatRegistro.cmbTipo.getSelectedIndex() == 0) {
                    viewCreatRegistro.cmbCategoria.removeAllItems();
                    obtCategoriasACombo(viewCreatRegistro.cmbCategoria, 0);
                }
                else{
                    viewCreatRegistro.cmbCategoria.removeAllItems();
                    obtCategoriasACombo(viewCreatRegistro.cmbCategoria, 1);
                }
            }
            else if(ae.getSource() == viewCreatRegistro.btnCancelar){
                viewCreatRegistro.dispose();
            }
            else if (ae.getSource() == viewEditRegistro.btnACategoria) {
                ViewCategorias cate = new ViewCategorias();
                CategoriasJpaController ctrlcate = new CategoriasJpaController(Entity_Main.getInstance(), cate);
                cate.setLocationRelativeTo(null);
                cate.setVisible(true);
                ctrlCategorias.iniciarFormDetalle();
            }
            else if (ae.getSource() == viewEditRegistro.btnAUnidad) {
                viewUnidades_de_medida uni = new viewUnidades_de_medida();
                UnidadesDeMedidaJpaController ctrluni = new UnidadesDeMedidaJpaController(Entity_Main.getInstance(), uni);
                ctrluni.iniciarForm();
            }
            else if (ae.getSource() == viewCreatRegistro.btnACategoria) {
                ViewCategorias cate = new ViewCategorias();
                CategoriasJpaController ctrlcate = new CategoriasJpaController(Entity_Main.getInstance(), cate);
                cate.setLocationRelativeTo(null);
                cate.setVisible(true);
                ctrlCategorias.iniciarFormDetalle();
            }
            else if (ae.getSource() == viewCreatRegistro.btnAUnidad) {
                viewUnidades_de_medida uni = new viewUnidades_de_medida();
                UnidadesDeMedidaJpaController ctrluni = new UnidadesDeMedidaJpaController(Entity_Main.getInstance(), uni);
                ctrluni.iniciarForm();
            }
            else if (ae.getSource() == viewCreatRegistro.idRefrescar1){
                if(viewCreatRegistro.cmbTipo.getSelectedIndex() == 0) {
                   viewCreatRegistro.cmbCategoria.removeAllItems();
                   obtCategoriasACombo(viewCreatRegistro.cmbCategoria, 0); 
                }
                else {
                    viewCreatRegistro.cmbCategoria.removeAllItems();
                    obtCategoriasACombo(viewCreatRegistro.cmbCategoria, 1); 
                }
            }
            else if (ae.getSource() == viewCreatRegistro.idRefrescar2){
                viewCreatRegistro.cmbUnidad.removeAllItems();
                obtUnidadesACombo(viewCreatRegistro.cmbUnidad);
            }
            else if (ae.getSource() == viewEditRegistro.btnRefrescarUnidad){
                viewEditRegistro.cmbUnidad.removeAllItems();
                obtUnidadesACombo(viewEditRegistro.cmbUnidad);
                viewEditRegistro.cmbUnidad.setSelectedItem(0);
            }
            else if (ae.getSource() == viewEditRegistro.btnRefrescarCate){
                if(viewEditRegistro.cmbTipo.getSelectedIndex() == 0) {
                   viewEditRegistro.cmbCategoria.removeAllItems();
                   obtCategoriasACombo(viewEditRegistro.cmbCategoria, 0); 
                }
                else {
                    viewEditRegistro.cmbCategoria.removeAllItems();
                    obtCategoriasACombo(viewEditRegistro.cmbCategoria, 1); 
                }
            }
            else if (ae.getSource()== view.btnReporte) {
                //int resp = JOptionPane.showConfirmDialog(null, "Desea un reporte normal o un reporte de resumen mensual?", "Normal", JOptionPane., JOptionPane.QUESTION_MESSAGE);
                
                Object[] optiones = { "Normal", "Resumen", "Cancelar" };

                JPanel panel = new JPanel();
                panel.add(new JLabel("Desea un reporte normal o un resumen mensual?"));           

                int resultado = JOptionPane.showOptionDialog(null, panel, "Reporte de gastos",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, optiones, null);
                
                if (resultado==0) {
                    ObtenerDatos();
                    try{
                        String nompro = view.lblNombre.getText().toString();
                        String gasto = view.lblGasto.getText().toString();
                        String ingreso = view.lblIngreso.getText().toString();
                        String balance = view.lblBalance.getText().toString();
                        Map parametros = new HashMap();
                        parametros.put("Nombre",nompro);
                        parametros.put("Gasto",gasto);
                        parametros.put("Ingreso",ingreso);
                        parametros.put("Balance",balance);

                        JasperReport rep = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/Rep_IE_Normal.jasper"));                 
                        JasperPrint jasperPrint = JasperFillManager.fillReport(rep, parametros,datasource);

                        JasperViewer Jview = new JasperViewer(jasperPrint,false);
                        Jview.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        Jview.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(view, "error:"+ex);
                    }
                }
                else if (resultado==1) {
                    LlenarResumen();
                    try{
                        String nompro = view.lblNombre.getText().toString();
                        String gasto = view.lblGasto.getText().toString();
                        String ingreso = view.lblIngreso.getText().toString();
                        String balance = view.lblBalance.getText().toString();
                        Map parametros = new HashMap();
                        parametros.put("Proyecto",nompro);
                        parametros.put("Gastos",gasto);
                        parametros.put("Ingresos",ingreso);
                        parametros.put("Balance",balance);

                        JasperReport rep = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/Rep_IE_Resumen.jasper"));                 
                        JasperPrint jasperPrint = JasperFillManager.fillReport(rep, parametros,datasource2);

                        JasperViewer Jview = new JasperViewer(jasperPrint,false);
                        Jview.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        Jview.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(view, "error:"+ex);
                    }
                }                
            }
        }
    };
    
    
    public void formEditar() throws ParseException{
        if (viewEditRegistro.txtDescripcion.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Ingrese la descripción del registro");
        }
        else if (viewEditRegistro.cmbTipo.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Seleccione un tipo de registro");
        }
        else if (viewEditRegistro.cmbCategoria.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Seleccione una categoría para el registro");
        }
        else if (viewEditRegistro.cmbCalidad.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Seleccione una calidad para el registro");
        }
        else if (viewEditRegistro.txtCantidad.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Ingrese una cantidad para el registro");
        }
        else if (viewEditRegistro.cmbUnidad.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Seleccione una unidad de medida para el registro");
        }
        else if (viewEditRegistro.txtMonto.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewEditRegistro, "Ingrese el monto para el registro");
        }
        else{
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
            String spinnerValue = formater.format(viewEditRegistro.spnFecha.getValue());
            Date date = formater.parse(spinnerValue);
            
            _ingresoEgreso = new IngresoEgreso();
            buscarProyectoID(view.lblNombre.getText());
            _ingresoEgreso.setProyId(_proyecto);
            _ingresoEgreso.setIeId(BigDecimal.valueOf(Double.parseDouble(viewEditRegistro.jTextField1.getText().trim().toString())));
            _ingresoEgreso.setIeDescripcion(viewEditRegistro.txtDescripcion.getText().trim().toString());
            _ingresoEgreso.setIeTipo(new BigInteger(String.valueOf(viewEditRegistro.cmbTipo.getSelectedIndex())));
            buscarCategoriaID(String.valueOf(viewEditRegistro.cmbCategoria.getSelectedItem()));
            _ingresoEgreso.setCatId(_categorias);
            _ingresoEgreso.setIeCalidad(new BigInteger(String.valueOf(viewEditRegistro.cmbCalidad.getSelectedIndex())));
            _ingresoEgreso.setIeCantidad(Double.parseDouble(viewEditRegistro.txtCantidad.getText().trim().toString()));
            buscarMedidaID(String.valueOf(viewEditRegistro.cmbUnidad.getSelectedItem()));
            _ingresoEgreso.setUmId(_unidades);
            _ingresoEgreso.setIeFecha(date);
            _ingresoEgreso.setIeMonto(Double.parseDouble(viewEditRegistro.txtMonto.getText().trim().toString()));
            
            
            try {
                edit(_ingresoEgreso);
                agregarATabla(registrosProyecto(Integer.parseInt(_proyecto.getProyId().toString())));
                double gasto = gastos(Integer.parseInt(_proyecto.getProyId().toString()));
                double ingreso = ingresos(Integer.parseInt(_proyecto.getProyId().toString()));
                view.lblGasto.setText("Gastos: $" + gasto);
                view.lblIngreso.setText("Ingresos: $" + ingreso);
                view.lblBalance.setText("Balance: $" + String.valueOf(ingreso-gasto));
                viewEditRegistro.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(viewEditRegistro, "Asegúrese que los campos estén correctos. " + ex.getMessage());
            }
        }
    }
    
    public void formCrear() throws ParseException{
        if (viewCreatRegistro.txtDescripcion.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Ingrese la descripción del registro");
        }
        else if (viewCreatRegistro.cmbTipo.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Seleccione un tipo de registro");
        }
        else if (viewCreatRegistro.cmbCategoria.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Seleccione una categoría para el registro");
        }
        else if (viewCreatRegistro.cmbCalidad.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Seleccione una calidad para el registro");
        }
        else if (viewCreatRegistro.txtCantidad.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Ingrese una cantidad para el registro");
        }
        else if (viewCreatRegistro.cmbUnidad.getSelectedIndex()<0) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Seleccione una unidad de medida para el registro");
        }
        else if (viewCreatRegistro.txtMonto.getText().trim().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Ingrese el monto para el registro");
        }
        else if (viewCreatRegistro.spnFecha.getValue().toString().isEmpty()) {
            JOptionPane.showMessageDialog(viewCreatRegistro, "Ingrese la fecha del registro");
        }
        else{
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
            String spinnerValue = formater.format(viewCreatRegistro.spnFecha.getValue());
            Date date = formater.parse(spinnerValue);
            
            
            List<IngresoEgreso> listp = findIngresoEgresoEntities();
            Collections.sort(listp, new Comparator<IngresoEgreso>() {
                @Override
                public int compare(IngresoEgreso o1, IngresoEgreso o2) {
                    return o1.getIeId().compareTo(o2.getIeId());
                }

            });
            BigDecimal idcar = new BigDecimal(Integer.parseInt(listp.get(listp.size() - 1).getIeId().toString()) + 1);
            _ingresoEgreso = new IngresoEgreso();
            buscarProyectoID(view.lblNombre.getText());
            _ingresoEgreso.setProyId(_proyecto);
            _ingresoEgreso.setIeId(idcar);
            _ingresoEgreso.setIeDescripcion(viewCreatRegistro.txtDescripcion.getText().trim().toString());
            _ingresoEgreso.setIeTipo(new BigInteger(String.valueOf(viewCreatRegistro.cmbTipo.getSelectedIndex())));
            buscarCategoriaID(String.valueOf(viewCreatRegistro.cmbCategoria.getSelectedItem()));
            _ingresoEgreso.setCatId(_categorias);
            _ingresoEgreso.setIeCalidad(new BigInteger(String.valueOf(viewCreatRegistro.cmbCalidad.getSelectedIndex())));
            _ingresoEgreso.setIeCantidad(Double.parseDouble(viewCreatRegistro.txtCantidad.getText().trim().toString()));
            buscarMedidaID(String.valueOf(viewCreatRegistro.cmbUnidad.getSelectedItem()));
            _ingresoEgreso.setUmId(_unidades);
            _ingresoEgreso.setIeFecha(date);
            _ingresoEgreso.setIeMonto(Double.parseDouble(viewCreatRegistro.txtMonto.getText().trim().toString()));
            
            
            try {
                create(_ingresoEgreso);
                agregarATabla(registrosProyecto(Integer.parseInt(_proyecto.getProyId().toString())));
                double gasto = gastos(Integer.parseInt(_proyecto.getProyId().toString()));
                double ingreso = ingresos(Integer.parseInt(_proyecto.getProyId().toString()));
                view.lblGasto.setText("Gastos: $" + gasto);
                view.lblIngreso.setText("Ingresos: $" + ingreso);
                view.lblBalance.setText("Balance: $" + String.valueOf(ingreso-gasto));
                viewCreatRegistro.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(viewCreatRegistro, "Asegúrese que los campos estén correctos. " + ex.getMessage());
            }
        }
    }
    
    IngresoEgresoDS datasource = new IngresoEgresoDS();
    public void ObtenerDatos(){
        for (int f = 0; f < view.jTable1.getRowCount(); f++) {
            EEgresoIngreso elemento = new EEgresoIngreso();
            elemento.setDescripcion(view.jTable1.getValueAt(f, 1).toString());
            elemento.setTipo(view.jTable1.getValueAt(f, 2).toString());
            elemento.setFecha(view.jTable1.getValueAt(f, 3).toString());
            elemento.setCategoria(view.jTable1.getValueAt(f, 4).toString());
            elemento.setCantidad(view.jTable1.getValueAt(f, 5).toString());
            elemento.setMedida(view.jTable1.getValueAt(f, 6).toString());
            elemento.setMonto(Double.parseDouble(view.jTable1.getValueAt(f, 7).toString()));
            datasource.addIE(elemento);
        }
    }
    
    IEReumenDS datasource2 = new IEReumenDS();
    public void LlenarResumen(){
       try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call LlamarResumen(?,?)}");
            cs.setString(1, view.lblNombre.getText().trim());
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);            
            while (rset.next()) {
                EIEResumen elemento = new EIEResumen();
                elemento.setFecha(rset.getString("MES"));
                elemento.setTipo(rset.getString("TIPO"));
                elemento.setMonto(rset.getDouble("MONTO"));
                datasource2.addResumen(elemento);
            }                           
            claseConnect.CerrarConexionBD();                         
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "El registro que intenta eliminar no existe.");
        }        
    }
}
