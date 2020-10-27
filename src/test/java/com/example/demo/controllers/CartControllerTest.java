package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

  private CartController cartController;
  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private ItemRepository itemRepository = mock(ItemRepository.class);

  private static User user;
  private static User removeUser;
  private static Cart cart;
  private static Cart removeCart;
  private static Item item;
  private static Item extraItem;
  private static ModifyCartRequest removeFromCartRequest;
  private static ModifyCartRequest modifyCartRequest;
  private static ModifyCartRequest invalidUserCartRequest;
  private static ModifyCartRequest invalidItemCartRequest;

  @BeforeClass
  public static void init(){

    user = new User();
    cart = new Cart();
    item = new Item();

    removeUser = new User();
    removeCart = new Cart();
    extraItem = new Item();

    removeFromCartRequest = new ModifyCartRequest();
    modifyCartRequest = new ModifyCartRequest();
    invalidUserCartRequest = new ModifyCartRequest();
    invalidItemCartRequest = new ModifyCartRequest();

    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    item.setId(1L);
    item.setName("Computer");
    item.setPrice(BigDecimal.valueOf(500.0));
    item.setDescription("cheap option");
    cart.setUser(user);
    user.setCart(cart);

    removeUser.setId(2L);
    removeUser.setUsername("user2");
    removeUser.setPassword("password");
    extraItem.setId(2L);
    extraItem.setName("Coffee");
    extraItem.setPrice(BigDecimal.valueOf(2.0));
    extraItem.setDescription("Dark Roast");
    removeCart.setUser(removeUser);
    removeUser.setCart(removeCart);


    removeFromCartRequest.setItemId(2L);
    removeFromCartRequest.setQuantity(1);
    removeFromCartRequest.setUsername("user2");

    modifyCartRequest.setItemId(1L);
    modifyCartRequest.setQuantity(1);
    modifyCartRequest.setUsername("user");

    invalidUserCartRequest.setItemId(1L);
    invalidUserCartRequest.setQuantity(1);
    invalidUserCartRequest.setUsername("iDoNotExist");

    invalidItemCartRequest.setItemId(3L);
    invalidItemCartRequest.setQuantity(10);
    invalidItemCartRequest.setUsername("user");
  }

  @Before
  public void setUp(){
    cartController = new CartController();
    TestUtils.injectObjects(cartController,"userRepository",userRepository);
    TestUtils.injectObjects(cartController,"cartRepository",cartRepository);
    TestUtils.injectObjects(cartController,"itemRepository",itemRepository);
  }

  @Test
  public void add_To_Cart_Success() throws Exception {
    when(userRepository.findByUsername("user")).thenReturn(user);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    final ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

    Cart cart = response.getBody();
    assertEquals(200,response.getStatusCodeValue());
    assertEquals("user",cart.getUser().getUsername());
    assertEquals("Computer",cart.getItems().get(0).getName());
  }

  @Test
  public void add_To_Cart_Unable_To_Find_Username() throws Exception {
    final ResponseEntity<Cart> response = cartController.addToCart(invalidUserCartRequest);
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void add_To_Cart_Unable_To_Find_Item() throws Exception {
    final ResponseEntity<Cart> response = cartController.addToCart(invalidItemCartRequest);
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void remove_From_Cart_Success() throws Exception {
    when(userRepository.findByUsername("user2")).thenReturn(removeUser);
    when(itemRepository.findById(2L)).thenReturn(Optional.of(extraItem));

    final ResponseEntity<Cart> response = cartController.removeFromCart(removeFromCartRequest);

    Cart cart = response.getBody();
    assertEquals(200,response.getStatusCodeValue());
    assertEquals(0, cart.getItems().size());
    assertEquals("user2",cart.getUser().getUsername());
  }

  @Test
  public void remove_From_Cart_Unable_To_Find_Username() throws Exception {
    final ResponseEntity<Cart> response = cartController.removeFromCart(invalidUserCartRequest);
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void remove_From_Cart_Unable_To_Find_Item() throws Exception {
    final ResponseEntity<Cart> response = cartController.removeFromCart(invalidItemCartRequest);
    assertEquals(404,response.getStatusCodeValue());
  }

}
