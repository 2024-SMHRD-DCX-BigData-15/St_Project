package com.smhrd.St_Project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smhrd.St_Project.service.MemberService;
import com.smhrd.St_Project.util.PasswordEncryptor;
import com.smhrd.St_Project.entity.MemberEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api") // API 엔드포인트 네임스페이스 지정
public class MemberRestController {

    @Autowired
    private MemberService memberService;

}
