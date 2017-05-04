package com.wafflestudio.snutt_staging.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.adapter.ExpandableTableListAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.manager.TableManager;
import com.wafflestudio.snutt_staging.model.Table;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class TableListActivity extends SNUTTBaseActivity {
    private static final String TAG = "TABLE_LIST_ACTIVITY";
    private static final String DIALOG_EDIT = "시간표 이름 변경";
    private static final String DIALOG_DELETE = "시간표 삭제";

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<Table>> mChildList = null;
    private ArrayList<Table> mChildListContent = null;

    private ExpandableListView mListView;
    private ExpandableTableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_table_list);
        setTitle("시간표 목록");
        
        mListView = (ExpandableListView) findViewById(R.id.listView);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), "c click = " + childPosition,
                        Toast.LENGTH_SHORT).show();

                String tableId = mChildList.get(groupPosition).get(childPosition).getId();
                startTableView(tableId);
                finish();
                return false;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    Log.d(TAG, groupPosition + " " + childPosition);
                    final Table table = mChildList.get(groupPosition).get(childPosition);

                    final CharSequence[] items = {DIALOG_EDIT, DIALOG_DELETE};
                    AlertDialog.Builder builder = new AlertDialog.Builder(TableListActivity.this);
                    builder.setTitle(table.getTitle())
                            .setItems(items, new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int index){
                                    if (items[index].equals(DIALOG_EDIT)) {

                                    } else {
                                        TableManager.getInstance().deleteTable(table.getId(), new Callback<List<Table>>() {
                                            @Override
                                            public void success(List<Table> tables, Response response) {
                                                mAdapter = getAdapter(tables);
                                                mListView.setAdapter(mAdapter);
                                                for (int i = 0;i < mGroupList.size();i ++) {
                                                    mListView.expandGroup(i);
                                                }
                                            }
                                            @Override
                                            public void failure(RetrofitError error) {
                                            }
                                        });
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.

                    // Return true as we are handling the event.
                    return true;
                }

                return false;
            }
        });


        TableManager.getInstance().getTableList(new Callback<List<Table>>() {
            @Override
            public void success(List<Table> tables, Response response) {
                mAdapter = getAdapter(tables);
                mListView.setAdapter(mAdapter);
                for (int i = 0;i < mGroupList.size();i ++) {
                    mListView.expandGroup(i);
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private ExpandableTableListAdapter getAdapter(List<Table> tables) {
        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<Table>>();

        if (tables.size()>0) {
            mGroupList.add(tables.get(0).getFullSemester());
            mChildListContent = new ArrayList<Table>();
            mChildListContent.add(tables.get(0));
        }

        for(int i=1;i<tables.size();i++) {
            Table table = tables.get(i);
            if( tables.get(i-1).getFullSemester().equals( table.getFullSemester() )) {
                mChildListContent.add(table);
            } else {
                mChildList.add(mChildListContent);

                mGroupList.add(table.getFullSemester());
                mChildListContent = new ArrayList<>();
                mChildListContent.add(table);
            }
        }
        if (tables.size()>0) {
            mChildList.add(mChildListContent);
        }

        return new ExpandableTableListAdapter(this, mGroupList, mChildList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_add) {
            startTableCreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TableManager.getInstance().getTableList(new Callback<List<Table>>() {
            @Override
            public void success(List<Table> tables, Response response) {
                mAdapter = getAdapter(tables);
                mListView.setAdapter(mAdapter);
                for (int i = 0;i < mGroupList.size();i ++) {
                    mListView.expandGroup(i);
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
