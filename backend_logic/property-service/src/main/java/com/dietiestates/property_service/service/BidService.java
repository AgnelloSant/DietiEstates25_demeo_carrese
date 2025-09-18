package com.dietiestates.property_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dietiestates.property_service.dto.BidSummaryDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.dto.BidCreateDTO;
import com.dietiestates.property_service.model.Bid;
import com.dietiestates.property_service.repository.BidRepository;
import com.dietiestates.property_service.repository.PropertyRepository;



@Service
public class BidService {
    private BidRepository bidRepository;
    private PropertyRepository PropertyRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public Boolean placeBid(BidCreateDTO bidCreateDTO) {
        Long propertyId = bidCreateDTO.getPropertyId();
        Long userId = bidCreateDTO.getUserId();
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


    public List<BidSummaryDTO> getBidsSummaryByUser(Long userId) {
        List<Property> properties;
        try {
                properties = PropertyRepository.findByIdUser(userId);
                List<BidSummaryDTO> summaries = new ArrayList<>();
                for (Property property : properties) {
                Long propertyId = property.getId();

                long count = bidRepository.countByPropertyId(propertyId);
                Double avg = bidRepository.findAvgByPropertyId(propertyId);
                LocalDateTime lastDate = bidRepository.findLastDateByProperty(propertyId);

                BidSummaryDTO dto = new BidSummaryDTO(propertyId, property.getTitle(), count, avg, lastDate);
                summaries.add(dto);
                return summaries;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }

        return null;


       
        
    }

    
}
