

package com.proyecto.demo.servicios;

import com.proyecto.demo.entidades.Barra;
import com.proyecto.demo.entidades.Cristaleria;
import com.proyecto.demo.errores.ErrorServicio;
import com.proyecto.demo.repositorios.BarraRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BarraServicio {
    
    @Autowired
    private BarraRepositorio barraRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Transactional
    public void registrar(String nombre,String idUsuario) throws ErrorServicio {

       
       Barra barra = new Barra();
       barra.setAlta(new Date());
       barra.setUsuario(usuarioServicio.buscarPorId(idUsuario));
       
       usuarioServicio.actualizarListBarras(idUsuario,barra.getId());
       barra.setNombre(nombre);
       
        
        barraRepositorio.save(barra);
        

       

    }
    
    public void cargarListaDeCristaleria(String id , List<Cristaleria> cristalerias) throws ErrorServicio{
        
        Barra barra = buscarPorId(id);
        barra.setListaCristalerias(cristalerias);
       
    
    
    
    
    }
    public List<Barra> listarTodas(){
        return barraRepositorio.findAll();
    }
    public float calcularPrecioTotal(List<Cristaleria> cristalerias){
        
        float suma =0;
        
        for (Cristaleria cristaleria : cristalerias) {
            
            suma = suma + cristaleria.getPrecio();
            
        }
        
    
    return suma;
    
    
    }
     public Barra buscarPorId(String id) throws ErrorServicio {

        Optional<Barra> respuesta = barraRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Barra barra = respuesta.get();
            return barra;
        } else {

            throw new ErrorServicio("No se encontró la barra solicitada");
        }

    }

}
