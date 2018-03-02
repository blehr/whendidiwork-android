package com.brandonlehr.whendidiwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by blehr on 2/12/2018.
 */

public class FileList {
    @SerializedName("sheets")
    @Expose
    private List<Sheet> sheets = null;
    @SerializedName("lastUsed")
    @Expose
    private String lastUsed;

    public List<Sheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<Sheet> sheets) {
        this.sheets = sheets;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }
}
