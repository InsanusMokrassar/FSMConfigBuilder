package com.github.insanusmokrassar.FSMConfigBuilder.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

data class StateRow(
        var numProperty: SimpleIntegerProperty = SimpleIntegerProperty(-1),
        var acceptProperty: SimpleBooleanProperty = SimpleBooleanProperty(),
        var errorProperty: SimpleBooleanProperty = SimpleBooleanProperty(),
        var stackProperty: SimpleBooleanProperty = SimpleBooleanProperty(),
        var nextProperty: SimpleIntegerProperty = SimpleIntegerProperty(-1),
        var regexProperty: SimpleStringProperty = SimpleStringProperty("."),
        var callbackProperty: SimpleStringProperty = SimpleStringProperty()
) {
    fun isValid(): Boolean {
        return numProperty.get() >= 0
                && !(nextProperty.get() == -1 && stackProperty.get())
    }

    override fun toString(): String {
        return "[" +
                "${acceptProperty.get()}," +
                "${errorProperty.get()}," +
                "${stackProperty.get()}," +
                "${regexProperty.get()}," +
                (if (nextProperty.get() == -1) "null" else nextProperty.get().toString()) +
                (if (callbackProperty.get() != null && callbackProperty.get().isNotEmpty()) ",${callbackProperty.get()}" else "") +
                "]"
    }
}
