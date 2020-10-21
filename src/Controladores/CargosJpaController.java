/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Cargos;
import AccesoDatos.Conexion;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.ViewCargos;
import gestor_de_proyectos.interfaces.ViewEditar_Cargo;
import gestor_de_proyectos.interfaces.ViewNuevo_Cargo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author Manuel
 */
public class CargosJpaController implements Serializable {

    ViewCargos view = new ViewCargos();
    Cargos _cargos;
    Conexion claseConnect = new Conexion();
    ViewNuevo_Cargo nuevocargo = new ViewNuevo_Cargo();
    ViewEditar_Cargo viewEditarCargos = new ViewEditar_Cargo();
    int idglobal;
    int fila = -1;
    int columna = -1;

    public CargosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public CargosJpaController(EntityManagerFactory emf, ViewCargos view) {
        this.emf = emf;
        this.view = view;
        this.view.btnNuevoCargo.addActionListener(al);
        this.view.dgvCargos.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == view.dgvCargos) {
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
        this.viewEditarCargos.btnGuardarCambios.addActionListener(al);
        this.viewEditarCargos.btnCancelar.addActionListener(al);
        this.nuevocargo.btnCancelar.addActionListener(al);
        this.nuevocargo.btnNuevocargo.addActionListener(al);

//         this.view.btnbuscar.addActionListener(al);
        this.view.txtBuscar.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                if (e.getSource() == view.txtBuscar) {
                    ArrayList<Cargos> list = new ArrayList<>();
                    list = findSearch(view.txtBuscar.getText().toString());
                    if (list != null) {
                        agregarATabla(list);
                    }

                }
            }
        });

    }

    public void iniciarForm() {
        view.setTitle("Formulario Cargos");
        List<Cargos> ls = findCargosEntities();
        agregarATabla(ls);

        view.setLocationRelativeTo(null);
    }

    public void agregarATabla(List<Cargos> obj) {

        if (obj.size() > 0) {

            Collections.sort(obj, new Comparator<Cargos>() {
                @Override
                public int compare(Cargos o1, Cargos o2) {
                    return o1.getCargosId().compareTo(o2.getCargosId());
                }

            });

            Object Datos[] = new Object[4];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("CARGOS");
            model.addColumn("");
            model.addColumn("");

            int cont = 0;
            for (Object valor : obj) {

                Datos[0] = obj.get(cont).getCargosId();
                Datos[1] = obj.get(cont).getCargos();
                Datos[2] = "Modificar";
                Datos[3] = "Eliminar";

                cont = cont + 1;
                model.addRow(Datos);
            }

            view.dgvCargos.setModel(model);
        }

    }

    public void agregarATabla(ArrayList<Cargos> obj) {
        if (obj.size() > 0) {

            Collections.sort(obj, new Comparator<Cargos>() {
                @Override
                public int compare(Cargos o1, Cargos o2) {
                    return o1.getCargosId().compareTo(o2.getCargosId());
                }

            });
            Object Datos[] = new Object[4];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("CARGOS");
            model.addColumn("");
            model.addColumn("");

            int cont = 0;
            for (Object valor : obj) {

                Datos[0] = obj.get(cont).getCargosId();
                Datos[1] = obj.get(cont).getCargos();
                Datos[2] = "Modificar";
                Datos[3] = "Eliminar";

                cont = cont + 1;
                model.addRow(Datos);
            }

            view.dgvCargos.setModel(model);
        }
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cargos cargos) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(cargos);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCargos(cargos.getCargosId()) != null) {
                throw new PreexistingEntityException("Cargos " + cargos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cargos cargos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cargos = em.merge(cargos);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = cargos.getCargosId();
                if (findCargos(id) == null) {
                    throw new NonexistentEntityException("The cargos with id " + id + " no longer exists.");
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
            Cargos cargos;
            try {
                cargos = em.getReference(Cargos.class, id);
                cargos.getCargosId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cargos with id " + id + " no longer exists.", enfe);
            }
            em.remove(cargos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cargos> findCargosEntities() {
        return findCargosEntities(true, -1, -1);
    }

    public List<Cargos> findCargosEntities(int maxResults, int firstResult) {
        return findCargosEntities(false, maxResults, firstResult);
    }

    private List<Cargos> findCargosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cargos.class));
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

    public Cargos findCargos(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cargos.class, id);
        } finally {
            em.close();
        }
    }

    public int getCargosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cargos> rt = cq.from(Cargos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == view.btnNuevoCargo) {
                nuevocargo.setVisible(true);
                nuevocargo.setLocationRelativeTo(null);
            } else if (e.getSource() == viewEditarCargos.btnGuardarCambios) {
                if (!viewEditarCargos.txteditarcargo.getText().toString().isEmpty()) {
                    _cargos.setCargos(viewEditarCargos.txteditarcargo.getText().toString().trim());
                  

                    try {
                        BigDecimal d = _cargos.getCargosId();
                        BigDecimal d2 = d.setScale(0, BigDecimal.ROUND_HALF_UP); // yields 34

                        int b = Integer.parseInt(d2.toString());
                        if (findSearchValidacionEditar(_cargos.getCargos(), b).size() > 0) {
                            JOptionPane.showMessageDialog(viewEditarCargos, "El nombre de la categoría ya existe.");

                        } else {
                            edit(_cargos);
                            agregarATabla(findCargosEntities());
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(viewEditarCargos, "Ha sucedido un error modificar.");
                    }
                } else {
                    JOptionPane.showMessageDialog(viewEditarCargos, "No puede dejar el nombre del cargo en blanco.");
                }

            } else if (e.getSource() == viewEditarCargos.btnCancelar) {
                viewEditarCargos.dispose();
            } else if (e.getSource() == nuevocargo.btnCancelar) {
                nuevocargo.dispose();
            } else if (e.getSource() == nuevocargo.btnNuevocargo) {
                if (nuevocargo.txtNuevocargo.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(nuevocargo, "Debe agregarle un nombre al nuevo cargo.");
                } else {
                    try {
                        if (findSearchValidacion(nuevocargo.txtNuevocargo.getText()).size() > 0) {
                            JOptionPane.showMessageDialog(nuevocargo, "El cargo que desea agregar ya existe.");

                        } else {
                            List<Cargos> list = findCargosEntities();
                            Collections.sort(list, new Comparator<Cargos>() {
                                @Override
                                public int compare(Cargos o1, Cargos o2) {
                                    return o1.getCargosId().compareTo(o2.getCargosId());
                                }

                            });

                            BigDecimal idcar = new BigDecimal(Integer.parseInt(list.get(list.size() - 1).getCargosId().toString()) + 1);
                            _cargos = new Cargos();
                            _cargos.setCargosId(idcar);
                            _cargos.setCargos(nuevocargo.txtNuevocargo.getText());
                            create(_cargos);
                            nuevocargo.txtNuevocargo.setText("");
                            agregarATabla(findCargosEntities());
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(nuevocargo, "No se pudo crear un nuevo cargo. Error:" + ex);
                    }
                }
            }
        }
    };

    public Cargos leerTabla() { //Leer tabla
        fila = view.dgvCargos.getSelectedRow();
        columna = view.dgvCargos.getSelectedColumn();
        if (columna == 2) {

            viewEditarCargos.txteditarcargo.setText(obtenerObjeto(fila).getCargos());
            viewEditarCargos.setLocationRelativeTo(null);
            viewEditarCargos.setVisible(true);

        } else if (columna == 3) {

            int resp = JOptionPane.showConfirmDialog(null, "¿Realmente desea eliminar el cargo?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp == 0) {
                try {
                    destroy(obtenerObjeto(fila).getCargosId());
//                    JOptionPane.showMessageDialog(view, "Registro eliminado correctamente.");
                    if (getCargosCount() > 0) {
                        agregarATabla(findCargosEntities());
                    }

                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(view, "Sucedió un error al eliminar.");
                }

            }
        }
//        int columna = view.jtableProducto.getSelectedColumn();
        idglobal = Integer.parseInt(view.dgvCargos.getValueAt(fila, 0).toString());
        return _cargos;
    }

    public Cargos obtenerObjeto(int fila) {
        _cargos = new Cargos();
        _cargos.setCargosId(BigDecimal.valueOf(Double.parseDouble(view.dgvCargos.getValueAt(fila, 0).toString())));
        _cargos.setCargos(view.dgvCargos.getValueAt(fila, 1).toString());
        return _cargos;
    }

    public ArrayList<Cargos> findSearch(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCargoNombre(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Cargos> Datos = new ArrayList<Cargos>();

            while (rset.next()) {
                _cargos = new Cargos();
                _cargos.setCargosId(rset.getBigDecimal("CARGOS_ID"));
                _cargos.setCargos(rset.getString("CARGOS"));

                Datos.add(_cargos);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "El registro que intenta eliminar no existe.");

        }
        return null;

    }

    public ArrayList<Cargos> findSearchValidacion(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCargoNombreValidar(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Cargos> Datos = new ArrayList<Cargos>();

            while (rset.next()) {
                _cargos = new Cargos();
                _cargos.setCargosId(rset.getBigDecimal("CARGOS_ID"));
                _cargos.setCargos(rset.getString("CARGOS"));

                Datos.add(_cargos);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "El registro que intenta eliminar no existe.");

        }
        return null;

    }

    public ArrayList<Cargos> findSearchValidacionEditar(String s, int id) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCargoNombreValidarEditar(?,?,?)}");
            cs.setString(1, s);
            cs.setInt(2, id);
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<Cargos> Datos = new ArrayList<Cargos>();

            while (rset.next()) {
                _cargos = new Cargos();
                _cargos.setCargosId(rset.getBigDecimal("CARGOS_ID"));
                _cargos.setCargos(rset.getString("CARGOS"));

                Datos.add(_cargos);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "El registro que intenta eliminar no existe.");

        }
        return null;

    }

}
