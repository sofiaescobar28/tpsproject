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
public class IEReumenDS implements JRDataSource {
    private int indice = -1;
    private List<EIEResumen> listaTabla = new ArrayList<EIEResumen>();
    @Override
    public boolean next() throws JRException {
        return ++indice < listaTabla.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object value = null;
        String fieldName = jrf.getName();
        switch(fieldName){
            case "Fecha":
                value=listaTabla.get(indice).getFecha();
            break;
            case "Tipo":
                value=listaTabla.get(indice).getTipo();
            break;
            case "Monto":
                value=listaTabla.get(indice).getMonto();
            break;
        }
        return value;
    }
    public void addResumen(EIEResumen resumen){
        this.listaTabla.add(resumen);
    }    
}
