package com.workfusion.academy.processing;

import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate emails with specific pattern - remove if data doesn't match.
 */
public class EmailPostProcessor implements Processor<IeDocument> {

    private final String fieldName;
    private final String regex;

    private transient Pattern pattern;

    public EmailPostProcessor(String fieldName, String regex) {
        this.fieldName = fieldName;
        this.regex = regex;
    }

    @OnInit
    public void onInit() {
        pattern = Pattern.compile(regex);
    }

    @Override
    public void process(IeDocument document) {
        Optional<Field> fieldOptional = document.findField(fieldName);
        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();
            String value = field.getValue();
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                document.remove(field);
            }
        }
    }

}
