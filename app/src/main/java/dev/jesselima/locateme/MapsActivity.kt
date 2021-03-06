package dev.jesselima.locateme

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.Locale

private const val REQUEST_LOCATION_PERMISSION = 1

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val latLngSaoPaulo = LatLng(-23.583517,-46.6547916)
        val markerLabelSaoPaulo = "E a?? mano!!! ?? n??is na fita!"
        map.addMarker(MarkerOptions().position(latLngSaoPaulo).title(markerLabelSaoPaulo))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSaoPaulo, 15f))

        setMapLongClick(map = map)
        setPoiClick(map = map)
        enableMyLocation()

       addGroundOverlay(map = map, LatLng(-23.59051, -46.65943), R.drawable.jetpack_logo)
       addGroundOverlay(map = map, LatLng(-23.58464, -46.65878), R.drawable.jetpack_logo)
       addGroundOverlay(map = map, LatLng(-23.58739, -46.64970), R.drawable.jetpack_logo)
    }

    private fun addGroundOverlay(
        map: GoogleMap,
        latLng: LatLng,
        resourceId: Int,
        overLaySize: Float = 200f
    ) {
        with(map) {
            addGroundOverlay(
                GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(resourceId))
                    .position(latLng, overLaySize)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_types, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.sao_paulo_map -> {
            val latLngSaoPaulo = LatLng(-23.583517,-46.6547916)
            val markerLabelSaoPaulo = "E a?? mano!!! ?? n??is na fita!"
            map.addMarker(MarkerOptions().position(latLngSaoPaulo).title(markerLabelSaoPaulo))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSaoPaulo, 12f))

            map.addMarker(MarkerOptions().position(LatLng(-23.5978056,-46.648792)).title("Here! -23.5978056,-46.648792"))
            map.addMarker(MarkerOptions().position(LatLng(-23.558632,-46.6742075)).title("Here! -23.558632,-46.6742075"))
            true
        }
        R.id.rio_de_janeiro_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            val latLngRioDeJaneiro = LatLng(-22.9519133,-43.210471)
            val markerLabelSaoPaulo = "E a?? maluco!!! Co???!"
            map.addMarker(MarkerOptions().position(latLngRioDeJaneiro).title(markerLabelSaoPaulo))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRioDeJaneiro, 18f))
            true
        }
        R.id.map_local_style_dark -> {
            setLocalMapStyle(map = map, resourceStyle = R.raw.map_local_style_dark)
            true
        }
        R.id.map_local_style_night-> {
            setLocalMapStyle(map = map, resourceStyle = R.raw.map_local_style_night)
            true
        }
        R.id.map_local_style_night_highway_highlighted -> {
            setLocalMapStyle(map = map, resourceStyle = R.raw.map_local_style_night_highway_highlighted)
            true
        }
        R.id.map_local_style_standard_highway_highlighted -> {
            setLocalMapStyle(map = map, resourceStyle = R.raw.map_local_style_standard_highway_highlighted)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener {

            val snippet = String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                it.latitude,
                it.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener {
            val poiMarker = map.addMarker(
                MarkerOptions()
                .position(it.latLng)
                .title(it.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private fun setLocalMapStyle(map: GoogleMap, resourceStyle: Int) {
        runCatching {
            val isMapStyleValid = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, resourceStyle))
            Log.d(MapsActivity::class.java.simpleName, "Map Style is valid: $isMapStyleValid")
        }.onFailure {
            Log.d(MapsActivity::class.java.simpleName, "Map Style could not be loaded")
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults.first() == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}