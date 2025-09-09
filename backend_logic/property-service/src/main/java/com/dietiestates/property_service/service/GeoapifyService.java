package com.dietiestates.property_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeoapifyService {

    private static final String API_KEY = "c4dc78950f8f486cbf36cb126f4efda1"; // 👈 l' API Key
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Controlla la presenza di scuole, parchi o trasporto pubblico
     * vicino alle coordinate passate.
     */
    public Map<String, Boolean> checkNearbyPlaces(double lat, double lon) {
        // Costruisco URL: cerco entro 1000m (1km) dalle coordinate
        String url = "https://api.geoapify.com/v2/places" +
                "?categories=education.school,leisure.park,public_transport" +
                "&filter=circle:" + lon + "," + lat + ",1000" +
                "&limit=5&apiKey=" + API_KEY;

        Map<String, Boolean> result = new HashMap<>();
        result.put("nearSchool", false);
        result.put("nearPark", false);
        result.put("nearTransport", false);

        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);

            if (response.getBody() != null && response.getBody().has("features")) {
                for (JsonNode feature : response.getBody().get("features")) {
                    String category = feature.get("properties").get("categories").toString();
                    if (category.contains("education.school")) result.put("nearSchool", true);
                    if (category.contains("leisure.park")) result.put("nearPark", true);
                    if (category.contains("public_transport")) result.put("nearTransport", true);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Errore chiamata Geoapify: " + e.getMessage());
        }

        return result;
    }
}
