package ru.yandex.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.event.EventMapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation);

    List<ru.yandex.practicum.event.dto.EventShortDto> eventsToDtos(Set<Event> events);
}
