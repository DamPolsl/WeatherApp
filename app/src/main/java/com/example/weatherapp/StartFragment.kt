package com.example.weatherapp

import android.graphics.drawable.GradientDrawable
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ToggleButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton

class StartFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var cityET: EditText
    private lateinit var searchButton: MaterialButton
    private lateinit var simpleToggleButton: ToggleButton
    private lateinit var searchBarLL: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBarLL = view.findViewById(R.id.ll_search_bar)

        simpleToggleButton = view.findViewById(R.id.tb_simple_toggle)
        simpleToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.simpleView.value = isChecked
            if(isChecked){
                searchButton.textSize = 24f
                cityET.textSize = 30f
                searchBarLL.orientation = LinearLayout.VERTICAL
            } else {
                searchButton.textSize = 16f
                cityET.textSize = 20f
                searchBarLL.orientation = LinearLayout.HORIZONTAL
            }
        }

        cityET = view.findViewById(R.id.et_city_input)
        searchButton = view.findViewById(R.id.btn_search)

        cityET.setOnClickListener {
            cityET.setText("")
        }
        cityET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                viewModel.loaded.value = false
                cityET.setText(cityET.text.toString().trim())
                viewModel.getWeather(requireContext(), cityET.text.toString())
                cityET.clearFocus()
                v.hideKeyboard()
                return@OnKeyListener false
            }
            false
        })

        searchButton.setOnClickListener {
            it.isEnabled = false
            viewModel.getWeather(requireContext(), cityET.text.toString())
            cityET.clearFocus()
            it.hideKeyboard()
        }

        viewModel.loaded.observe(viewLifecycleOwner){ loaded ->
            if(loaded){
                viewModel.simpleView.observe(viewLifecycleOwner){ isSimple ->
                    viewModel.loaded.value = false
                    if(isSimple){
                        view.findNavController().navigate(R.id.action_startFragment_to_simpleWeatherFragment)
                    } else {
                        view.findNavController().navigate(R.id.action_startFragment_to_normalWeatherFragment)
                    }
                }
            }
        }

        viewModel.failed.observe(viewLifecycleOwner){ failed ->
            if(failed){
                searchButton.isEnabled = true
            }
        }
    }
}