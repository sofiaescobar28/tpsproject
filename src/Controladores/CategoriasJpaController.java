/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Cargos;
import AccesoDatos.Categorias;
import AccesoDatos.Conexion;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import Reportes.entidades.CatogoriasDS;
import Reportes.entidades.ECategoria;
import gestor_de_proyectos.interfaces.ViewCategorias;
import gestor_de_proyectos.interfaces.ViewEditar_Categoria;
import gestor_de_proyectos.interfaces.ViewNueva_Categoria;
import gestor_de_proyectos.interfaces.ViewNuevo_Cargo;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @author Manuel
 */
public class CategoriasJpaController implements Serializable {

    ViewCategorias viewCategorias = new ViewCategorias();
    ViewEditar_Categoria viewEditCate = new ViewEditar_Categoria();
    ViewNueva_Categoria viewNuevaCat = new ViewNueva_Categoria();
    Categorias _categorias = new Categorias();
    int fila = -1;
    int columna = -1;
    Conexion claseConnect = new Conexion();

    public CategoriasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public CategoriasJpaController(EntityManagerFactory emf, ViewCategorias view) {
        this.emf = emf;
        this.viewCategorias = view;
        this.viewCategorias.btnNuevaCategoria.addActionListener(al);
        viewEditCate.btnCancelar.addActionListener(al);
        viewEditCate.btnGuardarcambios.addActionListener(al);
        viewNuevaCat.btnCancelar.addActionListener(al);
        viewNuevaCat.btnNuevacateg.addActionListener(al);
        viewCategorias.cmbTipodeCate.addActionListener(al);
        viewCategorias.btnReporte.addActionListener(al);
        this.viewCategorias.dgvCategorias.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == view.dgvCategorias) {
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
        this.viewCategorias.txtBuscar.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                if (e.getSource() == viewCategorias.txtBuscar) {
                    ArrayList<Categorias> list = new ArrayList<>();
                    list = findSearch(viewCategorias.txtBuscar.getText());
                    if (list != null) {
                        agregarATabla(list);
                    }

                }
            }
        });
    }

    public void iniciarForm() {
        viewCategorias.setTitle("Categorías");
        viewCategorias.getContentPane().setBackground(new Color(198,146,122));
        List<Categorias> ls = findCategoriasEntities();
        agregarATabla(ls);
        viewCategorias.setVisible(true);
        viewCategorias.setLocationRelativeTo(null);
    }

    public ArrayList<Categorias> findSearch(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCategoriasNombre(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Categorias> Datos = new ArrayList<Categorias>();

            while (rset.next()) {
                _categorias = new Categorias();
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(rset.getString("CAT_NOMBRE"));
                _categorias.setCatTipo(BigInteger.valueOf(Integer.parseInt(rset.getString("CAT_TIPO"))));

                Datos.add(_categorias);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(viewCategorias, "Sucedió un problema al realizar la consulta.");

        }
        return null;

    }

    public ArrayList<Categorias> findSearchValidar(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCategoriasNombreValidar(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Categorias> Datos = new ArrayList<Categorias>();

            while (rset.next()) {
                _categorias = new Categorias();
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(rset.getString("CAT_NOMBRE"));
                _categorias.setCatTipo(BigInteger.valueOf(Integer.parseInt(rset.getString("CAT_TIPO"))));

                Datos.add(_categorias);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(viewCategorias, "Sucedió un problema al realizar la consulta.");

        }
        return null;

    }

    public ArrayList<Categorias> findSearchValidarEditar(String s, int id) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCategoriasNValidarE(?,?,?)}");
            cs.setString(1, s);
            cs.setInt(2, id);
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(3);
            ArrayList<Categorias> Datos = new ArrayList<Categorias>();

            while (rset.next()) {
                _categorias = new Categorias();
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(rset.getString("CAT_NOMBRE"));
                _categorias.setCatTipo(BigInteger.valueOf(Integer.parseInt(rset.getString("CAT_TIPO"))));

                Datos.add(_categorias);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(viewCategorias, "Sucedió un problema al realizar la consulta.");

        }
        return null;

    }

    public ArrayList<Categorias> findSearchCombo(int s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findCategoriasNombreIE(?,?)}");
            cs.setInt(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<Categorias> Datos = new ArrayList<Categorias>();

            while (rset.next()) {
                _categorias = new Categorias();
                _categorias.setCatId(rset.getBigDecimal("CAT_ID"));
                _categorias.setCatNombre(rset.getString("CAT_NOMBRE"));
                _categorias.setCatTipo(BigInteger.valueOf(Integer.parseInt(rset.getString("CAT_TIPO"))));

                Datos.add(_categorias);
            }

            claseConnect.CerrarConexionBD();
            return Datos;

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(viewCategorias, "Sucedió un problema al buscar.");

        }
        return null;

    }

    public void agregarATabla(List<Categorias> obj) {

        if (obj.size() > 0) {
            Collections.sort(obj, new Comparator<Categorias>() {
                @Override
                public int compare(Categorias o1, Categorias o2) {
                    return o1.getCatId().compareTo(o2.getCatId());
                }

            });
            Object Datos[] = new Object[4];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("CATEGORIAS");
            model.addColumn("TIPO");
            model.addColumn("");

            int cont = 0;
            for (Object valor : obj) {

                Datos[0] = obj.get(cont).getCatId();
                Datos[1] = obj.get(cont).getCatNombre();
                int tipo =Integer.parseInt(obj.get(cont).getCatTipo().toString());
                if (tipo == 0) {
                    Datos[2] = "Ingresos";
                } else if (tipo == 1) {
                    Datos[2] = "Egresos";
                }
                Datos[3] = "Modificar";

                cont = cont + 1;
                model.addRow(Datos);
            }

            viewCategorias.dgvCategorias.setModel(model);
        }

    }

    public void agregarATabla(ArrayList<Categorias> obj) {
        if (obj.size() > 0) {
            Collections.sort(obj, new Comparator<Categorias>() {
                @Override
                public int compare(Categorias o1, Categorias o2) {
                    return o1.getCatId().compareTo(o2.getCatId());
                }

            });
            Object Datos[] = new Object[4];// 1-id, 2-nombre, 3-Cantidad
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("CATEGORIAS");
            model.addColumn("TIPO");
            model.addColumn("");

            int cont = 0;
            for (Object valor : obj) {

                Datos[0] = obj.get(cont).getCatId();
                Datos[1] = obj.get(cont).getCatNombre();
                if (obj.get(cont).getCatTipo() == BigInteger.valueOf(0)) {
                    Datos[2] = "Ingresos";
                } else if (obj.get(cont).getCatTipo() == BigInteger.valueOf(1)) {
                    Datos[2] = "Egresos";
                }

                Datos[3] = "Modificar";

                cont = cont + 1;
                model.addRow(Datos);
            }

            viewCategorias.dgvCategorias.setModel(model);
        }

    }
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == viewCategorias.btnNuevaCategoria) {
                viewNuevaCat.setVisible(true);
                viewNuevaCat.getContentPane().setBackground(new Color(153,168,178));
                viewNuevaCat.setLocationRelativeTo(null);

            } else if (e.getSource() == viewNuevaCat.btnCancelar) {
                viewNuevaCat.dispose();
            } else if (e.getSource() == viewNuevaCat.btnNuevacateg) {
                if (!viewNuevaCat.txtNuevocategoria.getText().trim().isEmpty()) {
                    List<Categorias> list = findCategoriasEntities();
                    Collections.sort(list, new Comparator<Categorias>() {
                        @Override
                        public int compare(Categorias o1, Categorias o2) {
                            return o1.getCatId().compareTo(o2.getCatId());
                        }

                    });
                    BigDecimal idcat = new BigDecimal(Integer.parseInt(list.get(list.size() - 1).getCatId().toString()) + 1);

                    _categorias.setCatId(idcat);
                    _categorias.setCatNombre(viewNuevaCat.txtNuevocategoria.getText());
                    _categorias.setCatTipo(BigInteger.valueOf(viewNuevaCat.cmbTipoCategoria.getSelectedIndex()));
                    try {
                        if (findSearchValidar(viewNuevaCat.txtNuevocategoria.getText()).size() > 0) {
                            JOptionPane.showMessageDialog(viewNuevaCat, "La categoría que desea agregar ya existe.");

                        } else {
                            create(_categorias);
                            viewNuevaCat.txtNuevocategoria.setText("");
                            agregarATabla(findCategoriasEntities());
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(viewNuevaCat, "Sucedió un error al crear categoría.");
                    }
                } else {
                    JOptionPane.showMessageDialog(viewNuevaCat, "Debe introducir un nombre para la nueva categoría.");
                }

            } else if (e.getSource() == viewEditCate.btnCancelar) {
                viewEditCate.dispose();
            } else if (e.getSource() == viewEditCate.btnGuardarcambios) {
                if (!viewEditCate.txtEditarcategoria.getText().trim().isEmpty()) {

                    _categorias.setCatNombre(viewEditCate.txtEditarcategoria.getText());
                    _categorias.setCatTipo(BigInteger.valueOf(viewEditCate.cmbTipoCategoria.getSelectedIndex()));
                    try {
                        BigDecimal d = _categorias.getCatId();
                        BigDecimal d2 = d.setScale(0, BigDecimal.ROUND_HALF_UP); // yields 34

                        int b = Integer.parseInt(d2.toString());

                        if (findSearchValidarEditar(_categorias.getCatNombre(), b).size() > 0) {
                            JOptionPane.showMessageDialog(viewEditCate, "El nombre de la categoría ya existe.");

                        } else {
                            edit(_categorias);
                            agregarATabla(findCategoriasEntities());
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(CategoriasJpaController.class.getName()).log(Level.SEVERE, null, ex);

                    }
                } else {
                    JOptionPane.showMessageDialog(viewEditCate, "Debe introducir un nombre a la categoría.");
                }

            } else if (e.getSource() == viewCategorias.cmbTipodeCate) {
                if (viewCategorias.cmbTipodeCate.getSelectedIndex() == 0) {
                    agregarATabla(findCategoriasEntities());
                } else if (viewCategorias.cmbTipodeCate.getSelectedIndex() == 1) {
                    agregarATabla(findSearchCombo(0));

                } else if (viewCategorias.cmbTipodeCate.getSelectedIndex() == 2) {
                    agregarATabla(findSearchCombo(1));

                }

            }            
            else if(e.getSource()==viewCategorias.btnReporte){
                ObtenerDatos();
                try{                    
                    
                    JasperReport rep = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/Rep_Categoria.jasper"));                 
                    JasperPrint jasperPrint = JasperFillManager.fillReport(rep, null,datasource);
                    
                    JasperViewer Jview = new JasperViewer(jasperPrint,false);
                    Jview.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    Jview.setVisible(true);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(viewCategorias, "error:"+ex);
                }
            }
        }

    };

    public void leerTabla() {
        fila = viewCategorias.dgvCategorias.getSelectedRow();
        columna = viewCategorias.dgvCategorias.getSelectedColumn();
        if (columna == 3) {
            obtenerObjeto(fila);
            viewEditCate.txtEditarcategoria.setText(_categorias.getCatNombre());
//            int val = _categorias.getCatId().intValue();
            viewEditCate.cmbTipoCategoria.setSelectedIndex(_categorias.getCatTipo().intValue());

            viewEditCate.setLocationRelativeTo(null);
            viewEditCate.getContentPane().setBackground(new Color(153,168,178));
            viewEditCate.setVisible(true);

        }

    }

    public void obtenerObjeto(int fila) {
//        _categorias = new Categorias();
        _categorias.setCatId(BigDecimal.valueOf(Double.parseDouble(viewCategorias.dgvCategorias.getValueAt(fila, 0).toString())));
        _categorias.setCatNombre(viewCategorias.dgvCategorias.getValueAt(fila, 1).toString());

        _categorias.setCatTipo(BigInteger.valueOf(valor(viewCategorias.dgvCategorias.getValueAt(fila, 2).toString())));
        //return _categorias;
//        BigInteger val =BigInteger.valueOf(valor(viewCategorias.dgvCategorias.getValueAt(fila, 2).toString()));
    }

    public int valor(String s) {

        if (s.equals("Ingresos")) {
            return 0;
        } else if (s.equals("Egresos")) {
            return 1;
        }
        return -1;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categorias categorias) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(categorias);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCategorias(categorias.getCatId()) != null) {
                throw new PreexistingEntityException("Categorias " + categorias + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categorias categorias) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            categorias = em.merge(categorias);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = categorias.getCatId();
                if (findCategorias(id) == null) {
                    throw new NonexistentEntityException("The categorias with id " + id + " no longer exists.");
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
            Categorias categorias;
            try {
                categorias = em.getReference(Categorias.class, id);
                categorias.getCatId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categorias with id " + id + " no longer exists.", enfe);
            }
            em.remove(categorias);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categorias> findCategoriasEntities() {
        return findCategoriasEntities(true, -1, -1);
    }

    public List<Categorias> findCategoriasEntities(int maxResults, int firstResult) {
        return findCategoriasEntities(false, maxResults, firstResult);
    }

    private List<Categorias> findCategoriasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categorias.class));
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

    public Categorias findCategorias(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categorias.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categorias> rt = cq.from(Categorias.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    CatogoriasDS datasource = new CatogoriasDS();
    public void ObtenerDatos(){
        for (int f = 0; f < viewCategorias.dgvCategorias.getRowCount(); f++) {
            ECategoria ca = new ECategoria();
            ca.setCategoria(viewCategorias.dgvCategorias.getValueAt(f, 1).toString());
            ca.setTipo(viewCategorias.dgvCategorias.getValueAt(f, 2).toString());
            datasource.addCate(ca);
        }
    }
}
