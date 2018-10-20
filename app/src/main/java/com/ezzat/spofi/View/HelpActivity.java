package com.ezzat.spofi.View;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezzat.spofi.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    HashMap<String, List<String>> header;
    List<String> mychild;
    ExpandableListView expandableListView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        expandableListView = findViewById(R.id.listView);
        header = MyAdapter.DataProvider.getInfo();
        mychild = new ArrayList<String>(header.keySet());
        adapter = new MyAdapter(this, header, mychild);
        expandableListView.setAdapter(adapter);
    }

    static class MyAdapter extends BaseExpandableListAdapter {

        private Context context;
        private HashMap<String, List<String>> childs;
        private List<String> headers;

        public MyAdapter(Context context, HashMap<String, List<String>> childs, List<String> headers) {
            this.context = context;
            this.childs = childs;
            this.headers = headers;
        }

        @Override
        public int getGroupCount() {
            return headers.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childs.get(headers.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return headers.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childs.get(headers.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String tit = (String) this.getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.custom_header, null);
            }

            TextView textView = convertView.findViewById(R.id.idTitle);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(tit);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String tit = (String) this.getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.custom_child, null);
            }

            TextView textView = convertView.findViewById(R.id.idChild);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(tit);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public static class DataProvider {

            public static HashMap<String, List<String>> getInfo() {
                HashMap<String, List<String>> headerDetails = new HashMap<>();
                List<String> child1 = new ArrayList<>();
                List<String> child2 = new ArrayList<>();
                List<String> child3 = new ArrayList<>();
                child1.add("Prepare an emergency kit.");
                child1.add("Check for, and remove, fire hazards in and around your home, such as dried out branches, leaves and debris.");
                child1.add("Keep a good sprinkler in an accessible location.");
                child1.add("Learn fire safety techniques and teach them to members of your family.");
                child1.add("Have fire drills with your family on a regular basis.");
                child1.add("Maintain first-aid supplies to treat the injured until help arrives.");
                child1.add("Have an escape plan so that all members of the family know how to get out of the house quickly and safely.");
                child1.add("Have a emergency plan so family members can contact each other in case they are separated during an evacuation.");
                child1.add("Make sure all family members are familiar with the technique of \"STOP, DROP, AND ROLL\" in case of clothes catching on fire.");
                child1.add("Make sure every floor and all sleeping areas have smoke detectors.");
                child1.add("Consult with your local fire department about making your home fire-resistant.");
                headerDetails.put("How to prepare for a wildfire ?", child1);
                child2.add("Close all windows and doors in the house.");
                child2.add("Cover vents, windows, and other openings of the house with duct tape and/or precut pieces of plywood.");
                child2.add("Park your car, positioned forward out of the driveway. Keep car windows closed and have your valuables already packed in your car.");
                child2.add("Turn off propane or natural gas. Move any propane barbeques into the open, away from structures.");
                child2.add("Turn on the lights in the house, porch, garage and yard.");
                child2.add("Place a ladder to the roof in the front of the house.");
                child2.add("Put lawn sprinklers on the roof of the house and turn on the water.");
                child2.add("Move all combustibles away from the house, including firewood and lawn furniture.");
                child2.add("Evacuate your family and pets to a safe location.");
                child2.add("Stay tuned to your local radio station for up-to-date information on the fire and possible road closures.");
                headerDetails.put("If you see a wildfire approaching your home :",child2);
                child3.add("Monitor local radio stations.");
                child3.add("Be prepared to evacuate at any time. If told to evacuate, do so.");
                child3.add("Keep all doors and windows closed in your home.");
                child3.add("Remove flammable drapes, curtains, awnings or other window coverings.");
                child3.add("Keep lights on to aid visibility in case smoke fills the house.");
                child3.add("If sufficient water is available, turn sprinklers on to wet the roof and any water-proof valuables.");
                headerDetails.put("What to do: During wildfire ?", child3);
                return headerDetails;
            }
        }
    }
}
