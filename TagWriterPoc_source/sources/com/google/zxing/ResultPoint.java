package com.google.zxing;

import com.google.zxing.common.detector.MathUtils;

public class ResultPoint {

    /* renamed from: x */
    private final float f26x;

    /* renamed from: y */
    private final float f27y;

    public ResultPoint(float x, float y) {
        this.f26x = x;
        this.f27y = y;
    }

    public final float getX() {
        return this.f26x;
    }

    public final float getY() {
        return this.f27y;
    }

    public final boolean equals(Object other) {
        if (!(other instanceof ResultPoint)) {
            return false;
        }
        ResultPoint otherPoint = (ResultPoint) other;
        if (this.f26x == otherPoint.f26x && this.f27y == otherPoint.f27y) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (Float.floatToIntBits(this.f26x) * 31) + Float.floatToIntBits(this.f27y);
    }

    public final String toString() {
        StringBuilder result = new StringBuilder(25);
        result.append('(');
        result.append(this.f26x);
        result.append(',');
        result.append(this.f27y);
        result.append(')');
        return result.toString();
    }

    public static void orderBestPatterns(ResultPoint[] patterns) {
        ResultPoint pointB;
        ResultPoint pointA;
        ResultPoint pointC;
        float zeroOneDistance = distance(patterns[0], patterns[1]);
        float oneTwoDistance = distance(patterns[1], patterns[2]);
        float zeroTwoDistance = distance(patterns[0], patterns[2]);
        if (oneTwoDistance >= zeroOneDistance && oneTwoDistance >= zeroTwoDistance) {
            pointB = patterns[0];
            pointA = patterns[1];
            pointC = patterns[2];
        } else if (zeroTwoDistance < oneTwoDistance || zeroTwoDistance < zeroOneDistance) {
            pointB = patterns[2];
            pointA = patterns[0];
            pointC = patterns[1];
        } else {
            pointB = patterns[1];
            pointA = patterns[0];
            pointC = patterns[2];
        }
        if (crossProductZ(pointA, pointB, pointC) < 0.0f) {
            ResultPoint temp = pointA;
            pointA = pointC;
            pointC = temp;
        }
        patterns[0] = pointA;
        patterns[1] = pointB;
        patterns[2] = pointC;
    }

    public static float distance(ResultPoint pattern1, ResultPoint pattern2) {
        return MathUtils.distance(pattern1.f26x, pattern1.f27y, pattern2.f26x, pattern2.f27y);
    }

    private static float crossProductZ(ResultPoint pointA, ResultPoint pointB, ResultPoint pointC) {
        float bX = pointB.f26x;
        float bY = pointB.f27y;
        return ((pointC.f26x - bX) * (pointA.f27y - bY)) - ((pointC.f27y - bY) * (pointA.f26x - bX));
    }
}
