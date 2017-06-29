package com.example.yadav.IM;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.yadav.IM.R.drawable.bg_bubble_gray;


public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private int userId;
    private int SELF = 100;
    private static String today;
    private int ATTACH = 0;
    private Context mContext;
    private ArrayList<Message> messageArrayList;
    private static final int CAMERA_REQUEST = 1 ;
    private Bitmap bm = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView  message , timestamp;
        private ImageView location ;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
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
        View itemView;
        TextView message ;
        ImageView card ;
        ImageView attachment ;
        if (ATTACH == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item, parent, false);
            // view type is to identify where to render the chat message
            // left or right
            message = (TextView) itemView.findViewById(R.id.message);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) message.getLayoutParams();
            if (viewType == SELF) {
                // self message

                ((RelativeLayout) itemView).setGravity(Gravity.RIGHT);
                message.setBackgroundResource(R.drawable.bg_bubble_gray);
                // we can change margina and align parent right by adding in params


            } else {
                // others message
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item, parent, false);
            }
        }
        else { // given message is card message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_location, parent, false);
            // view type is to identify where to render the chat message
            // left or right
            card = (ImageView) itemView.findViewById(R.id.image_location);
            if (ATTACH == 1)
            card.setImageResource(R.drawable.ic_location_on_black_24dp);
            else if (ATTACH == 2 || ATTACH == 3 ){
                card.setImageBitmap(bm);
            }
            else{ // attachment is file
                if (ATTACH == 5){ // file is pdf
                    card.setImageResource(R.drawable.ic_picture_as_pdf_black_24dp);
                }
                else {
                    card.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                }
            }
            if (viewType == SELF) {
                // self message

                ((RelativeLayout) itemView).setGravity(Gravity.RIGHT);
                card.setBackgroundResource(R.drawable.bg_bubble_gray);
                // we can change margina and align parent right by adding in params


            } else {
                // others message
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item, parent, false);
            }

        }




        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        location_card(position);
        getAttachBitmap(position);
        if (message.getUser().getPkUser() == userId) {
            return SELF;
        }

        return position;
    }
    public void location_card(int position) {
        Message message = messageArrayList.get(position);
        ATTACH = message.isLocation();
    }

    public void getAttachBitmap(int position) {
        Message message = messageArrayList.get(position);
        bm =  message.getBm();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        ((ViewHolder) holder).message.setText(message.getMessage());



        String timestamp = getTimeStamp(message.getCreatedAt());


        ((ViewHolder) holder).timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
