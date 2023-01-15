package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InvalidItemRequestException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.repository.UserDao;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao = new ItemDao();
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public void checkUserId(Long userId) throws UserNotFoundException {
        if (!userDao.findUserById(userId)) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public Item addItem(ItemDto item, Long userId) 
        throws UserNotFoundException, InvalidItemRequestException {
        checkUserId(userId);
        if (item.getName() == null || item.getName().isBlank() ||
            item.getDescription() == null || item.getDescription().isBlank() ||
            item.getAvailable() == null) {
            throw new InvalidItemRequestException("Invalid item fields");
        }
        return itemDao.addItem(item, userId);
    }

    @Override
    public List<Item> findAllByUser(Long userId) throws UserNotFoundException {
        checkUserId(userId);
        return itemDao.findAllByUser(userId);
    }

    @Override
    public Item findItemById(Long itemId) throws UserNotFoundException {
        return itemDao.findItemById(itemId);
    }

    @Override
    public List<Item> findAllByText(String text) {
        return itemDao.findAllByText(text);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto item) throws UserNotFoundException {
        checkUserId(userId);
        item.setId(itemId);
        return itemDao.updateItem(item, userId);
    }
    
}