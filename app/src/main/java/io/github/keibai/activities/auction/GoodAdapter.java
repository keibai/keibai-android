package io.github.keibai.activities.auction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;
import io.github.keibai.models.Good;


public class GoodAdapter extends ArrayAdapter {

    private Context context;
    private List<Good> goods;

    private TextView goodNameText;

    public GoodAdapter(@NonNull Context context, @NonNull List<Good> objects) {
        super(context, 0, objects);
        this.context = context;
        this.goods = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.good_list_item,
                    parent, false);
        }

        goodNameText = listItemView.findViewById(R.id.good_list_good_name);

        Good good = (Good) getItem(position);
        goodNameText.setText(good.name);

        return listItemView;
    }
}
