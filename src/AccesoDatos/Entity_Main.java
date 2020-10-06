/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Manuel
 */
public class Entity_Main {
    private static final EntityManagerFactory ent=
            Persistence.createEntityManagerFactory("Gestor_de_proyectosPU");
    public Entity_Main(){}
    public static EntityManagerFactory getInstance()
    {
    return ent;}
}
