package com.example.music_media_player

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Field


class MenuActivity() : AppCompatActivity() {

    private val allTracks: Array<Field> = R.raw::class.java.fields

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        allTracks.first().name
        Log.wtf("PauloLogs", "onCreate: ")

        val inflater = LayoutInflater.from(applicationContext)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val name: String = intent.extras?.getString("EXTRA_NAME").orEmpty()
        tvResult.text = "$name Play your songs"

        val subtitleSongs = findViewById<TextView>(R.id.subtitleSongs)
        subtitleSongs.text = "SONGS"

        val backBtn = findViewById<ImageView>(R.id.back_arrow)
        backBtn.setOnClickListener() {
            val intent = Intent(this, OpenActivity::class.java)
            startActivity(intent)
        }

        val songsContainer = findViewById<LinearLayout>(R.id.song_container)
        allTracks.forEachIndexed { index, field ->
            val metadataRetriever = MediaMetadataRetriever()
            val path = "android.resource://${this.packageName}/raw/${field.name}"
            val uri = Uri.parse(path)
            metadataRetriever.setDataSource(this, uri)
            val trackName =
                metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    .toString()
            val songRow = inflater.inflate(R.layout.song_row, null, false)
            val songName = songRow.findViewById<TextView>(R.id.row_namesong)
            songName.text = trackName
            songRow.setOnClickListener {
                val intent = Intent(this, DetailSongActivity::class.java)
                intent.putExtra("songIndex", index)
                intent.putExtra("trackName", trackName)
                startActivity(intent)
            }
            songsContainer.addView(songRow)
        }
    }
}
