package pe.edu.Frontend.ProyectoFrontend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.Frontend.ProyectoFrontend.client.AutenticacionClient;
import pe.edu.Frontend.ProyectoFrontend.dto.LoginRequestDTO;
import pe.edu.Frontend.ProyectoFrontend.dto.LoginResponseDTO;
import pe.edu.Frontend.ProyectoFrontend.viewmodel.LoginModel;

import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AutenticacionClient autenticacionClient;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        LoginModel loginModel = new LoginModel("00", "", "","");
        model.addAttribute("loginModel", loginModel);
        return "inicio";
    }

    @PostMapping("/autenticar")
    public String autenticar(@RequestParam("codigo") String codigo,
                             @RequestParam("password") String password,
                             Model model) {

        System.out.println("Consumiendo con Feign Client!!!!");

        if (codigo == null || codigo.trim().length() == 0 ||
                password == null || password.trim().length() == 0){

            LoginModel loginModel = new LoginModel("01", "Error: Debe completar correctamente sus credenciales", "","");
            model.addAttribute("loginModel", loginModel);
            return "inicio";
        }

        try {
            // Crear el DTO de solicitud
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(codigo,password);
            ResponseEntity<LoginResponseDTO> responseEntity = autenticacionClient.login(loginRequestDTO);

            // Validar la respuesta del backend
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LoginResponseDTO loginResponseDTO = responseEntity.getBody();
                if (loginResponseDTO != null && loginResponseDTO.codigoEstado().equals("00")) {
                    LoginModel loginModel = new LoginModel("00", "", loginResponseDTO.nombre(), loginResponseDTO.apellido());
                    model.addAttribute("loginModel", loginModel);
                    return "redirect:/login/listar-usuarios";
                } else {
                    LoginModel loginModel = new LoginModel("02", "Error: Autenticación fallida", "","");
                    model.addAttribute("loginModel", loginModel);
                    return "inicio";
                }
            } else {
                LoginModel loginModel = new LoginModel("99", "Error: Problema con el servicio", "","");
                model.addAttribute("loginModel", loginModel);
                return "inicio";
            }

        } catch (Exception e) {
            LoginModel loginModel = new LoginModel("99", "Error: Ocurrió un problema en la autenticación", "","");
            model.addAttribute("loginModel", loginModel);
            System.out.println(e.getMessage());
            return "inicio";
        }
    }

    @GetMapping("/listar-usuarios")
    public String listarUsuarios(Model model) {
        try {
            ResponseEntity<List<String[]>> responseEntity = autenticacionClient.listarUsuarios();
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                List<String[]> usuarios = responseEntity.getBody();
                if (usuarios != null && !usuarios.isEmpty()) {
                    model.addAttribute("usuarios", usuarios);  // Agrega la lista al modelo
                } else {
                    model.addAttribute("mensaje", "No hay usuarios registrados");
                }
                return "listaUsuarios";
            } else {
                model.addAttribute("error", "Error: No se pudo obtener la lista de usuarios");
                return "error";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "Error: Ocurrió un problema al obtener la lista de usuarios");
            return "error";
        }
    }

}
