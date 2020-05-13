package com.yanlaoge.gulimall.member.dao;

import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:19:47
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
