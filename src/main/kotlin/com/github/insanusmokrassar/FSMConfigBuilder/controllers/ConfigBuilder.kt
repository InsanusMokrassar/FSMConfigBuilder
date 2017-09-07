package com.github.insanusmokrassar.FSMConfigBuilder.controllers

import com.github.insanusmokrassar.FSMConfigBuilder.models.StateRow
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter
import java.net.URL
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

class ConfigBuilder : Initializable {

    private val statesList: ObservableList<StateRow> = observableArrayList()

    @FXML
    var infoLabel: Label? = null

    @FXML
    var generateBtn: Button? = null

    @FXML
    var saveBtn: Button? = null

    @FXML
    var newBtn: Button? = null

    @FXML
    var loadBtn: Button? = null

    @FXML
    var statesTableView: TableView<StateRow>? = null

    @FXML
    var numColumn: TableColumn<StateRow, Int>? = null

    @FXML
    var acceptColumn: TableColumn<StateRow, Boolean>? = null

    @FXML
    var errorColumn: TableColumn<StateRow, Boolean>? = null

    @FXML
    var stackColumn: TableColumn<StateRow, Boolean>? = null

    @FXML
    var nextColumn: TableColumn<StateRow, Int>? = null

    @FXML
    var regexColumn: TableColumn<StateRow, String>? = null

    @FXML
    var callbackColumn: TableColumn<StateRow, String>? = null

    @FXML
    var addBtn: Button? = null

    @FXML
    var removeBtn: Button? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        //Say me how to do better??? -----
        numColumn?.cellValueFactory = Callback {
            it.value.numProperty as? ObservableValue<Int>
        }
        nextColumn?.cellValueFactory = Callback {
            it.value.nextProperty as? ObservableValue<Int>
        }
        nextColumn?.let {
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, Int>(IntegerStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.nextProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        regexColumn?.let {
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, String>(DefaultStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.regexProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        callbackColumn?.let {
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, String>(DefaultStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.callbackProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }

        acceptColumn?.cellFactory = Callback {
            CheckBoxTableCell<StateRow, Boolean>(Callback {
                statesList[it].acceptProperty
            })
        }
        errorColumn?.cellFactory = Callback {
            CheckBoxTableCell<StateRow, Boolean>(Callback {
                statesList[it].errorProperty
            })
        }
        stackColumn?.cellFactory = Callback {
            CheckBoxTableCell<StateRow, Boolean>(Callback {
                statesList[it].stackProperty
            })
        }
        //-------

        statesTableView?.items = statesList

        addBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                val newState = StateRow()
                newState.numProperty.set(statesList.size)
                statesList.add(newState)
            }
        }

        removeBtn?.onAction = EventHandler {
            val inFocus = statesTableView?.focusModel?.focusedItem
            inFocus?.let {
                statesList.remove(it)
                recalculateNums()
                return@EventHandler
            }
            sendInfo("You must choose the row to delete")
        }

        generateBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                sendInfo(generateConfig())
                return@EventHandler
            }
        }
    }

    private fun recalculateNums() {
        var i = 0
        statesList.forEach {
            it.numProperty.set(i)
            i++
        }
    }

    private fun generateConfig(): String {
        val config = ArrayList<String>()
        statesList.forEach {
            config.add(it.toString())
        }
        return config.toString()
    }

    /**
     * return true if was error
     */
    private fun checkConfigAndShowError(): Boolean {
        val error = checkConfig()
        error?.let {
            infoLabel?.text = error
            return true
        }
        return false
    }

    private fun checkConfig(): String? {
        val buffer = StringBuilder()
        statesList.forEach {
            if (!it.isValid()) {
                buffer.append("${statesList.indexOf(it)}\n")
            }
        }
        return (if (buffer.isEmpty()) null else "Next states are invalid:\n$buffer")
    }

    private fun sendInfo(info: String) {
        infoLabel?.let {
            it.text = info
        }
        Logger.getGlobal().info(info)
    }
}