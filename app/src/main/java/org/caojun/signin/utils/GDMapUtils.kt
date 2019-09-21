package org.caojun.signin.utils

import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import org.caojun.signin.bmob.Sign
import org.caojun.signin.bmob.Student

object GDMapUtils {

    private var aMap: AMap? = null
    private var hmSiteMarker = HashMap<String, Marker>()

    fun onDestroy() {
        aMap = null
        clear()
    }

    fun clear() {
        hmSiteMarker.clear()
    }

    fun setAMap(aMap: AMap) {
        GDMapUtils.aMap = aMap
    }

    fun setHMSiteMarker(objectId: String, marker: Marker) {
        hmSiteMarker[objectId] = marker
    }

    fun moveMap(student: Student) {
        val zoom = aMap?.cameraPosition?.zoom?:18f
        val tilt = aMap?.cameraPosition?.tilt?:30f
        val bearing = aMap?.cameraPosition?.bearing?:0f
        val geoPoint = student.geoPoint
        val cameraPosition = CameraPosition(LatLng(geoPoint.latitude, geoPoint.longitude), zoom, tilt, bearing)
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        aMap?.moveCamera(cameraUpdate)
        val marker = hmSiteMarker[student.objectId]
        marker?.showInfoWindow()
    }

    fun distance(start: LatLng, end: LatLng): Float {
        return AMapUtils.calculateLineDistance(start, end)
    }
}