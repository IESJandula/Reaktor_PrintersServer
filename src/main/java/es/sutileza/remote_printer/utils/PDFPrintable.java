package es.sutileza.remote_printer.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFPrintable implements Printable {
    private PDDocument document;

    public PDFPrintable(PDDocument document) {
        this.document = document;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex < 0 || pageIndex >= document.getNumberOfPages()) {
            return Printable.NO_SUCH_PAGE;
        }
        try {
            // Obtener el renderizador para la página actual
            PDFRenderer renderer = new PDFRenderer(document);
            // Renderizar la página en un contexto gráfico
            Graphics2D g2d = (Graphics2D) graphics;
            renderer.renderPageToGraphics(pageIndex, g2d);
            // La página se renderiza correctamente
            return Printable.PAGE_EXISTS;
        } catch (Exception e) {
            throw new PrinterException(e.getMessage());
        }
    }
}