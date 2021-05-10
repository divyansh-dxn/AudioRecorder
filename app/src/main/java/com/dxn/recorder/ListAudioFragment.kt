package com.dxn.recorder

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dxn.recorder.databinding.FragmentListAudioBinding
import com.dxn.recorder.databinding.PlayerBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File


class ListAudioFragment : Fragment() {

    private var _binding: FragmentListAudioBinding? = null
    private val binding get() = _binding!!
    private var _binding_: PlayerBottomSheetBinding? = null
    private var fileToPlay: File? = null
    private val sheetBinding get() = _binding_!!
    private var isPlaying = false
    private val TAG = "ListAudioFragment"
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var PATH: String
    private lateinit var allfiles: Array<File>
    private var mediaPlayer: MediaPlayer? = null
    private var seekHandler: Handler? = null
    private lateinit var updateSeekBar: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListAudioBinding.inflate(inflater, container, false)
        _binding_ = binding.bottomSheet
        val view = binding.root

        PATH = activity?.getExternalFilesDir("/")?.absolutePath.toString()
        val dir = File(PATH)
        allfiles = dir.listFiles()

        bottomSheetBehavior =
            BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet))
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.audioList.adapter = AudioListAdapter(allfiles) { file: File, position: Int ->
            Log.e(TAG, "Clicked on item  ${file.name} at position $position")
            fileToPlay = file
            if (isPlaying) {
                stopPlaying()
                startPlaying(fileToPlay!!)
            } else {
                startPlaying(fileToPlay!!)
            }
        }
        binding.audioList.layoutManager = LinearLayoutManager(context)

        sheetBinding.playPauseBtn.setOnClickListener {
            if (isPlaying) {
                pausePlaying()
            } else {
                if (fileToPlay != null && mediaPlayer != null) {
                    resumePlaying()
                }
            }
        }

        sheetBinding.playerSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pausePlaying()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    mediaPlayer?.seekTo(seekBar.progress)
                }
                resumePlaying()
            }

        })
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun stopPlaying() {
        sheetBinding.playerTitle.text = "Stopped"
        sheetBinding.playPauseBtn.setImageResource(R.drawable.player_play_btn)
        Log.e(TAG, "Stopped playing")
        mediaPlayer?.stop()
        isPlaying = false
        seekHandler?.removeCallbacks(updateSeekBar)
    }

    @SuppressLint("SetTextI18n")
    private fun startPlaying(file: File) {
        sheetBinding.playerTitle.text = "Playing"
        sheetBinding.playingFileName.text = file.name
        sheetBinding.playPauseBtn.setImageResource(R.drawable.player_pause_btn)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        Log.e(TAG, "Playing file ${file.absolutePath}")
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(file.absolutePath)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
        isPlaying = true
        mediaPlayer!!.setOnCompletionListener {
            stopPlaying()
            sheetBinding.playerTitle.text = "Finished"
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        sheetBinding.playerSeekbar.max = mediaPlayer!!.duration
        seekHandler = Handler()
        updateSeekBar = object : Runnable {
            override fun run() {
                sheetBinding.playerSeekbar.progress = mediaPlayer!!.currentPosition
                seekHandler!!.postDelayed(this, 500)
            }
        }
        seekHandler!!.postDelayed(updateSeekBar, 0)
    }

    private fun pausePlaying() {
        sheetBinding.playPauseBtn.setImageResource(R.drawable.player_play_btn)
        mediaPlayer?.pause()
        isPlaying = false
        seekHandler?.removeCallbacks(updateSeekBar)
    }

    private fun resumePlaying() {
        sheetBinding.playPauseBtn.setImageResource(R.drawable.player_pause_btn)
        mediaPlayer?.start()
        isPlaying = true
    }

    override fun onStop() {
        super.onStop()
        stopPlaying()
    }

}