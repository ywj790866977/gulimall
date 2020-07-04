package com.yanlaoge.common.user;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thad
 */
@Data
public class SessionUser implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	private long id;

	/**
	 * 用户登录账号
	 */
	private String userName;

	/**
	 * 用户头像
	 */
	private String avatar;

	/**
	 * 用户授权码
	 */
	private String authCode;

	/**
	 * 是否管理员
	 */
	private boolean isAdmin;

	/**
	 * 登录IP
	 */
	private String loginIp;

	private String snno;


	/**
	 * 用户角色列表
	 */
	private List<SessionRole> roleList = new ArrayList<>();


}
