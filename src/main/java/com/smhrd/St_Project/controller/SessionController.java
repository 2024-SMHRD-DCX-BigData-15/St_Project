package com.smhrd.St_Project.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
	    
	 @GetMapping("/keep-session-alive")
	 public String keepSessionAlive(HttpSession session) {
	 session.setMaxInactiveInterval(3600); // 세션을 60분으로 연장
	    return "Session refreshed";
	}
}