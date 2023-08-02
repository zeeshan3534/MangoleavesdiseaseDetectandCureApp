package com.ice.mangosurveyour;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class home extends Fragment {

    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Button btn_form,logout;
    TextView curr_user;
    RecyclerView.Adapter adapter;
    RecyclerView featuredRecycler;

//    FirebaseAuth myauth;
    private LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationListener locationListener;
    private static final int request_code = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//
        // Inflate the layout for this fragment

//        return inflater.inflate(R.layout.fragment_home, container, false);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        featuredRecycler = root.findViewById(R.id.features);
        curr_user = root.findViewById(R.id.textView4);
        btn_form = root.findViewById(R.id.btnForm);
        logout = root.findViewById(R.id.logout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        BarChart barChart = root.findViewById(R.id.bar_chart);
        RelativeLayout layoutre = root.findViewById(R.id.relative);
        ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Signout();
            }
        });
        btn_form.setOnClickListener(view ->{
            startActivity(new Intent(getActivity(), form_activity.class));
        });

//        Animation Start
        Animation animation_bottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
        Animation animation_up = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
//        Animation work


        animation_bottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Reset the layout position to the top of the screen
                constraintLayout.setTranslationY(0);
//                barChart.setTranslationY(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }


        });

        animation_bottom.setDuration(1000);


        animation_bottom.setStartOffset(500);
        constraintLayout.startAnimation(animation_bottom);
//        mango.startAnimation(animation_bottom);


//        Fade in animation

        animation_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Reset the layout position to the top of the screen
                layoutre.setTranslationY(0);
                barChart.setTranslationY(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }


        });

        animation_up.setDuration(1000);


        animation_up.setStartOffset(500);
        layoutre.startAnimation(animation_up);

        barChart.startAnimation(animation_up);

//        Animation End


        //        Location work
        TextView locationTextView = root.findViewById(R.id.textView2);

//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//
//            fusedLocationProviderClient.requestLocationUpdates(LocationRequest.create()
//                    .setInterval(1000)
//                    .setFastestInterval(500)
//                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY), new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    Location location = locationResult.getLastLocation();
//                    if (location != null) {
//                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//                        try {
//                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                            locationTextView.setText(addressList.get(0).getAddressLine(0));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }, null);
//
//        }
//        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted
            System.out.println("Location permission not granted.");
        } else {

            // Permission granted
            System.out.println("Location permission granted.");
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null) {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        System.out.println("location null nh arahyi");
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (!addressList.isEmpty()) {
                                Address address = addressList.get(0);
                                String addressText = String.format("%s, %s, %s",
                                        address.getAddressLine(0),
                                        address.getLocality(),
                                        address.getCountryName());
                                System.out.println("Location address: " + addressText);
                                locationTextView.setText(addressText);
                            } else {
                                System.out.println("No location address found.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Location is null.");
                    }
                }
            });
        }

//

        featuredRecycler();
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 25f));
        entries.add(new BarEntry(2f, 50f));
        entries.add(new BarEntry(3f, 75f));
        BarDataSet dataSet = new BarDataSet(entries, "Bar Chart");
        int[] colors = {Color.parseColor("#9DB288"), Color.parseColor("#B1CBA6"), Color.parseColor("#D9DAA9")};
        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);
        barChart.setData(data);
        barChart.invalidate();


        dataSet = new BarDataSet(entries, "Bar Chart");

        return root;




    }

    private void Signout() {
        Intent intent = new Intent(getContext(),startingscreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));


        ArrayList<featureHelper> featureHelpers = new ArrayList<>();

        featureHelpers.add(new featureHelper(R.drawable.anthracnose,"Anthracnose","Mango Anthracnose is a fungal infection caused by the fungus Colletotrichum gloeosporioides and is presently recognized as the most destructive field and post-harvest disease of mango worldwide."));
        featureHelpers.add(new featureHelper(R.drawable.apoderus_javanicus,"Apoderus Javanicus","The Apoderus Javanicus, also known as the mango seed beetle, can cause damage to mango trees by feeding on the fruit and seeds of the tree, which can lead to reduced yield and fruit quality."));
        featureHelpers.add(new featureHelper(R.drawable.bacterial_canker,"Bacterial Canker","Bacterial canker in mango occurs due to Bacterium which causes Angular, water-soaked spots on leaves which coalesce and turn black"));
        featureHelpers.add(new featureHelper(R.drawable.dappula_tertia,"Dappula Tertia","Dappula is a genus of beetles. If a mango tree is infested by Dappula Tertia, the larvae of the beetles will bore into the wood of the tree, causing damage to the tree's stem and branches."));
        featureHelpers.add(new featureHelper(R.drawable.dialeuropora_decempuncta,"Dialeuropora Decempuncta","Dialeurodes is a genus of whitefly. They feed on the sap of plants and can cause yellowing and wilting of the leaves, as well as the production of honeydew which can lead to the growth of sooty mold."));
        featureHelpers.add(new featureHelper(R.drawable.gall_midge,"Gall Midge","The larvae of the gall midge feed on the shoots and fruit of the tree, causing the formation of galls or swellings on the tree's branches and fruit. This can lead to reduced yield and fruit quality, and in severe cases.    "));
        featureHelpers.add(new featureHelper(R.drawable.black_soothy_mold,"Black Soothy Mold","Mango sooty mold (Meliola mangiferae) is one of the species of fungi that grow on honeydew results from interactions among sap-feeding insects such as soft scale (wax, green and cottony cushion scales), mealybugs, aphids,"));
        featureHelpers.add(new featureHelper(R.drawable.icerya_seychellarum,"Icerya Seychellarum","Icerya Seychellarum is a polyphagous phloem-feeding coccid. This insect feeds on the underside of leaves."));
        featureHelpers.add(new featureHelper(R.drawable.mictis_longicornis,"Mictis Longicornis","Mictis longicornis, also known as the mango stem borer, is a type of insect that infests mango trees and can cause significant damage to the tree's stem and branches. "));
        featureHelpers.add(new featureHelper(R.drawable.neomelicharia_sparsa,"Neomelicharia Sparsa","Neomelicharia sparsa is a fungal disease that affects mango trees. The symptoms include small, dark spots on the leaves and fruit, as well as premature defoliation."));

        adapter = new FeatureAdapter(featureHelpers);
        featuredRecycler.setAdapter(adapter);


    }

//    private void featuredRecycler() {
//    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        System.out.println(userRef);
        if (user == null) {
            startActivity(new Intent(getActivity(), login.class));
        }

        if (user != null) {

            System.out.println(userRef);

            userRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method will be called whenever the data at the "messages" node changes
                    if (dataSnapshot.exists()){

                        String user = dataSnapshot.child("username").getValue().toString();


                        curr_user.setText(user);

                    }



                }



                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("MainActivity", "Failed to read value.", error.toException());
                }
            });
        }


    }
}