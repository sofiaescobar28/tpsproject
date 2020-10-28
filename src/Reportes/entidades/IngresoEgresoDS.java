/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reportes.entidades;

import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author Usuario
 */
public class IngresoEgresoDS implements JRDataSource {

    private int indice = -1;
    private List<EEgresoIngreso> listaTabla = new ArrayList<EEgresoIngreso>();
    @Override
    public boolean next() throws JRException {
        return ++indice < listaTabla.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object value = null;
        String fieldName = jrf.getName();
        switch(fieldName){
            case "Descripcion":
                value=listaTabla.get(indice).getDescripcion();
            break;
            case "Tipo":
                value=listaTabla.get(indice).getTipo();
            break;
            case "Fecha":
                value=listaTabla.get(indice).getFecha();
            break;
            case "Categoria":
                value=listaTabla.get(indice).getCategoria();
            break;
            case "Cantidad":
                value=listaTabla.get(indice).getCantidad();
            break;
            case "Medida":
                value=listaTabla.get(indice).getMedida();
            break;
            case "Monto":
                value=listaTabla.get(indice).getMonto();
            break;            
        }
        return value;
    }
    public void addIE(EEgresoIngreso IE){
        this.listaTabla.add(IE);
    }
}
