

package com.proyecto.demo.servicios;

import com.proyecto.demo.entidades.Barra;
import com.proyecto.demo.entidades.Cristal;
import com.proyecto.demo.entidades.Cristaleria;
import com.proyecto.demo.entidades.Foto;
import com.proyecto.demo.entidades.Usuario;
import com.proyecto.demo.errores.ErrorServicio;
import com.proyecto.demo.repositorios.BarraRepositorio;
import com.proyecto.demo.repositorios.CristalRepositorio;

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
     private CristalServicio cristalServicio;
     
     @Autowired
     private UsuarioServicio usuarioServicio;
     
     
     @Autowired
     private BarraRepositorio barraRepositorio;
      @Autowired
     private CristalRepositorio cristalRepositorio;
    
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
        Usuario usuario = usuarioServicio.buscarPorId(barra.getUsuario().getId());
        cristaleria.setIdUsuario(barra.getUsuario().getId());
         List<Cristaleria> cristalerias = barra.getListaCristalerias();
         cristalerias.add(cristaleria);
       
         barra.setListaCristalerias(cristalerias);
          float suma = barraServicio.calcularPrecioTotal(cristalerias);
          usuario.setCapitalTotal(suma);
        barra.setPrecioTotal(suma);
         
        cristaleria.setBarraPerteneciente(barra);
        
        
        cristaleriaRepositorio.save(cristaleria);
        

       

    }
    
    public List<Cristaleria> listarPorIdUsuario(String mail){
    
    
    return cristaleriaRepositorio.buscarPorIdUsuario(mail);
    }

    @Transactional
    public void modificar(MultipartFile archivo, String tipo, String descripcion, float precio, int enStock,String idBarra,String id,String idCristal) throws ErrorServicio {

       Cristaleria cristaleria = new Cristaleria();
       Cristal cristal = cristalServicio.buscarPorId(idCristal);
        if(cristal !=null){
              cristaleria.setCristalRepo(cristal);
           
            cristaleria.setFoto(cristal.getFoto());
               
        
        }else{
        Foto foto = fotoServicio.guardar(archivo);
        cristaleria.setFoto(foto);
        
        
        }
   
        if (!idBarra.isEmpty()) {

            
        Barra barraPerteneciente = barraServicio.buscarPorId(idBarra);
        Usuario usuario = usuarioServicio.buscarPorId(id);
        cristaleria.setDescripcion(descripcion);
        cristaleria.setPrecio(precio);
        cristaleria.setEnStock(enStock);
        cristaleria.setPrecioTotal();
        cristaleria.setIdUsuario(id);
        cristaleria.setTipo(tipo);
        cristaleria.setBarraPerteneciente(barraPerteneciente);
        cristaleria.setBarraPertenecienteNombre(barraPerteneciente.getNombre());
        List<Cristaleria> cristalerias = barraPerteneciente.getListaCristalerias();
        
        cristalerias.add(cristaleria);
        barraPerteneciente.setTotalUnidades(barraPerteneciente.getTotalUnidades()+cristaleria.getEnStock());
         List<Cristaleria> cristaleriaUsuario =usuario.getTodasLasCristalerias();
         cristaleriaUsuario.add(cristaleria);
         usuario.setTodasLasCristalerias(cristaleriaUsuario);
        
         barraPerteneciente.setPrecioTotal(barraServicio.calcularPrecioTotal(cristalerias));
        barraPerteneciente.setListaCristalerias(cristalerias);
        
           //barraRepositorio.save(barraPerteneciente);
/*
            String idFoto = null;
            if (cristaleria.getFoto() != null) {
                idFoto = cristaleria.getFoto().getId();
                   Foto foto = fotoServicio.actualizar(idFoto, archivo);
            cristaleria.setFoto(foto);

            }
*/
         
            cristaleriaRepositorio.save(cristaleria);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
      @Transactional
    public void alterar(MultipartFile archivo, String tipo, String descripcion, float precio, int enStock,String id) throws ErrorServicio {
        
         if(!id.isEmpty()){
             Optional<Cristaleria> respuesta = cristaleriaRepositorio.findById(id);
             if(respuesta.isPresent()){
             
                 Cristaleria cristaleria =  respuesta.get();
                 
                 cristaleria.setDescripcion(descripcion);
                 cristaleria.setPrecio(precio);
                 cristaleria.setTipo(tipo);
                 cristaleria.setPrecioTotal();
                 if(archivo!=null){
                     
                     Foto foto =fotoServicio.guardar(archivo);
                     if(foto!=null){
                     
                         cristaleria.setFoto(foto);
                     }
                 
                 
                 }
                 List<Cristaleria> cristalerias=null;
                 Usuario usuario = usuarioServicio.buscarPorId(cristaleria.getIdUsuario());
                 if(usuario!=null){
                     cristalerias=usuario.getTodasLasCristalerias();
                     cristalerias.add(cristaleria);
                     usuario.setTodasLasCristalerias(cristalerias);
                     usuarioServicio.actualizarCapitalTotal(usuario.getId());
                     usuarioServicio.actualizarNumeroTotalDeCristalerias(usuario.getId());
                     
                 
                     
                 }
                 cristaleriaRepositorio.save(cristaleria);
                  Barra barra =barraServicio.buscarPorId(cristaleria.getBarraPerteneciente().getId());
                  if(barra!=null){
                  
                        barraServicio.actualizarStockBarra(usuario.getId());
                        float suma = barraServicio.calcularPrecioTotal(cristalerias);
                  barra.setPrecioTotal(suma);
                  }
             }
            

            
          }
       
         else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    public List<Cristaleria> todasCristalrias(){
 
        return cristaleriaRepositorio.findAll();
        
    }
    
   
     public void deshabilitar(String id) throws ErrorServicio{
     
          cristaleriaRepositorio.deleteById(id);
     
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
