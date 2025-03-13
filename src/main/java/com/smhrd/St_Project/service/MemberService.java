package com.smhrd.St_Project.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.util.PasswordEncryptor;

import jakarta.transaction.Transactional;

import com.smhrd.St_Project.entity.MemberEntity;

@Service
public class MemberService {

      // repository 연결
      @Autowired()
      MemberRepository memberRepository;
      
      // 회원가입 기능 (비밀번호 암호화 적용)
      public void register(MemberEntity member) {
    	  memberRepository.save(member);
      }

     

}
