package net.macdidi.atk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 覆寫OnMapReadyCallback的函式
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 刪除原來預設的內容
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // 建立位置的座標物件
        val place = LatLng(25.033408, 121.564099)

        // 移動地圖
        moveMap(place)

        // 加入地圖標記
        addMarker(place, "Hello!", " Google Maps v2!");
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

        mMap.addMarker(markerOptions)
    }

}
