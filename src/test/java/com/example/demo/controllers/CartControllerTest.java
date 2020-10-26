package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class CartControllerTest {

  private CartController cartController;
  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private ItemRepository itemRepository = mock(ItemRepository.class);

  @Before
  public void setUp(){
    cartController = new CartController();
    TestUtils.injectObjects(cartController,"userRepository",userRepository);
    TestUtils.injectObjects(cartController,"cartRepository",cartRepository);
    TestUtils.injectObjects(cartController,"itemRepository",itemRepository);
  }

}
