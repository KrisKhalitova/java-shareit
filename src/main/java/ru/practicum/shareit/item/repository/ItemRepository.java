package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwner(User user, Sort sort);

    @Query(value = "select i from Item i where lower(i.name) like %:text% or lower(i.description) like %:text% " +
            "and i.available=true")
    List<Item> search(@Param("text") @NotNull String text);
}
