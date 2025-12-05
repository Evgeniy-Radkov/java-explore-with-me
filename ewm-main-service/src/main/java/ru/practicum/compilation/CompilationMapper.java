package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation);

    List<EventShortDto> eventsToDtos(Set<Event> events);
}
