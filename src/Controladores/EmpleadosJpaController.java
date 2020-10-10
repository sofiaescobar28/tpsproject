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
import AccesoDatos.Cargos;
import AccesoDatos.Conexion;
import AccesoDatos.Empleados;
import AccesoDatos.Entity_Main;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.ViewAdministrar_Proyecto;
import gestor_de_proyectos.interfaces.ViewCargos;
import gestor_de_proyectos.interfaces.ViewPlanilla;
import gestor_de_proyectos.interfaces.viewEditar_Empleado;
import gestor_de_proyectos.interfaces.viewEmpleados;
import gestor_de_proyectos.interfaces.viewNuevo_Empleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Spinner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author Manuel
 */
public class EmpleadosJpaController implements Serializable {

    viewEmpleados view = new viewEmpleados();
    viewNuevo_Empleado vNuevoE = new viewNuevo_Empleado();
    viewEditar_Empleado vEditE = new viewEditar_Empleado();
    Empleados _emp = new Empleados();
    CargosJpaController ctrlCargos = new CargosJpaController(Entity_Main.getInstance());
    ViewCargos vcargos = new ViewCargos();
    ViewPlanilla viewPlanilla = new ViewPlanilla();
    ViewAdministrar_Proyecto viewProyectos = new ViewAdministrar_Proyecto();
    Cargos _cargos = new Cargos();
    Conexion claseConnect = new Conexion();
    int fila = -1;
    int columna = -1;
    int id_proy;
    
    public EmpleadosJpaController(EntityManagerFactory emf, ViewPlanilla view2){
        this.emf = emf;
        this.viewPlanilla = view2;
        this.viewPlanilla.btnAdministrar.addActionListener(alP);
        this.viewPlanilla.btnBuscar.addActionListener(alP);
    }

