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
public class PlanillaDS implements JRDataSource{
    private int indice = -1;
    private List<EPlanilla> listaTabla = new ArrayList<EPlanilla>();

    @Override
    public boolean next() throws JRException {
        return ++indice < listaTabla.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object value = null;
        String fieldName = jrf.getName();
        switch(fieldName){
            case "Nombre":
                value=listaTabla.get(indice).getNombre();                
            break;
            case "Cargo":
                value=listaTabla.get(indice).getCargo();
            break;
            case "Salario":
                value=listaTabla.get(indice).getSalario();
            break;
            case "Estado":
                value=listaTabla.get(indice).getEstado();
            break;
            case "Telefono":
                value=listaTabla.get(indice).getTelefono();
            break;
        }
        return value;
    }
    public void addPlanilla (EPlanilla pl){
        this.listaTabla.add(pl);
    }
}
