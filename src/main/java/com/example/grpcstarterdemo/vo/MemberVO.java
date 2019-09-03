package com.example.grpcstarterdemo.vo;

import com.example.grpcdemo.grpc.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberVO {
    private String username;
    private String password;
    private String info;


    public static List<MemberVO> getList(List<Member> list){
        List<MemberVO> memberVOS = new ArrayList<>();
        for(Member m:list){
            MemberVO memberVO = new MemberVO(m.getUsername(), m.getPassword(), m.getInfo());
            memberVOS.add(memberVO);
        }
        return memberVOS;
    }
}