package com.dxn.recorder

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dxn.recorder.databinding.FragmentRecordBinding
import java.text.SimpleDateFormat
import java.util.*

class RecordFragment : Fragment() {

    private var isRecording = false
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private val RECORD_PERMISSION = android.Manifest.permission.RECORD_AUDIO
    private val REQUEST_CODE = 1
    private var recordFile = "filename.3gp"
    private var recordPath=""

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        recordPath = activity?.getExternalFilesDir("/")?.absolutePath.toString()

        binding.listAudioBtn.setOnClickListener {
            if (isRecording) {
                val alertDialog = context?.let { it1 -> AlertDialog.Builder(it1) }
                alertDialog?.setPositiveButton("Yes") { _, _ ->
                    isRecording = false
                    navController.navigate(R.id.action_recordFragment_to_listAudioFragment)
                }
                alertDialog?.setNegativeButton("No") { _, _ -> }
                alertDialog?.setTitle("Audio Still Recording")
                alertDialog?.setMessage("Do you want to cancel recording")
                alertDialog?.create()
                alertDialog?.show()
            } else {
                navController.navigate(R.id.action_recordFragment_to_listAudioFragment)

            }
        }

        binding.recordBtn.setOnClickListener {
            if (checkPermission()) {
                isRecording = if (isRecording) {
                    stopRecording()
                    binding.recordBtn.setImageResource(R.drawable.record_btn_stopped)
                    false
                } else {
                    startRecording()
                    binding.recordBtn.setImageResource(R.drawable.record_btn_recording)
                    true
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startRecording() {
        mediaRecorder = MediaRecorder()

        val formatter = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH)
        val date = Date()
        binding.recordTimer.base = SystemClock.elapsedRealtime()
        binding.recordTimer.start()
        recordFile = "REC_${formatter.format(date)}.3gp"
        binding.textView.text = "Recording audio:\n${recordFile}"
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setOutputFile("${recordPath}/${recordFile}")
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
    }

    @SuppressLint("SetTextI18n")
    private fun stopRecording() {
        binding.recordTimer.base = SystemClock.elapsedRealtime()
        binding.recordTimer.stop()
        Toast.makeText(context,"Saved at $recordPath",Toast.LENGTH_LONG).show()
        binding.textView.text = getString(R.string.rec_hint)
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun checkPermission(): Boolean {
        return if (context?.let { it1 ->
                ActivityCompat.checkSelfPermission(
                    it1,
                    RECORD_PERMISSION
                )
            } == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            activity?.let { it1 ->
                ActivityCompat.requestPermissions(
                    it1,
                    arrayOf(RECORD_PERMISSION), REQUEST_CODE
                )
            }
            false
        }
    }

    override fun onStop() {
        super.onStop()
        if(isRecording) {
            stopRecording()
        }
    }
}