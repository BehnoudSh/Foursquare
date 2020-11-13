package ir.behnoudsh.aroundme.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.behnoudsh.aroundme.R
import ir.behnoudsh.aroundme.data.model.LocationModel
import ir.behnoudsh.aroundme.ui.adapter.PlacesAdapter
import ir.behnoudsh.aroundme.ui.viewmodels.PlacesViewModel
import ir.behnoudsh.aroundme.utilities.GpsUtils
import kotlinx.android.synthetic.main.activity_places.*

class PlacesActivity : AppCompatActivity() {
    private lateinit var placesViewModel: PlacesViewModel
    private var isGPSEnabled = false
    val placesAdapter = PlacesAdapter(this, ArrayList())
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)
        placesViewModel = ViewModelProviders.of(this).get(PlacesViewModel::class.java)
        registerObservers()
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@PlacesActivity.isGPSEnabled = isGPSEnable
            }
        })
        initRecyclerView()

        btn_loadmore.setOnClickListener() {
            placesViewModel.loadMore()
        }
    }

    fun initRecyclerView() {
        rv_placesList.layoutManager = LinearLayoutManager(this)
        rv_placesList.adapter = placesAdapter
        initScrollListener()
    }

    var isLoading = false

    private fun initScrollListener() {
        rv_placesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == rv_placesList.adapter!!.itemCount - 1
                    ) {
                        placesViewModel.loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    fun registerObservers() {
        placesViewModel.newLocationLiveData.observe(this, {
            placesAdapter.placesList.clear()
            placesAdapter.notifyDataSetChanged()
        })

        placesViewModel.allPlacesSuccessLiveData.observe(this, {
            for (item in it)
                placesAdapter.placesList.add(item)
            placesAdapter.notifyDataSetChanged()
        })

        placesViewModel.allPlacesFailureLiveData.observe(this, {
            pb_loading.visibility = GONE
            if (it)
                btn_loadmore.visibility = VISIBLE
            else
                btn_loadmore.visibility = GONE
        })

        placesViewModel.loadingLiveData?.observe(this, {
            pb_loading.visibility = VISIBLE
        })
    }

    private fun startLocationUpdate() {
        placesViewModel.getLocationData().observe(this, Observer {
//            latlong.text = getString(R.string.latLong, it.longitude, it.latitude)
            placesViewModel.locationChanged(LocationModel(it.longitude, it.latitude));

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> latlong.text = getString(R.string.enable_gps)

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> latlong.text =
                getString(R.string.permission_request)

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        }
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }
}


const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101