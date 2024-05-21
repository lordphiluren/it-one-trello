package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.board.AddBoardRequest;
import ru.sushchenko.trelloclone.dto.board.BoardRequest;
import ru.sushchenko.trelloclone.dto.board.BoardResponse;
import ru.sushchenko.trelloclone.dto.task.TaskRequest;
import ru.sushchenko.trelloclone.dto.task.TaskResponse;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.Board;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BoardMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public BoardResponse toDto(Board board) {
        UserResponse creatorDto = userMapper.toDto(board.getCreator());
        Set<UserResponse> membersDto = board.getMembers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());

        BoardResponse boardResponse = modelMapper.map(board, BoardResponse.class);
        boardResponse.setCreator(creatorDto);
        boardResponse.setMembers(membersDto);

        return boardResponse;
    }

    public Board toEntity(BoardResponse boardResponse) {
        User creator = userMapper.toEntity(boardResponse.getCreator());
        Set<User> members = boardResponse.getMembers().stream()
                .map(userMapper::toEntity)
                .collect(Collectors.toSet());

        Board board = modelMapper.map(boardResponse, Board.class);
        board.setCreator(creator);
        board.setMembers(members);

        return board;
    }

    public Board toEntity(BoardRequest boardRequest) {
        return modelMapper.map(boardRequest, Board.class);
    }
    public Board toEntity(AddBoardRequest boardRequest) {
        return modelMapper.map(boardRequest, Board.class);
    }

    public void mergeDtoIntoEntity(BoardRequest boardDto, Board board) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(boardDto, board);
    }
}
