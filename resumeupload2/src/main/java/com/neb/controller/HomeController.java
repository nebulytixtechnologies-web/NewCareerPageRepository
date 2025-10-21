package com.neb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController handles HTTP GET requests to the root URL ("/").
 * This controller is responsible for rendering the home page of the application.
 * When a user visits the base URL, it returns the "index" view
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
    	// This returns the logical view name "index",
        return "index";
    }
}
