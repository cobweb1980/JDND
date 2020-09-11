package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@Transactional
@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	Logger logger;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			String password = createUserRequest.getPassword();
			String confirmPassword = createUserRequest.getConfirmPassword();

			if (password.length() < 7 || !password.equals(confirmPassword)) {
				logger.error("Create user failed for {}", user.getUsername());
				return ResponseEntity.badRequest().build();
			}
			password = bCryptPasswordEncoder.encode(password);
			user.setPassword(password);

			userRepository.save(user);
			cart.setUser(user);

			logger.info("Create user succeeded: {}", user.getUsername());
			return ResponseEntity.ok(user);
		} catch (Exception ex) {
			logger.error("Create user failed for {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
	}

}
