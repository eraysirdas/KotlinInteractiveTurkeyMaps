package com.eraysirdas.turkeymapskotlin.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.eraysirdas.turkeymapskotlin.api.MapsAPI
import com.eraysirdas.turkeymapskotlin.model.MapsModel
import com.eraysirdas.turkeymapskotlin.adapter.PlantRecyclerAdapter
import com.eraysirdas.turkeymapskotlin.api.RetrofitClient
import com.eraysirdas.turkeymapskotlin.databinding.FragmentPlantBinding
import com.eraysirdas.turkeymapskotlin.model.CityPlantModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PlantFragment : Fragment() {
    private var _binding: FragmentPlantBinding? = null
    private val binding get() = _binding!!
    private var cityName : String? = null
    private var isDataLoaded = false
    private lateinit var plantArrayList: ArrayList<CityPlantModel>
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var mapsAPI: MapsAPI
    private lateinit var mapsArrayList : ArrayList<MapsModel>
    private lateinit var plantAdapter: PlantRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plantArrayList = ArrayList()
        mapsArrayList = ArrayList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlantBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!=null){
            cityName = requireArguments().getString("cityName")
        }

        if (!isDataLoaded) {
            showPlaceholder()
            loadData()
        } else {
            binding.plantRecyclerView.visibility = View.VISIBLE
        }

        plantAdapter = PlantRecyclerAdapter(plantArrayList)
        binding.plantRecyclerView.layoutManager= GridLayoutManager(requireContext(),2)
        binding.plantRecyclerView.adapter=plantAdapter
    }

    private fun loadData() {
        mapsAPI = RetrofitClient.getClient().create(MapsAPI::class.java)

        compositeDisposable = CompositeDisposable()

        cityName?.let {
            compositeDisposable.add(mapsAPI.getDetailData(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)

            )
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun handleResponse(mapsModel: MapsModel) {
        mapsArrayList.clear()
        mapsArrayList.add(mapsModel)
        plantArrayList.clear()


        for (model in mapsArrayList) {
            val cityPlantModels: List<CityPlantModel>? = model.cityPlants
            if (!cityPlantModels.isNullOrEmpty()) {
                plantArrayList.addAll(cityPlantModels)
                plantAdapter.notifyDataSetChanged()

                isDataLoaded = true
                stopPlaceHolder()

                for (plantModel in cityPlantModels) {
                    System.out.println("Bitki Adı: " + plantModel.plantName)
                }
            } else {
                println("CityPlants listesi boş veya null.")
            }
        }
    }

    private fun stopPlaceHolder() {
        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility = View.GONE
        binding.plantRecyclerView.visibility = View.VISIBLE
    }

    private fun showPlaceholder() {
        binding.shimmerView.startShimmer()
        binding.shimmerView.visibility = View.VISIBLE
        binding.plantRecyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear() // Bellek sızıntısını önleyin
    }

}