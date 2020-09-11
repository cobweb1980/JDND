package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class SareetaApplicationTests {
	@Autowired
	UserController userController;
	@Autowired
	ItemController itemController;
	@Autowired
	OrderController orderController;
	@Autowired
	CartController cartController;

	@Test
	public void testCreateUser() {
		String username = "cobweb";
		String password = "password123";
		User newUser = saveAndGetUser(username, password);

		ResponseEntity<User> retrievedUserEntity = userController.findByUserName(username);
		User retrievedUser = retrievedUserEntity.getBody();
		Assertions.assertEquals(username, retrievedUser.getUsername());
		Assertions.assertEquals(username, newUser.getUsername());
		Assertions.assertEquals(newUser.getId(), retrievedUser.getId());
	}

	@Test
	public void testCreateUserPasswordLengthProblem() {
		ResponseEntity<User> reponseEntity = saveAndGetUserEntity("cobweb", "pass", "pass");
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, reponseEntity.getStatusCode());
	}

	@Test
	public void testCreateUserPasswordTypoProblem() {
		ResponseEntity<User> reponseEntity = saveAndGetUserEntity("cobweb", "password222", "password111");
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, reponseEntity.getStatusCode());
	}

	private ResponseEntity<User> saveAndGetUserEntity(String username, String password, String confirmPassword) {
		CreateUserRequest createUserRequest = createUserRequest(username, password, confirmPassword);
		createUserRequest.setUsername(username);
		createUserRequest.setPassword(password);
		createUserRequest.setConfirmPassword(confirmPassword);
		return userController.createUser(createUserRequest);
	}

	private User saveAndGetUser(String username, String password) {
		return saveAndGetUserEntity(username, password, password).getBody();
	}

	private CreateUserRequest createUserRequest(String username, String password, String confirmPassword) {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(username);
		createUserRequest.setPassword(password);
		createUserRequest.setConfirmPassword(confirmPassword);
		return createUserRequest;
	}

	@Test
	public void testCreateOrder() {
		User user = saveAndGetUser("cobweb", "password123");
		// data.sql contains 2 predefined items
		List<Item> items = getAllItems();
		Assertions.assertEquals(2, items.size());
		Item item = items.get(1);

		ModifyCartRequest cartRequest = createCartModRequest(user, item, 3);
		Assertions.assertEquals(HttpStatus.OK, cartController.addTocart(cartRequest).getStatusCode());

		UserOrder submittedOrder = orderController.submit(user.getUsername()).getBody();
		List<UserOrder> ordersList = orderController.getOrdersForUser(user.getUsername()).getBody();
		Assertions.assertEquals(3, ordersList.get(0).getItems().size());
	}

	private List<Item> getAllItems() {
		return itemController.getItems().getBody();
	}

	private ModifyCartRequest createCartModRequest(User user, Item item, int quantity) {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(item.getId());
		modifyCartRequest.setUsername(user.getUsername());
		modifyCartRequest.setQuantity(quantity);
		return modifyCartRequest;
	}


}
