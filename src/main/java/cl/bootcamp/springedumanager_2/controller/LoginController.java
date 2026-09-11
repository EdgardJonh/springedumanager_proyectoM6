package cl.bootcamp.springedumanager_2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	@GetMapping("/")
	public String irLogin() {
		// Los usuarios no autenticados seran redirigidos a /login
		// automaticamente por Spring Security antes de llegar aqui.
		return "redirect:/home";
	}
	
	@GetMapping("/login")
	public String mostrarLogin() {
		return "login";//templates --> login.html
	}
}
