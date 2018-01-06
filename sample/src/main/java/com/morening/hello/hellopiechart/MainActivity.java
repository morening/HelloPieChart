package com.morening.hello.hellopiechart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.morening.hello.piechart.DataBean;
import com.morening.hello.piechart.PieChartView;
import com.morening.hello.piechart.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private PieChartView mPieChartView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPieChartView = findViewById(R.id.id_piechart);
        mPieChartView.setCenterTextPostfix("GB");
        mPieChartView.setCenterTextSize(Utils.sp2px(this, 24));
        mPieChartView.show(getDataBean5());
        mPieChartView.setOnSegmentTwiceClickListener(data -> handleSegmentTwiceClicked(data));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPieChartView != null){
            mPieChartView.cancel();
        }
    }

    private List<DataBean> getDataBean5(){
        List<DataBean> datas = new ArrayList<>();
        datas.add(new DataBean().setData(10).setTag("A"));
        datas.add(new DataBean().setData(20).setTag("B"));
        datas.add(new DataBean().setData(30).setTag("C"));
        datas.add(new DataBean().setData(40).setTag("D"));
        datas.add(new DataBean().setData(50).setTag("E"));

        return datas;
    }

    private List<DataBean> getDataBean3(){
        List<DataBean> datas = new ArrayList<>();
        datas.add(new DataBean().setData(10).setTag("F"));
        datas.add(new DataBean().setData(20).setTag("G"));
        datas.add(new DataBean().setData(30).setTag("H"));

        return datas;
    }

    private void handleCenterTextTwiceClicked(DataBean data) {
        if (data != null){
            Toast.makeText(MainActivity.this, "双击退出"+data.getTag()+"详情", Toast.LENGTH_SHORT).show();
        }

        mPieChartView.show(getDataBean5());
        mPieChartView.setOnSegmentTwiceClickListener(data1 -> handleSegmentTwiceClicked(data1));
    }

    private void handleSegmentTwiceClicked(DataBean data) {
        if (data != null) {
            Toast.makeText(MainActivity.this, "双击进入" + data.getTag() + "详情", Toast.LENGTH_SHORT).show();
        }

        mPieChartView.show(getDataBean3());
        mPieChartView.setOnCenterTextTwiceClickListener(data1 -> handleCenterTextTwiceClicked(data1));
    }
}
