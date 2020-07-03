/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.SysRoleEntity;

import java.util.List;
import java.util.Map;


/**
 * 角色
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysRoleService extends IService<SysRoleEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存实体
     *
     * @param role role
     */
    void saveRole(SysRoleEntity role);

    /**
     * 更新
     *
     * @param role role
     */
    void update(SysRoleEntity role);

    /**
     * 删除
     *
     * @param roleIds ids
     */
    void deleteBatch(Long[] roleIds);


    /**
     * 查询用户创建的角色ID列表
     *
     * @param createUserId id
     * @return list
     */
    List<Long> queryRoleIdList(Long createUserId);
}
