package com.thekami.kamiplay.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.thekami.kamiplay.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.textVersion).text = "KamiPlay v1.1"

        view.findViewById<View>(R.id.btnDiscord).setOnClickListener {
            openUrl("https://www.thekami.tech/discord/")
        }
        view.findViewById<View>(R.id.btnWebsite).setOnClickListener {
            openUrl("https://www.thekami.tech")
        }
        view.findViewById<View>(R.id.btnBlog).setOnClickListener {
            openUrl("https://blog.thekami.tech")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
