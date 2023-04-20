package com.example.music_media_player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception
import android.os.Handler
import android.os.Looper


class DetailSongActivity : AppCompatActivity() {

    lateinit var fab_play: FloatingActionButton
    lateinit var fab_pause: FloatingActionButton
    lateinit var fab_stop: FloatingActionButton
    lateinit var fab_forward: FloatingActionButton
    lateinit var fab_backward: FloatingActionButton
    lateinit var seekbar: SeekBar

    private var mediaPlayer: MediaPlayer? = null
    private var songs = listOf(
        R.raw.aleluya,
        R.raw.candela,
        R.raw.jaleo,
        R.raw.nuestroamigo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_song)

        val songIndex: Int = intent.extras?.getInt("songIndex") ?: 0

        fab_play = findViewById(R.id.fab_play)
        fab_pause = findViewById(R.id.fab_pause)
        fab_stop = findViewById(R.id.fab_stop)
        fab_forward = findViewById(R.id.fab_forward)
        fab_backward = findViewById(R.id.fab_backward)
        seekbar = findViewById(R.id.seekbar)

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, songs[songIndex])
            Log.d("DetailSongActivity", "ID: ${mediaPlayer!!.audioSessionId}")
            mediaPlayer?.setVolume(1.0f, 1.0f)
            initializeSeekBar()
        }
        controlSound()
    }


    private fun controlSound() {
        fab_play.setOnClickListener {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.pause()
                }else{
                    mp.start()
                    Log.d(
                        "DetailSongActivity",
                        "Duration: ${mp.duration / 1000} seconds")
                }
                fab_stop.setOnClickListener {
                        mp.stop()
                        mp.reset()
                        mp.release()
                }
//        fab_forward.setOnClickListener {
//            mediaPlayer!!.audioSessionId++
//            fab_play
//        }
//        fab_backward.setOnClickListener {
//            songs--
//        }
                seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) mediaPlayer?.seekTo(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }
        }
    }

    private fun initializeSeekBar() {
        seekbar.max = mediaPlayer!!.duration

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekbar.progress = mediaPlayer!!.currentPosition
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekbar.progress = 0
                }
            }
        }, 0)
    }
}
