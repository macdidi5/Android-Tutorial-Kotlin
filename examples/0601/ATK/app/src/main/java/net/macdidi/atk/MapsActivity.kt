package net.macdidi.atk

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private lateinit var mMap: GoogleMap

    // Google API用戶端物件
    private lateinit var googleApiClient : GoogleApiClient
    // Location請求物件
    private lateinit var locationRequest : LocationRequest
    // 記錄目前最新的位置
    private lateinit var currentLocation : Location
    // 顯示目前位置的標記物件
    private var currentMarker : Marker? = null
    // 顯示儲存位置的標記物件
    private lateinit var itemMarker : Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 建立Google API用戶端物件
        configGoogleApiClient()

        // 建立Location請求物件
        configLocationRequest()

        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    // 覆寫OnMapReadyCallback的函式
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 讀取記事儲存的座標
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)

        // 如果記事已經儲存座標
        if (lat != 0.0 && lng != 0.0) {
            // 建立座標物件
            val itemPlace = LatLng(lat, lng)
            // 加入地圖標記
            addMarker(itemPlace, intent.getStringExtra("title"),
                    intent.getStringExtra("datetime"))
            // 移動地圖
            moveMap(itemPlace)
        }

        processController()
    }

    override fun onResume() {
        super.onResume()

        // 連線到Google API用戶端
        if (!googleApiClient.isConnected && currentMarker != null) {
            googleApiClient.connect()
        }
    }

    override fun onPause() {
        super.onPause()

        // 移除位置請求服務
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this)
        }
    }

    override fun onStop() {
        super.onStop()

        // 移除Google API用戶端連線
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    // 移動地圖到參數指定的位置
    private fun moveMap(place: LatLng) {
        // 建立地圖攝影機的位置物件
        val cameraPosition = CameraPosition.Builder()
                .target(place)
                .zoom(17f)
                .build()

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // 在地圖加入指定位置與標題的標記
    private fun addMarker(place: LatLng, title: String, context: String) {
        val icon : BitmapDescriptor =
                BitmapDescriptorFactory.fromResource(R.drawable.atk_launcher)

        val markerOptions = MarkerOptions()
        markerOptions.position(place)
                .title(title)
                .snippet(context)
                .icon(icon)

        // 加入並設定記事儲存的位置標記
        itemMarker = mMap.addMarker(markerOptions)
    }

    // ConnectionCallbacks
    override fun onConnected(bundle: Bundle?) {
        // 已經連線到Google Services
        // 取得授權狀態，參數是請求授權的名稱
        val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)

        // 如果已經授權
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            // 啟動位置更新服務
            // 位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }
    }

    // ConnectionCallbacks
    override fun onConnectionSuspended(i: Int) {
        // Google Services連線中斷
        // int參數是連線中斷的代號
    }

    // OnConnectionFailedListener
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        val errorCode = connectionResult.errorCode

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, R.string.google_play_service_missing,
                    Toast.LENGTH_LONG).show()
        }
    }

    // LocationListener
    override fun onLocationChanged(location: Location) {
        // 位置改變
        // Location參數是目前的位置
        currentLocation = location
        val latLng = LatLng(
                location.latitude, location.longitude)

        // 設定目前位置的標記
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(MarkerOptions().position(latLng))
        } else {
            currentMarker?.setPosition(latLng)
        }

        // 移動地圖到目前的位置
        moveMap(latLng)
    }

    // 建立Google API用戶端物件
    @Synchronized private fun configGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    // 建立Location請求物件
    private fun configLocationRequest() {
        locationRequest = LocationRequest()
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.interval = 1000
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.fastestInterval = 1000
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun processController() {
        // 對話框按鈕事件
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
            // 更新位置資訊
                DialogInterface.BUTTON_POSITIVE ->
                    // 連線到Google API用戶端
                    if (!googleApiClient.isConnected) {
                        googleApiClient.connect()
                    }
            // 清除位置資訊
                DialogInterface.BUTTON_NEUTRAL -> {
                    val result = Intent()
                    result.putExtra("lat", 0)
                    result.putExtra("lng", 0)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                }
            // 取消
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }

        // 標記訊息框點擊事件
        mMap.setOnInfoWindowClickListener { marker ->
            // 如果是記事儲存的標記
            if (marker.equals(itemMarker)) {
                val ab = AlertDialog.Builder(this@MapsActivity)

                ab.setTitle(R.string.title_update_location)
                        .setMessage(R.string.message_update_location)
                        .setCancelable(true)

                ab.setPositiveButton(R.string.update, listener)
                ab.setNeutralButton(R.string.clear, listener)
                ab.setNegativeButton(android.R.string.cancel, listener)

                ab.show()
            }
        }

        // 標記點擊事件
        mMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            // 如果是目前位置標記
            if (marker.equals(currentMarker)) {
                val ab = AlertDialog.Builder(this@MapsActivity)

                ab.setTitle(R.string.title_current_location)
                        .setMessage(R.string.message_current_location)
                        .setCancelable(true)

                ab.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    val result = Intent()
                    result.putExtra("lat", currentLocation.latitude)
                    result.putExtra("lng", currentLocation.longitude)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                })
                ab.setNegativeButton(android.R.string.cancel, null)

                ab.show()

                return@OnMarkerClickListener true
            }

            false
        })
    }

}
