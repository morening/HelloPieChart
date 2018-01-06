## 简介
HelloPieChart是Hello系列的子项目，学习知识点的同时，通过造轮子以提升编程能力。本项目实现了简单的饼状图，通过不同颜色标识，简洁、清晰的展示了数据源分类信息情况，单击查看数据信息，双击进入并展示所选部分的分类信息情况，双击中间数字返回上一级。

<img width="25%" height="25%" src="https://github.com/morening/HelloPieChart/blob/master/snapshot/120.png?raw=true" /> <img width="25%" height="25%" src="https://github.com/morening/HelloPieChart/blob/master/snapshot/120_selected.png?raw=true" /> <img width="25%" height="25%" src="https://github.com/morening/HelloPieChart/blob/master/snapshot/60.png?raw=true" />

## 实现功能
1. 不同颜色标识分类情况
2. 高亮即选中
3. 可层级展示分类信息情况

## 主要接口
1. 显示/取消
2. 中间文本设置（字体大小，颜色，单位）
3. 展示状态接口（开始，取消，展示和完成）

## 如何快速应用
本应用只具有展示数据分类情况功能，应用前，请自行组织好数据层级结构。
```
mPieChartView.setCenterTextPostfix("GB");
mPieChartView.setCenterTextSize(Utils.sp2px(this, 24));
mPieChartView.show(getDataBean5());
mPieChartView.setOnSegmentTwiceClickListener(data -> handleSegmentTwiceClicked(data));
```
```
private void handleSegmentTwiceClicked(DataBean data) {
    mPieChartView.show(getDataBean3());
    mPieChartView.setOnCenterTextTwiceClickListener(data1 -> handleCenterTextTwiceClicked(data1));
}
```

## Todo List
1. 完成单元测试
2. 增加loading效果
3. 收集需求，丰富接口
4. 尽可能完善文档
