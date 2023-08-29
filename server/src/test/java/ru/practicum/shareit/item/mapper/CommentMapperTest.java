package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {

    private Comment comment;
    private User user;
    private Item item;
    private UserDto userDto;
    private ItemDto itemDto;
    private final LocalDateTime created = LocalDateTime.now().plusDays(1);
    private Collection<Comment> comments = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "username", "user@mail.ru");
        item = new Item(1L, "item", "description to Item", true, user, null);
        comment = new Comment(1L, "text", item, user, created);
        userDto = new UserDto(1L, user.getName(), user.getEmail());
        itemDto = new ItemDto(1L, item.getName(), item.getDescription(), item.getAvailable(), userDto, null);
    }

    @Test
    void commentToResponseCommentDtoTest() {
        ResponseCommentDto responseCommentDto = CommentMapper.toResponseCommentDto(comment);

        assertThat(responseCommentDto.getId()).isEqualTo(1L);
        assertThat(responseCommentDto.getText()).isEqualTo(comment.getText());
        assertThat(responseCommentDto.getCreated()).isEqualTo(created);
        assertThat(responseCommentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
    }

    @Test
    void commentDtoToCommentTest() {
        CommentDto commentDto = new CommentDto("text", created, userDto, itemDto);
        comment = CommentMapper.toComment(commentDto, item, user, created);

        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getAuthor().getName()).isEqualTo(commentDto.getAuthor().getName());
        assertThat(comment.getItem().getId()).isEqualTo(commentDto.getItem().getId());
    }

    @Test
    void commentsToResponseCommentDtoListTest() {
        comments.add(comment);
        List<ResponseCommentDto> list = CommentMapper.toResponseCommentDtoList(comments);

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getText()).isEqualTo(comment.getText());
    }

    @Test
    void nullCommentsToResponseCommentDtoListTest() {
        comments = null;
        List<ResponseCommentDto> list = CommentMapper.toResponseCommentDtoList(comments);

        assertNull(list);
    }
}
