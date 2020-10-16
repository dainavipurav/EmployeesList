package com.example.employeeslist.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.employeeslist.R;
import com.example.employeeslist.adapters.EmployeesListAdapter;
import com.example.employeeslist.models.EmployeesDetailsModel;
import com.example.employeeslist.models.ResponseModel;
import com.example.employeeslist.network.APIClient;
import com.example.employeeslist.network.APIInterface;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.getSystemServiceName;


public class EmployeesListFragment extends Fragment {

    ConstraintLayout mConstraintLayoutEmployeesList;
    RecyclerView mRecyclerViewEmployeesList;
    TextView mTextViewEmployeesListUserCurrentLocation;
    List<EmployeesDetailsModel> mEmployeesDetailsModelList;

    EmployeesListAdapter mEmployeesListAdapter;
    APIInterface mAPIInterface;
    ProgressDialog progressDialog;
    ResponseModel mResponseModel;
    Geocoder mGeoCoder;

    List<Address> addresses;

    private Integer REQUEST_CODE_LOCATION_PERMISSION = 1;

    private ConnectivityManager mConnectivityManager;
    private Boolean isConnected;


    public EmployeesListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employees_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mConnectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        mConstraintLayoutEmployeesList = view.findViewById(R.id.constraintLayoutEmployeesList);
        mRecyclerViewEmployeesList = view.findViewById(R.id.recyclerViewEmployeesList);
        mTextViewEmployeesListUserCurrentLocation = view.findViewById(R.id.textViewEmployeesListUserCurrentLocation);

        mGeoCoder = new Geocoder(getContext(), Locale.getDefault());

        isConnected=isNetWorkConnected();

        if (isConnected){
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            getLocation();
            getAPIData();
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(
                    getContext(),
                    "Connection Error : Please Connect to the internet.",
                    Toast.LENGTH_SHORT
            ).show();
        }




    }

    private Boolean isNetWorkConnected(){
        if (
                mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
                        mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void getAPIData() {
        mEmployeesDetailsModelList = new ArrayList<>();
        mAPIInterface = APIClient.getAPI_Client().create(APIInterface.class);

        mAPIInterface.getResponse().enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                progressDialog.dismiss();
                mResponseModel = response.body();
                if (mResponseModel != null) {
                    if (mResponseModel.getStatus().equals("success")) {
                        mEmployeesDetailsModelList = mResponseModel.getData();
                        mEmployeesListAdapter = new EmployeesListAdapter(mEmployeesDetailsModelList, getContext());
                        mRecyclerViewEmployeesList.setAdapter(mEmployeesListAdapter);
                    } else {
                        Toast.makeText(getActivity(), "No Data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No Data found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        addresses = new ArrayList<>();


        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int mLatestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(mLatestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(mLatestLocationIndex).getLongitude();

                            try {
                                addresses = mGeoCoder.getFromLocation(latitude, longitude, 1);
                                String myAdd = addresses.get(0).getAddressLine(0);
                                String area = addresses.get(0).getLocality();
                                String city = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String fullAddress = myAdd + ", " + area + ", " + city + ", " + country + ", " + postalCode;

                                mTextViewEmployeesListUserCurrentLocation.setText(getString(
                                        R.string.my_current_location,
                                        fullAddress
                                ));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, Looper.getMainLooper());

    }


}