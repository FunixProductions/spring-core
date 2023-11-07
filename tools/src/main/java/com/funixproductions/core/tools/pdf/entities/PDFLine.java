package com.funixproductions.core.tools.pdf.entities;

import com.funixproductions.core.tools.pdf.generators.PDFGenerator;
import lombok.*;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * PDF line entity.
 * <p>
 *     This class represents a line of text in a PDF document.
 *     It contains the text, the font size and the font.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PDFLine {

    /**
     * The text of the line.
     */
    @NonNull
    private final String text;

    /**
     * The font size of the line.
     */
    private float fontSize = PDFGenerator.DEFAULT_FONT_SIZE;

    /**
     * The font of the line.
     */
    @NonNull
    private PDFont font = PDFGenerator.DEFAULT_FONT;

}
