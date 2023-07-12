package com.workfusion.academy.processing;

import com.workfusion.vds.nlp.similarity.impl.JaroWinkler;
import com.workfusion.vds.nlp.uima.resource.ClassPathResource;
import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;
import com.workfusion.vds.sdk.nlp.component.dictionary.CsvDictionaryKeywordProvider;

import java.util.*;

/**
 * Use Jaro-Winker similarity to repair corrupted data based on dictionary provided.
 * If similarity value is below threshold - value is removed.
 */
public class SupplierNamePostProcessor implements Processor<IeDocument> {

    private final String fieldName;

    public SupplierNamePostProcessor(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void process(IeDocument document) {
        CsvDictionaryKeywordProvider csvDictionaryKeywordProvider = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/supplier_name.txt"));
        Set<String> supplierNames = csvDictionaryKeywordProvider.getDictionary();

        List<String> list = new ArrayList<>(supplierNames);
        list.sort(Collections.reverseOrder());
        Set<String> resultSet = new LinkedHashSet<>(list);

        JaroWinkler jaroWinkler = new JaroWinkler();
        Optional<Field> optionalField = document.findField(fieldName);
        if (optionalField.isPresent()) {
            Field field = optionalField.get();
            String value = field.getValue();
            for (String supplierName : resultSet) {
                if (jaroWinkler.similarity(supplierName, value) > 0.95) {
                    field.setValue(supplierName);
                    return;
                }
            }
            document.remove(field);
        }
    }
}
