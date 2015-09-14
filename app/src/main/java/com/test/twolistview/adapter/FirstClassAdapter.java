package com.test.twolistview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.test.twolistview.R;
import com.test.twolistview.model.FirstClassItem;

import java.util.List;

/**
 * 一级分类（即左侧菜单）的adapter
 * Created by hanj on 14-9-25.
 */
public class FirstClassAdapter extends BaseAdapter {
    private Context context;
    private List<FirstClassItem> list;

    public FirstClassAdapter(Context context, List<FirstClassItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.left_listview_item, null);
            holder = new ViewHolder();

            holder.nameTV = (TextView) convertView.findViewById(R.id.left_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //选中和没选中时，设置不同的颜色
        if (position == selectedPosition){
            convertView.setBackgroundResource(R.color.popup_right_bg);
        }else{
            convertView.setBackgroundResource(R.drawable.selector_left_normal);
        }

        holder.nameTV.setText(list.get(position).getName());
        if (list.get(position).getSecondList() != null && list.get(position).getSecondList().size() > 0) {
            holder.nameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);
        } else {
            holder.nameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return convertView;
    }

    private int selectedPosition = 0;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private class ViewHolder {
        TextView nameTV;
    }
}
