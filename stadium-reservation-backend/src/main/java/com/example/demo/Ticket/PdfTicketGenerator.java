package com.example.demo.Ticket;

import com.example.demo.FootballMatch.FootballMatch;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfTicketGenerator {

    public static String generatePdfTicket(FootballMatch footballMatch, String ticketId, String bookedSeats) throws IOException, DocumentException {
        String fileName = "ticket_" + ticketId + ".pdf";
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        document.open();
        document.add(new Paragraph("Ticket ID: " + ticketId));
        document.add(new Paragraph("Match: " + footballMatch.getEquipe1() + " vs " + footballMatch.getEquipe2()));
        document.add(new Paragraph("Date: " + footballMatch.getDate()));
        document.add(new Paragraph("Time: " + footballMatch.getTime()));
        document.add(new Paragraph("Booked Seats: " + bookedSeats));
        document.close();

        return fileName;
    }
}