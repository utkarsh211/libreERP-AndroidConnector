package com.example.yadav.IM;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.libreerp.Helper;
import com.example.libreerp.User;
import com.example.libreerp.UserMeta;
import com.example.libreerp.UserMetaHandler;
import com.example.libreerp.Users;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yadav on 19/2/17.
 */
public class HomeFragment extends Fragment {

    View myView;

    private static ArrayList<ChatRoom> chatRoomArrayList;
    private static ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private static Context context ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_home , container, false);

        setHasOptionsMenu(true);
        recyclerView = (RecyclerView)  myView.findViewById(R.id.chatList_recycler_view);

        User usr = User.loadUser(getActivity());





        chatRoomArrayList = new ArrayList<>();
        fetchChatRooms();
        mAdapter = new ChatRoomsAdapter(this, chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity()
        ));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomArrayList.get(position);

                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);

                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchChatRooms();
        return myView;
    }


    private void fetchChatRooms() {
        System.out.print("yess");
        String serverURL = "http://pradeepyadav.net";
         context = getActivity();
        Helper helper = new Helper(context);

        AsyncHttpClient client = helper.getHTTPClient();

        String url = String.format("%s/%s/", serverURL, "/api/PIM/chatMessage/");

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                System.out.println("success 001xzc");
                try {
//                                JSONArray response = tasks.getJSONArray("results");
                    int pkMessage;
                    String message;
                    String attachement;
                    int pkOriginator;
                    Date created;
                    boolean read;
                    int pkUser;

                    String category;
                    String text;
                    int pkCommit;
                    String commitMessage;
                    int user;
                    String CommitDate;
                    String CommitBranch;
                    String CommitCode;

                    DBHandler dba = new DBHandler(context, null, null, 1);
                    User login = User.loadUser(context);
                    int login_pk = login.getPk();
                    ArrayList<Integer> ignore_id = new ArrayList<Integer>();
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject c = response.getJSONObject(i);
                        pkMessage = c.getInt("pk");
                        message = c.getString("message");
                        attachement = c.getString("attachment");
                        pkOriginator = c.getInt("originator");
                        created = (Date) c.get("created");
                        read = c.getBoolean("read");
                        pkUser = c.getInt("user");

                        int other_pk = pkOriginator;
                        if (other_pk == login_pk) {
                            other_pk = pkUser;
                        }

                        ChatRoomTable chatRoomTable = new ChatRoomTable();
                        chatRoomTable.setAttachement(attachement);
                        chatRoomTable.setCreated(created);
                        chatRoomTable.setMessage(message);
                        chatRoomTable.setPkMessage(pkMessage);
                        chatRoomTable.setPkOriginator(pkOriginator);
                        chatRoomTable.setPkUser(pkUser);
                        chatRoomTable.setOtherPk(other_pk);


                       if (searchInIgnoreIdArray(ignore_id,other_pk) == 0 ) {
                           // if not found in database insert it otherwise update it
                           int total_unread = 0;
                           for (int j = i ; j< response.length() ; j++){ // loop for counting unread
                               int pkOriginator_copy ,pkUser_copy;
                               boolean read_copy;
                               JSONObject d = response.getJSONObject(j);

                               pkOriginator_copy = d.getInt("originator");

                               read_copy = d.getBoolean("read");
                               pkUser_copy = d.getInt("user");

                               int other = pkOriginator_copy;
                               if (other == login_pk) {
                                   other = pkUser_copy;
                               }
                               if (other_pk == other && read == false){
                                   total_unread ++ ;
                               }

                           }

                           chatRoomTable.setRead(total_unread);
                           if (!dba.CheckIfPKAlreadyInDBorNot(other_pk)) { // check in table for chatroom
                               dba.insertTableChatRoom(chatRoomTable);
                           } else { // update it
                                // update query
                           }

                       }
                       // every messages will be inserted
                        if (!dba.CheckIfMessagePKAlreadyInDBorNot(pkMessage)) { // check in table of Message
                            dba.insertTableMessage(chatRoomTable);
                        }



                        ignore_id.add(other_pk);
                    }

                    load_data_from_database(0);
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());

                }


            }

            @Override
            public void onFinish() {
                System.out.println("finished 001cxczdfhgfg");
                // retrieve all the db entries

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                load_data_from_database(0);
                System.out.println("finished failed 001xczxc");
            }
        });
    }

    private int searchInIgnoreIdArray(ArrayList<Integer> a , int key){
        for (int i = 0 ; i < a.size() ; i++){
            if (a.get(i) == key){
               return 1 ;
            }
        }
        return 0 ;
    }
    private static void load_data_from_database(int id) {

        final AsyncTask<Integer, Void, Void> comment = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                final DBHandler dba = new DBHandler(context, null, null, 1); // see this
                //System.out.println("pkTask = "+pkTask);

                for (int i = 0; i < dba.getTotalDBEntries_CHATROOM(); i++) {
                    final ChatRoomTable data = new ChatRoomTable();
                    data.setOtherPk(dba.getWithPK(i));
                    data.setPkMessage(dba.getMessagePK(i));

                    String date = dba.getDate(i);
                    data.setRead(dba.getUnRead(i));
                     String messageDate;
                     messageDate = new SimpleDateFormat("dd MMM, yyyy").format(date);
                    Date current = new Date();

                    Users users = new Users(context);
                    final String[] name = new String[1];
                    final Bitmap[] bp = new Bitmap[1];
                    // Users user = new Users(dba.getPostUserPk(dba.getPostUser(comment_pk)));
                    users.get(dba.getWithPK(i) , new UserMetaHandler(){
                        @Override
                        public void onSuccess(UserMeta user){
                            System.out.println("yes65262626626");
                            name[0] = user.getFirstName() + " " + user.getLastName();
                            // set text in the layout here
                        }
                        @Override
                        public void handleDP(Bitmap dp){
                            System.out.println("dp dsda");
                            bp[0] = dp ;
                            // set text in the layout here
                        }

                    });
                    ChatRoom chatRoom = new ChatRoom(Integer.toString(i+1),name[0],data.getMessage(),date,data.getTotal_unread());
                    chatRoom.setDP(bp[0]);
                    chatRoomArrayList.add(chatRoom);
                }

                //
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url("http://192.168.178.26/test/script.php?id="+integers[0])
//                            .build();
//                    try {
//                        Response response = client.newCall(request).execute();
//
//                        JSONArray array = new JSONArray(response.body().string());
//
//                        for (int i=0; i<array.length(); i++){
//
//                            JSONObject object = array.getJSONObject(i);
//
//                            MyData data = new MyData(object.getInt("id"),object.getString("description"),
//                                    object.getString("image"));
//
//                            data_list.add(data);
//                        }
//
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        System.out.println("End of content");
//                    }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        };

        comment.execute(id);
    }


}
