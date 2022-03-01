package com.wk.chart.entry;

import android.graphics.Rect;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.compat.DateUtil;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.MarkerPointType;
import com.wk.chart.enumeration.TimeType;

import java.util.Date;

/**
 * <p>CandleEntry</p>
 */
public class CandleEntry extends AbsEntry {
    private final ValueEntry open; // 开盘价
    private final ValueEntry high; // 最高价
    private final ValueEntry low; // 最低价
    private final ValueEntry close; // 收盘价
    private final ValueEntry volume; // 量
    private final ValueEntry changeAmount; // 涨跌额
    private final ValueEntry changeProportion; // 涨跌幅
    //通用属性
    private final ArrayMap<Integer, ValueEntry[]> index; // 指标
    private final ArrayMap<Integer, ValueEntry[]> lineIndex; // 折线指标
    private final Rect markerPointRect;//标记点位置区域矩形
    private @MarkerPointType
    int markerPointType = MarkerPointType.NORMAL; // 标记点类型
    private TimeType timeType = null;//时间类型
    private String timeText = ""; // 时间显示文本（正常）
    private String shortTimeText = ""; // 时间显示文本（简短）

    /**
     * 自定义 K 线图用的数据
     *
     * @param open   开盘价
     * @param high   最高价
     * @param low    最低价
     * @param close  收盘价
     * @param volume 量
     * @param time   时间
     */
    public CandleEntry(String open, String high, String low, String close, String volume, Date time) {
        super(time);
        this.open = new ValueEntry(open);
        this.high = new ValueEntry(high);
        this.low = new ValueEntry(low);
        this.close = new ValueEntry(close);
        this.volume = new ValueEntry(volume);
        this.changeAmount = new ValueEntry();
        this.changeProportion = new ValueEntry();
        this.markerPointRect = new Rect();
        this.index = new ArrayMap<>();
        this.lineIndex = new ArrayMap<>();
        addAnimatorEntry(this.close, this.high, this.low, this.volume);
    }

    /**
     * 构建精度值
     *
     * @param scale 精度
     */
    public void buildScaleValue(@NonNull ScaleEntry scale) {
        Integer openScale = scale.getQuoteScale();
        if (!openScale.equals(open.scale)) {
            ValueUtils.buildScaleValue(open, openScale);
        }
        Integer highScale = scale.getQuoteScale();
        if (!highScale.equals(high.scale)) {
            ValueUtils.buildScaleValue(high, highScale);
        }
        Integer lowScale = scale.getQuoteScale();
        if (!lowScale.equals(low.scale)) {
            ValueUtils.buildScaleValue(low, lowScale);
        }
        Integer closeScale = scale.getQuoteScale();
        if (!closeScale.equals(close.scale)) {
            ValueUtils.buildScaleValue(close, closeScale);
        }
        Integer volumeScale = scale.getBaseScale();
        if (!volumeScale.equals(volume.scale)) {
            ValueUtils.buildScaleValue(volume, volumeScale);
        }
        Integer changeAmountScale = scale.getQuoteScale();
        long changeAmountValue = close.result - open.result;
        if (!changeAmountScale.equals(changeAmount.scale) || changeAmountValue != changeAmount.result) {
            ValueUtils.buildScaleValue(changeAmount, changeAmountValue, changeAmountScale);
        }
        Integer changeProportionScale = 2;
        long changeProportionValue = ValueUtils.scaleDivide(changeAmount.result, open.result, 4);
        if (!changeProportionScale.equals(changeProportion.scale) || changeProportionValue != changeProportion.result) {
            ValueUtils.buildScaleValue(changeProportion, changeProportionValue, changeProportionScale);
        }
    }

    /**
     * 构建时间显示文字
     *
     * @param timeType 时间类型
     */
    public void buildTimeText(@NonNull TimeType timeType) {
        if (this.timeType == timeType) return;
        this.timeType = timeType;
        shortTimeText = DateUtil.formatDateToString(getTime(), timeType.pattern());
        timeText = DateUtil.formatDateToString(getTime(), timeType == TimeType.day
                ? DateUtil.DATE_FORMAT_YMD : DateUtil.DATE_FORMAT_YMDHM);
    }


    /**
     * 构建QuoteScale精度的value
     *
     * @param value 值
     * @return 复原后的ValueEntry
     */
    public ValueEntry buildQuoteScaleValue(@NonNull ScaleEntry scale, long value) {
        return ValueUtils.buildScaleValue(value, scale.getQuoteScale());
    }

    /**
     * 构建BaseScale精度的value
     *
     * @param value 值
     * @return 复原后的ValueEntry
     */
    public ValueEntry buildBaseScaleValue(@NonNull ScaleEntry scale, long value) {
        return ValueUtils.buildScaleValue(value, scale.getBaseScale());
    }

    public void putLineIndex(@IndexType int indexType, ValueEntry... values) {
        this.lineIndex.put(indexType, values);
    }

    public @Nullable
    ValueEntry[] getLineIndex(@IndexType int indexType) {
        return lineIndex.get(indexType);
    }

    public void putIndex(@IndexType int indexType, ValueEntry... values) {
        this.index.put(indexType, values);
    }

    public @Nullable
    ValueEntry[] getIndex(@IndexType int indexType) {
        return index.get(indexType);
    }

    public @MarkerPointType
    int getMarkerPointType() {
        return markerPointType;
    }

    public void setMarkerPointType(@MarkerPointType int markerPointType) {
        this.markerPointType = markerPointType;
    }

    /**
     * 重置标记点位置区域坐标
     */
    public void updateMarkerRect(float left, float top, float right, float bottom) {
        this.markerPointRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    public Rect getMarkerPointRect() {
        return markerPointRect;
    }

    public String getShortTimeText() {
        return shortTimeText;
    }

    public String getTimeText() {
        return timeText;
    }

    public ValueEntry getOpen() {
        return open;
    }

    public ValueEntry getHigh() {
        return high;
    }

    public ValueEntry getLow() {
        return low;
    }

    public ValueEntry getClose() {
        return close;
    }

    public ValueEntry getVolume() {
        return volume;
    }

    public ValueEntry getChangeAmount() {
        return changeAmount;
    }

    public ValueEntry getChangeProportion() {
        return changeProportion;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nCandleEntry{" +
                "open=" + getOpen().text +
                ", high=" + getHigh().text +
                ", low=" + getLow().text +
                ", close=" + getClose().text +
                ", volume=" + getVolume().text +
                ", time=" + getTime() +
                '}';
    }
}