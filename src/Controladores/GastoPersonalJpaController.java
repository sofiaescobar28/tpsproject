/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Cargos;
import AccesoDatos.Conexion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AccesoDatos.Empleados;
import AccesoDatos.Entity_Main;
import AccesoDatos.GastoPersonal;
import AccesoDatos.Proyecto;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import Reportes.entidades.EGasto;
import Reportes.entidades.EPlanilla;
import Reportes.entidades.HistorialDePagoDS;
import gestor_de_proyectos.interfaces.ViewPago_de_personal_Historico;
import gestor_de_proyectos.interfaces.viewPagar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Policy;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author Usuario
 */
public class GastoPersonalJpaController implements Serializable {

    public GastoPersonalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(GastoPersonal gastoPersonal) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados empId = gastoPersonal.getEmpId();            
            //gastoPersonal.setGpId(new BigDecimal(getGastoPersonalCount()+1));
            if (empId != null) {
                empId = em.getReference(empId.getClass(), empId.getEmpId());
                gastoPersonal.setEmpId(empId);
            }
            Proyecto proyId = gastoPersonal.getProyId();
            if (proyId != null) {
                proyId = em.getReference(proyId.getClass(), proyId.getProyId());
                gastoPersonal.setProyId(proyId);
            }
            em.persist(gastoPersonal);
            if (empId != null) {
                empId.getGastoPersonalList().add(gastoPersonal);
                empId = em.merge(empId);
            }
            if (proyId != null) {
                proyId.getGastoPersonalList().add(gastoPersonal);
                proyId = em.merge(proyId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findGastoPersonal(gastoPersonal.getGpId()) != null) {
                throw new PreexistingEntityException("GastoPersonal " + gastoPersonal + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(GastoPersonal gastoPersonal) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            GastoPersonal persistentGastoPersonal = em.find(GastoPersonal.class, gastoPersonal.getGpId());
            Empleados empIdOld = persistentGastoPersonal.getEmpId();
            Empleados empIdNew = gastoPersonal.getEmpId();
            Proyecto proyIdOld = persistentGastoPersonal.getProyId();
            Proyecto proyIdNew = gastoPersonal.getProyId();
            if (empIdNew != null) {
                empIdNew = em.getReference(empIdNew.getClass(), empIdNew.getEmpId());
                gastoPersonal.setEmpId(empIdNew);
            }
            if (proyIdNew != null) {
                proyIdNew = em.getReference(proyIdNew.getClass(), proyIdNew.getProyId());
                gastoPersonal.setProyId(proyIdNew);
            }
            gastoPersonal = em.merge(gastoPersonal);
            if (empIdOld != null && !empIdOld.equals(empIdNew)) {
                empIdOld.getGastoPersonalList().remove(gastoPersonal);
                empIdOld = em.merge(empIdOld);
            }
            if (empIdNew != null && !empIdNew.equals(empIdOld)) {
                empIdNew.getGastoPersonalList().add(gastoPersonal);
                empIdNew = em.merge(empIdNew);
            }
            if (proyIdOld != null && !proyIdOld.equals(proyIdNew)) {
                proyIdOld.getGastoPersonalList().remove(gastoPersonal);
                proyIdOld = em.merge(proyIdOld);
            }
            if (proyIdNew != null && !proyIdNew.equals(proyIdOld)) {
                proyIdNew.getGastoPersonalList().add(gastoPersonal);
                proyIdNew = em.merge(proyIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = gastoPersonal.getGpId();
                if (findGastoPersonal(id) == null) {
                    throw new NonexistentEntityException("The gastoPersonal with id " + id + " no longer exists.");
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
            GastoPersonal gastoPersonal;
            try {
                gastoPersonal = em.getReference(GastoPersonal.class, id);
                gastoPersonal.getGpId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gastoPersonal with id " + id + " no longer exists.", enfe);
            }
            Empleados empId = gastoPersonal.getEmpId();
            if (empId != null) {
                empId.getGastoPersonalList().remove(gastoPersonal);
                empId = em.merge(empId);
            }
            Proyecto proyId = gastoPersonal.getProyId();
            if (proyId != null) {
                proyId.getGastoPersonalList().remove(gastoPersonal);
                proyId = em.merge(proyId);
            }
            em.remove(gastoPersonal);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<GastoPersonal> findGastoPersonalEntities() {
        return findGastoPersonalEntities(true, -1, -1);
    }

    public List<GastoPersonal> findGastoPersonalEntities(int maxResults, int firstResult) {
        return findGastoPersonalEntities(false, maxResults, firstResult);
    }

    private List<GastoPersonal> findGastoPersonalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(GastoPersonal.class));
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

    public GastoPersonal findGastoPersonal(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GastoPersonal.class, id);
        } finally {
            em.close();
        }
    }

    public int getGastoPersonalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<GastoPersonal> rt = cq.from(GastoPersonal.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    ViewPago_de_personal_Historico vPagoh = new ViewPago_de_personal_Historico();
    //int pro;
    Conexion claseConnect = new Conexion();
    Empleados emp = new Empleados();
    
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource()== vPagoh.btnTodo) {
                llenarTabla(findGastoPersonalEntities(),Integer.parseInt(vPagoh.lblProID.getText()));
            }            
            else if (e.getSource() == vPagoh.btnReporte) {
                ObtenerDatos();
                try{
                    String nompro = vPagoh.lblNomProy.getText().toString();
                    Map parametros = new HashMap();
                    parametros.put("NPro",nompro);
                    
                    JasperReport rep = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/Rep_HistorialDePagos.jasper"));                 
                    JasperPrint jasperPrint = JasperFillManager.fillReport(rep, parametros,datasource);
                    
                    JasperViewer Jview = new JasperViewer(jasperPrint,false);
                    Jview.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    Jview.setVisible(true);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(vPagoh, "error:"+ex);
                }
            }
        }
    };
        
    public GastoPersonalJpaController(EntityManagerFactory emf,ViewPago_de_personal_Historico ph) {
        this.emf = emf;
        this.vPagoh = ph;
        this.vPagoh.btnTodo.addActionListener(al);
        this.vPagoh.btnReporte.addActionListener(al);
        this.vPagoh.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                if (!vPagoh.txtBuscar.getText().trim().isEmpty()) {
                    if (e.getSource() == vPagoh.txtBuscar) {
                        if (vPagoh.radEmp.isSelected()) {
                            ArrayList<GastoPersonal> list = new ArrayList<GastoPersonal>();                                                    
                            list = buscarPorEmp(vPagoh.txtBuscar.getText(),Integer.parseInt(vPagoh.lblProID.getText()));
                            if (list.size()>0) {
                                llenarTabla(list,Integer.parseInt(vPagoh.lblProID.getText()));
                            }
                        } else if (vPagoh.radCom.isSelected()) {
                            ArrayList<GastoPersonal> list = new ArrayList<GastoPersonal>(); 
                            list = buscarPorComentario(vPagoh.txtBuscar.getText(),Integer.parseInt(vPagoh.lblProID.getText()));
                            if (list.size()>0) {
                                llenarTabla(list,Integer.parseInt(vPagoh.lblProID.getText()));
                            }
                        }
                        else if (vPagoh.radCarTemp.isSelected()) {
                            ArrayList<GastoPersonal> list = new ArrayList<GastoPersonal>(); 
                            list = buscarPorCargoT(vPagoh.txtBuscar.getText(),Integer.parseInt(vPagoh.lblProID.getText()));
                            if (list.size()>0) {
                                llenarTabla(list,Integer.parseInt(vPagoh.lblProID.getText()));
                            }
                        }
                    }
                    else{
                        llenarTabla(findGastoPersonalEntities(),Integer.parseInt(vPagoh.lblProID.getText()));
                    }
                }
            }
        });
    }
    
    public void iniciarPagoH (String nombrePr,int prId)
    {
        vPagoh.setTitle("Historial de pago de personal");
        vPagoh.lblNomProy.setText("Proyecto: "+nombrePr);        
        vPagoh.setLocationRelativeTo(null);
        vPagoh.setVisible(true);
        vPagoh.lblProID.setText(String.valueOf(prId));
        vPagoh.lblProID.setVisible(false);
        llenarTabla(findGastoPersonalEntities(),Integer.parseInt(vPagoh.lblProID.getText()));
    }
    private void llenarTabla(List<GastoPersonal> _listado,int proy)
    {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("#");
        model.addColumn("EMPLEADO");
        model.addColumn("CARGO(original)");
        model.addColumn("CARGO(Temporal)");
        model.addColumn("FECHA");            
        model.addColumn("PAGO");
        model.addColumn("COMENTARIO");
        
        Collections.sort(_listado, new Comparator<GastoPersonal>() {
            @Override
            public int compare(GastoPersonal o1, GastoPersonal o2) {
                return o1.getGpId().compareTo(o2.getGpId());
            }
        });
        if (_listado.size()>0) {
            SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
            Object Datos[] = new Object[7];                        
            int pro;
            for (int i = 0; i < _listado.size(); i++) {
                pro = _listado.get(i).getProyId().getProyId().intValueExact();
                if (pro == proy ) {                                    
                    Datos[0]=_listado.get(i).getGpId();
                    if (_listado.get(i).getEmpId()!=null) {
                        Datos[1]=_listado.get(i).getEmpId().getEmpNombre();                                        
                        Datos[2]=_listado.get(i).getEmpId().getCarId().getCargos();
                    }
                    Cargos c = new Cargos();
                    CargosJpaController ctrl = new CargosJpaController(Entity_Main.getInstance());
                    c=ctrl.findCargos(new BigDecimal(_listado.get(i).getGpCargo()));
                    Datos[3]=c.getCargos();
                    Datos[4]=objSDF.format(_listado.get(i).getGpFecha());
                    Datos[5]=_listado.get(i).getGpPago();
                    Datos[6]=_listado.get(i).getGpComentario();
                    model.addRow(Datos);
                }
            }            
        }
        vPagoh.dgvpagos.setModel(model);
    }
    public ArrayList<GastoPersonal> buscarPorEmp (String nombre,int proy){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call ObtenerGpPorNombre(?,?,?)}");
            cs.setString(1,nombre );
            cs.setInt(2,proy );
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<GastoPersonal> Datos = new ArrayList<GastoPersonal>();

           while (rset.next()) {
               GastoPersonal _gp = new GastoPersonal();
               BigDecimal id =rset.getBigDecimal("GP_ID");
               _gp=findGastoPersonal(id);
               Datos.add(_gp);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(null, "Sucedió un problema al buscar.");

        }
        return null;
    }
    
    public ArrayList<GastoPersonal> buscarPorComentario (String comentario,int proy){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call ObtenerGpPorComentario(?,?,?)}");
            cs.setString(1,comentario );
            cs.setInt(2,proy );
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<GastoPersonal> Datos = new ArrayList<GastoPersonal>();

           while (rset.next()) {
               GastoPersonal _gp = new GastoPersonal();
               BigDecimal id =rset.getBigDecimal("GP_ID");
               _gp=findGastoPersonal(id);
               Datos.add(_gp);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(null, "Sucedió un problema al buscar.");

        }
        return null;
    }
    
    public ArrayList<GastoPersonal> buscarPorCargoT (String cargoT,int proy){
        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call ObtenerGpPorCargoT(?,?,?)}");
            cs.setString(1,cargoT );
            cs.setInt(2,proy );
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<GastoPersonal> Datos = new ArrayList<GastoPersonal>();

           while (rset.next()) {
               GastoPersonal _gp = new GastoPersonal();
               BigDecimal id =rset.getBigDecimal("GP_ID");
               _gp=findGastoPersonal(id);
               Datos.add(_gp);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(null, "Sucedió un problema al buscar.");

        }
        return null;
    }    
    ////////////////////obtener objetos para el reporte
    HistorialDePagoDS datasource = new HistorialDePagoDS();    
    public void ObtenerDatos(){
        //List ListadoGastos = new ArrayList<>();
        for (int f = 0; f < vPagoh.dgvpagos.getRowCount(); f++) {
            EGasto elemento = new EGasto();
            elemento.setNombre(vPagoh.dgvpagos.getValueAt(f,1).toString());
            elemento.setCargoO(vPagoh.dgvpagos.getValueAt(f,2).toString());
            elemento.setCargoT(vPagoh.dgvpagos.getValueAt(f,3).toString());
            elemento.setFecha(vPagoh.dgvpagos.getValueAt(f,4).toString());
            elemento.setPago(Double.parseDouble(vPagoh.dgvpagos.getValueAt(f,5).toString()));
            elemento.setComentario(vPagoh.dgvpagos.getValueAt(f,6).toString());            
            datasource.addGasto(elemento);
        }        
    }    
}
