package org.caojun.signin.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import cn.bmob.v3.datatype.BmobGeoPoint
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.amap.api.services.geocoder.GeocodeQuery
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import kotlinx.android.synthetic.main.activity_student.*
import org.caojun.areapicker.AreaPicker
import org.caojun.areapicker.OnPickerClickListener
import org.caojun.areapicker.PickerData
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.bmob.Admin
import org.caojun.signin.bmob.Student
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * 学生表信息编辑（管理员、学生）
 */
class StudentActivity : BaseActivity() {

    companion object {
        var student: Student? = null
        const val KEY_TEACHER_ID = "KEY_TEACHER_ID"
    }

    interface Listener {
        fun onGeocodeSearched(result: GeocodeResult)
    }

    private var adCode = ""
    private var areaCode = ""
    private var teacherId: String? = null

    private val textWatcherMobile = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s.isNullOrEmpty() || s.length != 11) {
                btnSave.isEnabled = false
            } else {
                btnSave.isEnabled = true
                checkChange()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s.isNullOrEmpty()) {
                btnSave.isEnabled = false
            } else {
                btnSave.isEnabled = true
                checkChange()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        teacherId = intent.getStringExtra(KEY_TEACHER_ID)
        if (student == null && teacherId.isNullOrEmpty()) {
            finish()
            return
        }

        AreaPicker.init(this, etSignArea, object : OnPickerClickListener {
            override fun onPickerClick(pickerData: PickerData) {
                onPicker(pickerData)
            }

            override fun onPickerConfirmClick(pickerData: PickerData) {
                onPicker(pickerData)
            }
        }, MainApplication.province)

        if (student != null) {
            etMobile.setText(student?.mobile)
            etMasterName.setText(student?.masterName)
            etMasterMobile.setText(student?.masterMobile)
            etSignName.setText(student?.signName)
            etSignArea.setText(student?.signArea)
            etSignAddress.setText(student?.signAddress)
            etCompanyName.setText(student?.companyName)
            etCompanyAddress.setText(student?.companyAddress)
            etName.setText(student?.name)
            btnDelete.isEnabled = true
        } else {
            etMobile.setText("")
            etMasterName.setText("")
            etMasterMobile.setText("")
            etSignName.setText("")
            etSignArea.setText("")
            etSignAddress.setText("")
            etCompanyName.setText("")
            etCompanyAddress.setText("")
            etName.setText("")
            btnDelete.isEnabled = false
        }
        btnDelete.isEnabled = MainApplication.role is Admin
        btnSave.isEnabled = false

        etMobile.addTextChangedListener(textWatcherMobile)
        etName.addTextChangedListener(textWatcher)
        etMasterMobile.addTextChangedListener(textWatcherMobile)
        etMasterName.addTextChangedListener(textWatcher)
        etSignName.addTextChangedListener(textWatcher)
        etSignArea.addTextChangedListener(textWatcher)
        etSignAddress.addTextChangedListener(textWatcher)
        etCompanyName.addTextChangedListener(textWatcher)
        etCompanyAddress.addTextChangedListener(textWatcher)

        btnDelete.setOnClickListener {
            doDelete()
        }

        btnSave.setOnClickListener {
            doSave()
        }
    }

    private fun doSave() {
        var isUpdate = true
        if (student == null) {
            isUpdate = false
            student = Student()

            student?.signAreaCode = areaCode
            student?.signAdCode = adCode
            if (!teacherId.isNullOrEmpty()) {
                student?.teacherId = teacherId!!
            }
        }
        student?.mobile = etMobile.text.toString()
        student?.name = etName.text.toString()
        student?.masterName = etMasterName.text.toString()
        student?.masterMobile = etMasterMobile.text.toString()
        student?.signName = etSignName.text.toString()
        student?.signArea = etSignArea.text.toString()
        student?.signAddress = etSignAddress.text.toString()
        student?.companyName = etCompanyName.text.toString()
        student?.companyAddress = etCompanyAddress.text.toString()


        doSaveStudent(isUpdate)
    }

    private fun doSaveStudent(isUpdate: Boolean) {
        doAsync {

            if (student == null) {
                return@doAsync
            }
            searchGEO(student!!, object : Listener {
                override fun onGeocodeSearched(result: GeocodeResult) {

                    val latlon = result.geocodeAddressList[0].latLonPoint
                    student?.geoPoint = BmobGeoPoint(latlon.longitude, latlon.latitude)

                    if (isUpdate) {
                        student?.update(object : UpdateListener() {
                            override fun done(e: BmobException?) {
                                doDone(e)
                            }
                        })
                    } else {
                        student?.save(object : SaveListener<String>() {
                            override fun done(o: String, e: BmobException?) {
                                doDone(e)
                            }
                        })
                    }
                }
            })
        }
    }

    private fun doDone(e: BmobException?) {
        if (e == null) {
            student = null
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            runOnUiThread {
                toast(getString(R.string.bmob_error, e))
            }
        }
    }

    private fun doDelete() {
        if (student == null) {
            return
        }
        student?.isDeleted = true
        doSaveStudent(true)
    }

    private fun checkChange() {
        if (!btnSave.isEnabled) {
            return
        }
        if (student == null) {

            if (etMobile.text.isNullOrEmpty() || etMobile.text?.length != 11) {
                btnSave.isEnabled = false
            }

            if (etMasterMobile.text.isNullOrEmpty() || etMobile.text?.length != 11) {
                btnSave.isEnabled = false
            }

            if (etName.text.isNullOrEmpty()
                || etMasterName.text.isNullOrEmpty()
                || etSignName.text.isNullOrEmpty()
                || etSignArea.text.isNullOrEmpty()
                || etSignAddress.text.isNullOrEmpty()
                || etCompanyName.text.isNullOrEmpty()
                || etCompanyAddress.text.isNullOrEmpty()
            ) {
                btnSave.isEnabled = false
            }

        } else {
            if (student?.name == etName.text.toString()
                && student?.mobile == etMobile.text.toString()
                && student?.masterName == etMasterName.text.toString()
                && student?.masterMobile == etMasterMobile.text.toString()
                && student?.signName == etSignName.text.toString()
                && student?.signArea == etSignArea.text.toString()
                && student?.signAddress == etSignAddress.text.toString()
                && student?.companyName == etCompanyName.text.toString()
                && student?.companyAddress == etCompanyAddress.text.toString()
            ) {
                btnSave.isEnabled = false
            }
        }
    }

    private fun onPicker(pickerData: PickerData) {
        etSignArea.setText(pickerData.selectText)
        adCode = pickerData.adCode?:""
        areaCode = pickerData.areaCode?:""
        AreaPicker.dismiss()
    }

    private fun searchGEO(student: Student, listener: Listener) {
        val geocodeSearch = GeocodeSearch(this)
        geocodeSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
            override fun onRegeocodeSearched(result: RegeocodeResult, rCode: Int) {

            }

            override fun onGeocodeSearched(result: GeocodeResult, rCode: Int) {

                listener.onGeocodeSearched(result)
            }
        })
        val query = GeocodeQuery(student.signAddress, student.signAdCode)
        geocodeSearch.getFromLocationNameAsyn(query)
    }
}