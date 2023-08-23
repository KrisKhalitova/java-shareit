package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(User user, Pageable page);

    @Query(value = "select i from Item i where lower(i.name) like %:text% or lower(i.description) like %:text% " +
            "and i.available=true")
    List<Item> search(@Param("text") @NotNull String text, Pageable pageable);

    List<Item> findAllByItemRequest(ItemRequest request);

    List<Item> findAllByItemRequestIn(List<ItemRequest> requests);
}
