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
public class CargosDS implements JRDataSource {
    private int indice = -1;
    private List<ECargos> listaTabla = new ArrayList<ECargos>();
    @Override
    public boolean next() throws JRException {
        return ++indice < listaTabla.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object value = null;
        String fieldName = jrf.getName();
        switch(fieldName){
            case "Cargo":
                value=listaTabla.get(indice).getCargo();
            break;
            case "Empleados activos":
                value=listaTabla.get(indice).getNum_Emp();
            break;            
        }
        return value;
    }
    public void addCargo(ECargos cargo){
        this.listaTabla.add(cargo);
    }
}
