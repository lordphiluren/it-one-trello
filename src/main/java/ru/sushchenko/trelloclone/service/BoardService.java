package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.dto.board.AddBoardRequest;
import ru.sushchenko.trelloclone.dto.board.BoardRequest;
import ru.sushchenko.trelloclone.dto.board.BoardResponse;
import ru.sushchenko.trelloclone.dto.user.UserResponse;
import ru.sushchenko.trelloclone.entity.Board;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface BoardService {
    List<BoardResponse> getAllBoards();
    BoardResponse getBoardById(UUID id);
    BoardResponse addBoard(AddBoardRequest boardDto, User user);
    BoardResponse updateBoardById(UUID id, BoardRequest boardDto, User user);
    BoardResponse addMemberToBoardById(UUID id, UUID memberId, User user);
    void removeMemberFromBoardById(UUID id, UUID memberId, User user);
    void deleteBoardById(UUID id, User user);
    void validatePermissions(Board board, User currentUser);
    void validateOwnership(Board board, User currentUser);
    List<BoardResponse> getMemberedBoardsByUserId(UUID id);
    List<BoardResponse> getCreatedBoardsByUserId(UUID id);
}
