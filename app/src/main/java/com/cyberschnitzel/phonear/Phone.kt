package com.cyberschnitzel.phonear

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import java.util.function.Consumer

class Phone(private val context: Context, transformationSystem: TransformationSystem,
            private val phoneData: PhoneData, private val comparable: Boolean = true)
    : TransformableNode(transformationSystem), Node.OnTapListener {

    private lateinit var menu: Node
    private lateinit var frontFace: TransformableNode
    private lateinit var backFace: TransformableNode
    private lateinit var sideFace: TransformableNode
    private lateinit var downFace: TransformableNode
    private lateinit var upFace: TransformableNode


    private lateinit var phoneActionsPopup: Node
    private var canGoBack: Boolean = false
    var phoneDialog: PhoneDialog? = null

    companion object {
        private const val INFO_CARD_Y_POS_COEFF = 0.06f
        private const val SIZE_SCALE = 0.002f
    }


    init {
        setOnTapListener(this)
    }

    override fun onActivate() {
        if (scene == null) {
            throw IllegalStateException("Scene is null!")
        }

        if (!::phoneActionsPopup.isInitialized) {
            phoneActionsPopup = Node()
            phoneActionsPopup.setParent(this)
            phoneActionsPopup.isEnabled = false
            initMenuPopup()
        }

        if (!::menu.isInitialized) {
            initializeMenuNode()
        }

        if (phoneData.hasImages) {
            MaterialFactory.makeTransparentWithColor(context, Color(android.graphics.Color.TRANSPARENT)).thenAccept {
                val parentNode = ShapeFactory.makeCube(phoneData.size.vec3().scaled(SIZE_SCALE - 0.0001f), Vector3.zero(), it)
                parentNode.isShadowCaster = false
                renderable = parentNode
            }
            localScale = Vector3.one()
            worldScale = Vector3.one()

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

            if (!::sideFace.isInitialized) {
                sideFace = initializeSideFace()
            }
        }
        else {
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("Phone_01.sfb"))
                    .build()
                    .thenAccept { model ->
                        renderable = model
                    }
                    .exceptionally { throwable ->
                        Log.d(ContentValues.TAG, throwable.localizedMessage)
                        val toast = Toast.makeText(context, "Unable to load phone renderable", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        return@exceptionally null
                    }
        }
    }

    private fun initializeSideFace(): TransformableNode {
        val sideNode = TransformableNode(transformationSystem)
        sideNode.setParent(this)
        sideNode.localPosition = Vector3.zero()
        sideNode.translationController.isEnabled = false
        sideNode.rotationController.isEnabled = false
        sideNode.scaleController.isEnabled = false

        Texture.builder()
                .setSource(context, Uri.parse(phoneData.sideImage))
                .build()
                .thenAccept { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                            .thenAccept { material ->
                                sideNode.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, phoneData.size.h * SIZE_SCALE * 0.999f, phoneData.size.d * SIZE_SCALE * 0.999f), Vector3.zero(), material)
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
                .setSource(context, Uri.parse(phoneData.frontImage))
                .build()
                .thenAccept { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                            .thenAccept { material ->
                                frontNode.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, phoneData.size.h * SIZE_SCALE, 0.001f), Vector3.zero(), material)
                                frontNode.renderable.isShadowCaster = false
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
                .setSource(context, Uri.parse(phoneData.backImage))
                .build()
                .thenAccept { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                            .thenAccept { material ->
                                backNode.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, phoneData.size.h * SIZE_SCALE, 0.001f), Vector3.zero(), material)
                                backNode.renderable.isShadowCaster = false
                            }
                }
        return backNode
    }


    private fun initializeBlackFace(h: Float): TransformableNode {
        val node = TransformableNode(transformationSystem)
        node.setParent(this)
        node.localPosition = up.scaled(h)

        MaterialFactory.makeOpaqueWithColor(context, Color(android.graphics.Color.BLACK))
                .thenAccept {
                    node.renderable = ShapeFactory.makeCube(Vector3(phoneData.size.w * SIZE_SCALE, 0.001f, phoneData.size.d * SIZE_SCALE), Vector3.zero(), it)
                    node.renderable.isShadowCaster = false
                }


        return node
    }

    private fun initializeMenuNode() {
        menu = Node()
        menu.setParent(this)
        menu.isEnabled = false
        val dimension = phoneData.size.h * SIZE_SCALE + 0.5f
        menu.localPosition = up.scaled(dimension)
        menu.localScale = Vector3.one()

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

    private val compareButtonClick = View.OnClickListener {
        initComparePopup()
    }

    private val specsButtonClick = View.OnClickListener {
        initSpecsPopup()
    }

    private val handsModeButtonClick = View.OnClickListener {

    }

    private fun initPopup(layout: Int, then: Consumer<ViewRenderable>) {
        val dimension = phoneData.size.h * INFO_CARD_Y_POS_COEFF + 1.0f
        phoneActionsPopup.localPosition = Vector3(0.0f, dimension, 0.0f)
        phoneActionsPopup.localScale = Vector3(dimension, dimension, dimension)

        ViewRenderable.builder()
                .setView(context, layout)
                .build()
                .thenAccept(then)
                .exceptionally { throwable ->
                    throw AssertionError("Could not load plane card view.", throwable)
                }
    }

    private fun initMenuPopup() {
        initPopup(R.layout.menu_phone_tap, Consumer { renderable ->
            phoneActionsPopup.renderable = renderable
            val view = renderable.view  // Get the phoneActionsPopup view

            val compareButton = view.findViewById(R.id.compare) as Button
            compareButton.setOnClickListener(compareButtonClick)
            if (!this.comparable) {
                compareButton.visibility = View.GONE
            }
            val specsButton = view.findViewById(R.id.specs) as Button
            specsButton.setOnClickListener(specsButtonClick)
            val handsModeButton = view.findViewById(R.id.hands_mode) as Button
            handsModeButton.setOnClickListener(handsModeButtonClick)
        })
        canGoBack = false
    }

    private fun initComparePopup() {
        if (phoneDialog != null) {
            phoneActionsPopup.renderable = phoneDialog!!.inputRenderable
            canGoBack = true
        }
    }

    private fun initSpecsPopup() {
        initPopup(R.layout.fragment_specs, Consumer { renderable ->
            phoneActionsPopup.renderable = renderable
            val view = renderable.view  // Get the view

            val phoneName = view.findViewById(R.id.phone_name) as TextView
            phoneName.text = phoneData.phoneName

            val os = view.findViewById(R.id.os) as TextView
            os.text = phoneData.os

            val cpu = view.findViewById(R.id.cpu) as TextView
            cpu.text = phoneData.cpu

            val camera = view.findViewById(R.id.camera) as TextView
            camera.text = phoneData.camera
        })
        canGoBack = true
    }

    override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {
        if (!::phoneActionsPopup.isInitialized) {
            return
        }

        phoneActionsPopup.isEnabled = !phoneActionsPopup.isEnabled
    }

    override fun onUpdate(p0: FrameTime?) {
        if (!::phoneActionsPopup.isInitialized || !phoneActionsPopup.isEnabled) {
            return
        }

        if (scene == null) {
            return
        }

        val cameraPosition = scene.camera.worldPosition
        val cardPosition = phoneActionsPopup.worldPosition
        val direction = Vector3.subtract(cameraPosition, cardPosition)
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        phoneActionsPopup.worldRotation = lookRotation
    }

    fun onBackPressed(): Boolean {
        return if (canGoBack) {
            initMenuPopup()
            false
        } else {
            true
        }
    }
}