package com.eraysirdas.turkeymapskotlin.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.eraysirdas.turkeymapskotlin.utils.Constant
import com.eraysirdas.turkeymapskotlin.api.MapsAPI
import com.eraysirdas.turkeymapskotlin.model.MapsModel
import com.eraysirdas.turkeymapskotlin.api.repository.MapsRepository
import com.eraysirdas.turkeymapskotlin.R
import com.eraysirdas.turkeymapskotlin.api.RetrofitClient
import com.eraysirdas.turkeymapskotlin.model.response.SearchCityResponse
import com.eraysirdas.turkeymapskotlin.model.response.SearchDataByTypeResponse
import com.eraysirdas.turkeymapskotlin.model.request.SearchRequest
import com.eraysirdas.turkeymapskotlin.databinding.ActivityMainBinding
import com.eraysirdas.turkeymapskotlin.databinding.PopupMenuBinding
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var mapsModels: ArrayList<MapsModel>? = null
    private lateinit var webSettings: WebSettings
    private lateinit var mapsAPI : MapsAPI
    private lateinit var mapsRepository: MapsRepository
    private lateinit var compositeDisposable: CompositeDisposable
    private var isFilterPanelOpen = false;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // WebView ayarları
        webSettings = binding.webView.settings;
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        webSettings.useWideViewPort = true

        binding.webView.setInitialScale(100)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                injectJavaScript(view)
                applyRegionColors(view)
            }
        }

        binding.webView.addJavascriptInterface(WebAppInterface(this), "Android")
        binding.webView.loadUrl(Constant.svgUrl)

        loadData()
        setupFirstTypeSpinner()

    }

    private fun setupFirstTypeSpinner() {
        val type = arrayOf("Seçiniz", "Bitki", "Bal")
        val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, type)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPlantOrHoney.setAdapter(adapterType)
        setupFirstTypeSpinnerListener()
    }

    private fun setupFirstTypeSpinnerListener() {
        binding.spinnerPlantOrHoney.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Seçilen öğe üzerinde işlem yap
                if ("Bitki".equals(selectedItem, ignoreCase = true)) {
                    changeAllPathsColor("rgb(29, 186, 71)")
                    searchDataByType("bitki", "", "")
                } else if ("Bal".equals(selectedItem, ignoreCase = true)) {
                    changeAllPathsColor("rgb(211, 111, 0)")
                    searchDataByType("bal", "", "")
                    // onAllCitiesDataLoaded(allHoneyNames);
                } else {
                    binding.spinnerSearchType.setAdapter(null)
                    applyRegionColors(binding.webView)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun searchDataByType(isBalType: String, searchQuery: String, searchType: String) {
        val mapsAPI = RetrofitClient.getClient().create(MapsAPI::class.java)

        val request = SearchRequest(isBalType, searchQuery, searchType)

        val call = mapsAPI.searchDataByType(request)

        call.enqueue(object : Callback<List<SearchDataByTypeResponse>?> {
            override fun onResponse(call: Call<List<SearchDataByTypeResponse>?>, response: Response<List<SearchDataByTypeResponse>?>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()
                    setupSecondTypeSpinner(data)
                } else {
                    Log.e("MainActivity", "API isteği başarısız.")
                }
            }

            override fun onFailure(call: Call<List<SearchDataByTypeResponse>?>, t: Throwable) {
                Log.e("MainActivity", "API isteği başarısız: " + t.message)
            }
        })
    }

    private fun setupSecondTypeSpinner(data: List<SearchDataByTypeResponse>?) {
        val plantOrHoneyList = mutableListOf("Tür Seçin veya Arayın")

        data!!.forEach { item->
            item.name?.let { plantOrHoneyList.add(it) }
        }

        plantOrHoneyList.subList(1, data.size).sort()

        val adapterSearchType = ArrayAdapter(this, android.R.layout.simple_spinner_item, plantOrHoneyList)
        adapterSearchType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSearchType.setAdapter(adapterSearchType)
        setupSecondTypeSpinnerListener()
    }

    private fun setupSecondTypeSpinnerListener() {
        binding.spinnerSearchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (!"Tür Seçin veya Arayın".equals(selectedItem, ignoreCase = true)) {
                    val isBalType = if (binding.spinnerPlantOrHoney.getSelectedItem().toString().equals("Bitki", ignoreCase = true)) "bitki" else "bal"
                    val color = if (isBalType.equals("Bitki", ignoreCase = true)) "rgb(29, 186, 71)" else "rgb(211, 111, 0)"
                    changeAllPathsColor(color)
                    searchCity(isBalType, selectedItem, "")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun searchCity(isBalType: String, searchQuery: String, searchType: String) {
        val mapsAPI = RetrofitClient.getClient().create(MapsAPI::class.java)

        val request = SearchRequest(isBalType, searchQuery, searchType)

        val call = mapsAPI.searchCity(request)

        call.enqueue(object : Callback<List<SearchCityResponse>?> {
            override fun onResponse(call: Call<List<SearchCityResponse>?>, response: Response<List<SearchCityResponse>?>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()

                    val cities: MutableSet<String> = HashSet()
                    for (city in data!!) {
                        cities.add(city.name!!.toLowerCase())
                    }

                    val color = if (isBalType.equals("Bitki", ignoreCase = true)) "rgb(18, 121, 46)" else "rgb(151, 91, 0)"
                    highlightCitiesOnMap(cities, color)
                } else {
                    Log.e("MainActivity", "API isteği başarısız.")
                }
            }

            override fun onFailure(call: Call<List<SearchCityResponse>?>, t: Throwable) {
                Log.e("MainActivity", "API isteği başarısız: " + t.message)
            }
        })
    }

    private fun highlightCitiesOnMap(cities: MutableSet<String>, color: String) {


        // JavaScript kodu: Belirli şehirlerin path'lerini bul ve boya
        val jsCode = StringBuilder()
        jsCode.append("var paths = document.querySelectorAll('path');")
        jsCode.append("paths.forEach(function(path) {")
        jsCode.append("    var parentG = path.closest('g');")
        jsCode.append("    if (parentG) {")
        jsCode.append("        var cityName = parentG.getAttribute('id');")


        // Şehir adlarını JavaScript'e aktar
        jsCode.append("        var targetCities = [")
        for (city in cities) {
            jsCode.append("'").append(city).append("',")
        }
        jsCode.append("];")

        jsCode.append("        if (targetCities.includes(cityName)) {")
        jsCode.append("            path.setAttribute('fill', '").append(color).append("');")
        jsCode.append("        }")
        jsCode.append("    }")
        jsCode.append("});")


        // JavaScript'i WebView'de çalıştır
        binding.webView.evaluateJavascript(jsCode.toString(), null)
    }

    private fun changeAllPathsColor(color: String) {
        val jsCode = "var paths = document.querySelectorAll('path');" +
                "paths.forEach(function(path) {" +
                "    path.setAttribute('fill', '" + color + "');" +
                "});"
        binding.webView.evaluateJavascript(jsCode, null)
    }

    private fun loadData() {
        mapsAPI = RetrofitClient.getClient().create(MapsAPI::class.java)

        mapsRepository = MapsRepository(mapsAPI)
        compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            mapsRepository.getMapsData()
                .subscribe(this::loadDataHandleResponse))

    }

    private fun loadDataHandleResponse(mapsModelList: List<MapsModel>){
        mapsModels = ArrayList(mapsModelList)
    }


    private fun injectJavaScript(webView: WebView) {
        val jsCode = "document.addEventListener('click', function(event) {" +
                "    var target = event.target;" +
                "    var oldHighlight = document.querySelectorAll('.highlighted');" +
                "    oldHighlight.forEach(function(item) {" +
                "        item.classList.remove('highlighted');" +
                "        item.style.stroke = 'none';" +
                "    });" +
                "    if (target.tagName.toLowerCase() === 'path') {" +
                "        target.classList.add('highlighted');" +
                "        target.style.stroke = '#FF0000';" +
                "        target.style.strokeWidth = '3px';" +
                "        target.style.strokeDasharray = '5,5';" +
                "        target.style.pointerEvents = 'auto';" +
                "" +
                "        event.stopPropagation();" +
                "        event.preventDefault();" +
                "        var parentG = target.closest('g');" +
                "        if (parentG) {" +
                "            var id = parentG.getAttribute('id');" +
                "            Android.onPathClicked(id);" +
                "        }" +
                "    }" +
                "});"
        webView.evaluateJavascript(jsCode, null)

        val jsCode2 = "document.addEventListener('contextmenu', function(event) {" +
                "    event.preventDefault();" +
                "    var target = event.target;" +
                "    var oldHighlight = document.querySelectorAll('.highlighted');" +
                "    oldHighlight.forEach(function(item) {" +
                "        item.classList.remove('highlighted');" +
                "        item.style.stroke = 'none';" +
                "    });" +
                "    if (target.tagName.toLowerCase() === 'path') {" +
                "        target.classList.add('highlighted');" +
                "        target.style.stroke = '#FF0000';" +
                "        target.style.strokeWidth = '3px';" +
                "        target.style.strokeDasharray = '5,5';" +
                "        target.style.pointerEvents = 'auto';" +
                "" +
                "        var parentG = target.closest('g');" +
                "        if (parentG) {" +
                "            var id = parentG.getAttribute('id');" +
                "            var x = event.clientX;" +
                "            var y = event.clientY;" +
                "            Android.onLongClick(id, x, y);" +
                "        }" +
                "    }" +
                "});"
        webView.evaluateJavascript(jsCode2, null)
    }

    private fun applyRegionColors(webView: WebView) {
        val colorJsCode =
            "var regions = document.querySelectorAll('g[class]');" +  // class özelliği olan tüm <g> elementlerini seç
                    "regions.forEach(function(region) {" +
                    "    var className = region.getAttribute('class');" +  // class değerini al
                    "    var paths = region.querySelectorAll('path');" +  // <g> içindeki TÜM <path> elementlerini seç
                    "    paths.forEach(function(path) {" +  // Her bir <path> üzerinde döngü yap
                    "        switch (className) {" +
                    "            case 'akdeniz':" +
                    "                path.setAttribute('fill', '#71b1b1');" +
                    "                break;" +
                    "            case 'marmara':" +
                    "                path.setAttribute('fill', '#d01b1b');" +
                    "                break;" +
                    "            case 'ic-anadolu':" +
                    "                path.setAttribute('fill', '#8d8484');" +
                    "                break;" +
                    "            case 'ege':" +
                    "                path.setAttribute('fill', '#3498DB');" +
                    "                break;" +
                    "            case 'karadeniz':" +
                    "                path.setAttribute('fill', '#E79C2A');" +
                    "                break;" +
                    "            case 'guney-dogu-anadolu':" +
                    "                path.setAttribute('fill', '#9b3e00');" +
                    "                break;" +
                    "            case 'dogu-anadolu':" +
                    "                path.setAttribute('fill', '#706385');" +
                    "                break;" +
                    "            case 'kuzey-kibris':" +
                    "                path.setAttribute('fill', '#d01b1b');" +
                    "                break;" +
                    "            case 'guney-kibris':" +
                    "                path.setAttribute('fill', '#FFFFFFFF');" +
                    "                break;" +
                    "        }" +
                    "    });" +
                    "});"
        webView.evaluateJavascript(colorJsCode, null)
    }

    inner class WebAppInterface internal constructor(var mContext: Context) {
        @JavascriptInterface
        fun onPathClicked(id: String?) {
            if (mapsModels != null) {
                for (model in mapsModels!!) {
                    if (model.name.equals(id,ignoreCase = true)) { // id ve name eşleşmesi
                        goDetailsActivity(model)
                        return
                    }
                }
            }
        }


        @JavascriptInterface
        fun onLongClick(id: String?, x: Int, y: Int) {
            if (mapsModels != null) {
                for (model in mapsModels!!) {
                    if (model.name.equals(id,ignoreCase = true)) {
                        val info: String = (((model.name + "\n" +
                                model.districtCount).toString() + " ilçe\n" +
                                model.produceCount).toString() + " üretici\n" +
                                model.produceCount).toString() + " kovan"

                        //Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
                        showPopup(info, x, y)
                        return
                    }
                }
            }
        }
    }

    private fun goDetailsActivity(model: MapsModel) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
        intent.putExtra("model", model)
        startActivity(intent)
    }

    fun filterDeleteBtnClicked(view : View){
        binding.spinnerPlantOrHoney.setSelection(0)
        binding.spinnerSearchType.setSelection(0)
        binding.spinnerSearchType.setAdapter(null)
        applyRegionColors(binding.webView)
    }

    fun sideBarBtnClicked(view: View) {
        if (isFilterPanelOpen) {
            binding.filterPanel.animate()
                ?.translationX(binding.filterPanel.width.toFloat())
                ?.withEndAction(Runnable {
                    // Panel kapatılıyor
                    binding.filterPanel.visibility = View.GONE
                    binding.spinnerPlantOrHoney.visibility = View.INVISIBLE
                    binding.spinnerSearchType.visibility = View.INVISIBLE
                    binding.filterButton.setImageResource(R.drawable.baseline_arrow_back)
                })?.start()
        } else {
            // Panel açılıyor
            binding.filterPanel.animate()
                ?.translationX(0f)
                ?.withStartAction(Runnable {
                    binding.filterPanel.visibility = View.VISIBLE
                    binding.spinnerPlantOrHoney.visibility = View.VISIBLE
                    binding.spinnerSearchType.visibility = View.VISIBLE
                    binding.filterButton.setImageResource(R.drawable.baseline_arrow_forward)
                })?.start()
        }

        isFilterPanelOpen = !isFilterPanelOpen
    }

    private fun showPopup(info: String, x: Int, y: Int) {
        val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupBinding: PopupMenuBinding = PopupMenuBinding.inflate(inflater)

        popupBinding.popupText.text=info

        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val wbViewLocation = IntArray(2)
        binding.webView.getLocationOnScreen(wbViewLocation)
        val adjustedX = wbViewLocation[0] + x
        val adjustedY = wbViewLocation[1] + y

        popupWindow.showAtLocation(binding.webView, Gravity.NO_GRAVITY, adjustedX, adjustedY)
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}