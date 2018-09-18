package com.polaris.technicalintegration.controller;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.technicalintegration.model.DemoModel;

@Controller
public class HibernateValidate {
	AtomicInteger i = new AtomicInteger(0);
	@RequestMapping(value="/demo",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String demo(@RequestBody @Valid DemoModel demo, BindingResult result) {
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				System.out.println(error.getDefaultMessage());
				return error.getDefaultMessage();
			}
		}
		return "ok";
	}
	
	@RequestMapping(value="/test",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String test(String msg) {
		System.out.println(i.incrementAndGet());
		return msg;
	}
	
}
