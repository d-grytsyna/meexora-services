package com.meexora.emailservice.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

@Component
public class TicketPdfGenerator {

    public byte[] generateTicket(String userName, String eventTitle, String location, OffsetDateTime dateTime, BigDecimal price, String qrContent) throws IOException, WriterException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Event Ticket", titleFont));
        document.add(new Paragraph("Name: " + userName, textFont));
        document.add(new Paragraph("Event: " + eventTitle, textFont));
        document.add(new Paragraph("Location: " + location, textFont));
        document.add(new Paragraph("Date: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), textFont));
        document.add(new Paragraph("Price: " + price + " UAH", textFont));
        document.add(new Paragraph(" "));

        // Generate QR code
        Image qrImage = generateQrCodeImage(qrContent);
        document.add(qrImage);

        document.close();
        return outputStream.toByteArray();
    }

    private Image generateQrCodeImage(String text) throws WriterException, IOException, BadElementException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        return Image.getInstance(baos.toByteArray());
    }
}
