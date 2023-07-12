package com.workfusion.academy.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.cache.CacheBuilder;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.*;

import java.util.*;

/**
 * Create a feature if data is located in lower right cell of the table.
 *
 * @param <T>
 */
public class IsLastCellInTableFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final transient List<Cell> lastCells = new ArrayList<>();
    private transient List<Feature> features;
    private transient Map<Token, Collection<Cell>> tokensCoveredWithCells;

    @OnInit
    public void onInit(CacheBuilder cacheBuilder) {
        cacheBuilder.covered(Cell.class, Table.class);
        cacheBuilder.covered(Token.class, Cell.class);
        features = Collections.singletonList(new Feature("last_cell_in_table", 1.0));
    }

    @OnDocumentStart
    public void OnDocumentStart(Document document) {
        Collection<Table> coveringTable = document.findAll(Table.class);
        for (Table table : coveringTable) {
            List<Cell> coveredCells = document.findCovered(Cell.class, table);
            Cell cell = null;
            for (Cell coveredCell : coveredCells) {
                if (cell == null) {
                    cell = coveredCell;
                } else {
                    int coveredCellColumnIndex = coveredCell.getColumnIndex();
                    int cellColumnIndex = cell.getColumnIndex();
                    if (coveredCellColumnIndex > cellColumnIndex) {
                        cell = coveredCell;
                    } else {
                        int coveredCellRowIndex = coveredCell.getRowIndex();
                        int cellRowIndex = cell.getRowIndex();
                        if (coveredCellColumnIndex == cellColumnIndex && coveredCellRowIndex > cellRowIndex) {
                            cell = coveredCell;
                        }
                    }
                }
            }
            lastCells.add(cell);
        }

        tokensCoveredWithCells = document.findAllCovering(Token.class, Cell.class);
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        Collection<Cell> cells = tokensCoveredWithCells.get(element);
        if (cells != null) {
            for (Cell cell : cells) {
                if (lastCells.contains(cell)) {
                    return features;
                }
            }
        }
        return Collections.emptyList();
    }

}
