package com.google.zxing;

import com.google.zxing.common.detector.MathUtils;

public class ResultPoint {

    /* renamed from: x */
    private final float f18x;

    /* renamed from: y */
    private final float f19y;

    public ResultPoint(float x, float y) {
        this.f18x = x;
        this.f19y = y;
    }

    public final float getX() {
        return this.f18x;
    }

    public final float getY() {
        return this.f19y;
    }

    public final boolean equals(Object other) {
        if (!(other instanceof ResultPoint)) {
            return false;
        }
        ResultPoint otherPoint = (ResultPoint) other;
        if (this.f18x == otherPoint.f18x && this.f19y == otherPoint.f19y) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (Float.floatToIntBits(this.f18x) * 31) + Float.floatToIntBits(this.f19y);
    }

    public final String toString() {
        StringBuilder result = new StringBuilder(25);
        result.append('(');
        result.append(this.f18x);
        result.append(',');
        result.append(this.f19y);
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
        return MathUtils.distance(pattern1.f18x, pattern1.f19y, pattern2.f18x, pattern2.f19y);
    }

    private static float crossProductZ(ResultPoint pointA, ResultPoint pointB, ResultPoint pointC) {
        float bX = pointB.f18x;
        float bY = pointB.f19y;
        return ((pointC.f18x - bX) * (pointA.f19y - bY)) - ((pointC.f19y - bY) * (pointA.f18x - bX));
    }
}
