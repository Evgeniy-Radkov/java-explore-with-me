package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private String baseUrl;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void hit(EndpointHitDto hitDto) {
        restTemplate.postForLocation(baseUrl + "/hit", hitDto);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        String startStr = start.format(FORMATTER);
        String endStr = end.format(FORMATTER);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/stats")
                .queryParam("start", startStr)
                .queryParam("end", endStr)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        String url = builder.build().toUriString();

        ViewStatsDto[] body = restTemplate.getForObject(url, ViewStatsDto[].class);

        if (body == null || body.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(body);
    }
}
