/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Conexion;
import AccesoDatos.UnidadesDeMedida;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.viewCrea_Editar_Unidad_Medida;
import gestor_de_proyectos.interfaces.viewUnidades_de_medida;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
 * @author Usuario
 */
public class UnidadesDeMedidaJpaController implements Serializable {

    viewUnidades_de_medida view = new viewUnidades_de_medida();
    viewCrea_Editar_Unidad_Medida viewCrear_Editar = new viewCrea_Editar_Unidad_Medida();
    UnidadesDeMedida _unidad;
    Conexion claseConnect = new Conexion();    
    int idglobal;
    int fila = -1;
    int columna = -1;  
    
    public UnidadesDeMedidaJpaController(EntityManagerFactory emf,viewUnidades_de_medida view) {        
        this.emf = emf;
        this.view = view;
        this.view.btnBuscar.addActionListener(al);        
        this.view.btnNuevaUnidad.addActionListener(al);
        this.view.dgvUnidades.addMouseListener(new MouseListener() {                    
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource()==view.dgvUnidades) {
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
        
        //view crear editar 
        this.viewCrear_Editar.btnCancelar.addActionListener(al);
        this.viewCrear_Editar.btnGuardar.addActionListener(al);
    }
    public UnidadesDeMedida leerTabla ()
    {
        fila=view.dgvUnidades.getSelectedRow();
        columna=view.dgvUnidades.getSelectedColumn();
        if (columna == 2) {
            _unidad = new UnidadesDeMedida();
            _unidad.setUmId(BigDecimal.valueOf(Double.parseDouble(view.dgvUnidades.getValueAt(fila, 0).toString())));
            _unidad.setUmNombre(view.dgvUnidades.getValueAt(fila,1).toString());
            
            viewCrear_Editar.txtUnidadMed.setText(_unidad.getUmNombre());            
            
            viewCrear_Editar.setLocationRelativeTo(null);
            viewCrear_Editar.setVisible(true);
            idglobal = Integer.parseInt(view.dgvUnidades.getValueAt(fila, 0).toString());
        }
        return _unidad;
    }
    
    public void iniciarForm(){
        view.setTitle("Formulario Unidades de medida");
        List<UnidadesDeMedida> lista = findUnidadesDeMedidaEntities();
        llenarTabla(lista);
        view.setLocationRelativeTo(null);
    }
    public void llenarTabla(List<UnidadesDeMedida> _ls){
        if (_ls.size()>0) {            
            Object Datos[] = new Object[3];
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.addColumn("Id");
            modelo.addColumn("UNIDADES");
            modelo.addColumn("");
            
            int count =0;
            for (Object valor :_ls) {
                Datos[0] = _ls.get(count).getUmId();
                Datos[1] = _ls.get(count).getUmNombre();
                Datos[2] = "Modificar";
                
                count +=1;
                modelo.addRow(Datos);
            }
            view.dgvUnidades.setModel(modelo);
        }
   
    }
    
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource()==view.btnNuevaUnidad) {
                _unidad=null;
                idglobal=-1;
                viewCrear_Editar.txtUnidadMed.setText(null);
                viewCrear_Editar.setLocationRelativeTo(null);
                viewCrear_Editar.setVisible(true);
            }
            else if (e.getSource()==viewCrear_Editar.btnGuardar) {
                if (!viewCrear_Editar.txtUnidadMed.getText().trim().toString().isEmpty()) {
                    if (_unidad==null) {                                    
                        _unidad= new UnidadesDeMedida();                        
                        _unidad.setUmNombre(viewCrear_Editar.txtUnidadMed.getText().trim());
                        try{
                            create(_unidad);
                            llenarTabla(findUnidadesDeMedidaEntities());                           
                        }catch(Exception ex){
                            JOptionPane.showMessageDialog(view,"No se pudo agregar la unidad de medida");
                        }                            
                    }else{
                        _unidad= new UnidadesDeMedida();
                        _unidad.setUmId(BigDecimal.valueOf(Double.valueOf(idglobal)));                       
                        _unidad.setUmNombre(viewCrear_Editar.txtUnidadMed.getText().trim()); 
                        viewCrear_Editar.dispose();
                        try{
                            edit(_unidad);
                            llenarTabla(findUnidadesDeMedidaEntities());                            
                        }catch(Exception ex){
                            JOptionPane.showMessageDialog(viewCrear_Editar, "Ha sucedido un error modificar.");
                        }                    
                    }                    
                }
            }
            else if (e.getSource()==viewCrear_Editar.btnCancelar) {
                viewCrear_Editar.dispose();
            }
            else if (e.getSource()==view.btnBuscar)
            {
                if (!view.txtBuscar.getText().trim().isEmpty()) {
                    ArrayList<UnidadesDeMedida> list ;
                    list= findUnidadSearch(view.txtBuscar.getText().trim().toString());
                    if (list!=null) {
                        llenarTabla(list);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(view,"No existen Unidades relacionadas con: "+view.btnBuscar.getText());
                    }
                }
                else
                {
                    List<UnidadesDeMedida> lista = findUnidadesDeMedidaEntities();
                    llenarTabla(lista);
                }
            }
        }
    };
    //------------------------ Método que extrae un valor en específico --------------------//
    
    public ArrayList<UnidadesDeMedida> findUnidadSearch(String s) {

        try {
            claseConnect.AbrirConexionBD();
            CallableStatement cs
                    = claseConnect.con.prepareCall("{call findUnidadNombre(?,?)}");
            cs.setString(1, s);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.executeQuery();

            ResultSet rset = ((OracleCallableStatement) cs).getCursor(2);
            ArrayList<UnidadesDeMedida> Datos = new ArrayList<UnidadesDeMedida>();
            
                while (rset.next()) {
                    _unidad = new UnidadesDeMedida();
                    _unidad.setUmId(rset.getBigDecimal("UM_ID"));
                    _unidad.setUmNombre(rset.getString("UM_NOMBRE"));

                    Datos.add(_unidad);
                }
               
            

            claseConnect.CerrarConexionBD();
             return Datos;
            
        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(view, "El registro que intenta eliminar no existe.");

        }
        return null;

    }
    
    //------------------------ Métodos del acceso a datos --------------------//  
    
    public UnidadesDeMedidaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UnidadesDeMedida unidadesDeMedida) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(unidadesDeMedida);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUnidadesDeMedida(unidadesDeMedida.getUmId()) != null) {
                throw new PreexistingEntityException("UnidadesDeMedida " + unidadesDeMedida + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UnidadesDeMedida unidadesDeMedida) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            unidadesDeMedida = em.merge(unidadesDeMedida);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = unidadesDeMedida.getUmId();
                if (findUnidadesDeMedida(id) == null) {
                    throw new NonexistentEntityException("The unidadesDeMedida with id " + id + " no longer exists.");
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
            UnidadesDeMedida unidadesDeMedida;
            try {
                unidadesDeMedida = em.getReference(UnidadesDeMedida.class, id);
                unidadesDeMedida.getUmId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The unidadesDeMedida with id " + id + " no longer exists.", enfe);
            }
            em.remove(unidadesDeMedida);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UnidadesDeMedida> findUnidadesDeMedidaEntities() {
        return findUnidadesDeMedidaEntities(true, -1, -1);
    }

    public List<UnidadesDeMedida> findUnidadesDeMedidaEntities(int maxResults, int firstResult) {
        return findUnidadesDeMedidaEntities(false, maxResults, firstResult);
    }

    private List<UnidadesDeMedida> findUnidadesDeMedidaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UnidadesDeMedida.class));
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

    public UnidadesDeMedida findUnidadesDeMedida(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UnidadesDeMedida.class, id);
        } finally {
            em.close();
        }
    }

    public int getUnidadesDeMedidaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UnidadesDeMedida> rt = cq.from(UnidadesDeMedida.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
