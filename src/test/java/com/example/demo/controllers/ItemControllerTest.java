package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

  private ItemController itemController;
  private ItemRepository itemRepository = mock(ItemRepository.class);

  private List<Item> items;
  private List<Item> itemsByName;


  public static Item itemInit(long id, String name, BigDecimal price, String desc){
    Item item = new Item();
    item.setId(id);
    item.setName(name);
    item.setPrice(price);
    item.setDescription(desc);
    return item;
  }

  @Before
  public void setUp(){
    itemController = new ItemController();
    TestUtils.injectObjects(itemController,"itemRepository",itemRepository);

    Item item1 = itemInit(1L,"Computer",BigDecimal.valueOf(500.00),"a Computer");
    Item item2 = itemInit(2L,"Computer",BigDecimal.valueOf(10000.00),"a Super Computer");
    Item item3 = itemInit(3L,"Coffee",BigDecimal.valueOf(2.00),"Medium Roast");

    items = Arrays.asList(new Item[]{item1, item2, item3});

    itemsByName = Arrays.asList(new Item[]{item1,item2});
  }

  @Test
  public void get_All_Items() throws Exception {
    when(itemRepository.findAll()).thenReturn(items);

    final ResponseEntity<List<Item>> response = itemController.getItems();

    List<Item> theItems = response.getBody();
    assertNotNull(items);
    assertEquals(200,response.getStatusCodeValue());
    assertEquals(3,response.getBody().size());
    assertEquals(items,theItems);
  }

  @Test
  public void get_All_Items_Empty_List() throws Exception {
    final ResponseEntity<List<Item>> response = itemController.getItems();
    
    assertEquals(200,response.getStatusCodeValue());
    assertEquals(0,response.getBody().size());

  }

  @Test
  public void get_Item_By_Id() throws Exception {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(items.get(0)));

    final ResponseEntity<Item> response = itemController.getItemById(1L);
    Item item = response.getBody();

    assertNotNull(item);
    assertEquals(200,response.getStatusCodeValue());
    assertEquals("Computer",item.getName());
    assertEquals("a Computer",item.getDescription());
    assertEquals(BigDecimal.valueOf(500.00),item.getPrice());
  }

  @Test
  public void get_Item_By_Id_Not_Found() throws Exception {
    final ResponseEntity<Item> response = itemController.getItemById(4L);
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void get_Items_By_Name() throws Exception {
    when(itemRepository.findByName("Computer")).thenReturn(itemsByName);

    final ResponseEntity<List<Item>> response = itemController.getItemsByName("Computer");

    List<Item> theItems = response.getBody();
    assertNotNull(items);
    assertEquals(200,response.getStatusCodeValue());
    assertEquals(2,response.getBody().size());
    assertEquals(itemsByName,theItems);
  }

  @Test
  public void get_Items_By_Name_Not_Found() throws Exception {
    final ResponseEntity<List<Item>> response = itemController.getItemsByName("iDoNotExist");
    assertEquals(404,response.getStatusCodeValue());
  }
}
