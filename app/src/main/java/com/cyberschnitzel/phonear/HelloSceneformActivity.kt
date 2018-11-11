package com.cyberschnitzel.phonear

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_ux.*

class HelloSceneformActivity : AppCompatActivity(), PhoneSelectedTrigger {
    private var arFragment: ArFragment? = null
    private var phoneDialog: PhoneDialog? = null
    private var firstPhone: Phone? = null // Main phone
    private var secondPhone: Phone? = null // Comparable phone
    private lateinit var network: Network

    override// CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_ux)

        network = Network(this)

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?

        phoneDialog = PhoneDialog(applicationContext, arFragment!!.transformationSystem)
        phoneDialog!!.parentPhoneNameInput = phone_name_input
        phoneDialog!!.phoneSelectedTrigger = this
        phone_name_input.addTextChangedListener(PhoneInputWatcher())

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, _: MotionEvent ->
            if (phoneDialog == null) {
                return@setOnTapArPlaneListener
            }

            if (firstPhone == null) { // Show phone dialog
                attachNodeToAnchor(phoneDialog!!, createAnchor(hitResult))
            } else { // Show phones
                if (secondPhone != null) { // Show second phone
                    // Create the Anchor.
                    phoneDialog = null
                    attachNodeToAnchor(secondPhone!!, createAnchor(hitResult))
                }
            }
        }
    }

    companion object {
        private val TAG = HelloSceneformActivity::class.java.simpleName
        private const val MIN_OPENGL_VERSION = 3.0

        fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
            val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                    .deviceConfigurationInfo
                    .glEsVersion
            if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
                Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
                Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
                activity.finish()
                return false
            }
            return true
        }
    }

    private fun createAnchor(hitResult: HitResult): AnchorNode {
        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment!!.arSceneView.scene)
        return anchorNode
    }

    private fun attachNodeToAnchor(node: TransformableNode, anchorNode: Node) {
        node.setParent(anchorNode)
        node.select()
    }

    override fun onPhoneSelected(phone: Phone) {
        if (firstPhone == null) {
            firstPhone = phone
            firstPhone!!.phoneDialog = phoneDialog

            // Set firstPhone anchor to phone dialog one
            attachNodeToAnchor(firstPhone!!, phoneDialog!!.parent)

            // Make phone dialog disappear
            phoneDialog!!.setParent(null)
            // phoneDialog = null // TODO don't make null maybe
        } else if (secondPhone == null) {
            secondPhone = phone
            secondPhone!!.comparable = false
            onBackPressed()
        }
    }

    inner class PhoneInputWatcher : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            phoneDialog!!.updateText(p0.toString())

            // Make server request
            network.getPhonesByQuery(p0.toString(), object:RequestHandler {
                override fun onFailed(responseDto: ResponseDto) {
                    Log.d("FAIL", responseDto.data)
                }

                override fun onSuccessful(responseDto: ResponseDto) {
                    val phoneDataDtos: List<PhoneDataDTO> = Gson().fromJson(responseDto.data!!, object : TypeToken<List<PhoneDataDTO>>() {}.type)
                    phoneDialog!!.updateSuggestions(phoneDataDtos.map { it.toPhoneData() }.toList())
                }

            })
        }
    }

    override fun onBackPressed() {
        var toSuper = true
        if (firstPhone != null) {
            toSuper = firstPhone!!.onBackPressed()
        }
        if (secondPhone != null) {
            toSuper = secondPhone!!.onBackPressed() && toSuper
        }

        if (toSuper) super.onBackPressed()
    }

}
