package com.workfusion.academy.processing;

import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

import java.util.Collection;

/**
 * Removes OCR errors when $ sign is misrecognized as 's' or 'S'.
 */
public class RemoveSInTheBeginningFromPrice implements Processor<IeDocument> {

    private final String fieldName;

    public RemoveSInTheBeginningFromPrice(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void process(IeDocument document) {
        Collection<Field> fields = document.findFields(fieldName);
        for (Field field : fields) {
            String value = field.getValue();
            if (value.startsWith("s") || value.startsWith("S")) {
                field.setValue(value.substring(1));
            }
        }
    }
}
