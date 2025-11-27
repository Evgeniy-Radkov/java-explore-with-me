package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
    select new ru.practicum.stats.dto.ViewStatsDto(
        e.app,
        e.uri,
        count(*)
    )
    from EndpointHit e
    where e.timestamp between :start and :end
      and (:uris is null or e.uri in :uris)
    group by e.app, e.uri
    order by count(*) desc
    """)
    List<ViewStatsDto> getStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris);

    @Query("""
    select new ru.practicum.stats.dto.ViewStatsDto(
        e.app,
        e.uri,
        count(distinct e.ip)
    )
    from EndpointHit e
    where e.timestamp between :start and :end
      and (:uris is null or e.uri in :uris)
    group by e.app, e.uri
    order by count(distinct e.ip) desc
    """)
    List<ViewStatsDto> getUniqueStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris);
}
