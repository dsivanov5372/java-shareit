package ru.practicum.shareit.paginator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.shareit.exception.PageSizeException;

public class Paginator {
    public static <T> List<T> paginate(Integer from, Integer size, List<T> list) {
        if (size != null && size <= 0) {
            throw new PageSizeException("Page size can not be less zero!");
        }
        if (from != null && from < 0) {
            throw new PageSizeException("Object index can not be negative!");
        }

        if (from == null) {
            if (size == null) {
                return list;
            }
            return list.stream().limit(size).collect(Collectors.toList());
        }
        if (from > list.size()) {
            return new ArrayList<>();
        }
        return list.stream().skip(from).limit(size).collect(Collectors.toList());
    }
}