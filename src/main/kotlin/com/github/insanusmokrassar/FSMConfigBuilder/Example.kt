package com.github.insanusmokrassar.FSMConfigBuilder

import com.github.insanusmokrassar.FSMConfigBuilder.utils.ScenesManager
import javafx.application.Application
import javafx.stage.Stage
import java.net.URL

fun main(args: Array<String>) {
    FSMConfigBuilder().init()
}

class FSMConfigBuilder : Application() {
    private var scenesManager: ScenesManager? = null
    override fun start(primaryStage: Stage?) {
        scenesManager = ScenesManager(primaryStage!!)
        scenesManager?.addScene(URL("main.fxml"))
    }

}
