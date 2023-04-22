package com.example.music_media_player

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import java.lang.reflect.Field

class DetailSongActivity : AppCompatActivity() {

    lateinit var fab_play: FloatingActionButton
    lateinit var fab_forward: FloatingActionButton
    lateinit var fab_backward: FloatingActionButton
    lateinit var seekbar: SeekBar
    private var songIndex: Int = 0
    val handler = Handler(Looper.getMainLooper())
    lateinit var runnable: Runnable
    lateinit var currentSongName: TextView
    private val allTracks: Array<Field> = R.raw::class.java.fields
    private var mediaPlayer: MediaPlayer? = null
    private var songs = listOf(
        R.raw.aleluya,
        R.raw.candela,
        R.raw.jaleo,
        R.raw.nuestroamigo
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_song)

        songIndex = intent.extras?.getInt("songIndex") ?: 0
        fab_play = findViewById(R.id.fab_play)
        fab_forward = findViewById(R.id.fab_forward)
        fab_backward = findViewById(R.id.fab_backward)
        seekbar = findViewById(R.id.seekbar)
        currentSongName = findViewById(R.id.songname)

        createMediaPlayer()
        initializeSeekBar()

        val backBtn = findViewById<ImageView>(R.id.back_arrow)
        backBtn.setOnClickListener() {
            mediaPlayer?.stop()
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createMediaPlayer() {

        mediaPlayer = MediaPlayer.create(this, songs[songIndex])
        val fileName = allTracks[songIndex].name
        val metadataRetriever = MediaMetadataRetriever()
        val path = "android.resource://${this.packageName}/raw/${fileName}"
        val uri = Uri.parse(path)
        metadataRetriever.setDataSource(this, uri)
        val trackName =
            metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                .toString()
        currentSongName.text = trackName
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.start()
        mediaPlayer?.let { mp ->
            runnable = object : Runnable {
                override fun run() {
                    try {
                        seekbar.progress = mp.currentPosition
                        handler.postDelayed(this, 100)
                    } catch (e: Exception) {
                        Log.wtf("Exception", e.message)
                    }
                }
            }
        }
        controlSound()
    }

    private fun controlSound() {

        mediaPlayer?.let { mp ->
            fab_play.setOnClickListener {
                if (!mp.isPlaying) {
                    mp.start()
                    fab_play.setImageResource(R.drawable.ic_pause_circle)
                } else {
                    mp.pause()
                    fab_play.setImageResource(R.drawable.ic_play)
                }
            }

            fab_forward.setOnClickListener {
                if (songIndex == songs.size - 1) {
                    songIndex = 0
                } else {
                    songIndex++
                }
                stopSeekBar()
                destroyPlayer()
                createMediaPlayer()
                initializeSeekBar()
            }

            fab_backward.setOnClickListener {
                if (songIndex == 0) {
                    songIndex = songs.size - 1
                } else {
                    songIndex--
                }
                stopSeekBar()
                destroyPlayer()
                createMediaPlayer()
                initializeSeekBar()
            }
            seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mp.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }
    }

    private fun destroyPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let { mp ->
            mp.stop()
            mp.reset()
            mp.release()
        }
    }

    private fun initializeSeekBar() {

        mediaPlayer?.let { mp ->
            seekbar.max = mp.duration
            handler.postDelayed(runnable, 100)
        }
    }

    fun stopSeekBar() {
        handler.removeCallbacks(runnable)
    }
}
