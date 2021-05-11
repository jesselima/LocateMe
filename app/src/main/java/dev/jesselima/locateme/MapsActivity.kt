package dev.jesselima.locateme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

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
        val markerLabelSaoPaulo = "E aí mano!!! É nóis na fita!"
        map.addMarker(MarkerOptions().position(latLngSaoPaulo).title(markerLabelSaoPaulo))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSaoPaulo, 12f))

        setMapLongClick(map = map)
        setPoiClick(map = map)
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
            val markerLabelSaoPaulo = "E aí mano!!! É nóis na fita!"
            map.addMarker(MarkerOptions().position(latLngSaoPaulo).title(markerLabelSaoPaulo))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSaoPaulo, 12f))

            map.addMarker(MarkerOptions().position(LatLng(-23.5978056,-46.648792)).title("Here! -23.5978056,-46.648792"))
            map.addMarker(MarkerOptions().position(LatLng(-23.558632,-46.6742075)).title("Here! -23.558632,-46.6742075"))
            true
        }
        R.id.rio_de_janeiro_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            val latLngRioDeJaneiro = LatLng(-22.9519133,-43.210471)
            val markerLabelSaoPaulo = "E aí maluco!!! Coé?!"
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
}