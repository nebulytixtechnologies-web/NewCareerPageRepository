package com.neb.service;

import com.neb.entity.CareerApplication;

/**
 * Service interface for handling career applications.
 */
public interface ICareerApplicationService 
{
	/**
     * Inserts a new career application into the database.
     *
     * @param app the application to insert
     * @return the inserted CareerApplication with any generated values (e.g., ID)
     */
	public CareerApplication insert(CareerApplication app);

}
