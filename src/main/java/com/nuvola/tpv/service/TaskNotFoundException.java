package com.nuvola.tpv.service;

public class TaskNotFoundException extends Exception {
	private String userName;
	public TaskNotFoundException(String userName) {
		super("Task not found for user : " + userName + ".");
		this.userName = userName;
	}

}
