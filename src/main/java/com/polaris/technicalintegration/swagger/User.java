package com.polaris.technicalintegration.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@Api("用户信息")
public class User {
	@ApiParam("用户ID")
	private String userId;
	@ApiParam("用户名称")
	private String username;
	@ApiParam("用户密码")
	private String password;

	public User() {

	}

	public User(String userId, String username, String password) {
		this.userId = userId;
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		User user = (User) o;

		return userId != null ? userId.equals(user.userId) : user.userId == null;
	}

	@Override
	public int hashCode() {
		int result = userId != null ? userId.hashCode() : 0;
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		return result;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
