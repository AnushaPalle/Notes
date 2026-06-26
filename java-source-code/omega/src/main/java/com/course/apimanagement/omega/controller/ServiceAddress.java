package com.course.apimanagement.omega.controller;

interface ServiceAddress {

	String GATEWAY = "http://kong:8000";
	String ALPHA = GATEWAY + "/alpha";
	String BETA = GATEWAY + "/beta";
	String GAMMA = GATEWAY + "/gamma";
	String OMEGA = GATEWAY + "/omega";

//	String GATEWAY = "http://localhost";
//	String ALPHA = GATEWAY + ":9001";
//	String BETA = GATEWAY + ":9002";
//	String GAMMA = GATEWAY + ":9003";
//	String OMEGA = GATEWAY + ":9004";

//	String ALPHA = "http://alpha:9001";
//	String BETA = "http://beta:9002";
//	String GAMMA = "http://gamma:9003";
//	String OMEGA = "http://omega:9004";

}
