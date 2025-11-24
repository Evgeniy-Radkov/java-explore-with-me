package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", expression = "java(parseDate(dto.getTimestamp()))")
    EndpointHit toEntity(EndpointHitDto dto);

    default LocalDateTime parseDate(String value) {
        return LocalDateTime.parse(value, FORMATTER);
    }
}
