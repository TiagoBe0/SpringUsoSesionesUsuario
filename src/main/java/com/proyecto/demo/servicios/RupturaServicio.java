

package com.proyecto.demo.servicios;

import com.proyecto.demo.repositorios.RupturaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RupturaServicio {
    
    
    
    @Autowired
    private RupturaRepositorio rupturaRepositorio;

}
