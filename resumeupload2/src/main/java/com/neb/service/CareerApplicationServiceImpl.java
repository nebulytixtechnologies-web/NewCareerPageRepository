package com.neb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neb.entity.CareerApplication;
import com.neb.repo.CareerRepository;

@Service
public class CareerApplicationServiceImpl implements ICareerApplicationService
{
	@Autowired
	private CareerRepository repo;
	
	@Override
	public CareerApplication insert(CareerApplication app) 
	{
		CareerApplication save = repo.save(app);
		
		return save;
	}

}
