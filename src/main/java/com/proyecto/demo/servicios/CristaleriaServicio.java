

package com.proyecto.demo.servicios;

import com.proyecto.demo.entidades.Barra;
import com.proyecto.demo.entidades.Cristaleria;
import com.proyecto.demo.entidades.Foto;
import com.proyecto.demo.errores.ErrorServicio;

import com.proyecto.demo.repositorios.CristaleriaRepositorio;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CristaleriaServicio {
    
    
    @Autowired
    private CristaleriaRepositorio cristaleriaRepositorio;
     @Autowired
    private BarraServicio barraServicio;
    
     @Autowired
    private FotoServicio fotoServicio;

    
    @Transactional
    public void registrar(MultipartFile archivo, String tipo, String descripcion, float precio, int enStock,String idBarra) throws ErrorServicio {

       

        

       Cristaleria cristaleria = new Cristaleria();
       
      
        Foto foto = fotoServicio.guardar(archivo);
        cristaleria.setFoto(foto);
        cristaleria.setAlta(new Date());
        cristaleria.setDescripcion(descripcion);
        cristaleria.setPrecio(precio);
        cristaleria.setEnStock(enStock);
        cristaleria.setTipo(tipo);
        //Creamos una barra de pertenecencia y añadimos a lista de cristaleria comoa tributo
        Barra barra =barraServicio.buscarPorId(idBarra);
         List<Cristaleria> cristalerias = barra.getListaCristalerias();
         cristalerias.add(cristaleria);
         barra.setListaCristalerias(cristalerias);
          float suma = barraServicio.calcularPrecioTotal(cristalerias);
        barra.setPrecioTotal(suma);
         
        cristaleria.setBarraPerteneciente(barra);
        
        
        cristaleriaRepositorio.save(cristaleria);
        

       

    }

    @Transactional
    public void modificar(MultipartFile archivo, String tipo, String descripcion, float precio, int enStock,String id) throws ErrorServicio {

       
        
        

        Optional<Cristaleria> respuesta = cristaleriaRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Cristaleria cristaleria = respuesta.get();
            
        cristaleria.setAlta(new Date());
        cristaleria.setDescripcion(descripcion);
        cristaleria.setPrecio(precio);
        cristaleria.setEnStock(enStock);
        cristaleria.setTipo(tipo);
           

            String idFoto = null;
            if (cristaleria.getFoto() != null) {
                idFoto = cristaleria.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            cristaleria.setFoto(foto);

            cristaleriaRepositorio.save(cristaleria);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    public List<Cristaleria> todosLosUsuarios(){
 
        return cristaleriaRepositorio.findAll();
        
    }
    
    
    public Cristaleria buscarPorId(String id) throws ErrorServicio {

        Optional<Cristaleria> respuesta = cristaleriaRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Cristaleria cristaleria = respuesta.get();
            return cristaleria;
        } else {

            throw new ErrorServicio("No se encontró la cristaleria solicitada");
        }

    }
    
}
