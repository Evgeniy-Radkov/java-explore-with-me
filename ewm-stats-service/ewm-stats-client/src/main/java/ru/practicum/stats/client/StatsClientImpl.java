package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private final String baseUrl;

    @Override
    public void hit(EndpointHitDto hitDto) {
        String url = baseUrl + "/hit";
        restTemplate.postForLocation(url, hitDto);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String startStr = URLEncoder.encode(start.format(formatter), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(end.format(formatter), StandardCharsets.UTF_8);

        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append("/stats")
                .append("?start=").append(startStr)
                .append("&end=").append(endStr)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlBuilder.append("&uris=")
                        .append(URLEncoder.encode(uri, StandardCharsets.UTF_8));
            }
        }

        String url = urlBuilder.toString();

        ViewStatsDto[] response = restTemplate.getForObject(url, ViewStatsDto[].class);
        return response != null ? List.of(response) : List.of();
    }
}
