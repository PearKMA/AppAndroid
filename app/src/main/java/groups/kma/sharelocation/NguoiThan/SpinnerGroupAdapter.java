package groups.kma.sharelocation.NguoiThan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import groups.kma.sharelocation.R;

public class SpinnerGroupAdapter extends ArrayAdapter<SpinnerGroup> {
    private Context context;
    private Activity activity;
    private List<SpinnerGroup> groupList;
    public SpinnerGroupAdapter( Context context, int resource, List<SpinnerGroup> objects) {
        super(context, resource, objects);
        this.context = context;
        this.groupList = objects;
    }


    @Override
    public int getCount() {
        return groupList.size();
    }

    @Nullable
    @Override
    public SpinnerGroup getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(context);
        view.setTextColor(Color.WHITE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        view.setGravity(Gravity.CENTER);
        view.setText(groupList.get(position).getName());

        return view;
    }

    //View of Spinner on dropdown Popping

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView view = new TextView(context);
        view.setTextColor(Color.BLACK);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        view.setText(groupList.get(position).getName());
        view.setHeight(100);

        return view;
    }


}
