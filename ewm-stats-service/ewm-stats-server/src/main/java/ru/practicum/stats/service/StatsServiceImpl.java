package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;
    private final StatsMapper statsMapper;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTime parseDate(String value) {
        String decoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decoded, FORMATTER);
    }

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = statsMapper.toEntity(hitDto);
        endpointHitRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = parseDate(start);
        LocalDateTime endTime = parseDate(end);

        List<String> urisOrNull = (uris == null || uris.isEmpty()) ? null : uris;

        if (unique) {
            return endpointHitRepository.getUniqueStats(startTime, endTime, urisOrNull);
        } else {
            return endpointHitRepository.getStats(startTime, endTime, urisOrNull);
        }
    }
}
