package com.smhrd.St_Project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.St_Project.service.MemberService;

@RestController
public class MemberRestController {

	@Autowired
	MemberService memberService;
	
	@GetMapping("/check-id")
	public boolean checkId(@RequestParam String id) {
//		System.out.println("check-id 확인");
		boolean result = memberService.isIdCheck(id);
		
		return result;
	}
}
