package com.workfusion.academy.processing;

import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

import java.util.Collection;

/**
 * Just removes commas from numbers.
 */
public class RemoveCommasFromPrice implements Processor<IeDocument> {

    private final String fieldName;

    public RemoveCommasFromPrice(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void process(IeDocument document) {
        Collection<Field> fields = document.findFields(fieldName);
        for (Field field : fields) {
            String fieldValue = field.getValue().replace(",", "");
            field.setValue(fieldValue);
        }
    }
}
