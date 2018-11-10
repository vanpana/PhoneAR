package com.cyberschnitzel.phonear

import android.content.Context
import android.view.MotionEvent
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.math.Vector3
import android.widget.TextView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class Phone(private val context: Context, transformationSystem: TransformationSystem,
            val phoneData: PhoneData, phoneRenderable: ModelRenderable)
    : TransformableNode(transformationSystem), Node.OnTapListener {

    private lateinit var menu: Node

    companion object {
        private const val INFO_CARD_Y_POS_COEFF = 0.55f
    }


    init {
        setOnTapListener(this)
        renderable = phoneRenderable

        scaleController.minScale = phoneData.minScale
        scaleController.maxScale = phoneData.maxScale
    }

    override fun onActivate() {
        if (scene == null)
            throw IllegalStateException("Scene is null!")

        if (!::menu.isInitialized) {
            menu = Node()
            menu.setParent(this)
            menu.isEnabled = false
            menu.localPosition = Vector3(0.0f, 0.0f, 0.55f)

            ViewRenderable.builder()
                    .setView(context, R.layout.menu_phone_tap)
                    .build()
                    .thenAccept { renderable ->
                        menu.renderable = renderable
                        val view = renderable.view
                        val phoneName = view.findViewById(R.id.phone_name) as TextView
                        phoneName.text = phoneData.phoneName
                    }
                    .exceptionally { throwable ->
                        throw AssertionError("Could not load plane card view.", throwable)
                    }
        }
    }

    override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {
        if (!::menu.isInitialized) {
            return
        }

        menu.isEnabled = !menu.isEnabled
    }

    override fun onUpdate(p0: FrameTime?) {
        if (!::menu.isInitialized) {
            return
        }

        if (scene == null) {
            return
        }

        val cameraPosition = scene.camera.worldPosition
        val cardPosition = menu.worldPosition
        val direction = Vector3.subtract(cameraPosition, cardPosition)
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        menu.worldRotation = lookRotation
    }
}