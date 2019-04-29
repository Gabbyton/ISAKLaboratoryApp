package com.uwcisak.isaklaboratoryapp.utilities;

import com.uwcisak.isaklaboratoryapp.utilities.Item;

public class ReturnItem {
    private Item item;
    private boolean checked;

    public ReturnItem(Item item, boolean checked) {
        this.item = item;
        this.checked = checked;
    }

    public Item getItem() {
        return item;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
