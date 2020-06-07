package com.yanlaoge.gulimall.member.test;

import com.yanlaoge.gulimall.member.controller.MemberController;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDemo {
    @Resource
    private MemberController memberController;

    @Test
    public void test01(){
        MemberRegisterVo memberRegisterVo = new MemberRegisterVo();
        memberRegisterVo.setUserName("tom");
        memberRegisterVo.setPassword("123");
        memberRegisterVo.setPhone("12342424");
        memberController.regist(memberRegisterVo);
    }
}
