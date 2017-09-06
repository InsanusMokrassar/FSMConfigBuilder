package com.github.insanusmokrassar.FSMConfigBuilder.controllers

import com.github.insanusmokrassar.FSMConfigBuilder.models.StateRow
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import java.net.URL
import java.util.*

class ConfigBuilder : Initializable {

    val statesList: ObservableList<StateRow> = observableArrayList()

    @FXML
    var previewLabel: Label? = null

    @FXML
    var generateBtn: Button? = null

    @FXML
    var saveBtn: Button? = null

    @FXML
    var newBtn: Button? = null

    @FXML
    var statesTableView: TableView<StateRow>? = null

    @FXML
    var numColumn: TableColumn<TableView<StateRow>, Int>? = null

    @FXML
    var acceptColumn: TableColumn<TableView<StateRow>, Boolean>? = null

    @FXML
    var errorColumn: TableColumn<TableView<StateRow>, Boolean>? = null

    @FXML
    var returnColumn: TableColumn<TableView<StateRow>, Boolean>? = null

    @FXML
    var nextColumn: TableColumn<TableView<StateRow>, Int>? = null

    @FXML
    var regexColumn: TableColumn<TableView<StateRow>, String>? = null

    @FXML
    var callbackColumn: TableColumn<TableView<StateRow>, String>? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        //Say me how to do better??? -----
        numColumn?.cellValueFactory = PropertyValueFactory("numField")
        acceptColumn?.cellValueFactory = PropertyValueFactory("acceptField")
        errorColumn?.cellValueFactory = PropertyValueFactory("errorField")
        returnColumn?.cellValueFactory = PropertyValueFactory("returnField")
        nextColumn?.cellValueFactory = PropertyValueFactory("nextField")
        regexColumn?.cellValueFactory = PropertyValueFactory("regexField")
        callbackColumn?.cellValueFactory = PropertyValueFactory("callbackField")
        //-------

        statesTableView?.items = statesList
        val testState = StateRow(0)
        statesList.add(testState)
        Thread({
            while(true) {

                Thread.sleep(1000)
            }
        }).start()
    }
}