package org.caojun.signin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.adapter.NameItem
import org.caojun.signin.bmob.Admin
import org.caojun.signin.bmob.BmobUtils
import org.caojun.signin.bmob.Student
import org.caojun.signin.bmob.Teacher
import org.caojun.signin.utils.GDMapUtils
import org.caojun.utils.ActivityUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.support.v7.app.AlertDialog
import org.caojun.signin.R
import org.jetbrains.anko.startActivity

/**
 * 高德地图
 */
class MapActivity : BaseActivity(), LocationSource, AMapLocationListener, AMap.OnMarkerClickListener {

    private var mLocationChangedListener: LocationSource.OnLocationChangedListener? = null
    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    private val hmMarkerSite = HashMap<Marker, Student>()

    private var amapLocation: AMapLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fab.setOnClickListener { view ->
            when (MainApplication.role) {
                is Student -> {
                    doPerson(false)
                }
                is Teacher -> {
                    doPerson(true)
                }
                is Admin -> {
                    doAdmin()
                }
            }
        }

        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION, object : ActivityUtils.RequestPermissionListener {
            override fun onSuccess() {
                mapView.onCreate(savedInstanceState)
                initialize()
            }

            override fun onFail() {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            refreshData()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun refreshData() {
        doAsync {
            val list = BmobUtils.students
            val aMap = mapView.map
            aMap.clear()
            GDMapUtils.clear()
            hmMarkerSite.clear()
            for (i in list.indices) {
                addMarkerToMap(aMap, list[i])
            }
            uiThread {
                listView.adapter = object : CommonAdapter<Student>(list, 1) {
                    override fun createItem(type: Any?): AdapterItem<*> {
                        return NameItem()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        refreshData()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        GDMapUtils.onDestroy()
        amapLocation = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun deactivate() {
        mLocationChangedListener = null
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
        mLocationClient = null
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        mLocationChangedListener = listener
        if (mLocationClient == null) {
            mLocationClient = AMapLocationClient(this)
            mLocationOption = AMapLocationClientOption()
            // 设置定位监听
            mLocationClient?.setLocationListener(this)
            // 设置为高精度定位模式
            mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption?.isOnceLocation = (true)
            // 设置定位参数
            mLocationClient?.setLocationOption(mLocationOption)
            mLocationClient?.startLocation()
        }
    }

    override fun onLocationChanged(amapLocation: AMapLocation) {
        mLocationChangedListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
        MainApplication.province = amapLocation.province
        this.amapLocation = amapLocation
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.isInfoWindowShown) {
            val site = hmMarkerSite[marker]!!
            alert {
                title = site.name
                messageResource = R.string.modify_call
                positiveButton(R.string.call) {
                    call(site.mobile)
                }
                negativeButton(R.string.modify) {
                    //TODO
//                    startActivityForResult<AddressActivity>(RequestCode_Address, AddressActivity.Key_Site to site, AddressActivity.Key_Province to province)
                }
            }.show()
        } else {
            marker.showInfoWindow()
        }
        return true
    }

    private fun addMarkerToMap(aMap: AMap, student: Student) {

        val geoPoint = student.geoPoint
        val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)

        val markerOption = MarkerOptions().icon(
            BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .position(latLng)
            .title(student.name)
            .snippet(student.mobile + "\n" + student.signAddress)
            .draggable(true)
        val marker = aMap.addMarker(markerOption)
        marker?.showInfoWindow()

        GDMapUtils.setHMSiteMarker(student.objectId, marker)

        hmMarkerSite[marker] = student
    }

    private fun initialize() {
        val aMap = mapView.map
        aMap.uiSettings.isRotateGesturesEnabled = true//旋转手势
        aMap.moveCamera(CameraUpdateFactory.zoomBy(6f))
        aMap.setLocationSource(this)// 设置定位监听
        // 自定义系统定位蓝点
        val myLocationStyle = MyLocationStyle()
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0))
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0f)
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0))
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.myLocationStyle = myLocationStyle
        aMap.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.uiSettings.isMyLocationButtonEnabled = true// 定位按钮
        aMap.uiSettings.isTiltGesturesEnabled = true//倾斜
        aMap.uiSettings.isCompassEnabled = true//指南针
        aMap.uiSettings.isScaleControlsEnabled = true//比例尺
        aMap.setOnMarkerClickListener(this)

        GDMapUtils.setAMap(aMap)
    }

    private fun doAdmin() {
        startActivity<TeacherStudentListActivity>()
    }

    private fun doPerson(isTeacher: Boolean) {
        val items = this.resources.getStringArray(R.array.person_main)
        val listDialog = AlertDialog.Builder(this@MapActivity)
        listDialog.setItems(items) { dialog, which ->
            when (which) {
                0 -> {
                    //个人信息
                    if (isTeacher) {
                        TeacherActivity.teacher = MainApplication.role as Teacher
                        startActivity<TeacherActivity>()
                    } else {
                        StudentActivity.student = MainApplication.role as Student
                        startActivity<StudentActivity>()
                    }
                }
                1 -> {
                    //签到列表
                    if (isTeacher) {
                        startActivity<TeacherSignListActivity>()
                    } else {
                        startActivity<StudentSignListActivity>()
                    }
                }
            }
        }
        listDialog.show()
    }
}