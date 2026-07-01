package com.thekami.kamiplay.share

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.local.MusicScanner
import com.thekami.kamiplay.data.model.Song
import java.io.File

class ShareActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private var selectedSong: Song? = null
    private var connectionClient: ConnectionsClient? = null
    private var endpointId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        statusText = findViewById(R.id.statusText)
        connectionClient = Nearby.getConnectionsClient(this)

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            checkPermissionsAndSend()
        }
        findViewById<Button>(R.id.btnReceive).setOnClickListener {
            startReceive()
        }
    }

    private fun checkPermissionsAndSend() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO), 100)
            return
        }
        val songs = MusicScanner.getAllSongs(this)
        if (songs.isEmpty()) {
            Toast.makeText(this, "No songs to share", Toast.LENGTH_SHORT).show()
            return
        }
        selectedSong = songs.first()
        statusText.text = "Advertising for receivers..."
        startAdvertising()
    }

    private fun startAdvertising() {
        connectionClient?.startAdvertising(
            "KamiPlayShare", "com.thekami.kamiplay",
            object : ConnectionLifecycleCallback() {
                override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                    connectionClient?.acceptConnection(endpointId, payloadCallback)
                }
                override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                    if (result.status.isSuccess) {
                        this@ShareActivity.endpointId = endpointId
                        statusText.text = "Connected, sending file..."
                        sendFile()
                    } else {
                        statusText.text = "Connection failed"
                    }
                }
                override fun onDisconnected(endpointId: String) {
                    statusText.text = "Disconnected"
                }
            }, AdvertisingOptions(Strategy.P2P_STAR))
            .addOnSuccessListener {
                statusText.text = "Advertising... waiting for receiver"
            }
            .addOnFailureListener {
                statusText.text = "Advertising failed"
            }
    }

    private fun startReceive() {
        connectionClient?.startDiscovery("com.thekami.kamiplay",
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    connectionClient?.requestConnection("KamiPlayRecv", endpointId,
                        object : ConnectionLifecycleCallback() {
                            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                                connectionClient?.acceptConnection(endpointId, payloadCallback)
                            }
                            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                                if (result.status.isSuccess) {
                                    this@ShareActivity.endpointId = endpointId
                                    statusText.text = "Connected, waiting for file..."
                                }
                            }
                            override fun onDisconnected(endpointId: String) {
                                statusText.text = "Disconnected"
                            }
                        })
                }
                override fun onEndpointLost(endpointId: String) {}
            }, DiscoveryOptions(Strategy.P2P_STAR))
            .addOnSuccessListener {
                statusText.text = "Discovering senders..."
            }
    }

    private fun sendFile() {
        val song = selectedSong ?: return
        val file = File(song.path)
        if (!file.exists()) {
            statusText.text = "File not found"
            return
        }
        val payload = Payload.fromFile(file)
        connectionClient?.sendPayload(endpointId!!, payload)
        statusText.text = "Sending ${song.title}..."
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.FILE) {
                val receivedFile = payload.asFile()?.asJavaFile()
                receivedFile?.let {
                    val dest = File(getExternalFilesDir(null), "shared_${it.name}")
                    it.copyTo(dest, overwrite = true)
                    statusText.text = "File received: ${dest.absolutePath}"
                }
            }
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    override fun onDestroy() {
        connectionClient?.stopAllEndpoints()
        super.onDestroy()
    }
}
