package com.app.krambook.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.krambook.R;
import com.app.krambook.activity.BookDetailActivity;
import com.app.krambook.activity.EnterBookInfoActivity;
import com.app.krambook.activity.MyProfileActivity;
import com.app.krambook.models.Favourteitem;
import com.app.krambook.utils.ImageLoaderGrid;
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import customfonts.MyTextView;
import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;
import info.vividcode.android.zxing.CaptureResult;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hp1 on 21-01-2015.
 */
public class MyListingFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    public static final String BASE_URl = "https://www.googleapis.com/books/v1/volumes?q=";
    View rootView;
    public List<Favourteitem> data = null;
    private HomeItemListAdapter adapter;
    private MyTextView item_posted_view;
    private ZXingScannerView zXingScannerView;
    public static final int PERM_REQ_CODE = 111;
    GridView gridview;
    List<ParseObject> ob;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        setRetainInstance(true);
        new RemoteDataTask().execute();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_mylisting,container,false);
        item_posted_view = (MyTextView)rootView.findViewById(R.id.item_posted_view);
        gridview = (GridView)rootView.findViewById(R.id.gridList);

        FloatingActionButton actionA = (FloatingActionButton) rootView.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*    PackageManager pm = getActivity().getApplicationContext().getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent captureIntent = new Intent(getActivity(), CaptureActivity.class);
                    CaptureActivityIntents.setPromptMessage(captureIntent, "Scanning for ISBN...");
                    startActivityForResult(captureIntent, 1);
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No camera detected. Please enter ISBN manually.", Toast.LENGTH_SHORT);
                    TextView txView = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (txView != null) txView.setGravity(Gravity.CENTER);
                    toast.show();
                }*/
                zXingScannerView=new ZXingScannerView(getActivity());
                zXingScannerView.setResultHandler(new ZXingScannerResultHandler());
                getActivity().setContentView(zXingScannerView);
                zXingScannerView.startCamera();

                int perm = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

                if (perm == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),  Manifest.permission.CAMERA)) {
                        Toast.makeText(getActivity(), "Give the damn permission", Toast.LENGTH_SHORT).show();
                    }

                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA }, PERM_REQ_CODE);
                }




            }

        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String isbn = "";

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                CaptureResult res = CaptureResult.parseResultIntent(data);
                isbn = res.getContents();
            }
        }

        //isbn += BASE_URl;
        Intent isbnIntent = new Intent(getActivity(), EnterBookInfoActivity.class);
        isbnIntent.putExtra("isbn", isbn);
        //isbnIntent.setAction("isbn");
        getActivity().startActivity(isbnIntent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);

    }*/

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler{

        @Override
        public void handleResult(Result result) {
            String resultcode = result.getText();
            Intent isbnIntent = new Intent(getActivity(), EnterBookInfoActivity.class);
            isbnIntent.putExtra("isbn", resultcode);
            //isbnIntent.setAction("isbn");
            getActivity().startActivity(isbnIntent);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);

            Log.v("ScannerApp", result.getText()); // Prints scan results
            Log.v("ScannerApp", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

            zXingScannerView.stopCamera();

        }
    }


    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            showProgressDialog();
            mProgressDialog.setIndeterminate(false);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            data = new ArrayList<Favourteitem>();

            try {
                ParseQuery<ParseObject> photosFromCurrentUserQuery = ParseQuery.getQuery("Photo");
                photosFromCurrentUserQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                photosFromCurrentUserQuery.whereExists("image");
                photosFromCurrentUserQuery.orderByDescending("updatedAt");
                ob = photosFromCurrentUserQuery.find();
                for (ParseObject country : ob) {
                    // Locate images in flag column
                    ParseFile image = (ParseFile) country.get("image");

                    final Favourteitem map = new Favourteitem();

                    map.setImageID(country.getObjectId());


                    map.setInmageurl(image.getUrl());


                    data.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml

            // Pass the results into ListViewAdapter.java
            adapter = new HomeItemListAdapter(getActivity(),
                    data);
            // Binds the Adapter to the ListView
            gridview.setAdapter(adapter);
            if(gridview.getCount() == 0){
                item_posted_view.setVisibility(View.VISIBLE);
            }
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    data.get(position).getImageID();
                    Intent zoom = new Intent(getActivity(), BookDetailActivity.class); //this is CommentActivity.class
                    zoom.putExtra("photoID", data.get(position).getImageID());
                    startActivity(zoom);

                    // Toast.makeText(HomeActivity.this,worldpopulationlist.get(position).getID(),Toast.LENGTH_SHORT).show();

//                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });




            gridview.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this photo?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
                                    query.getInBackground(data.get(position).getImageID(), new GetCallback<ParseObject>() {
                                                public void done(ParseObject country, ParseException e) {
                                                    if (e == null) {


                                                        try {
                                                            country.delete();
                                                            adapter.notifyDataSetChanged();

                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                        new RemoteDataTask().execute();
//

                                                    }
                                                }
                                            }
                                    );  // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();



                    return true;
                }
            });
            // Close the progressdialog
            hideProgressDialog();
        }
    }


    public class HomeItemListAdapter extends BaseAdapter {
        boolean flag = true;
        Context context;
        LayoutInflater inflater;
        ImageLoaderGrid imageLoader;
        private ParseFile image;
        private List<Favourteitem> worldpopulationlist = null;
        private ArrayList<Favourteitem> arraylist;

        /**
         * Constructor from a list of items
         */
        public HomeItemListAdapter(Context context, List<Favourteitem> worldpopulationlist) {

            this.context = context;
            this.worldpopulationlist = worldpopulationlist;
            inflater = LayoutInflater.from(context);
            this.arraylist = new ArrayList<Favourteitem>();
            this.arraylist.addAll(worldpopulationlist);
            imageLoader = MyProfileActivity.imageLoader;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.favourits_item_layout, null);

                holder.productimageview = (ImageView) view.findViewById(R.id.product_portfolio_imageView);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


//            imageLoader.DisplayImage(worldpopulationlist.get(position).getInmageurl(), holder.productimageview);
            Glide.with(context).load(worldpopulationlist.get(position).getInmageurl()).into(holder.productimageview);



            // Restore the checked state properly
            final GridView lv = (GridView) parent;
//        holder.layout.setChecked(lv.isItemChecked(position));

            return view;
        }

        @Override
        public int getCount() {
            return worldpopulationlist.size();
        }

        @Override
        public Object getItem(int position) {
            return worldpopulationlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {

            public ImageView productimageview;

        }
    }



    private void showProgressDialog() {
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }
}