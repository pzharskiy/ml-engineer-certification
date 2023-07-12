package com.workfusion.academy.processing;

import com.workfusion.academy.config.Placeholders;
import com.workfusion.nlp.uima.workflow.task.extract.processing.DateNormalizer;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Try to normalized specific custom date format to MM/dd/yyyy first.
 * Then clear out all forbidden symbols.
 * Then use OOTB date normalizer.
 * Finally remove all dates that starts with '00' in year.
 */
public class DatePostProcessing implements Processor<IeDocument> {


    private final String fieldName;
    private final DateNormalizer dateNormalizer = new DateNormalizer(Placeholders.DATE_FORMAT);
    private final String[] forbiddenSymbols = new String[]{"*"};

    private transient Pattern datePattern;

    public DatePostProcessing(String fieldName) {
        this.fieldName = fieldName;
    }

    @OnInit
    public void onInit() {
        String customPattern = "(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|" +
                "January|February|March|April|May|June|July|August|September|October|November|December)" +
                " [0-9]{1,2}(.|\\,) ?[0-9]{4}";
        datePattern = Pattern.compile(customPattern);
    }

    @Override
    public void process(IeDocument document) {
        Optional<Field> fieldOptional = document.findField(fieldName);
        if (fieldOptional.isPresent()) {

            Field field = fieldOptional.get();
            String value = field.getValue();
            value = value.replace("0ct", "October");

            Matcher matcher = datePattern.matcher(value);
            if (matcher.matches()) {
                value = customProcess(value);
            }

            value = removeForbiddenSymbols(value);
            value = dateNormalizer.normalize(value);

            if (value != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(Placeholders.DATE_FORMAT);
                try {
                    Date parsedDate = sdf.parse(value);

                    SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
                    if (sdfYear.format(parsedDate).startsWith("00")) {
                        document.remove(field);
                        return;
                    }

                    field.setValue(value);
                } catch (ParseException e) {
                    document.remove(field);
                }
            }
        }
    }

    private String removeForbiddenSymbols(String value) {
        for (String forbiddenSymbol : forbiddenSymbols) {
            while (value.contains(forbiddenSymbol)) {
                value = value.replace(forbiddenSymbol, "");
            }
        }
        return value;
    }

    private String customProcess(String value) {
        return value.replaceAll("JANUARY|January", "01")
                .replaceAll("FEBRUARY|February", "02")
                .replaceAll("MARCH|March", "03")
                .replaceAll("APRIL|April", "04")
                .replaceAll("MAY|May", "05")
                .replaceAll("JUNE|June", "06")
                .replaceAll("JULY|July", "07")
                .replaceAll("AUGUST|August", "08")
                .replaceAll("SEPTEMBER|September", "09")
                .replaceAll("OCTOBER|October", "10")
                .replaceAll("NOVEMBER|November", "11")
                .replaceAll("DECEMBER|December", "12")
                .replaceAll("(, |\\. | |,|\\.)", "/");
    }
}
