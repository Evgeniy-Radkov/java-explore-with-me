package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;
    private final StatsMapper statsMapper;

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = statsMapper.toEntity(hitDto);
        endpointHitRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        List<String> urisOrNull = (uris == null || uris.isEmpty()) ? null : uris;

        if (unique) {
            return endpointHitRepository.getUniqueStats(start, end, urisOrNull);
        } else {
            return endpointHitRepository.getStats(start, end, urisOrNull);
        }
    }
}
