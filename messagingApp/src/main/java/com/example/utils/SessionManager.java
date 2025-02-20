package com.example.utils;

import com.example.model.User;

public class SessionManager {
	private User user;

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
