package com.ezzat.spofi.View;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.ezzat.spofi.Control.Constants;
import com.ezzat.spofi.Control.FirebaseCallback;
import com.ezzat.spofi.Control.FirebaseMethods;
import com.ezzat.spofi.Model.Location;
import com.ezzat.spofi.Model.User;
import com.ezzat.spofi.R;
import com.ezzat.spofi.Model.Report;
import com.ezzat.spofi.Model.ReportType;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RVAdapter adapter;
    private List<Report> reports;
    private TextView no;
    private User currentUser;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public HistoryFragment(User user) {
        currentUser = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        no = view.findViewById(R.id.no);
        if (reports == null)
            reports = new ArrayList<>();
        else
            reports.clear();

        FirebaseMethods.getReports(currentUser.getReportsId(), new FirebaseCallback() {
            @Override
            public void onValueReturned(Object value) {
                for (Report r : (ArrayList<Report>)value) {
                    reports.add(r);
                }

                if (reports.size() == 0) {
                    no.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    no.setVisibility(View.GONE);
                }

                adapter = new RVAdapter(getActivity(), reports);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        });
        return view;
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {

        private Context context;
        private List<Report> reports;

        public RVAdapter(Context mContext, List<Report> reports){
            this.context = mContext;
            this.reports = reports;
        }

        @Override
        public RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.report_item, parent, false);
            return new RVViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RVViewHolder holder, int position) {
            Report report = reports.get(position);
            holder.date.setText(report.getDate());
            holder.city.setText(report.getLocation().getCity());
            holder.country.setText(report.getLocation().getCountry());
            holder.comment.setText(report.getComment() != null ? report.getComment() : "");
            holder.rate.setProgress(report.getOwnerRate());
            holder.state.setText(report.getState().toString());
        }

        @Override
        public int getItemCount() {
            return reports.size();
        }

        public class RVViewHolder extends RecyclerView.ViewHolder {

            public TextView date, city, country, location, comment, state;
            public NumberProgressBar rate;

            public RVViewHolder(View view) {
                super(view);
                date = view.findViewById(R.id.date);
                city = view.findViewById(R.id.city);
                country = view.findViewById(R.id.country);
                location = view.findViewById(R.id.location);
                comment = view.findViewById(R.id.comment);
                rate = view.findViewById(R.id.rate);
                state = view.findViewById(R.id.state);
            }
        }
    }

    public void addReport(Report report){
        reports.add(report);
        notifyList();
    }

    public void verifyReport(Report report) {
        if (reports.contains(report)) {
            reports.remove(report);
            reports.add(report);
            notifyList();
        }
    }

    public void notifyList() {
        adapter.notifyDataSetChanged();
        if (reports.size() == 0) {
            no.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            no.setVisibility(View.GONE);
        }
    }
}
