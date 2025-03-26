package com.eraysirdas.turkeymapskotlin.api.repository

import com.eraysirdas.turkeymapskotlin.api.MapsAPI
import com.eraysirdas.turkeymapskotlin.model.MapsModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MapsRepository(private val mapsAPI: MapsAPI) {

    fun getMapsData(): Observable<List<MapsModel>> {
        return mapsAPI.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}