package com.uwcisak.isaklaboratoryapp.utilities;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;

public class GlobalState extends Application {

    private GoogleAccountCredential mGoogleAccountCredential;
    private ArrayList<Item> inventory;
    private ArrayList<Item> borrowingList;
    private ArrayList<ReturnItem> loaningList;
    private ArrayList<Item> returnQueue;
    private ReadResult mReadResult;

    public GoogleAccountCredential getGoogleAccountCredential() {
        return mGoogleAccountCredential;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public ArrayList<Item> getBorrowingList() {
        return borrowingList;
    }

    public ArrayList<ReturnItem> getLoaningList() {
        return loaningList;
    }

    public ReadResult getReadResult() {
        return mReadResult;
    }

    public ArrayList<Item> getReturnQueue() {
        return returnQueue;
    }

    public void setGoogleAccountCredential(GoogleAccountCredential googleAccountCredential) {
        mGoogleAccountCredential = googleAccountCredential;
    }

    public void setInventory(ArrayList<Item> inventory) {
        this.inventory = inventory;
    }

    public void setBorrowingList(ArrayList<Item> borrowingList) {
        this.borrowingList = borrowingList;
    }

    public void setLoaningList(ArrayList<ReturnItem> loaningList) {
        this.loaningList = loaningList;
    }

    public void setReadResult(ReadResult readResult) {
        mReadResult = readResult;
    }

    public void setReturnQueue(ArrayList<Item> returnQueue) {
        this.returnQueue = returnQueue;
    }
}
