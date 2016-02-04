package space.weme.remix.ui.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.University;
import space.weme.remix.ui.base.BaseActivity;

/**
 * Created by Liujilong on 16/2/4.
 * liujilong.me@gmail.com
 */
public class AtySearchCity extends BaseActivity{
    private static final String TAG = "AtySearchCity";
    public static final String INTENT_UNIVERSITY = "intent_university";

    ImageView ivBack, ivSearch;
    EditText etText;
    ExpandableListView expandableListView;
    Adapter adapter;

    List<List<University>> wholeList;
    boolean loadFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_search_city);

        ivBack = (ImageView) findViewById(R.id.aty_search_city_back);
        ivSearch = (ImageView) findViewById(R.id.aty_search_city_search);
        etText = (EditText) findViewById(R.id.aty_search_city_text);
        expandableListView = (ExpandableListView) findViewById(R.id.aty_search_city_list);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new Adapter(this);
        expandableListView.setAdapter(adapter);

        loadJSON();

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                List<List<University>> filteredUniversity = filter(s.toString());
                adapter.setLists(filteredUniversity);
                adapter.notifyDataSetChanged();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                University university = (University) adapter.getChild(groupPosition,childPosition);
                Intent i = new Intent();
                i.putExtra(INTENT_UNIVERSITY, university.name);
                setResult(RESULT_OK, i);
                finish();
                return true;
            }
        });

    }

    private List<List<University>> filter(String s){
        List<List<University>> res = new ArrayList<>();
        for(List<University> universityList : wholeList){
            boolean found = false;
            ArrayList<University> filteredU = new ArrayList<>();
            for(University u : universityList){
                if(u.province.contains(s)||u.name.contains((s))){
                    if(!found){
                        found = true;
                    }
                    filteredU.add(u);
                }
            }
            if(found){
                res.add(filteredU);
            }
        }
        return res;
    }

    @SuppressWarnings("unused")
    private void loadJSON(){
        new Thread(){
            @Override
            public void run() {
                final String res;
                try{
                    InputStream in = getResources().openRawResource(R.raw.university);
                    int length = in.available();

                    byte [] buffer = new byte[length];
                    int len =  in.read(buffer);
                    res = new String(buffer,"utf-8");
                    in.close();
                    List<List<University>> lists = University.fromJSON(res);
                    wholeList = lists;
                    adapter.setLists(lists);
                    adapter.notifyDataSetChanged();
                    loadFinish = true;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.run();
    }


    @Override
    protected String tag() {
        return TAG;
    }


    class Adapter extends BaseExpandableListAdapter{

        List<List<University>> lists = null;
        private Context mContext;

        public Adapter(Context context){
            mContext = context;
        }

        public void setLists(List<List<University>> lists) {
            this.lists = lists;
        }

        @Override
        public int getGroupCount() {
            return lists==null?0:lists.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return lists.get(groupPosition)==null?0:lists.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return lists.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return lists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition<<8+childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.aty_search_city_group,parent,false);
            }
            TextView tv = (TextView) convertView;
            University fir  = lists.get(groupPosition).get(0);
            tv.setText(fir.province);

            ExpandableListView mExpandableListView = (ExpandableListView) parent;
            mExpandableListView.expandGroup(groupPosition);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.aty_search_city_child,parent,false);
            }
            TextView tv = (TextView) convertView;
            University fir  = lists.get(groupPosition).get(childPosition);
            tv.setText(fir.name);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
