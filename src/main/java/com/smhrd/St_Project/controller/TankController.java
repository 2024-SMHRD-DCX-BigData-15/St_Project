package com.smhrd.St_Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smhrd.St_Project.entity.TankEntity;
import com.smhrd.St_Project.service.TankService;

@Controller()
@RequestMapping("/tank")
public class TankController {

	@GetMapping("/edit/{tankIdx}")
    public String editTankPage(@PathVariable("tankIdx") Long tankIdx, Model model) {
        // 해당 tankIdx의 정보를 가져오기
        TankEntity tank = TankService.getTankById(tankIdx);
        model.addAttribute("tank", tank);
        
        return "tankedit"; // tankEdit.html 뷰 반환
    }
	
}
