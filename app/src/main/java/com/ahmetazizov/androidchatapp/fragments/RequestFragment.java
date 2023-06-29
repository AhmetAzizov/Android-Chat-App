package com.ahmetazizov.androidchatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.Request;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.RequestAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RequestFragment extends Fragment {

    RecyclerView recyclerView;
    RequestAdapter requestAdapter;
    FirebaseFirestore db;
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false);
    }


    public RequestAdapter getRequestAdapter() {
        return requestAdapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        db = FirebaseFirestore.getInstance();
        swipeContainer = view.findViewById(R.id.swipeContainer);

        swipeListener();

        requestAdapter = new RequestAdapter(requireContext(), Constants.requests);
        recyclerView.setAdapter(requestAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }






    private void swipeListener() {

        swipeContainer.setOnRefreshListener(() -> {
            Constants.requests.clear();

            getRequests();

            swipeContainer.setRefreshing(false);
        });
    }


    private void getRequests() {
        final CollectionReference requestsRef = db.collection("users").document(Constants.currentUserName).collection("requests");

        Constants.requests.clear();

        requestsRef.orderBy("requestTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {

                                Request request = document.toObject(Request.class);

                                Constants.requests.add(request);
                            }
                        }

                        requestAdapter.notifyDataSetChanged();
                    }
                });
    }
}