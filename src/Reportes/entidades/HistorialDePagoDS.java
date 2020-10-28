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
public class HistorialDePagoDS implements JRDataSource{
    private int indice = -1;
    private List<EGasto> listaTabla = new ArrayList<EGasto>();

    @Override
    public boolean next() throws JRException {
        return ++indice < listaTabla.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object value = null;
        String fieldName = jrf.getName();
        switch(fieldName){
            case "Empleado":
                value=listaTabla.get(indice).getNombre();
            break;
            case "CargoO":
                value=listaTabla.get(indice).getCargoO();
            break;
            case "CargoT":
                value=listaTabla.get(indice).getCargoT();
            break;
            case "Fecha":
                value=listaTabla.get(indice).getFecha();
            break;
            case "Pago":
                value=listaTabla.get(indice).getPago();
            break;
            case "Comentario":
                value=listaTabla.get(indice).getComentario();
            break;
        }
        return value;
    }
    
    public void addGasto(EGasto gasto)
    {
        this.listaTabla.add(gasto);
    }      
}
