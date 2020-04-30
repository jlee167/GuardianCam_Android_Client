package com.example.guardiancamera_wifi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.MalformedURLException;
import java.util.ArrayList;



/**
 *  Class: PeerData
 *
 *  Contains information of peers.
 *  A user is allowed to acquire profile picture, name, and streaming server address of a friend.
 *  Peer's current emergency status is also included.
 *
 */

class PeerData {
    ImageView profilePicture;   // Peer's profile picture
    String name;                // Peer's name
    String serverAddress;       // Peer's streaming Address
    String status;              // Peer's emergency status


    public PeerData(ImageView profilePictureIn, String nameIn, String serverAddressIn, String statusIn) {
        this.profilePicture = profilePictureIn;
        this.status = statusIn;
        this.serverAddress =serverAddressIn;
        this.name = nameIn;
    };
}


/**
 *  Adapter for the recyclerview displaying list of peers.
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.mViewHolder> {

    // Dataset Array containing peer info objects
    ArrayList<PeerData> dataset;


    public RecyclerViewAdapter() {
        dataset = new ArrayList<PeerData>();
    }


    public RecyclerViewAdapter(ArrayList<PeerData> inputDataset) {
       dataset = new ArrayList<PeerData>();
       dataset.addAll(inputDataset);
    }


    public void addItem(PeerData dataIn) {

        dataset.add(dataIn);
    }


    @Override
    public int getItemCount() {
        return dataset.size();
    }


    /**
     *  View Holder format for recyclerview
     *  Consists of profile picture, personal information, and streaming address
     */
    static class mViewHolder extends  RecyclerView.ViewHolder {

        ImageView profilePicture;   // Peer's profile picture
        TextView name;              // Peer's name
        TextView serverAddress;     // Peer's streaming Address
        TextView status;            // Peer's emergency status

        mViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            serverAddress = itemView.findViewById(R.id.serverAddress);
        }
    }


    /**
     * Generate a view holder for recyclerview
     *
     * @param parent    -   View that will be used for each list
     * @param viewType  -
     * @return
     *      Viewholder with a view item generated from 'recycleritem' layout
     */
    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem, parent, false);
        return new mViewHolder(listItem);
    }

    /**
     * Change recyclerview's item specified by position parameter
     *
     * @param holder    - view holder for recyclerview
     * @param position  - list position of item to be changed
     */
    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.profilePicture = dataset.get(position).profilePicture;
        holder.name.setText(dataset.get(position).name);
        holder.serverAddress.setText(dataset.get(position).serverAddress);
        holder.status.setText(dataset.get(position).status);
    }
}


public class PeerListActivity extends AppCompatActivity {

    // RecyclerView variables
    RecyclerView peerList;
    RecyclerView.Adapter peerListAdapter;
    LinearLayoutManager layoutManager;

    // AuthHandler for fetching peer list
    LazywebAuthHandler authHandler;

    private void refreshPeerList() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_list);

        /**
         *  Setting Recycler View containing the list of information on the user's peers.
         */
        peerList = findViewById(R.id.peerList);
        peerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        peerList.setLayoutManager(layoutManager);

        peerListAdapter = new RecyclerViewAdapter();
        peerList.setAdapter(peerListAdapter);

        try {
            authHandler = new LazywebAuthHandler (this, getIntent());
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {

        super.onResume();
    }


    @Override
    protected void onRestart() {

        super.onRestart();
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
