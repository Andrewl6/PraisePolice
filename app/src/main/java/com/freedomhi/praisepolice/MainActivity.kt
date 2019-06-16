/*
 *
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.freedomhi.praisepolice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.freedomhi.praisepolice.camera.CameraSource
import com.freedomhi.praisepolice.camera.CameraSourcePreview
import com.freedomhi.praisepolice.utils.GraphicOverlay
import com.freedomhi.praisepolice.recognition.TextRecognitionProcessor
import com.freedomhi.praisepolice.recognition.TextRecognitionProcessor.OnTextDetectedListener
import java.io.IOException
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.manual_id_input.*

class MainActivity : AppCompatActivity(), OnTextDetectedListener {

    private var cameraSource: CameraSource? = null

    private lateinit var policeInfoAdapter: PoliceInfoAdapter
    private lateinit var policeParser: PoliceParser
    private var lastSeenPids = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupParser()
        setupRecyclerView()
        createCameraSource()
    }

    private fun setupParser() {
        this.policeParser = PoliceParser(this)
        this.policeParser.setup()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(dividerItemDecoration)
        recycler_view.isNestedScrollingEnabled = false

        this.policeInfoAdapter = PoliceInfoAdapter()
        recycler_view.adapter = this.policeInfoAdapter
    }

    private fun createCameraSource() {
        checkCameraPermission({
            if (this.cameraSource == null) {
                this.cameraSource = CameraSource(this, overlay)
                this.cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
            }

            val processor = TextRecognitionProcessor()
            processor.setListener(this)
            this.cameraSource?.setMachineLearningFrameProcessor(processor)
        })

        startCameraSource()
    }

    private fun startCameraSource() {
        val source = this.cameraSource ?: return

        try {
            camera_preview.start(source, overlay)
        } catch (e: IOException) {
            e.printStackTrace()
            this.cameraSource?.release()
            this.cameraSource = null
        }
    }

    private fun checkCameraPermission(callback: () -> Unit) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        callback()
                    }
                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        finish()
                    }
                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    override fun onNumberDetected(list: ArrayList<String>) {
        list.forEach { text ->
            if(!lastSeenPids.contains(text)) {
                val policeList = policeParser.search(text)
                policeList.forEach { policeInfoAdapter.addPoliceInfo(it) }
            }
        }

        lastSeenPids = list
    }
    fun onTypeNumber(view: View){
        policeNumberText.setText(policeNumberText.text.toString()+view.tag.toString())
    }
    fun onSearchNumber(view: View){
        val policeList = policeParser.search(policeNumberText.text.toString())
        policeList.forEach { policeInfoAdapter.addPoliceInfo(it)}
        policeNumberText.setText("")
    }
    fun onClear(view: View){
        policeNumberText.setText("")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==R.id.manualInput){
            if(manualInputInclude.visibility==View.GONE){
                manualInputInclude.visibility=View.VISIBLE
            }
            else{
                manualInputInclude.visibility=View.GONE
            }
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        camera_preview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
    }
}

