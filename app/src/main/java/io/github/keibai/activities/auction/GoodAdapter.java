package io.github.keibai.activities.auction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;
import io.github.keibai.models.Good;


public class GoodAdapter extends ArrayAdapter {

    private Context context;
    private List<Good> goods;
    private boolean withDeleteButton;

    private TextView goodNameText;

    public GoodAdapter(@NonNull Context context, @NonNull List<Good> objects, boolean withDeleteButton) {
        super(context, 0, objects);
        this.context = context;
        this.goods = objects;
        this.withDeleteButton = withDeleteButton;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.good_list_item,
                    parent, false);
        }

        goodNameText = listItemView.findViewById(R.id.good_list_good_name);

        final Good good = (Good) getItem(position);
        goodNameText.setText(good.name);

        Button deleteButton = listItemView.findViewById(R.id.button_delete_good);
        if (withDeleteButton) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goods.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        return listItemView;
    }
}
