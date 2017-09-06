package com.github.insanusmokrassar.FSMConfigBuilder

import com.github.insanusmokrassar.FSMConfigBuilder.utils.ScenesManager
import javafx.application.Application
import javafx.stage.Stage

class FSMConfigBuilderApplication : Application() {
    private var scenesManager: ScenesManager? = null
    override fun start(primaryStage: Stage?) {
        scenesManager = ScenesManager(primaryStage!!)
        scenesManager?.addScene(javaClass.classLoader.getResource("main.fxml"))
    }
}
