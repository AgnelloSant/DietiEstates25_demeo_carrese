package com.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeoapifyService {

    @Value("${geoapify.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeoapifyService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public double[] getCoordinates(String address) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Geoapify API key is missing");
            return null;
        }

        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://api.geoapify.com/v1/geocode/search")
                    .queryParam("text", address)
                    .queryParam("apiKey", apiKey)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode features = root.path("features");

            if (features.isArray() && features.size() > 0) {
                JsonNode geometry = features.get(0).path("geometry");
                JsonNode coordinates = geometry.path("coordinates");
                if (coordinates.isArray() && coordinates.size() >= 2) {
                    double lon = coordinates.get(0).asDouble();
                    double lat = coordinates.get(1).asDouble();
                    return new double[] { lat, lon };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if not found or error
    }

    public boolean checkNearby(Double lat, Double lon, String category) {
        if (apiKey == null || apiKey.isEmpty() || lat == null || lon == null) {
            return false;
        }

        try {
            // Circle search: radius 1000m (1km)
            String url = UriComponentsBuilder.fromHttpUrl("https://api.geoapify.com/v2/places")
                    .queryParam("categories", category)
                    .queryParam("filter", "circle:" + lon + "," + lat + ",1000")
                    .queryParam("limit", 1)
                    .queryParam("apiKey", apiKey)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode features = root.path("features");

            return features.isArray() && features.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
