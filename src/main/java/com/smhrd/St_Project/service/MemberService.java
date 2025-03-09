package com.smhrd.St_Project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smhrd.St_Project.repository.MemberRepository;
import com.smhrd.St_Project.entity.MemberEntity;

@Service
public class MemberService {

		// repository 연결
		@Autowired()
		MemberRepository memberRepository;
		
		// 회원가입 기능
		public String register(MemberEntity member) {
		
			// jpa insert가 진행되는 경우
			// save() 메소드 사용
			memberRepository.save(member);
			
			return "";
		}
		
		// 아이디 중복체크 기능
		public boolean isUserIdExists(String id) {
			// 기능 추가
			// id 중복체크!!
			// existsById -> member pk idx
			// 실제 id라는 컬럼을 희망
			// true -> 데이터가 있다 --> 아이디가 중복되었다!
			return memberRepository.existsByUserId(id);
		}
		
		// 로그인 기능
		public MemberEntity login(String id, String pw) {
			
			// select * from member_entity where id = ? and pw = ?
			return memberRepository.findByUserIdAndUserPw(id, pw);
			
		}
		
		
		
		
		
		
}
