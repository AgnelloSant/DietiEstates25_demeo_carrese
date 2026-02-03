package com.backend.service;

import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.backend.dto.UserBidsReceived;
import com.backend.dto.UserReservation;

import com.backend.model.Property;
import com.backend.repository.BidRepository;
import com.backend.repository.ReservationRepository;

@Service
public class ExcelService {

    private final ReservationRepository reservationRepository;
    private final BidRepository bidRepository;

    public ExcelService(ReservationRepository reservationRepository, BidRepository bidRepository) {
        this.reservationRepository = reservationRepository;
        this.bidRepository = bidRepository;
    }

    public ByteArrayInputStream loadReservationsToExcel(List<UserReservation> reservations) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 1. Crea il foglio
            Sheet sheet = workbook.createSheet("Prenotazioni");

            // 2. Crea l'intestazione (Header)
            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID Proprietà", "Utente Prenotato", "Telefono", "Data", "Ora", "Nome Proprietà",
                    "Indirizzo", "Città", "Tipologia", "Prezzo" };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (UserReservation res : reservations) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(res.getPropertyId());
                row.createCell(1).setCellValue(res.getUserName());
                row.createCell(2).setCellValue(res.getPhoneNumber());
                row.createCell(3).setCellValue(res.getReservationDate());
                row.createCell(4).setCellValue(res.getReservationTime());
                row.createCell(5).setCellValue(res.getPropertyName());
                row.createCell(6).setCellValue(res.getPropertyAddress());
                row.createCell(7).setCellValue(res.getPropertyCity());
                row.createCell(8).setCellValue(res.getPropertyType());
                row.createCell(9).setCellValue(res.getPropertyPrice());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Errore durante la creazione del file Excel: " + e.getMessage());
        }
    }

    public List<UserReservation> getReservationsSummaryByUser(Long userId) {
        return reservationRepository.getReservationsSummaryByUser(userId);
    }

    public ByteArrayInputStream loadReceivedBidsToExcel(List<UserBidsReceived> bids) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Offerte ricevute");

            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID Offerta", "Utente Offerta", "Telefono", "Data", "Ora", "Nome Proprietà",
                    "Indirizzo", "Città", "Tipologia", "Prezzo" };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (UserBidsReceived bid : bids) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(bid.getBidId());
                row.createCell(1).setCellValue(bid.getBidderName());
                row.createCell(2).setCellValue(bid.getBidderPhone());
                if (bid.getBidDate() != null) {
                    row.createCell(3).setCellValue(bid.getBidDate().toLocalDate().toString());
                    row.createCell(4).setCellValue(bid.getBidDate().toLocalTime().toString().substring(0, 5)); // HH:mm
                } else {
                    row.createCell(3).setCellValue("");
                    row.createCell(4).setCellValue("");
                }
                row.createCell(5).setCellValue(bid.getPropertyName());
                row.createCell(6).setCellValue(bid.getPropertyAddress());
                row.createCell(7).setCellValue(bid.getPropertyCity());
                row.createCell(8).setCellValue(bid.getPropertyType());
                row.createCell(9).setCellValue(bid.getPropertyPrice());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Errore durante la creazione del file Excel: " + e.getMessage());
        }

    }

    public List<UserBidsReceived> getReceivedBidsToExcel(Long userId) {
        return bidRepository.getReceivedBidsToExcel(userId);
    }

}