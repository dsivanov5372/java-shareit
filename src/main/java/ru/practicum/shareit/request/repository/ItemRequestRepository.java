package ru.practicum.shareit.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long userId);

    List<ItemRequest> findAllByRequestorIdIsNot(Long userId);
}