package com.workfusion.academy.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.cache.CacheBuilder;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Cell;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Token;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Create a feature if data is in a cell of a table.
 *
 * @param <T>
 */
public class IsCoveredWithCellFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "covered_with_cell";
    private transient Map<Token, Collection<Cell>> tokensCoveredWithCells;

    @OnInit
    public void onInit(CacheBuilder cacheBuilder) {
        cacheBuilder.covering(Token.class, Cell.class);
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        tokensCoveredWithCells = document.findAllCovering(Token.class, Cell.class);
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        return tokensCoveredWithCells.containsKey(element) ? Collections.singletonList(new Feature(FEATURE_NAME, 1.0)) : Collections.emptyList();
    }

    @OnDocumentComplete
    public void onDocumentComplete() {
        tokensCoveredWithCells.clear();
    }

}