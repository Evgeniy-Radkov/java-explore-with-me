package ru.yandex.practicum.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private Long requester;

    private LocalDateTime created;

    private String status;
}
