package com.workfusion.academy.fe.custom;

import com.workfusion.vds.nlp.similarity.StringSimilarityUtils;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.fe.annotation.FeatureName;
import com.workfusion.vds.sdk.api.nlp.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@FeatureName(TotalAmountFE.FEATURE_NAME)
public class TotalAmountFE<T extends Element> implements FeatureExtractor<T> {

    private static final String TOKEN_NAME_TOTAL = "total";
    public static final String FEATURE_NAME = "total_amount";

    @Override
    public Collection<Feature> extract(Document document, T element) {
        List<Feature> result = new ArrayList<>();
        List<NamedEntity> namedEntities = document.findCovered(NamedEntity.class, element); //this is checker NE
        List<Line> lines = document.findPrevious(Line.class, element, 1); //this is previous line
        List<Token> currentLine = document.findPrevious(Token.class, element, 5);//this is 3 token before element
        if (!lines.isEmpty()) {
            for (Line line : lines) {
                if (line.getText().toLowerCase(Locale.ROOT).contains("total")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (line.getText().toLowerCase(Locale.ROOT).contains("amount")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (line.getText().toLowerCase(Locale.ROOT).contains("due")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (line.getText().toLowerCase(Locale.ROOT).contains("balance")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (line.getText().toLowerCase(Locale.ROOT).contains("duo")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                double cos = StringSimilarityUtils.cosine(line.getText().toLowerCase(Locale.ROOT), "total");
                if (cos > 0.6) {
                    result.add(new Feature(FEATURE_NAME, cos));
                }
            }
        }

        if (!currentLine.isEmpty()) {
            for (Token word : currentLine) {
                if (word.getText().toLowerCase(Locale.ROOT).contains("total")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (word.getText().toLowerCase(Locale.ROOT).contains("amount")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (word.getText().toLowerCase(Locale.ROOT).contains("due")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (word.getText().toLowerCase(Locale.ROOT).contains("balance")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                if (word.getText().toLowerCase(Locale.ROOT).contains("duo")) {
                    result.add(new Feature(FEATURE_NAME, 1.0));
                }
                for (Token line : currentLine) {
                    double cos = StringSimilarityUtils.cosine(line.getText().toLowerCase(Locale.ROOT), "total");
                    if (cos > 0.5) {
                        result.add(new Feature(FEATURE_NAME, cos));
                    }
                }
            }

        }

        return result;
    }

}
