package com.yanlaoge.common.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class SessionRole implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色id
	 */
	private int roleId;

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 上级角色id
	 */
	private int parentRoleId;


}
