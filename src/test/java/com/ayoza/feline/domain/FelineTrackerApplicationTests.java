package com.ayoza.feline.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ayoza.feline.FelineTrackerApplication;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FelineTrackerApplication.class)
@WebAppConfiguration
public class FelineTrackerApplicationTests {

	@Test
	public void contextLoads() {
	}

}