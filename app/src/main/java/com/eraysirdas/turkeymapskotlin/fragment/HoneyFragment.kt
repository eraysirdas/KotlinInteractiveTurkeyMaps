package com.eraysirdas.turkeymapskotlin.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eraysirdas.turkeymapskotlin.adapter.HoneyRecyclerAdapter
import com.eraysirdas.turkeymapskotlin.api.MapsAPI
import com.eraysirdas.turkeymapskotlin.model.MapsModel
import com.eraysirdas.turkeymapskotlin.api.RetrofitClient
import com.eraysirdas.turkeymapskotlin.databinding.FragmentHoneyBinding
import com.eraysirdas.turkeymapskotlin.model.CityHoneyModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class HoneyFragment : Fragment() {
    private var _binding: FragmentHoneyBinding? = null
    private val binding get() = _binding!!
    private var cityName : String? = null
    private var isDataLoaded = false
    private lateinit var honeyArrayList: ArrayList<CityHoneyModel>
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var mapsAPI: MapsAPI
    private lateinit var mapsArrayList : ArrayList<MapsModel>
    private lateinit var honeyAdapter: HoneyRecyclerAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        honeyArrayList = ArrayList()
        mapsArrayList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHoneyBinding.inflate(inflater, container, false)
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
            binding.honeyRecyclerView.visibility = View.VISIBLE
        }

        honeyAdapter = HoneyRecyclerAdapter(honeyArrayList)
        binding.honeyRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.honeyRecyclerView.adapter=honeyAdapter
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
        honeyArrayList.clear()


        for (model in mapsArrayList) {
            val cityHoneyModels: List<CityHoneyModel>? = model.cityHoney
            if (!cityHoneyModels.isNullOrEmpty()) {
                honeyArrayList.addAll(cityHoneyModels)
                honeyAdapter.notifyDataSetChanged()

                isDataLoaded = true
                stopPlaceHolder()

                for (honeyModel in cityHoneyModels) {
                    System.out.println("Bitki Adı: " + honeyModel.honeyVerietyName)
                }
            } else {
                println("CityPlants listesi boş veya null.")
            }
        }
    }

    private fun showPlaceholder() {
        binding.shimmerView.startShimmer()
        binding.shimmerView.visibility = View.VISIBLE
        binding.honeyRecyclerView.visibility = View.GONE
    }


    private fun stopPlaceHolder() {

        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility = View.GONE
        binding.honeyRecyclerView.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }


}