package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static ResponseCommentDto toResponseCommentDto(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .author(author)
                .item(item)
                .build();
    }

    public static List<ResponseCommentDto> toResponseCommentDtoList(Collection<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream().map(CommentMapper::toResponseCommentDto).collect(Collectors.toList());
    }
}
