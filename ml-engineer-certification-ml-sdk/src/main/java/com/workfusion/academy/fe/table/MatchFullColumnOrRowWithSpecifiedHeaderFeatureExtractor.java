package com.workfusion.academy.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.cache.CacheBuilder;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.*;

import java.util.*;

/**
 * Create a feature for every value in a column\row where appropriate column\row header contains any of keyword list provided.
 *
 * @param <T>
 */
public class MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final String featureName;
    private final Set<String> headerKeywords;
    private final boolean isUsedForColumn;
    private transient Map<Integer, Cell> headersMapping;
    private transient Map<Token, Collection<Cell>> tokensCoveredWithCells;
    private transient Map<Token, Collection<Table>> tokensCoveredWithTables;
    private transient List<Feature> features;

    public MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor(String featureName, Set<String> columnNames, boolean isUsedForColumn) {
        this.featureName = featureName + (isUsedForColumn ? "_match_table_column_header" : "_match_table_row_header");
        this.isUsedForColumn = isUsedForColumn;
        this.headerKeywords = columnNames;
    }

    @OnInit
    public void onInit(CacheBuilder cacheBuilder) {
        cacheBuilder.covering(Token.class, Cell.class);
        cacheBuilder.covering(Token.class, Table.class);
        cacheBuilder.covering(Cell.class, Table.class);
        features = Collections.singletonList(new Feature(featureName, 1.0));
    }

    /**
     * Code is running on document load one time per document.
     *
     * @param document
     */
    @OnDocumentStart
    public void onDocumentStart(Document document) {
        headersMapping = new TreeMap<>();
        Collection<Table> coveringTable = document.findAll(Table.class);
        for (Table table : coveringTable) {
            List<Cell> coveredCells = document.findCovered(Cell.class, table);
            for (Cell coveredCell : coveredCells) {
                String cellText = coveredCell.getText().toLowerCase();
                for (String columnName : headerKeywords) {
                    if (cellText.contains(columnName)) {
                        headersMapping.put(table.hashCode(), coveredCell);
                    }
                }
            }
        }
        tokensCoveredWithCells = document.findAllCovering(Token.class, Cell.class);
        tokensCoveredWithTables = document.findAllCovering(Token.class, Table.class);
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        Collection<Cell> coveringCell = tokensCoveredWithCells.get(element);
        Collection<Table> coveringTable = tokensCoveredWithTables.get(element);
        if (!coveringCell.isEmpty() && !coveringTable.isEmpty() && !headersMapping.isEmpty()) {
            int key = coveringTable.iterator().next().hashCode();
            for (Cell cell : coveringCell) {
                if (headersMapping.containsKey(key)) {
                    Cell headerCell = headersMapping.get(key);
                    if (isUsedForColumn) {
                        if (headerCell.getColumnIndex() == cell.getColumnIndex() && headerCell.getRowIndex() < cell.getRowIndex()) {
                            return features;
                        }
                    } else {
                        if (headerCell.getRowIndex() == cell.getRowIndex() && headerCell.getColumnIndex() < cell.getColumnIndex()) {
                            return features;
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    @OnDocumentComplete
    public void onDocumentComplete() {
        tokensCoveredWithCells.clear();
        tokensCoveredWithTables.clear();
        headersMapping.clear();
    }
}