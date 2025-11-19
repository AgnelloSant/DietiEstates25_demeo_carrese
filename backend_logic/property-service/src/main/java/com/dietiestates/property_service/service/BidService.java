package com.dietiestates.property_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.dietiestates.property_service.dto.BidSummaryDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.dto.BidCreateDTO;
import com.dietiestates.property_service.model.Bid;
import com.dietiestates.property_service.repository.BidRepository;
import com.dietiestates.property_service.repository.PropertyRepository;
import com.dietiestates.property_service.dto.BidTrendDTO;


@Service
public class BidService {

    private  BidRepository bidRepository;

    @Autowired
    private  PropertyRepository PropertyRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public Boolean placeBid(BidCreateDTO bidCreateDTO, Long userId) {

        Long propertyId = bidCreateDTO.getPropertyId();
        Double amount = bidCreateDTO.getAmount();

        Bid newBid = new Bid(propertyId, userId, amount);

       return bidRepository.save(newBid) != null;
    }

    public Boolean deleteBid(Long id) {
        try {
            bidRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Bid> getBidsByPropertyId(Long propertyId) {
        return bidRepository.findByPropertyId(propertyId);
    }

    public List<Bid> getBidsByUserId(Long userId) {
        return bidRepository.findByUserId(userId);
    }

    public Long countBidsByUserId(Long userId) {
        return bidRepository.countByUserId(userId);
    }

    public Bid getDateLastBid(Long idProp){
        return bidRepository.findTopByPropertyIdOrderByPublishedAtDesc(idProp); 
    }

    public Long countBidsByPropertyId(Long idProp){
        return bidRepository.countByPropertyId(idProp); 
    }

    public List<BidSummaryDTO> getBidsSummaryByUser(Long userId) {
        //Prende le offerte alle proprietà dell'utente loggato. 

        try {
            List<Property> properties = PropertyRepository.findByIdUser(userId);
            List<BidSummaryDTO> summaries = new ArrayList<>();

            for (Property property : properties) {
                Long propertyId = property.getId();
                BidSummaryDTO dto = new BidSummaryDTO();

                long count = bidRepository.countByPropertyId(propertyId);
                Double avg = bidRepository.findAvgByPropertyId(propertyId);
                LocalDateTime lastDate = bidRepository.findMaxPublishedAtByPropertyId(propertyId);

                dto.setPropertyId(propertyId);
                dto.setAvgPrice(avg);
                dto.setCount(count);
                dto.setPropertyTitle(property.getTitle());
                dto.setLastDate(lastDate);   

                System.out.println("Id proprieta: "+ propertyId + "\nNumero offerte: " + count + "\nOfferta media: " + avg +
                "\nLast Date: " + lastDate);
                summaries.add(dto);
            }
            
            return summaries;
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
    }

    public List<BidTrendDTO> findDailyOfferCount(Long userId) {
        List<Object[]> rawData = bidRepository.findDailyOfferCountByOwner(userId);
        System.out.println("Raw Data Daily Offer Count: " + rawData);
        return rawData.stream()
        .map(row -> new BidTrendDTO((String) row[0], ((Number) row[1]).longValue()))
        .collect(Collectors.toList());
    }



    
}
