

package com.proyecto.demo.servicios;

import com.proyecto.demo.entidades.Cristaleria;
import com.proyecto.demo.entidades.Ruptura;
import com.proyecto.demo.entidades.Usuario;
import com.proyecto.demo.errores.ErrorServicio;
import com.proyecto.demo.repositorios.RupturaRepositorio;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RupturaServicio {
    
    
    
    @Autowired
    private RupturaRepositorio rupturaRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private CristaleriaServicio cristaleriaServicio;
    @Autowired
    private BarraServicio barraServicio;
    
    @Transactional
    public void modificar( String nombre, String explicacion, int cantidad,String idCristaleria,String id) throws ErrorServicio {

       Ruptura ruptura = new Ruptura();
        
  

        
        if (!idCristaleria.isBlank()) {

            Cristaleria cristaleria = cristaleriaServicio.buscarPorId(idCristaleria);
                 Usuario usuario = usuarioServicio.buscarPorId(id);
                 
           ruptura.setNombre(nombre);
           ruptura.setExplicacion(explicacion);
           ruptura.setNumeroDeRuptura(cantidad);
           ruptura.setCostoRuptura(cristaleria.getPrecio()*cantidad);
           ruptura.setTipoCristaleria(cristaleria);
           cristaleria.setEnStock(cristaleria.getEnStock()-cantidad);
           cristaleria.setIdUsuario(usuario.getId());
           List<Ruptura> rupturas =usuario.getTodasLasRupturas();
           rupturas.add(ruptura);
           usuario.setTodasLasRupturas(rupturas);
           barraServicio.actualizarPrecioBarra(cristaleria.getBarraPerteneciente(), ruptura.getCostoRuptura());
        
           //barraRepositorio.save(barraPerteneciente);

        }
        rupturaRepositorio.save(ruptura);
    }
    
    
    
   

}
