package com.ygznsl.noskurt.oyla.helper;

import android.view.View;
import android.widget.RadioButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class RadioButtonCollection implements Serializable {

    private final List<RadioButton> list = Collections.synchronizedList(new LinkedList<RadioButton>());
    private Nullable<ValueChangedEvent<RadioButton>> onSelectedItemChanged = new Nullable<>();
    private RadioButton selectedItem = null;

    public void add(final RadioButton button){
        list.add(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RadioButton rb : list){
                    if (rb == button) continue;
                    rb.setChecked(false);
                }
                final RadioButton tmp = selectedItem;
                selectedItem = button;
                if (selectedItem != tmp){
                    onSelectedItemChanged.operate(new Consumer<ValueChangedEvent<RadioButton>>() {
                        @Override
                        public void accept(ValueChangedEvent<RadioButton> in) {
                            in.valueChanged(tmp, selectedItem);
                        }
                    });
                }
            }
        });
    }

    public void remove(RadioButton button){
        if (list.remove(button)) button.setOnClickListener(null);
    }

    public RadioButton getSelectedItem() {
        return selectedItem;
    }

    public ValueChangedEvent<RadioButton> getOnSelectedItemChanged() {
        return onSelectedItemChanged.get();
    }

    public void setOnSelectedItemChanged(ValueChangedEvent<RadioButton> onSelectedItemChanged) {
        this.onSelectedItemChanged.set(onSelectedItemChanged);
    }

}
