package com.cpsc411.dictatorgpt.ui.pics

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.cpsc411.dictatorgpt.R
import com.cpsc411.dictatorgpt.databinding.FragmentPicsBinding

class PicsFragment : Fragment() {

    private var _binding: FragmentPicsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPicsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val topImg: ImageView = root.findViewById(R.id.top_img)
        val mid1Img: ImageView = root.findViewById(R.id.mid_1)
        val mid2Img: ImageView = root.findViewById(R.id.mid_2)
        val mid3Img: ImageView = root.findViewById(R.id.mid_3)
        val mid4Img: ImageView = root.findViewById(R.id.mid_4)
        val mid5Img: ImageView = root.findViewById(R.id.mid_5)
        val mid6Img: ImageView = root.findViewById(R.id.mid_6)
        val bottomImg: ImageView = root.findViewById(R.id.bottom_img)
        val videoView: VideoView = root.findViewById(R.id.videoView)

        videoView!!.setVideoURI(Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.fish_video))
        videoView!!.start()

        topImg.setImageResource(R.drawable.bird_flu)
        mid1Img.setImageResource(R.drawable.college_tours)
        mid2Img.setImageResource(R.drawable.cat)
        mid3Img.setImageResource(R.drawable.clever)
        mid4Img.setImageResource(R.drawable.dj)
        mid5Img.setImageResource(R.drawable.chicken)
        mid6Img.setImageResource(R.drawable.dog_chicken)
        bottomImg.setImageResource(R.drawable.arabic_bug)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}