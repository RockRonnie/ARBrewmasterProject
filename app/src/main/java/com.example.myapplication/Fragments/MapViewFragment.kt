import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapViewFragment: Fragment(), PermissionsListener, LocationEngineListener {

    companion object {

        fun newInstance(): MapViewFragment {
            return MapViewFragment()
        }
    }

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap

    //Starting point and destination variables
    private lateinit var originLocation: Location
    private lateinit var originPosition: com.mapbox.geojson.Point
    private lateinit var destinationPosition: com.mapbox.geojson.Point
    private lateinit var permissionManager: PermissionsManager

    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin : LocationLayerPlugin? = null
    private var destinationMarker: Marker? = null
    private var navigationMapRoute: NavigationMapRoute? = null


    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = activity as Context
        return inflater.inflate(R.layout.mapview_fragment,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(ctx, getString(R.string.access_token))

        //Setup the MapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            map = mapboxMap
            map.addOnMapClickListener { point ->
                destinationMarker?.let{
                    map.removeMarker(it)
                }
                destinationMarker = map.addMarker(MarkerOptions().position(point))
                destinationPosition = Point.fromLngLat(point.longitude,point.latitude)
                originPosition = Point.fromLngLat(originLocation.longitude, originLocation.latitude)
                getRoute(originPosition, destinationPosition)
            }
            enableLocation()
        }
    }


    fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(context)){
            initializeLocationEngine()
            initializeLocationLayer()
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(activity)
        }
    }

    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation
        if (lastLocation != null){
            originLocation = lastLocation
            setCameraPosition(lastLocation)
        } else {
            locationEngine?.addLocationEngineListener(this)
        }
    }

    private fun initializeLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView, map, locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL
    }

    private fun setCameraPosition(location: Location){
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),13.0))
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted){
            enableLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            originLocation = location
            setCameraPosition(location)
        }
    }

    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder()
            .accessToken(Mapbox.getAccessToken())
            .profile("walking")
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object: Callback<DirectionsResponse> {
                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    val routeResponse = response ?: return
                    val body = routeResponse.body() ?: return
                    if (body.routes().count() == 0){
                        Log.e("shiet", "No route found")
                        return
                    }
                    if (navigationMapRoute != null){
                        navigationMapRoute?.removeRoute()
                    } else {
                        navigationMapRoute = NavigationMapRoute(null, mapView, map)
                    }
                    navigationMapRoute?.addRoute(body.routes().first())
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("shiet", "Error: ${t.message}")
                }
            })
    }

    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(context)){

        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null){
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}