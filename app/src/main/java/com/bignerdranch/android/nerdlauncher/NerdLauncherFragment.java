package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

    // Private Static Variables
    private static String TAG_NERD_LAUNCHER_FRAGMENT = "NerdLauncherFragment";


    // Private Variables
    private RecyclerView mRecyclerView;


    // Public Static Methods
    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }


    // Public Methods
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        initRecyclerView(v);

        return v;
    }

    // Private Methods
    private List<ResolveInfo> getActivities() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                PackageManager pm = getActivity().getPackageManager();

                return String.CASE_INSENSITIVE_ORDER.compare(
                        lhs.loadLabel(pm).toString(),
                        rhs.loadLabel(pm).toString()
                );
            }
        });

        Log.i(TAG_NERD_LAUNCHER_FRAGMENT, "Found " + activities.size() + " activities");

        return activities;
    }
    private void initRecyclerView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ActivityAdapter(getActivities()));
    }


    // Private Classes
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_application, parent, false);

            return new ActivityHolder(view);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo info = mActivities.get(position);
            holder.bindActivity(info);
        }
    }

    private class ActivityHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView mImageView;
        private ResolveInfo mResolveInfo;
        private TextView mTextView;



        public ActivityHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTextView = (TextView) itemView.findViewById(R.id.app_name_text_view);
            mImageView = (ImageView) itemView.findViewById(R.id.app_icon_image_view);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            PackageManager pm = getActivity().getPackageManager();
            Drawable icon = resolveInfo.loadIcon(pm);
            String name = resolveInfo.loadLabel(pm).toString();

            mResolveInfo = resolveInfo;
            mTextView.setText(name);
            mImageView.setImageDrawable(icon);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo info = mResolveInfo.activityInfo;
            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setClassName(info.applicationInfo.packageName, info.name);

            startActivity(intent);
        }
    }
}
