package com.caowei.utdemo;

import android.graphics.Path;
import android.graphics.PointF;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

import java.util.Arrays;
import java.util.List;

public class BezierUtils {
    /**
     * 获取二项式系数
     *
     * @param l 行(杨辉三角)
     * @param c 列(杨辉三角)
     * @return 系数
     */
    private static int getBinomialCoefficient(@IntRange(from = 0) int l, @IntRange(from = 0) int c) {
        int lotteryOdds = 1;
        for (int i = 1; i <= c; i++)
            lotteryOdds = lotteryOdds * (l - i + 1) / i;
        return lotteryOdds;
    }

    /**
     * 获取贝塞尔曲线上t位置的点
     *
     * @param t      变化值
     * @param points 控制点
     * @return 当前t对应的点
     */
    public static PointF getBezierPoint(@FloatRange(from = 0, to = 1) float t, List<PointF> points) {
        if (points == null || points.size() < 2)
            return null;
        PointF result = new PointF();
        int n = points.size() - 1;//N阶
        for (int i = 0; i <= n; i++) {
            int binomialCoefficient = getBinomialCoefficient(n, i);
            double v = Math.pow(1 - t, n - i) * Math.pow(t, i);
            result.x += binomialCoefficient * (points.get(i).x * v);
            result.y += binomialCoefficient * (points.get(i).y * v);
        }
        return result;
    }

    /**
     * 获取贝塞尔曲线上t位置的点
     *
     * @param t      变化值
     * @param points 控制点
     * @return 当前t对应的点
     */
    public static PointF getBezierPoint(@FloatRange(from = 0, to = 1) float t, PointF... points) {
        if (points == null || points.length < 2)
            return null;
        return getBezierPoint(t, Arrays.asList(points));
    }

    /**
     * 获取贝塞尔曲线路径
     *
     * @param precision  生成路径分段的精度
     * @param points 控制点
     * @return 贝塞尔曲线路径
     */
    public static Path getBezierPath(@IntRange(from = 1) int precision, List<PointF> points) {
        if (precision < 1 || points == null || points.size() < 2)
            return null;
        float division = 1f / precision;
        float t = division;
        PointF lastP = points.get(0);
        Path path = new Path();
        path.moveTo(lastP.x, lastP.y);
        while (t < 1) {
            PointF point = getBezierPoint(t, points);
            path.lineTo(point.x, point.y);
            t += division;
        }
        PointF endP = points.get(points.size() - 1);
        path.lineTo(endP.x, endP.y);
        return path;
    }

    /**
     * 获取贝塞尔曲线路径
     *
     * @param precision  生成路径分段的精度
     * @param points 控制点
     * @return 贝塞尔曲线路径
     */
    public static Path getBezierPath(@IntRange(from = 1) int precision, PointF... points) {
        if (precision < 1 || points == null || points.length < 2)
            return null;
        return getBezierPath(precision, Arrays.asList(points));
    }
}

