package com.eraysirdas.turkeymapskotlin.api

import com.eraysirdas.turkeymapskotlin.model.MapsModel
import com.eraysirdas.turkeymapskotlin.model.response.SearchCityResponse
import com.eraysirdas.turkeymapskotlin.model.response.SearchDataByTypeResponse
import com.eraysirdas.turkeymapskotlin.model.request.SearchRequest
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MapsAPI {
    @GET("city-information")
    fun getData(): Observable<List<MapsModel>>

    @GET("city-detail/{cityName}")
    fun getDetailData(@Path("cityName") cityName: String?): Observable<MapsModel>

    @POST("search-data-by-type")
    fun searchDataByType(@Body searchRequest: SearchRequest?): Call<List<SearchDataByTypeResponse>>

    @POST("search-city")
    fun searchCity(@Body searchRequest: SearchRequest?): Call<List<SearchCityResponse>>
}