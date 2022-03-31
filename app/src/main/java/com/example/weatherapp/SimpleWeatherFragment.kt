package com.example.weatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton

class SimpleWeatherFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private lateinit var temperatureTV: TextView
    private lateinit var pressureTV: TextView
    private lateinit var cityET: EditText
    private lateinit var shortDescTV: TextView
    private lateinit var dateTV: TextView
    private lateinit var timeTV: TextView
    private lateinit var sunriseTV: TextView
    private lateinit var sunsetTV: TextView
    private lateinit var weatherIV: ImageView

    private lateinit var searchIV: MaterialButton
    private lateinit var refreshIV: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return inflater.inflate(R.layout.fragment_simple_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loaded.value = false

        temperatureTV = view.findViewById(R.id.tv_temperature)
        pressureTV = view.findViewById(R.id.tv_pressure)
        cityET = view.findViewById(R.id.et_city_input)
        shortDescTV = view.findViewById(R.id.tv_short_desc)
        dateTV = view.findViewById(R.id.tv_date)
        timeTV = view.findViewById(R.id.tv_time)
        sunriseTV = view.findViewById(R.id.tv_sunrise)
        sunsetTV = view.findViewById(R.id.tv_sunset)
        weatherIV = view.findViewById(R.id.iv_weather)

        viewModel.temperature.observe(this.viewLifecycleOwner) {
            temperatureTV.text = it
        }
        viewModel.pressure.observe(this.viewLifecycleOwner) {
            pressureTV.text = it
        }
        cityET.setText(viewModel.name)
        viewModel.description.observe(this.viewLifecycleOwner) {
            shortDescTV.text = it
        }
        viewModel.date.observe(this.viewLifecycleOwner) {
            dateTV.text = it
        }
        viewModel.time.observe(this.viewLifecycleOwner) {
            timeTV.text = it
        }
        viewModel.sunrise.observe(this.viewLifecycleOwner) {
            sunriseTV.text = it
        }
        viewModel.sunset.observe(this.viewLifecycleOwner) {
            sunsetTV.text = it
        }
        viewModel.icon.observe(this.viewLifecycleOwner) {
            weatherIV.setImageBitmap(it)
        }

        refreshIV = view.findViewById(R.id.iv_refresh)
        searchIV = view.findViewById(R.id.iv_search)

        refreshIV.setOnClickListener {
            viewModel.getWeather(requireContext())
            Toast.makeText(requireContext(), "Refreshed!", Toast.LENGTH_SHORT).show()
            cityET.clearFocus()
            it.hideKeyboard()
        }
        cityET.setOnClickListener {
            cityET.setText("")
        }
        searchIV.setOnClickListener {
            cityET.setText(cityET.text.toString().trim())
            viewModel.name = cityET.text.toString()
            viewModel.getWeather(requireContext())
            cityET.clearFocus()
            it.hideKeyboard()
        }
    }
}