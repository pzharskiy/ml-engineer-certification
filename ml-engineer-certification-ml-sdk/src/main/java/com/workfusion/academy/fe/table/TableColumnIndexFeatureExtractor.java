package com.workfusion.academy.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Cell;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create a feature for every column value in a table. Value is column number.
 *
 * @param <T>
 */
public class TableColumnIndexFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "column_index_feature";

    private transient List<Cell> cells;

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        cells = new ArrayList<>(document.findAll(Cell.class));
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        List<Feature> features = new ArrayList<>();
        String elementText = element.getText();

        for (Cell cell : cells) {
            if (cell.getText().contains(elementText)) {
                features.add(new Feature(FEATURE_NAME + "=" + cell.getColumnIndex(), 1.));
            }
        }
        return features;
    }

    @OnDocumentComplete
    public void onDocumentComplete() {
        cells.clear();
    }
}
