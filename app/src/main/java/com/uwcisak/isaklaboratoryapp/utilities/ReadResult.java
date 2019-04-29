package com.uwcisak.isaklaboratoryapp.utilities;

import java.util.ArrayList;
import java.util.List;

public class ReadResult {
    private List<String> inventoryItems;
    private ArrayList<String> previouslyLoanedList;
    private int possessionSheetRowNo;

    public ReadResult(List<String> inventoryItems, ArrayList<String> previouslyLoanedList, int possessionSheetRowNo) {
        this.inventoryItems = inventoryItems;
        this.previouslyLoanedList = previouslyLoanedList;
        this.possessionSheetRowNo = possessionSheetRowNo;
    }

    public List<String> getInventoryItems() {
        return inventoryItems;
    }

    public ArrayList<String> getPreviouslyLoanedList() {
        return previouslyLoanedList;
    }

    public int getPossessionSheetRowNo() {
        return possessionSheetRowNo;
    }

    public void setPreviouslyLoanedList(ArrayList<String> previouslyLoanedList) {
        this.previouslyLoanedList = previouslyLoanedList;
    }
}
