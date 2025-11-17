package dev.ladera.battleship.dto;

public record ShipDto(Integer rowStart, Integer rowEnd, Integer colStart, Integer colEnd, Long playerId, Long gameId) {}
