package com.github.insanusmokrassar.FSMConfigBuilder.controllers

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import java.net.URL
import java.util.*

class ConfigBuilder : Initializable {

    @FXML
    var previewLabel: Label? = null

    @FXML
    var generateBtn: Button? = null

    @FXML
    var saveBtn: Button? = null

    @FXML
    var newBtn: Button? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }
}