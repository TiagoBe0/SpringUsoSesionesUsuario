package com.proyecto.demo.controladores;

import com.proyecto.demo.entidades.Usuario;
import com.proyecto.demo.entidades.Zona;
import com.proyecto.demo.errores.ErrorServicio;
import com.proyecto.demo.repositorios.ZonaRepositorio;
import com.proyecto.demo.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {

        List<Zona> zonas = zonaRepositorio.findAll();
        model.put("zonas", zonas);

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        try {
            Usuario usuario = usuarioServicio.buscarPorId(id);
            model.addAttribute("perfil", usuario);
        } catch (ErrorServicio e) {
            model.addAttribute("error", e.getMessage());
        }
        return "perfil.html";
    }
     @GetMapping("/registroUsuario")
    public String registro() {
        
        
        return "registroUsuario.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @PostMapping("/actualizar-perfil")
    public String actualizar(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2, String idZona) {

        Usuario usuario = null;
        try {

            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }

            usuario = usuarioServicio.buscarPorId(id);
            usuarioServicio.modificar(archivo, id, nombre, apellido, mail, clave2, clave2, idZona);
            session.setAttribute("usuariosession", usuario);

            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", usuario);

            return "perfil.html";
        }

    }
      @GetMapping("/loginUsuarioModelo")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
        if (error != null) {
            model.put("error", "Usuario o clave incorrectos");
        }
        if (logout != null) {
            model.put("logout", "Ha salido correctamente.");
        }
        return "loginUsuario1.html";
    }
     @GetMapping("/loginUsuario")
    public String palabrota() {
        
        return "loginUsuario1.html";
    }
     @GetMapping("/inicioUsuario")
    public String inicioUsuario() {
        
        return "inicioUsuario.html";
    }
    
     @PostMapping("/registrar")
    public String registrar( ModelMap modelo,MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2) {
        System.out.println("LLEGAMOS A LOS CONTROLADORES TIOOOOOOO");
        try {
            usuarioServicio.registrar(archivo, nombre, apellido, mail, clave1, clave2);
        } catch (ErrorServicio ex) {
           
            return "index.html";
        }
        modelo.put("titulo", "Bienvenido a Tinder de Mascotas");
        modelo.put("descripcion", "Tu usuario fue registrado de manera satisfactoria");
        return "exito.html";
    } 
}
