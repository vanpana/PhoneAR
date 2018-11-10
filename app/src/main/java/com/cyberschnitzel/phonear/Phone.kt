package com.cyberschnitzel.phonear

import android.content.Context
import android.view.MotionEvent
import android.widget.TextView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class Phone(private val context: Context, transformationSystem: TransformationSystem,
            val phoneData: PhoneData)
    : TransformableNode(transformationSystem), Node.OnTapListener {

    private lateinit var menu: Node
    private lateinit var frontFace: TransformableNode
    private lateinit var backFace: TransformableNode
    private lateinit var rightFace: TransformableNode
    private lateinit var leftFace: TransformableNode
    private lateinit var downFace: TransformableNode
    private lateinit var upFace: TransformableNode

    companion object {
        private const val INFO_CARD_Y_POS_COEFF = 0.06f
        private const val SIZE_SCALE = 0.01f
    }


    init {
        setOnTapListener(this)
//        worldPosition = Vector3.add(worldPosition, up.scaled(phoneData.size.h * SIZE_SCALE / 2))
        MaterialFactory.makeTransparentWithColor(context, Color(android.graphics.Color.RED)).thenAccept {
            val parentNode = ShapeFactory.makeSphere(phoneData.size.d / 2 * SIZE_SCALE, Vector3.zero(), it)
            parentNode.isShadowCaster = false
            renderable = parentNode
        }
        localScale = Vector3.one()
        worldScale = Vector3.one()
//        scaleController.minScale = phoneData.minScale
//        scaleController.maxScale = phoneData.maxScale
    }

    override fun onActivate() {
        if (scene == null) {
            throw IllegalStateException("Scene is null!")
        }

        if (!::menu.isInitialized) {
            initializeMenuNode()
        }

        if (!::upFace.isInitialized) {
            upFace = initializeBlackFace(phoneData.size.h * SIZE_SCALE / 2)
        }
        if (!::downFace.isInitialized) {
            downFace = initializeBlackFace(-phoneData.size.h * SIZE_SCALE / 2)
        }

        if (!::frontFace.isInitialized) {
            frontFace = initializeFrontFace()
        }

        if (!::backFace.isInitialized) {
            backFace = initializeBackFace()
        }

        if (!::leftFace.isInitialized) {
            leftFace = initializeSideFace(phoneData.size.w * SIZE_SCALE / 2)
        }

        if (!::rightFace.isInitialized) {
            rightFace = initializeSideFace(-phoneData.size.w * SIZE_SCALE / 2)
        }

    }


    private fun initializeSideFace(fl: Float): TransformableNode {
        val sideNode = TransformableNode(transformationSystem)
        sideNode.setParent(this)
        sideNode.localPosition = left.scaled(fl)
        sideNode.translationController.isEnabled = false
        sideNode.rotationController.isEnabled = false
        sideNode.scaleController.isEnabled = false

        Texture.builder()
                .setSource(context, context.resources.getIdentifier(phoneData.phoneName + "_side", "drawable", context.packageName))
                .build()
                .thenAccept { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                            .thenAccept { material ->
                                sideNode.renderable = ShapeFactory.makeCube(Vector3(0.02f, phoneData.size.h * SIZE_SCALE, phoneData.size.d * SIZE_SCALE), Vector3.zero(), material)
                            }
                }


        return sideNode
    }

    private fun initializeFrontFace(): TransformableNode {
        val frontNode = TransformableNode(transformationSystem)
        frontNode.setParent(this)
        frontNode.localPosition = back.scaled(phoneData.size.d * SIZE_SCALE / 2)

        frontNode.translationController.isEnabled = false
        frontNode.rotationController.isEnabled = false
        frontNode.scaleController.isEnabled = false

        Texture.builder()
                .setSource(context, context.resources.getIdentifier(phoneData.phoneName + "_front", "drawable", context.packageName))
                .build()
                .thenAccept { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                            .thenAccept { material ->
                                frontNode.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, phoneData.size.h * SIZE_SCALE, 0.01f), Vector3.zero(), material)
                            }
                }
        return frontNode
    }

    private fun initializeBackFace(): TransformableNode {
        val backNode = TransformableNode(transformationSystem)
        backNode.setParent(this)
        backNode.localPosition = forward.scaled(phoneData.size.d * SIZE_SCALE / 2)
        backNode.translationController.isEnabled = false
        backNode.rotationController.isEnabled = false
        backNode.scaleController.isEnabled = false
        Texture.builder()
                .setSource(context, context.resources.getIdentifier(phoneData.phoneName + "_back", "drawable", context.packageName))
                .build()
                .thenAccept { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                            .thenAccept { material ->
                                backNode.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, phoneData.size.h * SIZE_SCALE, 0.01f), Vector3.zero(), material)
                            }
                }
        return backNode
    }


    private fun initializeBlackFace(h: Float): TransformableNode {
        val node = TransformableNode(transformationSystem)
        node.setParent(this)
        node.localPosition = up.scaled(h)

        MaterialFactory.makeOpaqueWithColor(context, Color(android.graphics.Color.BLACK))
                .thenAccept { node.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, 0.01f, phoneData.size.d * SIZE_SCALE), Vector3.zero(), it) }

        val trackSphere = TransformableNode(transformationSystem)
        trackSphere.setParent(node)
        trackSphere.localPosition = Vector3.zero()
        MaterialFactory.makeTransparentWithColor(context, Color(android.graphics.Color.BLUE)).thenAccept {
            trackSphere.renderable = ShapeFactory.makeSphere(0.1f, Vector3.zero(), it)
        }


        return node
    }

    private fun initializeMenuNode() {
        menu = Node()
        menu.setParent(this)
        menu.isEnabled = false
        val dimension = phoneData.size.h * INFO_CARD_Y_POS_COEFF + 1.0f
        menu.localPosition = Vector3(0.0f, dimension, 0.0f)
        menu.localScale = Vector3(dimension, dimension, dimension)

        ViewRenderable.builder()
                .setView(context, R.layout.menu_phone_tap)
                .build()
                .thenAccept { renderable ->
                    menu.renderable = renderable
                    val view = renderable.view  // Get the menu view
                    val phoneName = view.findViewById(R.id.phone_name) as TextView
                    phoneName.text = phoneData.phoneName
                }
                .exceptionally { throwable ->
                    throw AssertionError("Could not load plane card view.", throwable)
                }
    }

    override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {
        if (!::menu.isInitialized) {
            return
        }

        menu.isEnabled = !menu.isEnabled
    }

    override fun onUpdate(p0: FrameTime?) {
        if (!::menu.isInitialized && menu.isEnabled) {
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