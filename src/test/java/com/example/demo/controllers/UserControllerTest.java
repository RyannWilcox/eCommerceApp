package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

  private UserController userController;
  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

  @Before
  public void setUp(){
    userController = new UserController();

    //Inject into the user controller object, this simulates 'autowired'
    TestUtils.injectObjects(userController,"userRepository",userRepository);
    TestUtils.injectObjects(userController,"cartRepository",cartRepository);
    TestUtils.injectObjects(userController,"bCryptPasswordEncoder",encoder);
  }

  @Test
  public void create_User_Happy_Path() throws Exception {
    when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

    CreateUserRequest request = new CreateUserRequest();

    request.setUsername("test");
    request.setPassword("testPassword");
    request.setConfirmPassword("testPassword");

    final ResponseEntity<User> response = userController.createUser(request);

    assertNotNull(response);
    assertEquals(200,response.getStatusCodeValue());

    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0,user.getId());
    assertEquals("test",user.getUsername());
    assertEquals("thisIsHashed",user.getPassword());
  }

  @Test
  public void create_User_Invalid_Password() throws Exception {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("test");
    request.setPassword("password");
    request.setConfirmPassword("notThePassword");

    final ResponseEntity<User> response = userController.createUser(request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void find_User_By_Id() throws Exception {
    User user = new User();
    long id = 1L;
    user.setId(id);
    user.setUsername("user");
    user.setPassword("password");
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    final ResponseEntity<User> response = userController.findById(id);

    User theUser = response.getBody();
    assertNotNull(theUser);
    assertEquals(200,response.getStatusCodeValue());
    assertEquals("user",theUser.getUsername());
    assertEquals("password",theUser.getPassword());
  }

  @Test
  public void find_User_By_Id_Not_Found() throws Exception {
    final ResponseEntity<User> response = userController.findById(1L);
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void find_User_By_Name() throws Exception {
    User user = new User();
    long id = 1L;
    String userName = "user";
    user.setId(id);
    user.setUsername(userName);
    user.setPassword("password");
    when(userRepository.findByUsername(userName)).thenReturn(user);

    final ResponseEntity<User> response = userController.findByUserName(userName);

    User theUser = response.getBody();
    assertNotNull(theUser);
    assertEquals(200,response.getStatusCodeValue());
    assertEquals("user",theUser.getUsername());
    assertEquals("password",theUser.getPassword());


  }

  @Test
  public void find_User_By_Name_Not_Found() throws Exception {
    final ResponseEntity<User> response = userController.findByUserName("IDoNotExist");
    assertEquals(404,response.getStatusCodeValue());
  }


}
