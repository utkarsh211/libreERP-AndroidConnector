package com.example.yadav.IM;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.libreerp.Helper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.example.yadav.IM.ChatRoomActivity.cacheCopy;
import static com.example.yadav.IM.R.drawable.bg_bubble_gray;


public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private int userId;
    private int SELF = 100;
    private static String today;
    private ImageView card ;
   
    private boolean margin = false;
    private Context mContext;
    private ArrayList<Message> messageArrayList;
    private static final int CAMERA_REQUEST = 1 ;
    private Helper helper;
    private static final int PLACE_PICKER_REQUEST = 1000;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView  message , timestamp;
        private ImageView location ;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.time);
            location = (ImageView) itemView.findViewById(R.id.image_location);
        }
    }


    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList , int userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        View itemView;
        TextView message ;

        ImageView attachment ;
        TextView timestamp ;
        final int position = viewType ;
       
        final String type =  messageType(position);
        Boolean self = isSelf(position);
        Boolean margin = giveMargin(position); 
        
        if (type.equals("MSG")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item, parent, false);
            itemView.setVisibility(View.GONE);
            if(!messageArrayList.get(position).getMessage().equals("")) {
                itemView.setVisibility(View.VISIBLE);
                RelativeLayout relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
                // view type is to identify where to render the chat message
                // left or right
                message = (TextView) itemView.findViewById(R.id.message);
                timestamp = (TextView) itemView.findViewById(R.id.time);

                if (self) {
                    // self message
                    if (margin == true) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        int sizeInDP = 40;

                        int marginInDp = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, mContext.getResources().getDisplayMetrics());
                        params.setMargins(params.leftMargin, marginInDp, params.rightMargin, params.bottomMargin);
                        // relativeLayout.setLayoutParams(params);
                        margin = false;
                    }

                    ((RelativeLayout) itemView).setGravity(Gravity.RIGHT);
                    message.setBackgroundResource(R.drawable.bg_bubble_gray);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) message.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                    message.setLayoutParams(params);

                    RelativeLayout.LayoutParams params_time = (RelativeLayout.LayoutParams) timestamp.getLayoutParams();
                    params_time.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                    timestamp.setLayoutParams(params_time);
                    // we can change margina and align parent right by adding in params


                } else {
                    // others message
                    if (margin == true) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        int sizeInDP = 40;

                        int marginInDp = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, mContext.getResources().getDisplayMetrics());
                        params.setMargins(params.leftMargin, marginInDp, params.rightMargin, params.bottomMargin);
                        // relativeLayout.setLayoutParams(params);
                        margin = false;
                    }
                    //itemView = LayoutInflater.from(parent.getContext())
                    //      .inflate(R.layout.chat_item, parent, false);
                    // timestamp.setGravity(Gravity.LEFT);
                }
            }

        }
        else { // given message is card message
            ;
            if (type.equals("GPS")) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_location, parent, false);
                // view type is to identify where to render the chat message
                // left or right
                card = (ImageView) itemView.findViewById(R.id.image_location);

                card.setImageResource(R.drawable.ic_location_on_black_24dp);
            }
            else if (type.equals("IMG")){

                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_location, parent, false);
                // view type is to identify where to render the chat message
                // left or right
                card = (ImageView) itemView.findViewById(R.id.image_location);
                card.setImageResource(R.drawable.ic_burst_mode_black_24dp);
                saveFileFromUrl(messageArrayList.get(position).getAttachment());


            }
            else{ // attachment is file
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_files, parent, false);
                // view type is to identify where to render the chat message
                // left or right
                card = (ImageView) itemView.findViewById(R.id.image_location);
                if (type.equals("PDF")){ // file is pdf
                    card.setImageResource(R.drawable.ic_picture_as_pdf_black_24dp);
                    saveFileFromUrl(messageArrayList.get(position).getAttachment());
                }
                else {
                    card.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                    saveFileFromUrl(messageArrayList.get(position).getAttachment());
                }
            }
            if (self) {
                // self message

                ((RelativeLayout) itemView).setGravity(Gravity.RIGHT);
                card.setBackgroundResource(R.drawable.bg_bubble_gray);
                // we can change margina and align parent right by adding in params


            } else {
                // others message
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item, parent, false);
            }
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageType = messageType(position);
                    if (messageType.equals("GPS")){
                        String message = messageArrayList.get(position).getMessage().substring(6);
                        String[] cordinate = message.split("\\s+");
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        LatLng northWest = new LatLng( Double.parseDouble(cordinate[0]), Double.parseDouble(cordinate[1]));
                        LatLng southEast = new LatLng( Double.parseDouble(cordinate[0]) + 0.00001, Double.parseDouble(cordinate[1])+0.00001);
                        LatLngBounds bounds = new LatLngBounds(northWest , southEast);
                        builder.setLatLngBounds(bounds);
                        Intent intent = new Intent() ;
                        try {
                            intent = builder.build((Activity) mContext);
                            Activity activity = (Activity)mContext;
                            activity.startActivityForResult(intent,PLACE_PICKER_REQUEST);

                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        String url = messageArrayList.get(position).getAttachment();
                        String filename = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ "/"+ url.substring(url.lastIndexOf('/') + 1)) ;
                        Uri fileURI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName()  + ".com.example.yadav.IM.provider", new File(filename));
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
                        String mimeType = myMime.getMimeTypeFromExtension(fileExt(url));
                        newIntent.setDataAndType(fileURI,mimeType);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            mContext.startActivity(newIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText( mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                        }
                    }


                }
            });

        }



        return new ViewHolder(itemView);
    }
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    /* public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();

         Log.i("Receiver", "Broadcast received: " + action);

         if(action.equals("com.example.broadcast.MY_NOTIFICATION")){
             char isType = intent.getExtras().getChar("isTyping");
             String type_user = intent.getExtras().getString("type_user");

         }
     }*/
    private String messageType(int position){
        Message msg = messageArrayList.get(position);
        String msgText = msg.getMessage();
        String attachment = msg.getAttachment();
        String toReturn="MSG";
        if (msgText.startsWith("GPS://")){
            toReturn = "GPS";
        }
        else if (attachment != null && !attachment.equals("null")){
            String extension = attachment.substring(attachment.length()-3);
            if (extension.equals("pdf") || extension.equals("PDF")){
                toReturn = "PDF";
            }else if (extension.equals("JPG") || extension.equals("jpg") || extension.equals("PNG") || extension.equals("png")){
                toReturn = "IMG";
            }else {
                toReturn="FILE";
            }
        }

        return toReturn ;
    }





    @Override
    public int getItemViewType(int position) {
        return position ;
    }
    
    public Boolean giveMargin(int position) {
       if (position + 1 < messageArrayList.size()){
           Message msg1 = messageArrayList.get(position);
           Message msg2 = messageArrayList.get(position + 1);
           if (msg1.getUser().getPkUser() != msg2.getUser().getPkUser()){
               return true ;
           }
       }
        return false ;
    }

    public Bitmap getAttachBitmap(int position) {
        Message message = messageArrayList.get(position);

        return  message.getBm();
    }

    private Boolean isSelf(int position){
        Message msg = messageArrayList.get(position);
        if (msg.getUser().getPkUser() == userId){
            return true;
        }else {
            return false;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);


        ((ViewHolder) holder).message.setText(message.getMessage());
        String msgType = messageType(position);
        if (msgType.equals("MSG")){
            ((ViewHolder) holder).message.setVisibility(View.VISIBLE);
        }
        else {
            ((ViewHolder) holder).message.setVisibility(View.GONE);
        }


        //String timestamp = getTimeStamp(message.getCreatedAt());
        String timestamp = getCommitDate(message.getCreatedAt());
        ((ViewHolder) holder).timestamp.setText(timestamp);
        if (!giveMargin(position)){
            ((ViewHolder) holder).timestamp.setVisibility(View.GONE);
        }
        else{
            ((ViewHolder) holder).timestamp.setVisibility(View.VISIBLE);
        }

    }

    public void saveFileFromUrl(final String url){
        if(getFileFromCache(url.substring(url.lastIndexOf('/')+1, url.length()))==null) {
            helper = new Helper(mContext);
            final AsyncHttpClient httpClient = helper.getHTTPClient();
            AsyncHttpClient client = helper.getHTTPClient();

            client.get(url, new FileAsyncHttpResponseHandler(mContext) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    // Do something with the file `response`

                    try {
                        //Splitting a File Name from SourceFileName
                        String DestinationName = url.substring(url.lastIndexOf('/') + 1, url.length());
                        //Saving an image into DCIM Folder
                        File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DestinationName);
                        cacheCopy(file, newFile);
                        String extension = url.substring(url.length()-3);
                        if (extension.equals("JPG") || extension.equals("jpg") || extension.equals("PNG") || extension.equals("png")){
                            Bitmap bm = getFileFromCache(DestinationName);
                            card.setImageBitmap(bm);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, File file) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    System.out.println("failure");
                    System.out.println(statusCode);
                }
            });
        }
        else{
            String extension = url.substring(url.length()-3);
            if (extension.equals("JPG") || extension.equals("jpg") || extension.equals("PNG") || extension.equals("png")){
                Bitmap bm = getFileFromCache(url.substring(url.lastIndexOf('/') + 1, url.length()));
                card.setImageBitmap(bm);
            }
        }

    }

    public Bitmap getFileFromCache(String fileName){
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"");
        if (dir.exists()) {
            if(dir.listFiles() != null) {
                for (File f : dir.listFiles()) {
                    //perform here your operation
                    if (f.getName().equals(fileName)) {
                        return BitmapFactory.decodeFile(f.getPath());
                    }

                }
            }
        }

     return null;

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


    public String getCommitDate(String timestamp) {



        Date date = new Date();
        Date currentDate = new Date() ;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String targetDate =date.toString();
        try {

            date = formatter.parse(timestamp);
            targetDate = (date).toString();
            if (date.getYear() == currentDate.getYear()){
                SimpleDateFormat formatter_yr = new SimpleDateFormat("hh:mm a , dd MMM");
                targetDate = formatter_yr.format(date);

                if (date.getDate() == currentDate.getDate()){
                    SimpleDateFormat formatter_day = new SimpleDateFormat("hh:mm a");
                    targetDate = formatter_day.format(date);

                }
            }
            else {
                SimpleDateFormat formatter_yr = new SimpleDateFormat("hh:mm a , dd|MM|yy");
                targetDate = formatter_yr.format(date);
            }
        } catch (ParseException e) {
            System.out.println("error while parsing date 3");
        }

        return targetDate ;
    }
}