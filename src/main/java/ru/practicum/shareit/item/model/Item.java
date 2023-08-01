package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@Entity(name = "items")
@Table(name = "items")
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(length = 128)
    private String name;
    @Column(length = 256)
    private String description;
    @Column
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
