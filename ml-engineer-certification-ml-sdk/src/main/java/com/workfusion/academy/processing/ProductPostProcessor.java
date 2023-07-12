package com.workfusion.academy.processing;

import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.model.Cell;
import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

import java.util.*;

/**
 * Custom post-processor with specific of training dataset used.
 * Keep in mind that data should be taken from table cells.
 * Multiple spaces are removed.
 * Also there are some specific values to replace of remove.
 */
public class ProductPostProcessor implements Processor<IeDocument> {

    private final List<String> valuesToRemove = new ArrayList<>();
    private final Map<String, String> replaceValues = new TreeMap<>();
    private final String fieldName;

    public ProductPostProcessor(String fieldName) {
        this.fieldName = fieldName;
    }

    @OnInit
    public void init() {
        replaceValues.put("Media Marketing", "MediaMarketing");
        valuesToRemove.add("Load Date");
    }

    @Override
    public void process(IeDocument document) {
        Collection<Field> fields = document.findFields(fieldName);
        for (Field field : fields) {
            List<Cell> covering = document.findCovering(Cell.class, field);
            String value = field.getValue();
            if (!covering.isEmpty()) {
                value = covering.get(0).getText();
            }
            value = value.trim().replaceAll("\\s+", " ");
            for (String valueToRemove : valuesToRemove) {
                if (value.contains(valueToRemove)) {
                    document.remove(field);
                    return;
                }
            }
            for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
                if (value.contains(entry.getKey())) {
                    value = value.replace(entry.getKey(), entry.getValue());
                }
            }
            field.setValue(value);
        }
    }
}
