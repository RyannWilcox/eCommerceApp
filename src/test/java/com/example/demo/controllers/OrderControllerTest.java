package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

  private OrderController orderController;
  private OrderRepository orderRepository = mock(OrderRepository.class);
  private UserRepository userRepository = mock(UserRepository.class);

  private static User user;
  private static Cart cart;
  private static Item item;
  private static UserOrder order;

  @BeforeClass
  public static void init(){
    List<Item> items = new ArrayList<>();
    user = new User();
    cart = new Cart();
    item = new Item();
    order = new UserOrder();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    item.setId(1L);
    item.setName("Computer");
    item.setPrice(BigDecimal.valueOf(500.0));
    item.setDescription("cheap option");
    items.add(item);
    cart.setId(1L);
    cart.setItems(items);
    cart.setTotal(BigDecimal.valueOf(500.0));

    order.setId(1L);
    order.setItems(items);
    order.setUser(user);
    order.setTotal(BigDecimal.valueOf(500.0));

    cart.setUser(user);
    user.setCart(cart);
  }


  @Before
  public void setUp(){
    orderController = new OrderController();
    TestUtils.injectObjects(orderController,"orderRepository",orderRepository);
    TestUtils.injectObjects(orderController,"userRepository",userRepository);

  }

  @Test
  public void submit_Order_By_Username() throws Exception {
    when(userRepository.findByUsername("user")).thenReturn(user);
    final ResponseEntity<UserOrder> response = orderController.submit("user");

    UserOrder userOrder = response.getBody();
    assertEquals(200,response.getStatusCodeValue());
    assertEquals("user",userOrder.getUser().getUsername());
    assertEquals(BigDecimal.valueOf(500.0),userOrder.getTotal());
    assertEquals("Computer",userOrder.getItems().get(0).getName());
  }

  @Test
  public void submit_Order_ByUsername_Not_Found() throws Exception {
    final ResponseEntity<UserOrder> response = orderController.submit("iDoNotExist");
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void get_Orders_By_Username() throws Exception {
    List<UserOrder> orders = new ArrayList<>();
    orders.add(order);
    when(userRepository.findByUsername("user")).thenReturn(user);
    when(orderRepository.findByUser(user)).thenReturn(orders);

    final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user");

    List<UserOrder> theOrders = response.getBody();
    assertEquals(1,theOrders.size());
    assertEquals(200,response.getStatusCodeValue());
    assertEquals("user",theOrders.get(0).getUser().getUsername());
    assertEquals(BigDecimal.valueOf(500.0),theOrders.get(0).getTotal());
    assertEquals("Computer",theOrders.get(0).getItems().get(0).getName());
  }

  @Test
  public void get_Orders_By_Username_Not_Found() throws Exception {
    final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("iDoNotExist");
    assertEquals(404, response.getStatusCodeValue());
   }


}
