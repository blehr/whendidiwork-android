package com.brandonlehr.whendidiwork.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by blehr on 3/9/2018.
 */

@Entity(tableName = "selected_sheet")
public class SelectedSheet {

    @Embedded
    private Sheet mSheet;

    @PrimaryKey
    private int selectedSheetKey = 1;

    public SelectedSheet(Sheet sheet) {
        mSheet = sheet;
    }

    public Sheet getSheet() {
        return mSheet;
    }

    public void setSheet(Sheet sheet) {
        mSheet = sheet;
    }

    public int getSelectedSheetKey() {
        return selectedSheetKey;
    }

    public void setSelectedSheetKey(int selectedSheetKey) {
        this.selectedSheetKey = 1;
    }
}
