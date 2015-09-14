package com.test.twolistview;

import android.app.Activity;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.test.twolistview.adapter.FirstClassAdapter;
import com.test.twolistview.adapter.SecondClassAdapter;
import com.test.twolistview.model.FirstClassItem;
import com.test.twolistview.model.SecondClassItem;
import com.test.twolistview.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用PopupWindow+2个ListView实现仿 美团/百度糯米 等多级菜单效果。
 * @author hanj
 *
 */
public class MainActivity extends Activity {
    private TextView mainTab1TV;
    /**左侧一级分类的数据*/
    private List<FirstClassItem> firstList;
    /**右侧二级分类的数据*/
    private List<SecondClassItem> secondList;

    /**使用PopupWindow显示一级分类和二级分类*/
    private PopupWindow popupWindow;

    /**左侧和右侧两个ListView*/
    private ListView leftLV, rightLV;
    //弹出PopupWindow时背景变暗
    private View darkView;

    //弹出PopupWindow时，背景变暗的动画
    private Animation animIn, animOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initData();
        initPopup();

        OnClickListenerImpl l = new OnClickListenerImpl();
        mainTab1TV.setOnClickListener(l);


    }

    private void findView() {
        mainTab1TV = (TextView) findViewById(R.id.main_tab1);
        darkView = findViewById(R.id.main_darkview);

        animIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
        animOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_anim);
    }

    private void initData() {
        firstList = new ArrayList<FirstClassItem>();
        //1
        ArrayList<SecondClassItem> secondList1 = new ArrayList<SecondClassItem>();
        secondList1.add(new SecondClassItem(101, "自助餐"));
        secondList1.add(new SecondClassItem(102, "西餐"));
        secondList1.add(new SecondClassItem(103, "川菜"));
        firstList.add(new FirstClassItem(1, "美食", secondList1));
        //2
        ArrayList<SecondClassItem> secondList2 = new ArrayList<SecondClassItem>();
        secondList2.add(new SecondClassItem(201, "天津"));
        secondList2.add(new SecondClassItem(202, "北京"));
        secondList2.add(new SecondClassItem(203, "秦皇岛"));
        secondList2.add(new SecondClassItem(204, "沈阳"));
        secondList2.add(new SecondClassItem(205, "大连"));
        secondList2.add(new SecondClassItem(206, "哈尔滨"));
        secondList2.add(new SecondClassItem(207, "锦州"));
        secondList2.add(new SecondClassItem(208, "上海"));
        secondList2.add(new SecondClassItem(209, "杭州"));
        secondList2.add(new SecondClassItem(210, "南京"));
        secondList2.add(new SecondClassItem(211, "嘉兴"));
        secondList2.add(new SecondClassItem(212, "苏州"));
        firstList.add(new FirstClassItem(2, "旅游", secondList2));
        //3
        ArrayList<SecondClassItem> secondList3 = new ArrayList<SecondClassItem>();
        secondList3.add(new SecondClassItem(301, "南开区"));
        secondList3.add(new SecondClassItem(302, "和平区"));
        secondList3.add(new SecondClassItem(303, "河西区"));
        secondList3.add(new SecondClassItem(304, "河东区"));
        secondList3.add(new SecondClassItem(305, "滨海新区"));
        firstList.add(new FirstClassItem(3, "电影", secondList3));
        //4
        firstList.add(new FirstClassItem(4, "手机", new ArrayList<SecondClassItem>()));
        //5
        firstList.add(new FirstClassItem(5, "娱乐", null));

        //copy
        firstList.addAll(firstList);
    }

    //点击事件
    class OnClickListenerImpl implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_tab1:
                    tab1OnClick();
                    break;
                default:
                    break;
            }
        }
    }

    private void initPopup() {
        popupWindow = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
        leftLV = (ListView) view.findViewById(R.id.pop_listview_left);
        rightLV = (ListView) view.findViewById(R.id.pop_listview_right);

        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setFocusable(true);

        popupWindow.setHeight(ScreenUtils.getScreenH(this) * 2 / 3);
        popupWindow.setWidth(ScreenUtils.getScreenW(this));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkView.startAnimation(animOut);
                darkView.setVisibility(View.GONE);

                leftLV.setSelection(0);
                rightLV.setSelection(0);
            }
        });


        //为了方便扩展，这里的两个ListView均使用BaseAdapter.如果分类名称只显示一个字符串，建议改为ArrayAdapter.
        //加载一级分类
        final FirstClassAdapter firstAdapter = new FirstClassAdapter(this, firstList);
        leftLV.setAdapter(firstAdapter);

        //加载左侧第一行对应右侧二级分类
        secondList = new ArrayList<SecondClassItem>();
        secondList.addAll(firstList.get(0).getSecondList());
        final SecondClassAdapter secondAdapter = new SecondClassAdapter(this, secondList);
        rightLV.setAdapter(secondAdapter);

        //左侧ListView点击事件
        leftLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //二级数据
                List<SecondClassItem> list2 = firstList.get(position).getSecondList();
                //如果没有二级类，则直接跳转
                if (list2 == null || list2.size() == 0) {
                    popupWindow.dismiss();

                    int firstId = firstList.get(position).getId();
                    String selectedName = firstList.get(position).getName();
                    handleResult(firstId, -1, selectedName);
                    return;
                }

                FirstClassAdapter adapter = (FirstClassAdapter) (parent.getAdapter());
                //如果上次点击的就是这一个item，则不进行任何操作
                if (adapter.getSelectedPosition() == position){
                    return;
                }

                //根据左侧一级分类选中情况，更新背景色
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();

                //显示右侧二级分类
                updateSecondListView(list2, secondAdapter);
            }
        });

        //右侧ListView点击事件
        rightLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭popupWindow，显示用户选择的分类
                popupWindow.dismiss();

                int firstPosition = firstAdapter.getSelectedPosition();
                int firstId = firstList.get(firstPosition).getId();
                int secondId = firstList.get(firstPosition).getSecondList().get(position).getId();
                String selectedName = firstList.get(firstPosition).getSecondList().get(position)
                        .getName();
                handleResult(firstId, secondId, selectedName);
            }
        });
    }

    //顶部第一个标签的点击事件
    private void tab1OnClick() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(findViewById(R.id.main_div_line));
            popupWindow.setAnimationStyle(-1);
            //背景变暗
            darkView.startAnimation(animIn);
            darkView.setVisibility(View.VISIBLE);
        }
    }

    //刷新右侧ListView
    private void updateSecondListView(List<SecondClassItem> list2,
                                      SecondClassAdapter secondAdapter) {
        secondList.clear();
        secondList.addAll(list2);
        secondAdapter.notifyDataSetChanged();
    }

    //处理点击结果
    private void handleResult(int firstId, int secondId, String selectedName){
        String text = "first id:" + firstId + ",second id:" + secondId;
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

        mainTab1TV.setText(selectedName);
    }
}
