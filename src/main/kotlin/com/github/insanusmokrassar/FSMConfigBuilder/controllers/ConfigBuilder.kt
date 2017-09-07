package com.github.insanusmokrassar.FSMConfigBuilder.controllers

import com.github.insanusmokrassar.FSMConfigBuilder.models.StateRow
import com.github.insanusmokrassar.IObjectKRealisations.JSONIObject
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import javafx.util.StringConverter
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

class ConfigBuilder : Initializable {

    private val statesList: ObservableList<StateRow> = observableArrayList()

    @FXML
    var previewLabel: Label? = null

    @FXML
    var errorLabel: Label? = null

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
    var addRowBtn: Button? = null

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
                TextFieldTableCell<StateRow, Int>(
                        object: StringConverter<Int>() {
                            override fun toString(what: Int?): String {
                                what?.let {
                                    return it.toString()
                                }
                                return "null"
                            }

                            override fun fromString(string: String?): Int {
                                string?.let {
                                    try {
                                        return it.toInt()
                                    } catch (e: NumberFormatException) {
                                        sendError("You have typed not integer")
                                    }
                                }
                                return -1
                            }

                        }
                )
            }
            it.onEditCommit = EventHandler {
                it.rowValue.nextProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        regexColumn?.cellValueFactory = Callback {
            it.value.regexProperty
        }
        callbackColumn?.cellValueFactory = Callback {
            it.value.callbackProperty
        }

        acceptColumn?.cellFactory = Callback{
            CheckBoxTableCell<StateRow, Boolean>( Callback{
                statesList[it].acceptProperty
            })
        }
        errorColumn?.cellFactory = Callback{
            CheckBoxTableCell<StateRow, Boolean>( Callback{
                statesList[it].errorProperty
            })
        }
        stackColumn?.cellFactory = Callback{
            CheckBoxTableCell<StateRow, Boolean>( Callback{
                statesList[it].stackProperty
            })
        }
        //-------

        statesTableView?.items = statesList

        addRowBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                val newState = StateRow()
                newState.numProperty.set(statesList.size)
                statesList.add(newState)
            }
        }

        generateBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                previewLabel?.text = generateConfig()
                return@EventHandler
            }
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
            errorLabel?.text = error
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

    private fun sendError(error: String) {
        errorLabel?.let {
            it.text = error
        }
        Logger.getGlobal().warning(error)
    }
}