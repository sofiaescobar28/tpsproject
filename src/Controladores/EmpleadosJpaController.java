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
import AccesoDatos.Categorias;
import AccesoDatos.Conexion;
import AccesoDatos.Empleados;
import AccesoDatos.Entity_Main;
import AccesoDatos.GastoPersonal;
import AccesoDatos.Proyecto;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.ViewAdministrar_Proyecto;
import gestor_de_proyectos.interfaces.ViewCargos;
import gestor_de_proyectos.interfaces.ViewPlanilla;
import gestor_de_proyectos.interfaces.viewEditar_Empleado;
import gestor_de_proyectos.interfaces.viewEmpleados;
import gestor_de_proyectos.interfaces.viewNuevo_Empleado;
import gestor_de_proyectos.interfaces.viewPagar;
import java.awt.Toolkit;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    viewPagar viewPago = new viewPagar();
    Empleados _emp = new Empleados();
    CargosJpaController ctrlCargos = new CargosJpaController(Entity_Main.getInstance());
    ViewCargos vcargos = new ViewCargos();
    ViewPlanilla viewPlanilla = new ViewPlanilla();
    ViewAdministrar_Proyecto viewProyectos = new ViewAdministrar_Proyecto();
    Cargos _cargos = new Cargos();
    Conexion claseConnect = new Conexion();
    int fila = -1, Nombre = -1, Salario = -1, Telefono = -1;
    int columna = -1;
    int id_proy;

    //Constructor de planilla
    public EmpleadosJpaController(EntityManagerFactory emf, ViewPlanilla view2) {
        this.emf = emf;
        this.viewPlanilla = view2;
        this.viewPlanilla.btnBuscar.addActionListener(alP);
        this.viewPlanilla.btnNuevoE.addActionListener(alP);
        this.viewPlanilla.dgvP.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == viewPlanilla.dgvP) {
                    leerTablaP();
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
        this.vNuevoE.btnGuardar.addActionListener(alP);
        this.vNuevoE.btnCancelar.addActionListener(alP);
        this.vEditE.btnCancelar.addActionListener(alP);
        this.vEditE.btnGuardar.addActionListener(alP);

        ///--------------------------------------------------------------------------------
        ///--------------------------------------------------------------------------------
        this.viewPago.btnCancelar.addActionListener(alP);
        this.viewPago.btnPagar.addActionListener(alP);
        ///--------------------------------------------------------------------------------
        ///--------------------------------------------------------------------------------
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
                            if (list.size() > 0) {
                                agregarATabla(list);
                            }
                        } else {
                            agregarATabla(findEmpleadosEntities());
                        }

                    } else if (view.radEmpleado.isSelected()) {
                        ArrayList<Empleados> list = new ArrayList<>();
                        if (!view.txtBuscar.getText().trim().isEmpty()) {
                            list = findSearchEmpleado(view.txtBuscar.getText());
                            if (list.size() > 0) {
                                agregarATabla(list);
                            }
                        } else {
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
        this.vNuevoE.txtEmpleado.addKeyListener(new KeyAdapter() {

            public void KeyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent ex) {

            }

            public void keyReleased(KeyEvent ex) {
                if (ex.getSource() == vNuevoE.txtEmpleado) {
                    Pattern patron = Pattern.compile("([A-Za-z]{3,}[\\s][A-Za-z]{3,})");
                    Matcher encaja = patron.matcher(vNuevoE.txtEmpleado.getText());
                    vNuevoE.txtvalidacion.setText("El nombre no es válido.");
                    Nombre = -1;
                    if (encaja.find()) {

                        Nombre = 1;
                        vNuevoE.txtvalidacion.setText("");
                    }
                }
            }
        });
        this.vNuevoE.txtSalario.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ex) {
                if (vNuevoE.txtSalario.getText().contains(".")) {
                    Pattern patron = Pattern.compile("[.][0-9]{2}");
                    Matcher encaja = patron.matcher(vNuevoE.txtSalario.getText());
                    vNuevoE.txtvalidacion.setText("El salario no es válido.");
                    Salario = -1;
                    if (encaja.find()) {

                        Salario = 1;
                        vNuevoE.txtvalidacion.setText("");
                    }
                } else {
                    Pattern patron = Pattern.compile("[0-9]{1,}");
                    Matcher encaja = patron.matcher(vNuevoE.txtSalario.getText());
                    vNuevoE.txtvalidacion.setText("El salario no es válido.");
                    Salario = -1;
                    if (encaja.find()) {

                        Salario = 1;
                        vNuevoE.txtvalidacion.setText("");
                    }
                }

            }
        });
        this.vNuevoE.txtTelefono.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ex) {
                if (ex.getSource() == vNuevoE.txtTelefono) {
                    Pattern patron = Pattern.compile("[762][0-9]{3}-[0-9]{4}");
                    Matcher encaja = patron.matcher(vNuevoE.txtTelefono.getText());
                    vNuevoE.txtvalidacion.setText("El teléfono no es válido."
                            + "Debe cumplir el formato (7XXX-XXXX).");
                    Telefono = -1;
                    if (encaja.find()) {

                        Telefono = 1;
                        vNuevoE.txtvalidacion.setText("");
                    }
                }
            }
        });
        this.vEditE.txtEmpleado.addKeyListener(new KeyAdapter() {

            public void KeyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent ex) {

            }

            public void keyReleased(KeyEvent ex) {
                if (ex.getSource() == vEditE.txtEmpleado) {
                    Pattern patron = Pattern.compile("([A-Za-z]{3,}[\\s][A-Za-z]{3,})");
                    Matcher encaja = patron.matcher(vEditE.txtEmpleado.getText());
                    vEditE.txtvalidacion.setText("El nombre no es válido.");
                    Nombre = -1;
                    if (encaja.find()) {

                        Nombre = 1;
                        vEditE.txtvalidacion.setText("");
                    }
                }
            }
        });
        this.vEditE.txtSalario.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ex) {
                if (vEditE.txtSalario.getText().contains(".")) {
                    Pattern patron = Pattern.compile("[.][0-9]{2}");
                    Matcher encaja = patron.matcher(vEditE.txtSalario.getText());
                    vEditE.txtvalidacion.setText("El salario no es válido.");
                    Salario = -1;
                    if (encaja.find()) {

                        Salario = 1;
                        vEditE.txtvalidacion.setText("");
                    }
                } else {
                    Pattern patron = Pattern.compile("[0-9]{1,}");
                    Matcher encaja = patron.matcher(vEditE.txtSalario.getText());
                    vEditE.txtvalidacion.setText("El salario no es válido.");
                    Salario = -1;
                    if (encaja.find()) {

                        Salario = 1;
                        vEditE.txtvalidacion.setText("");
                    }
                }

            }
        });
        this.vEditE.txtTelefono.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ex) {
                if (ex.getSource() == vEditE.txtTelefono) {
                    Pattern patron = Pattern.compile("[762][0-9]{3}-[0-9]{4}");
                    Matcher encaja = patron.matcher(vEditE.txtTelefono.getText());
                    vEditE.txtvalidacion.setText("El teléfono no es válido."
                            + "Debe cumplir el formato (7XXX-XXXX).");
                    Telefono = -1;
                    if (encaja.find()) {

                        Telefono = 1;
                        vEditE.txtvalidacion.setText("");
                    }
                }
            }
        });
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public void iniciarForm() {
        view.setTitle("Formulario Cargos");
        List<Empleados> ls = ordenarLista();
        agregarATabla(ls);
        view.setLocationRelativeTo(null);
    }

    public void iniciarFormPlanilla(int id, String nombre) {

        viewPlanilla.setTitle("Planilla");
        viewPlanilla.lblNombre.setText("Planilla-Proyecto: " + nombre);
        agregarATablaPlanilla(ordenarLista());
        id_proy = id;
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
            Nombre = 1;
            Salario = 1;
            Telefono = 1;
        }

    }

    ///--------------------------------------------------------------------------------
    ///--------------------------------------------------------------------------------    
    public void leerTablaP() {
        fila = viewPlanilla.dgvP.getSelectedRow();
        columna = viewPlanilla.dgvP.getSelectedColumn();
        if (columna == 6) {
            obtenerObjetoP(fila);

            vEditE.txtEmpleado.setText(_emp.getEmpNombre());
            vEditE.txtTelefono.setText(_emp.getEmpTelefono());
            vEditE.txtSalario.setText(_emp.getEmpSalario().toString());
            vEditE.cmbestado.setSelectedIndex(Integer.parseInt(_emp.getEmpEstado().toString()));
            vEditE.txtEmpleado.setText(_emp.getEmpNombre());
            vEditE.cmbcargo.removeAllItems();
            obtCargoACombo(vEditE.cmbcargo);
            vEditE.cmbcargo.setSelectedItem(_cargos.getCargos());
            vEditE.setLocationRelativeTo(null);
            vEditE.setVisible(true);
        } else if (columna == 7) {
            obtenerObjetoP(fila);
            viewPago.txtEmpleado.setText(_emp.getEmpNombre());
            viewPago.cmbcargo.removeAllItems();
            obtCargoACombo(viewPago.cmbcargo);
            viewPago.cmbcargo.setSelectedItem(_cargos.getCargos());
            viewPago.txtPago.setText(_emp.getEmpSalario().toString());
            viewPago.setLocationRelativeTo(null);
            viewPago.setVisible(true);
        }
    }
    ///--------------------------------------------------------------------------------
    ///--------------------------------------------------------------------------------

    public ArrayList<Empleados> findSearchEmpleadoValidacion(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findempleadosNombreValidar(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Empleados> Datos = new ArrayList<Empleados>();

            while (rset.next()) {
                _emp = new Empleados();
                _cargos = new Cargos();
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

    public ArrayList<Empleados> findSearchEmpleadoEditarValidar(String s, int id) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findempleadosNombreVEditar(?,?,?)}");
            cs.setString(1, s);
            cs.setInt(2, id);
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<Empleados> Datos = new ArrayList<Empleados>();

            while (rset.next()) {
                _emp = new Empleados();
                _cargos = new Cargos();
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
                _emp = new Empleados();
                _cargos = new Cargos();
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
                _emp = new Empleados();
                _cargos = new Cargos();
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

    ///--------------------------------------------------------------------------------
    ///--------------------------------------------------------------------------------
    int IdUsuario;

    public void obtenerObjetoP(int fila) {
        _emp = new Empleados();
        _emp.setEmpId(BigDecimal.valueOf(Double.parseDouble(viewPlanilla.dgvP.getValueAt(fila, 0).toString())));
        IdUsuario = Integer.parseInt(viewPlanilla.dgvP.getValueAt(fila, 0).toString());
        _emp.setEmpNombre(viewPlanilla.dgvP.getValueAt(fila, 1).toString());
        buscarCargoID(viewPlanilla.dgvP.getValueAt(fila, 2).toString());
        _emp.setCarId(_cargos);
        _emp.setEmpSalario(Double.parseDouble(viewPlanilla.dgvP.getValueAt(fila, 3).toString()));
        _emp.setEmpTelefono(viewPlanilla.dgvP.getValueAt(fila, 4).toString());
        _emp.setEmpEstado(valorEstado(viewPlanilla.dgvP.getValueAt(fila, 5).toString()));
    }
    ///--------------------------------------------------------------------------------
    ///--------------------------------------------------------------------------------

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
                    if (Nombre == 1 && Telefono == 1 && Salario == 1) {
                        Nombre =-1; Telefono=-1; Salario=-1;
                        List<Empleados> list = findEmpleadosEntities();
                        Collections.sort(list, new Comparator<Empleados>() {
                            @Override
                            public int compare(Empleados o1, Empleados o2) {
                                return o1.getEmpId().compareTo(o2.getEmpId());
                            }

                        });
                        BigDecimal idemp = new BigDecimal(Integer.parseInt(list.get(list.size() - 1).getEmpId().toString()) + 1);
                        _emp.setEmpId(idemp);
                        _emp.setEmpNombre(vNuevoE.txtEmpleado.getText().trim());
                        _emp.setEmpSalario(Double.valueOf(vNuevoE.txtSalario.getText()));
                        _emp.setEmpTelefono(vNuevoE.txtTelefono.getText());
                        buscarCargoID(vNuevoE.cmbcargo.getItemAt(vNuevoE.cmbcargo.getSelectedIndex()));
                        _emp.setCarId(_cargos);
                        _emp.setEmpEstado(BigInteger.valueOf(vNuevoE.cmbestado.getSelectedIndex()));

                        try {
                            if (findSearchEmpleadoValidacion(vNuevoE.txtEmpleado.getText()).size() > 0) {
                                JOptionPane.showMessageDialog(vNuevoE, "El cargo que desea agregar ya existe.");

                            } else {
                                _emp.setEmpId(new BigDecimal(getEmpleadosCount() + 1));//asignamos id
                                create(_emp);
                                agregarATabla(findEmpleadosEntities());
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(vNuevoE, "Ha sucedido un error al guardar.");
                            Logger.getLogger(EmpleadosJpaController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else {
                    vNuevoE.txtvalidacion.setText("Debe escribir los campos correctamente.");
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
                    if (Nombre == 1 && Telefono == 1 && Salario == 1) {
                         Nombre =-1; Telefono=-1; Salario=-1;
                        _emp.setEmpNombre(vEditE.txtEmpleado.getText().trim());
                        _emp.setEmpSalario(Double.valueOf(vEditE.txtSalario.getText().trim()));
                        _emp.setEmpTelefono(vEditE.txtTelefono.getText().trim());
                        buscarCargoID(vEditE.cmbcargo.getItemAt(vEditE.cmbcargo.getSelectedIndex()));
                        _emp.setCarId(_cargos);
                        _emp.setEmpEstado(BigInteger.valueOf(vEditE.cmbestado.getSelectedIndex()));

                        try {
                            BigDecimal d = _emp.getEmpId();
                            BigDecimal d2 = d.setScale(0, BigDecimal.ROUND_HALF_UP); // yields 34

                            int b = Integer.parseInt(d2.toString());
                            if (findSearchEmpleadoEditarValidar(_emp.getEmpNombre().trim(), b).size() > 0) {
                                JOptionPane.showMessageDialog(vEditE, "El nombre de la categoría ya existe.");

                            } else {
                                edit(_emp);
                                agregarATabla(findEmpleadosEntities());
                            }

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(vEditE, "Ha sucedido un error al modificar empleado..");
                            Logger.getLogger(EmpleadosJpaController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            vEditE.txtvalidacion.setText("");
                            vEditE.dispose();
                    }else{ vEditE.txtvalidacion.setText("Debe escribir los campos correctamente.");}
                  

                } else {
                    JOptionPane.showMessageDialog(vEditE, "Los campos no pueden quedar en blanco.");
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
            Collections.sort(obj, new Comparator<Empleados>() {
                @Override
                public int compare(Empleados o1, Empleados o2) {
                    return o1.getEmpId().compareTo(o2.getEmpId());
                }

            });
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
            Collections.sort(obj, new Comparator<Empleados>() {
                @Override
                public int compare(Empleados o1, Empleados o2) {
                    return o1.getEmpId().compareTo(o2.getEmpId());
                }

            });
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

            viewPlanilla.dgvP.setModel(model);
        }

    }

    public void agregarATabla(ArrayList<Empleados> obj) {
        if (obj.size() > 0) {
            Collections.sort(obj, new Comparator<Empleados>() {
                @Override
                public int compare(Empleados o1, Empleados o2) {
                    return o1.getEmpId().compareTo(o2.getEmpId());
                }

            });
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
            Collections.sort(obj, new Comparator<Empleados>() {
                @Override
                public int compare(Empleados o1, Empleados o2) {
                    return o1.getEmpId().compareTo(o2.getEmpId());
                }

            });
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

            viewPlanilla.dgvP.setModel(model);
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
    GastoPersonalJpaController ctrl = new GastoPersonalJpaController(Entity_Main.getInstance());
    ActionListener alP = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == viewPlanilla.btnBuscar) {
                if (!viewPlanilla.txtBuscar.getText().trim().toString().isEmpty()) {
                    agregarATablaPlanilla(findSearchEmpleadoL(viewPlanilla.txtBuscar.getText()));
                } else {
                    agregarATablaPlanilla(findEmpleadosEntities());
                }
            } else if (ae.getSource() == viewPlanilla.btnNuevoE) {
                _emp = new Empleados();
                obtCargoACombo(vNuevoE.cmbcargo);
                vNuevoE.setVisible(true);
                vNuevoE.setLocationRelativeTo(null);
            } else if (ae.getSource() == vNuevoE.btnGuardar) {
                if (!vNuevoE.txtEmpleado.getText().trim().isEmpty() && !vNuevoE.txtSalario.getText().trim().isEmpty()) {
                    //__________________________________________________________________                    
                    List<Empleados> list = ordenarLista();
                    if (list.size() > 0) {
                        BigDecimal id = new BigDecimal(Integer.parseInt(list.get(list.size() - 1).getEmpId().toString()) + 1);
                        _emp.setEmpId(id);
                    } else {
                        _emp.setEmpId(new BigDecimal("1"));
                    }
                    //__________________________________________________________________
                    _emp.setEmpNombre(vNuevoE.txtEmpleado.getText());
                    _emp.setEmpSalario(Double.valueOf(vNuevoE.txtSalario.getText()));
                    _emp.setEmpTelefono(vNuevoE.txtTelefono.getText());
                    buscarCargoID(vNuevoE.cmbcargo.getItemAt(vNuevoE.cmbcargo.getSelectedIndex()));
                    _emp.setCarId(_cargos);
                    _emp.setEmpEstado(BigInteger.valueOf(vNuevoE.cmbestado.getSelectedIndex()));

                    try {
                        create(_emp);
                        agregarATablaPlanilla(ordenarLista());
                        _emp = null;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(vNuevoE, "Ha sucedido un error al guardar.");
                        Logger.getLogger(EmpleadosJpaController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(vNuevoE, "Los campos no pueden quedar en blanco.");
                }
            } else if (ae.getSource() == vNuevoE.btnCancelar) {
                vNuevoE.cmbcargo.removeAllItems();
                _emp = null;
                vNuevoE.dispose();
            } else if (ae.getSource() == vEditE.btnCancelar) {
                vEditE.dispose();
                _emp = null;
            } else if (ae.getSource() == vEditE.btnGuardar) {
                if (!vEditE.txtEmpleado.getText().trim().isEmpty() && !vEditE.txtSalario.getText().trim().isEmpty()) {
                    _emp.setEmpNombre(vEditE.txtEmpleado.getText().trim());
                    _emp.setEmpSalario(Double.valueOf(vEditE.txtSalario.getText().trim()));
                    _emp.setEmpTelefono(vEditE.txtTelefono.getText().trim());
                    buscarCargoID(vEditE.cmbcargo.getItemAt(vEditE.cmbcargo.getSelectedIndex()));
                    _emp.setCarId(_cargos);
                    _emp.setEmpEstado(BigInteger.valueOf(vEditE.cmbestado.getSelectedIndex()));

                    try {
                        edit(_emp);
                        agregarATablaPlanilla(ordenarLista());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(vNuevoE, "Ha sucedido un error al modificar empleado..");
                        Logger.getLogger(EmpleadosJpaController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(vNuevoE, "Los campos no pueden quedar en blanco.");
                }
            } ///--------------------------------------------------------------------------------
            ///--------------------------------------------------------------------------------
            else if (ae.getSource() == viewPago.btnCancelar) {
                viewPago.dispose();
                limpiarTxtPagos();
            } else if (ae.getSource() == viewPago.btnPagar) {
                if (!viewPago.txtEmpleado.getText().trim().isEmpty() || !viewPago.txtPago.getText().trim().isEmpty()
                        || !viewPago.txtComentario.getText().trim().isEmpty()) {
                    GastoPersonal gp = new GastoPersonal();
                    gp = llenarPago();
                    try {
                        ctrl.create(gp);
                        JOptionPane.showMessageDialog(viewPago, "Pagado!!!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(viewPago, ",," + ex.getMessage() + ",Cause ," + ex.getCause());
                    }

                    _cargos = new Cargos();
                    _emp = new Empleados();
                    IdUsuario = -1;
                    viewPago.dispose();
                } else {
                    JOptionPane.showMessageDialog(viewPago, "Faltan campos por llenar");
                }
            }
            ///--------------------------------------------------------------------------------
            ///--------------------------------------------------------------------------------
        }
    };

    ///--------------------------------------------------------------------------------
    ///--------------------------------------------------------------------------------
    public GastoPersonal llenarPago() {
        Date Hoy = new Date();
        String strDateFormat = "dd-MMM-aaaa";
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        objSDF.format(Hoy);
        Proyecto pr = new Proyecto();
        ProyectoJpaController ctrlP = new ProyectoJpaController(emf);
        pr = ctrlP.findProyecto(new BigDecimal(id_proy));
        _emp = new Empleados();
        _emp = findEmpleados(new BigDecimal(IdUsuario));

        GastoPersonal gasp = new GastoPersonal();
        ////////////___________________________________________________
        List<GastoPersonal> list = ctrl.findGastoPersonalEntities();
        Collections.sort(list, new Comparator<GastoPersonal>() {
            @Override
            public int compare(GastoPersonal o1, GastoPersonal o2) {
                return o1.getGpId().compareTo(o2.getGpId());
            }
        });
        if (list.size() > 0) {
            BigDecimal id = new BigDecimal(Integer.parseInt(list.get(list.size() - 1).getGpId().toString()) + 1);
            gasp.setGpId(id);
        } else {
            gasp.setGpId(new BigDecimal("1"));
        }

        ////////////___________________________________________________
        buscarCargoID(viewPago.cmbcargo.getItemAt(viewPago.cmbcargo.getSelectedIndex()));

        gasp.setEmpId(_emp);
        gasp.setProyId(pr);
        gasp.setGpCargo(Double.parseDouble(_cargos.getCargosId().toString()));
        gasp.setGpPago(Double.parseDouble(viewPago.txtPago.getText()));
        gasp.setGpFecha(Hoy);
        gasp.setGpComentario(viewPago.txtComentario.getText());
        return gasp;
    }

    public List<Empleados> ordenarLista() {
        List<Empleados> list = findEmpleadosEntities();
        Collections.sort(list, new Comparator<Empleados>() {
            @Override
            public int compare(Empleados o1, Empleados o2) {
                return o1.getEmpId().compareTo(o2.getEmpId());
            }
        });
        return list;
    }

    public void limpiarTxtPagos() {
        viewPago.txtEmpleado.setText(null);
        viewPago.txtComentario.setText(null);
        viewPago.txtPago.setText(null);
        viewPago.cmbcargo.setSelectedIndex(0);
    }
    ///--------------------------------------------------------------------------------
    ///--------------------------------------------------------------------------------
}
