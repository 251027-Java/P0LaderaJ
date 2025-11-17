package dev.ladera.battleship.dto;

public record MoveDto(Integer turn, Integer row, Integer col, Long playerId, Long gameId) {}