    public EmpleadosJpaController(EntityManagerFactory emf, viewEmpleados view) {
        this.view = view;
        this.view.btnNuevoempleado.addActionListener(al);
        this.vNuevoE.btnGuardar.addActionListener(al);
        this.vNuevoE.btnCancelar.addActionListener(al);
        this.vEditE.btnCancelar.addActionListener(al);
        this.vEditE.btnGuardar.addActionListener(al);
        this.view.txtBuscar.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                if (e.getSource() == view.txtBuscar) {
                    if (view.rdCargo.isSelected()) {
                        ArrayList<Empleados> list = new ArrayList<>();
                        if (!view.txtBuscar.getText().trim().isEmpty()) {
                            list = findSearchCargo(view.txtBuscar.getText());
                            if (list.size()>0) {
                            agregarATabla(list);
                        }
                        }else{
                            agregarATabla(findEmpleadosEntities());
                        }
                        
                        
                    } else if (view.radEmpleado.isSelected()) {
                        ArrayList<Empleados> list = new ArrayList<>();
                        if (!view.txtBuscar.getText().trim().isEmpty()) {
                            list = findSearchEmpleado(view.txtBuscar.getText());
                            if (list.size()>0) {
                            agregarATabla(list);
                        }
                        }else{
                            agregarATabla(findEmpleadosEntities());
                        }
                    }

                }
            }
        });
        this.view.jTableEmpleados.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == view.jTableEmpleados) {
                    leerTabla();
                }
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });

        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public void iniciarForm() {
        view.setTitle("Formulario Cargos");
        List<Empleados> ls = findEmpleadosEntities();
        agregarATabla(ls);
        view.setLocationRelativeTo(null);
    }
    
    public void iniciarFormPlanilla(int id, String nombre){
        viewPlanilla.setTitle("Planilla");
        viewPlanilla.lblNombre.setText("Planilla-Proyecto: " + nombre);
        agregarATablaPlanilla(findEmpleadosEntities());
        viewPlanilla.setVisible(true);
        viewPlanilla.setLocationRelativeTo(null);
    }

    public void leerTabla() {
        fila = view.jTableEmpleados.getSelectedRow();
        columna = view.jTableEmpleados.getSelectedColumn();
        if (columna == 6) {
            obtenerObjeto(fila);

            vEditE.txtEmpleado.setText(_emp.getEmpNombre());
            vEditE.txtTelefono.setText(_emp.getEmpTelefono());
            vEditE.txtSalario.setText(_emp.getEmpSalario().toString());
            vEditE.cmbestado.setSelectedIndex(Integer.parseInt(_emp.getEmpEstado().toString()));
            vEditE.txtEmpleado.setText(_emp.getEmpNombre());
            vEditE.cmbcargo.removeAllItems();
            obtCargoACombo(vEditE.cmbcargo);
            vEditE.cmbcargo.setSelectedItem(_cargos.getCargos());
//            int val = _categorias.getCatId().intValue();

            vEditE.setLocationRelativeTo(null);
            vEditE.setVisible(true);

        }

    }

    public ArrayList<Empleados> findSearchEmpleado(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findempleadosNombreP(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Empleados> Datos = new ArrayList<Empleados>();

           while (rset.next()) {
                _emp.setEmpId(rset.getBigDecimal("EMP_ID"));
                _emp.setEmpNombre(rset.getString("EMP_NOMBRE"));
                _cargos.setCargosId(rset.getBigDecimal("CAR_ID"));
                _cargos.setCargos(ctrlCargos.findCargos(rset.getBigDecimal("CAR_ID")).getCargos());
                _emp.setCarId(_cargos);
                _emp.setEmpSalario(Double.valueOf(rset.getDouble("EMP_SALARIO")));
                _emp.setEmpEstado(BigInteger.valueOf(rset.getInt("EMP_ESTADO")));
                _emp.setEmpTelefono(rset.getString("EMP_TELEFONO"));
                Datos.add(_emp);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar.");

        }
        return null;

    }
    
    public ArrayList<Empleados> findSearchEmpleadoL(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findempleadosNombrePL(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Empleados> Datos = new ArrayList<Empleados>();

           while (rset.next()) {
                _emp.setEmpId(rset.getBigDecimal("EMP_ID"));
                _emp.setEmpNombre(rset.getString("EMP_NOMBRE"));
                _cargos.setCargosId(rset.getBigDecimal("CAR_ID"));
                _cargos.setCargos(ctrlCargos.findCargos(rset.getBigDecimal("CAR_ID")).getCargos());
                _emp.setCarId(_cargos);
                _emp.setEmpSalario(Double.valueOf(rset.getDouble("EMP_SALARIO")));
                _emp.setEmpEstado(BigInteger.valueOf(rset.getInt("EMP_ESTADO")));
                _emp.setEmpTelefono(rset.getString("EMP_TELEFONO"));
                Datos.add(_emp);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar.");

        }
        return null;

    }

    public ArrayList<Empleados> findSearchCargo(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findEmpleadosCArgo(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Empleados> Datos = new ArrayList<Empleados>();

            while (rset.next()) {
                _emp.setEmpId(rset.getBigDecimal("EMP_ID"));
                _emp.setEmpNombre(rset.getString("EMP_NOMBRE"));
                _cargos.setCargosId(rset.getBigDecimal("CAR_ID"));
                _cargos.setCargos(ctrlCargos.findCargos(rset.getBigDecimal("CAR_ID")).getCargos());
                _emp.setCarId(_cargos);
                _emp.setEmpSalario(Double.valueOf(rset.getDouble("EMP_SALARIO")));
                _emp.setEmpEstado(BigInteger.valueOf(rset.getInt("EMP_ESTADO")));
                _emp.setEmpTelefono(rset.getString("EMP_TELEFONO"));
                Datos.add(_emp);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "Sucedió un problema al buscar." + ex);

        }
        return null;

    }

    public void obtenerObjeto(int fila) {
//        _categorias = new Categorias();
        _emp.setEmpId(BigDecimal.valueOf(Double.parseDouble(view.jTableEmpleados.getValueAt(fila, 0).toString())));
        _emp.setEmpNombre(view.jTableEmpleados.getValueAt(fila, 1).toString());
        buscarCargoID(view.jTableEmpleados.getValueAt(fila, 2).toString());
        _emp.setCarId(_cargos);
        _emp.setEmpSalario(Double.parseDouble(view.jTableEmpleados.getValueAt(fila, 3).toString()));
        _emp.setEmpTelefono(view.jTableEmpleados.getValueAt(fila, 4).toString());
        _emp.setEmpEstado(valorEstado(view.jTableEmpleados.getValueAt(fila, 5).toString()));
    }

    public void obtCargoACombo(JComboBox c) {
        List<Cargos> lst = ctrlCargos.findCargosEntities();
        for (Cargos carg : lst) {
            c.addItem(carg.getCargos());
        }
    }

    public BigInteger valorEstado(String s) {

        if (s.equals("Inactivo")) {
            return BigInteger.valueOf(0);
        } else if (s.equals("Activo")) {
            return BigInteger.valueOf(1);
        }
        return BigInteger.valueOf(-1);
    }
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == view.btnNuevoempleado) {

                obtCargoACombo(vNuevoE.cmbcargo);
                vNuevoE.setVisible(true);
                vNuevoE.setLocationRelativeTo(null);

            } else if (e.getSource() == vNuevoE.btnGuardar) {
                if (!vNuevoE.txtEmpleado.getText().trim().isEmpty() && !vNuevoE.txtSalario.getText().trim().isEmpty()) {
                    _emp.setEmpNombre(vNuevoE.txtEmpleado.getText());
                    _emp.setEmpSalario(Double.valueOf(vNuevoE.txtSalario.getText()));
                    _emp.setEmpTelefono(vNuevoE.txtTelefono.getText());
                    buscarCargoID(vNuevoE.cmbcargo.getItemAt(vNuevoE.cmbcargo.getSelectedIndex()));
                    _emp.setCarId(_cargos);
                    _emp.setEmpEstado(BigInteger.valueOf(vNuevoE.cmbestado.getSelectedIndex()));

                    try {
                        create(_emp);
                        agregarATabla(findEmpleadosEntities());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(vNuevoE, "Ha sucedido un error al guardar.");
                        Logger.getLogger(EmpleadosJpaController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(vNuevoE, "Los campos no pueden quedar en blanco.");
                }
            } else if (e.getSource() == vNuevoE.btnCancelar) {
                vNuevoE.cmbcargo.removeAllItems();
                vNuevoE.dispose();
            } else if (e.getSource() == vEditE.btnCancelar) {
                vEditE.dispose();

            } else if (e.getSource() == vEditE.btnGuardar) {
                if (!vEditE.txtEmpleado.getText().trim().isEmpty() && !vEditE.txtSalario.getText().trim().isEmpty()) {
                    _emp.setEmpNombre(vEditE.txtEmpleado.getText());
                    _emp.setEmpSalario(Double.valueOf(vEditE.txtSalario.getText()));
                    _emp.setEmpTelefono(vEditE.txtTelefono.getText());
                    buscarCargoID(vEditE.cmbcargo.getItemAt(vEditE.cmbcargo.getSelectedIndex()));
                    _emp.setCarId(_cargos);
                    _emp.setEmpEstado(BigInteger.valueOf(vEditE.cmbestado.getSelectedIndex()));

                    try {
                        edit(_emp);
                        agregarATabla(findEmpleadosEntities());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(vNuevoE, "Ha sucedido un error al modificar empleado..");
                        Logger.getLogger(EmpleadosJpaController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(vNuevoE, "Los campos no pueden quedar en blanco.");
                }

            }

        }

    };

    public void buscarCargoID(String cargo) {
        ArrayList<Cargos> lst = new ArrayList<Cargos>();
        lst = ctrlCargos.findSearch(cargo);
        if (lst.size() > 0) {
            _cargos.setCargosId(lst.get(0).getCargosId());
            _cargos.setCargos(lst.get(0).getCargos());

        }

    }

    public void agregarATabla(List<Empleados> obj) {
        if (obj.size() > 0) {
            Object Datos[] = new Object[7];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("NOMBRE");
            model.addColumn("CARGO");
            model.addColumn("SALARIO");
            model.addColumn("TELEFONO");
            model.addColumn("ESTADO");
            model.addColumn("");

            for (int i = 0; i < obj.size(); i++) {

                Datos[0] = obj.get(i).getEmpId();
                Datos[1] = obj.get(i).getEmpNombre();

                if (obj.get(i).getCarId() != null) {
                    Datos[2] = obj.get(i).getCarId().getCargos();
                } else {
                    Datos[2] = "No tiene cargo";
                }

                Datos[3] = obj.get(i).getEmpSalario();
                Datos[4] = obj.get(i).getEmpTelefono();
                if (obj.get(i).getEmpEstado() == BigInteger.valueOf(0)) {
                    Datos[5] = "Inactivo";
                } else if (obj.get(i).getEmpEstado() == BigInteger.valueOf(1)) {
                    Datos[5] = "Activo";
                }
                Datos[6] = "Modificar";

                model.addRow(Datos);
            }

            view.jTableEmpleados.setModel(model);
        }

    }
    
    public void agregarATablaPlanilla(List<Empleados> obj) {
        if (obj.size() > 0) {
            Object Datos[] = new Object[8];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("NOMBRE");
            model.addColumn("CARGO");
            model.addColumn("SALARIO");
            model.addColumn("TELEFONO");
            model.addColumn("ESTADO");
            model.addColumn("");
            model.addColumn("");

            for (int i = 0; i < obj.size(); i++) {

                Datos[0] = obj.get(i).getEmpId();
                Datos[1] = obj.get(i).getEmpNombre();

                if (obj.get(i).getCarId() != null) {
                    Datos[2] = obj.get(i).getCarId().getCargos();
                } else {
                    Datos[2] = "No tiene cargo";
                }

                Datos[3] = obj.get(i).getEmpSalario();
                Datos[4] = obj.get(i).getEmpTelefono();
                if (obj.get(i).getEmpEstado() == BigInteger.valueOf(0)) {
                    Datos[5] = "Inactivo";
                } else if (obj.get(i).getEmpEstado() == BigInteger.valueOf(1)) {
                    Datos[5] = "Activo";
                }
                Datos[6] = "Modificar";
                Datos[7] = "Pagar";
                model.addRow(Datos);
            }

            viewPlanilla.jTable1.setModel(model);
        }

    }

    public void agregarATabla(ArrayList<Empleados> obj) {
        if (obj.size() > 0) {
            Object Datos[] = new Object[7];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("NOMBRE");
            model.addColumn("CARGO");
            model.addColumn("SALARIO");
            model.addColumn("TELEFONO");
            model.addColumn("ESTADO");
            model.addColumn("");

            int cont = 0;
            for (Object valor : obj) {

                Datos[0] = obj.get(cont).getEmpId();
                Datos[1] = obj.get(cont).getEmpNombre();
                if (obj.get(cont).getCarId() != null) {
                    Datos[2] = obj.get(cont).getCarId().getCargos();
                } else {
                    Datos[2] = "No tiene cargo";
                }
                Datos[3] = obj.get(cont).getEmpSalario();
                Datos[4] = obj.get(cont).getEmpTelefono();
               
                  if (obj.get(cont).getEmpEstado() == BigInteger.valueOf(0)) {
                    Datos[5] = "Inactivo";
                } else if (obj.get(cont).getEmpEstado() == BigInteger.valueOf(1)) {
                    Datos[5] = "Activo";
                }

                Datos[6] = "Modificar";

                cont = cont + 1;
                model.addRow(Datos);
            }

            view.jTableEmpleados.setModel(model);
        }
    }
    
    public void agregarATablaPlanilla(ArrayList<Empleados> obj) {
        if (obj.size() > 0) {
            Object Datos[] = new Object[8];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("NOMBRE");
            model.addColumn("CARGO");
            model.addColumn("SALARIO");
            model.addColumn("TELEFONO");
            model.addColumn("ESTADO");
            model.addColumn("");
            model.addColumn("");

            for (int i = 0; i < obj.size(); i++) {

                Datos[0] = obj.get(i).getEmpId();
                Datos[1] = obj.get(i).getEmpNombre();

                if (obj.get(i).getCarId() != null) {
                    Datos[2] = obj.get(i).getCarId().getCargos();
                } else {
                    Datos[2] = "No tiene cargo";
                }

                Datos[3] = obj.get(i).getEmpSalario();
                Datos[4] = obj.get(i).getEmpTelefono();
                if (obj.get(i).getEmpEstado() == BigInteger.valueOf(0)) {
                    Datos[5] = "Inactivo";
                } else if (obj.get(i).getEmpEstado() == BigInteger.valueOf(1)) {
                    Datos[5] = "Activo";
                }
                Datos[6] = "Modificar";
                Datos[7] = "Pagar";
                model.addRow(Datos);
            }

            viewPlanilla.jTable1.setModel(model);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleados empleados) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cargos carId = empleados.getCarId();
            if (carId != null) {
                carId = em.getReference(carId.getClass(), carId.getCargosId());
                empleados.setCarId(carId);
            }
            em.persist(empleados);
            if (carId != null) {
                carId.getEmpleadosList().add(empleados);
                carId = em.merge(carId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEmpleados(empleados.getEmpId()) != null) {
                throw new PreexistingEntityException("Empleados " + empleados + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empleados empleados) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados persistentEmpleados = em.find(Empleados.class, empleados.getEmpId());
            Cargos carIdOld = persistentEmpleados.getCarId();
            Cargos carIdNew = empleados.getCarId();
            if (carIdNew != null) {
                carIdNew = em.getReference(carIdNew.getClass(), carIdNew.getCargosId());
                empleados.setCarId(carIdNew);
            }
            empleados = em.merge(empleados);
            if (carIdOld != null && !carIdOld.equals(carIdNew)) {
                carIdOld.getEmpleadosList().remove(empleados);
                carIdOld = em.merge(carIdOld);
            }
            if (carIdNew != null && !carIdNew.equals(carIdOld)) {
                carIdNew.getEmpleadosList().add(empleados);
                carIdNew = em.merge(carIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = empleados.getEmpId();
                if (findEmpleados(id) == null) {
                    throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.");
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
            Empleados empleados;
            try {
                empleados = em.getReference(Empleados.class, id);
                empleados.getEmpId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.", enfe);
            }
            Cargos carId = empleados.getCarId();
            if (carId != null) {
                carId.getEmpleadosList().remove(empleados);
                carId = em.merge(carId);
            }
            em.remove(empleados);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empleados> findEmpleadosEntities() {
        return findEmpleadosEntities(true, -1, -1);
    }

    public List<Empleados> findEmpleadosEntities(int maxResults, int firstResult) {
        return findEmpleadosEntities(false, maxResults, firstResult);
    }

    private List<Empleados> findEmpleadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleados.class));
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

    public Empleados findEmpleados(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleados.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleados> rt = cq.from(Empleados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    ActionListener alP = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == viewPlanilla.btnAdministrar) {
                ProyectoJpaController ctrlPtoyecto = new ProyectoJpaController(Entity_Main.getInstance(), viewProyectos);
                ctrlPtoyecto.iniciarForm();
                viewProyectos.setVisible(true);
                viewProyectos.setLocationRelativeTo(null);
                viewPlanilla.dispose();
            }
            else if (ae.getSource() == viewPlanilla.btnBuscar) {
                if (!viewPlanilla.txtBuscar.getText().trim().toString().isEmpty()) {
                    agregarATablaPlanilla(findSearchEmpleadoL(viewPlanilla.txtBuscar.getText()));
                }
                else{
                    agregarATablaPlanilla(findEmpleadosEntities());
                }
            }
        }
    };
}