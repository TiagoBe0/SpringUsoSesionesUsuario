package com.proyecto.demo.servicios;

import com.proyecto.demo.entidades.Barra;
import com.proyecto.demo.entidades.Cristaleria;
import com.proyecto.demo.entidades.Foto;
import com.proyecto.demo.entidades.Usuario;
import com.proyecto.demo.entidades.Zona;
import com.proyecto.demo.enums.Rol;
import com.proyecto.demo.errores.ErrorServicio;
import com.proyecto.demo.repositorios.BarraRepositorio;
import com.proyecto.demo.repositorios.UsuarioRepositorio;
import com.proyecto.demo.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private CristaleriaServicio cristaleriaServicio;

    @Autowired
    private FotoServicio fotoServicio;
    @Autowired
    private BarraServicio barraServicio;

     @Autowired
    private BarraRepositorio barraRepositorio;

 @javax.transaction.Transactional
    public void registrarBarra(String nombre,String idUsuario) throws ErrorServicio {

       System.out.println("LLEGAMOS A USUARIO REGISTRO BARRAS");
       Barra barra = new Barra();
       barra.setNombre(nombre);

       //barra.setAlta(new Date());
       barra.setUsuario(buscarPorId(idUsuario));
       
       actualizarListBarras(idUsuario,barra.getId());
       
       
        
        barraRepositorio.save(barra);
        

       

    }
    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String mail, String clave, String clave2) throws ErrorServicio {
        System.out.println("LLEGARON DATOS A LOS SERVICIOOOOOOOOOOOOOOS");
        System.out.println("LLEGARON DATOS A LOS SERVICIOOOOOOOOOOOOOOS");

        validar(nombre, apellido, mail, clave, clave2);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        
        usuario.setRol(Rol.ADMIN);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);
        
        //usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

    }

    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String mail, String clave, String clave2) throws ErrorServicio {

        
        
        validar(nombre, apellido, mail, clave, clave2);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setApellido(apellido);
            usuario.setNombre(nombre);
            usuario.setMail(mail);
            
            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuarioRepositorio.save(usuario);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    @Transactional
    public void modificarBarra( String id, String nombre) throws ErrorServicio {

        
        Barra barra = new Barra();
        barra.setNombre(nombre);
        barra.setUsuario(buscarPorId(id));
       
         barraRepositorio.save(barra);
        

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
           
            actualizarListBarras(id, barra.getId());
            
            
           
            

            

            usuarioRepositorio.save(usuario);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
        
@Transactional
public void actualizarListaDeCristalerias(Usuario usuario,List<Cristaleria> cristalerias){
    
    usuario.setTodasLasCristalerias(cristalerias);



}
    
    
@Transactional
    public void modificarCristaleria( String id, String nombre,String tipo, String descripcion, float precio, int enStock,String idBarra) throws ErrorServicio {

        
        Barra barra = new Barra();
        barra.setNombre(nombre);
        barra.setUsuario(buscarPorId(id));
       
         barraRepositorio.save(barra);
        

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
           
            actualizarListBarras(id, barra.getId());
            
            
           
            

            

            usuarioRepositorio.save(usuario);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }

    @Transactional
    public void habilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    
    @Transactional
    public void cambiarRol(String id) throws ErrorServicio{
    
    	Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
    	
    	if(respuesta.isPresent()) {
    		
    		Usuario usuario = respuesta.get();
    		
    		if(usuario.getRol().equals(Rol.USUARIO)) {
    			
    		usuario.setRol(Rol.ADMIN);
    		
    		}else if(usuario.getRol().equals(Rol.ADMIN)) {
    			usuario.setRol(Rol.USUARIO);
    		}
    	}
    }

    public void validar(String nombre, String apellido, String mail, String clave, String clave2) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail no puede ser nulo");
        }

        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("La clave del usuario no puede ser nula y tiene que tener mas de seis digitos");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las claves deben ser iguales");
        }

       

    }
    
    
    //listar toda crtistaleria de barras
    public List<Cristaleria> listarTodaLaCristaleria(String id) throws ErrorServicio{
          
        Usuario usuario = buscarPorId(id);
        List<Barra> barras =usuario.getBarras();
        List<Cristaleria> todasLasCristalerias = null;
        
        for (Barra barra : barras) {
            
            for (Cristaleria obj : barra.getListaCristalerias()) {
                todasLasCristalerias.add(obj);
            }
            
        }
    return todasLasCristalerias;
    
    }
    
   //huscar cristaleria por id
    public List<Cristaleria> buscarCristaleriaPorIdUsuario(String id){
    
    return cristaleriaServicio.listarPorIdUsuario(id);
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
    	
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        
        if (usuario != null) {
        	
            List<GrantedAuthority> permisos = new ArrayList<>();
                        
            //Creo una lista de permisos! 
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_"+ usuario.getRol());
            
            permisos.add(p1);
         
            //Esto me permite guardar el OBJETO USUARIO LOG, para luego ser utilizado
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            
            session.setAttribute("usuariosession", usuario); // llave + valor

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            
    
            
            return user;

        } else {
            return null;
        }

    }

 @Transactional(readOnly=true)
    public Usuario buscarPorId(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            return usuario;
        } else {

            throw new ErrorServicio("No se encontró el usuario solicitado");
        }

    }
    
   @Transactional(readOnly=true)
    public List<Usuario>  todosLosUsuarios(){
 
        return usuarioRepositorio.findAll();
        
    }
    @Transactional(readOnly=true)
    public List<Barra>  todasLasBarras(String idUsuario) throws ErrorServicio{
     Usuario usuario =buscarPorId(idUsuario);
     
 
        return usuario.getBarras();
        
    }
    
    public void actualizarListBarras(String idUsuario,String idBarra) throws ErrorServicio{
        //buscamos el usuario y getiamos las listas de Barra
    Usuario usuario = buscarPorId(idUsuario);
    List<Barra> barras = usuario.getBarras();
    //sumamos las barras a la list
    barras.add(barraServicio.buscarPorId(idBarra));
    usuario.setBarras(barras);
    
    
    
    
    }
   
 

    
  
}
