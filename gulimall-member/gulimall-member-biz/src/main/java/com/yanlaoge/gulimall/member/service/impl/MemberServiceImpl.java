package com.yanlaoge.gulimall.member.service.impl;

import com.yanlaoge.common.utils.ServiceAssert;
import com.yanlaoge.gulimall.member.entity.MemberLevelEntity;
import com.yanlaoge.gulimall.member.enums.MemberStatusEnum;
import com.yanlaoge.gulimall.member.service.MemberLevelService;
import com.yanlaoge.gulimall.member.vo.MemberLoginVo;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.member.dao.MemberDao;
import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.yanlaoge.gulimall.member.service.MemberService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        // 查询默认等级
        MemberLevelEntity levelEntity =  memberLevelService.getDefaultLevel();
        // 校验手机号, 用户名 是否唯一
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(vo.getPassword());
        // 设置属性
        memberEntity.setUsername(vo.getUserName())
                .setPassword(encodePassword)
                .setLevelId(levelEntity.getId())
                .setMobile(vo.getPhone());

        save(memberEntity);
    }

    @Override
    public boolean checkEmailUnique(String email) {
        return false;
    }

    @Override
    public void checkPhoneUnique(String phone) {
        int count = count(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        ServiceAssert.isFalse(count <=0,
                MemberStatusEnum.NOT_PHONE.getCode(),MemberStatusEnum.NOT_PHONE.getMsg());
    }

    @Override
    public void checkUserNameUnique(String userName) {
        int count = count(new QueryWrapper<MemberEntity>().eq("username", userName));
        ServiceAssert.isFalse( count <=0,
                MemberStatusEnum.NOT_USERNAME.getCode(),MemberStatusEnum.NOT_USERNAME.getMsg());
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity entity = getOne(new QueryWrapper<MemberEntity>()
                .eq("username", vo.getLoginacct()).or().eq("mobile", vo.getLoginacct()));
        ServiceAssert.isNull(entity,MemberStatusEnum.NOT_USER.getCode(),MemberStatusEnum.NOT_USER.getMsg());
        boolean isOk = new BCryptPasswordEncoder().matches(vo.getPassword(), entity.getPassword());
        ServiceAssert.isFalse(isOk,MemberStatusEnum.NOT_PASSWORD.getCode(),MemberStatusEnum.NOT_PASSWORD.getMsg());
        return entity;
    }

}