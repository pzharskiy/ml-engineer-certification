package com.workfusion.academy.processing;

import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.normalization.NumberNormalizer;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;
import com.workfusion.vds.sdk.nlp.component.processing.normalization.OcrAmountNormalizer;

import java.util.Collection;

/**
 * Normalize numbers to #.00 pattern. Adds 0 in front of dot sign if missed.
 * If final value is not parsed as double - it is removed from document.
 */
public class AmountNormalizationPostProcessor implements Processor<IeDocument> {

    private final String fieldName;
    private final NumberNormalizer numberNormalizer = new OcrAmountNormalizer();

    public AmountNormalizationPostProcessor(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void process(IeDocument document) {
        final Collection<Field> fields = document.findFields(fieldName);
        for (Field field : fields) {
            String fieldValue = field.getValue();
            if (fieldValue.isEmpty()) {
                continue;
            }

            fieldValue = fieldValue.replace("O","0");

            String normalizedValue = numberNormalizer.normalize(fieldValue);
            if (normalizedValue.startsWith(".")) {
                normalizedValue = "0" + normalizedValue;
            }

            try {
                Double.parseDouble(normalizedValue);
            } catch (NumberFormatException e) {
                document.remove(field);
                return;
            }

            field.setValue(normalizedValue);
        }
    }
}
