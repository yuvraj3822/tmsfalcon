package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tmsfalcon.device.tmsfalcon.R


/**
 * A simple [Fragment] subclass.
 * Use the [HolisticViewOfDamage.newInstance] factory method to
 * create an instance of this fragment.
 */
class HolisticViewOfDamage : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_holistic_view_of_damage, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HolisticViewOfDamage.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic fun newInstance(param1: String, param2: String) =
//                HolisticViewOfDamage().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
//                    }
//                }
//    }




    // newInstance constructor for creating fragment with arguments
    companion object {
        @JvmStatic fun newInstance(page: Int, title: String?): HolisticViewOfDamage? {
            val fragmentFirst = HolisticViewOfDamage()
            return fragmentFirst
        }
    }



    // Store instance variables based on arguments passed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_holistic_view_of_damage, container, false)

        return view
    }

}