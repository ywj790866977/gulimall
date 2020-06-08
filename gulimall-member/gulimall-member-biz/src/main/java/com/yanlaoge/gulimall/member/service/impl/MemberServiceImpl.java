package com.yanlaoge.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yanlaoge.common.utils.*;
import com.yanlaoge.gulimall.member.entity.MemberLevelEntity;
import com.yanlaoge.gulimall.member.enums.MemberStatusEnum;
import com.yanlaoge.gulimall.member.service.MemberLevelService;
import com.yanlaoge.gulimall.member.vo.MemberLoginVo;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;
import com.yanlaoge.gulimall.member.vo.SocialUserVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yanlaoge.gulimall.member.dao.MemberDao;
import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.yanlaoge.gulimall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
@Slf4j
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
        MemberLevelEntity levelEntity = memberLevelService.getDefaultLevel();
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
        ServiceAssert.isFalse(count <= 0,
                MemberStatusEnum.NOT_PHONE.getCode(), MemberStatusEnum.NOT_PHONE.getMsg());
    }

    @Override
    public void checkUserNameUnique(String userName) {
        int count = count(new QueryWrapper<MemberEntity>().eq("username", userName));
        ServiceAssert.isFalse(count <= 0,
                MemberStatusEnum.NOT_USERNAME.getCode(), MemberStatusEnum.NOT_USERNAME.getMsg());
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity entity = getOne(new QueryWrapper<MemberEntity>()
                .eq("username", vo.getLoginacct()).or().eq("mobile", vo.getLoginacct()));
        ServiceAssert.isNull(entity, MemberStatusEnum.NOT_USER.getCode(), MemberStatusEnum.NOT_USER.getMsg());
        boolean isOk = new BCryptPasswordEncoder().matches(vo.getPassword(), entity.getPassword());
        ServiceAssert.isFalse(isOk, MemberStatusEnum.NOT_PASSWORD.getCode(), MemberStatusEnum.NOT_PASSWORD.getMsg());
        return entity;
    }

    @SneakyThrows
    @Override
    public MemberEntity oauthLogin(SocialUserVo vo) {
        //1.查询是否已经注册
        MemberEntity memberEntity = getOne(new QueryWrapper<MemberEntity>().eq("social_uid", vo.getUid()));
        //2.已经注册
        if (memberEntity != null) {
            return getExistMember(vo, memberEntity);
        }
        //3.未注册
        return autoRegister(vo);
    }

    private MemberEntity autoRegister(SocialUserVo vo) throws Exception {
        // 查询用户信息
        JSONObject object = getUserDetail(vo);
        // 设置注册实体
        MemberEntity register = new MemberEntity().setSocialUid(vo.getUid()).setExpiresIn(vo.getExpiresIn())
                .setAccessToken(vo.getAccessToken());
        //
        if (object != null) {
            register.setGender("m".equals(object.getString("gender")) ? 1 : 0)
                    .setHeader(object.getString("profile_image_url"))
                    .setCity(object.getString("location"))
                    .setUsername(object.getString("name"));
        }
        save(register);
        return register;
    }

    private MemberEntity getExistMember(SocialUserVo vo, MemberEntity memberEntity) {
        MemberEntity updateEntity = new MemberEntity();
        updateEntity.setId(memberEntity.getId());
        updateEntity.setExpiresIn(vo.getExpiresIn());
        updateEntity.setAccessToken(vo.getAccessToken());
        // 更新
        updateById(updateEntity);
        memberEntity.setAccessToken(vo.getAccessToken());
        memberEntity.setExpiresIn(vo.getExpiresIn());
        return memberEntity;
    }

    private JSONObject getUserDetail(SocialUserVo vo) throws Exception {
//        try {
        Map<String, String> maps = Maps.newHashMap();
        maps.put("access_token", vo.getAccessToken());
        maps.put("uid", vo.getUid());
        HttpResponse response =
                HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", Maps.newHashMap(), maps);
        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("[getUserDetail] is error res:{}", response);
            ResponseHelper.execption(MemberStatusEnum.NOT_QUERY_USER_INFO.getCode(),
                    MemberStatusEnum.NOT_QUERY_USER_INFO.getMsg());
        }
        String json = EntityUtils.toString(response.getEntity());
        return JSON.parseObject(json);
//        } catch (Exception e) {
//            log.error("[getUserDetail] is error ", e);
//            ResponseHelper.execption(MemberStatusEnum.NOT_QUERY_USER_INFO.getCode(),
//                    MemberStatusEnum.NOT_QUERY_USER_INFO.getMsg());
//            return null;
//        }
    }

}