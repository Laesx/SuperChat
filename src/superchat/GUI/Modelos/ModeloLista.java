/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package superchat.GUI.Modelos;

import javax.swing.*;
import java.util.ArrayList;

/**
 * ModeloLista modelo para crearlo dentro de la ventana luego añadirlo al init
 * @author losgu
 */
public class ModeloLista extends DefaultListModel<String> {
    
    private ArrayList<String> lista;
    
    public ModeloLista(){
        lista = new ArrayList();
    }


    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String getElementAt(int index) {
        return null;
    }

}
