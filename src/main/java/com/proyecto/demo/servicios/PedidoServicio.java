

package com.proyecto.demo.servicios;

import com.proyecto.demo.entidades.Barra;
import com.proyecto.demo.entidades.Cristaleria;
import com.proyecto.demo.entidades.Pedido;
import com.proyecto.demo.entidades.Proveedor;
import com.proyecto.demo.entidades.Usuario;
import com.proyecto.demo.errores.ErrorServicio;
import com.proyecto.demo.repositorios.BarraRepositorio;
import com.proyecto.demo.repositorios.PedidoRepositorio;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoServicio {
    
    @Autowired
    private PedidoRepositorio barraRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Transactional
    public void registrar(String nombre,String idUsuario) throws ErrorServicio {

       
       Pedido barra = new Pedido();
       //barra.setAlta(new Date());
       barra.setUsuario(usuarioServicio.buscarPorId(idUsuario));
       barra.setActiva(true);
       usuarioServicio.actualizarListBarras(idUsuario,barra.getId());
       barra.setNombre(nombre);
       
      
        
        barraRepositorio.save(barra);
        

       

    }
    
    //ACTUALIZAR PRECIO TOTAL DE BARRA
    
    @Transactional
    public void actualizarPrecioBarra(Barra barra,float costoRuptura) throws ErrorServicio{
        //buscamos el usuario y getiamos las listas de Barra
    
    barra.setPrecioTotal(barra.getPrecioTotal()-costoRuptura);
    
    
    
    
    }
    public void modificarPedido( String id, String nombre,int contacto,String link) throws ErrorServicio {

        
        Pedido barra = new Pedido();
        barra.setNombre(nombre.toUpperCase());
        Usuario usuario = usuarioServicio.buscarPorId(id);
        barra.setUsuario(usuario);
        barra.setTotalUnidades(contacto);
        barra.setAclaracion(link);
       
       
         barraRepositorio.save(barra);
        

        
        if (usuario!=null) {

           
           
            usuarioServicio.actualizarListProveedores(id, barra.getId());
            
            
           
            

            

            
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    
    public int actualizarStockBarra(String idUsuario) throws ErrorServicio{
        int total=0;
        Usuario usuario = usuarioServicio.buscarPorId(idUsuario);
        if(usuario!=null){
            
            for (Barra barra : usuario.getBarras()) {
                total=total+barra.getTotalUnidades();
            }
        
        
        }
    
    return total;
    }
        
    @Transactional
    public void actualizarStockBarra(String idBarra,int numeroRupturas) throws ErrorServicio{
        //buscamos el usuario y getiamos las listas de Barra
    Pedido barra = buscarPorId(idBarra);
    barra.setTotalUnidades(barra.getTotalUnidades()-numeroRupturas);
     
            
    
    }
   
    public List<Pedido> listarTodas(){
        return barraRepositorio.findAll();
    }
    public float calcularPrecioTotal(List<Cristaleria> cristalerias){
        
        float suma =0;
        
        for (Cristaleria cristaleria : cristalerias) {
            
            suma = suma + cristaleria.getPrecioTotal();
            System.out.println("precio"+suma);
        }
        
    
    return suma;
    
    
    }
    //ELIMINAR BARRA
    @Transactional
    public void eliminar(String idBarra) throws ErrorServicio{
        
        buscarPorId(idBarra).setActiva(false);
        
    
    
        
        
    }
    
    
    
     public Pedido buscarPorId(String id) throws ErrorServicio {

        Optional<Pedido> respuesta = barraRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Pedido barra = respuesta.get();
            return barra;
        } else {

            throw new ErrorServicio("No se encontró la barra solicitada");
        }

    }
     
     public void deshabilitar(String id) throws ErrorServicio{
     
          Pedido barra = buscarPorId(id);
          barraRepositorio.delete(barra);
          barraRepositorio.deleteById(id);
     
     }
     
     
      @Transactional
    public void borrarTodo(){
    
    barraRepositorio.deleteAll();
    }
       
     

}
