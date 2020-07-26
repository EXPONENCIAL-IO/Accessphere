package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class GenericMultipleBarcodeReader implements MultipleBarcodeReader {
    private static final int MAX_DEPTH = 4;
    private static final int MIN_DIMENSION_TO_RECUR = 100;
    private final Reader delegate;

    public GenericMultipleBarcodeReader(Reader delegate2) {
        this.delegate = delegate2;
    }

    public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
        return decodeMultiple(image, null);
    }

    public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
        List<Result> results = new ArrayList<>();
        doDecodeMultiple(image, hints, results, 0, 0, 0);
        if (!results.isEmpty()) {
            return (Result[]) results.toArray(new Result[results.size()]);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private void doDecodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints, List<Result> results, int xOffset, int yOffset, int currentDepth) {
        if (currentDepth <= 4) {
            try {
                Result result = this.delegate.decode(image, hints);
                boolean alreadyFound = false;
                Iterator it = results.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((Result) it.next()).getText().equals(result.getText())) {
                            alreadyFound = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!alreadyFound) {
                    results.add(translateResultPoints(result, xOffset, yOffset));
                }
                ResultPoint[] resultPoints = result.getResultPoints();
                if (resultPoints != null && resultPoints.length != 0) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    float minX = (float) width;
                    float minY = (float) height;
                    float maxX = 0.0f;
                    float maxY = 0.0f;
                    for (ResultPoint point : resultPoints) {
                        if (point != null) {
                            float x = point.getX();
                            float y = point.getY();
                            if (x < minX) {
                                minX = x;
                            }
                            if (y < minY) {
                                minY = y;
                            }
                            if (x > maxX) {
                                maxX = x;
                            }
                            if (y > maxY) {
                                maxY = y;
                            }
                        }
                    }
                    if (minX > 100.0f) {
                        doDecodeMultiple(image.crop(0, 0, (int) minX, height), hints, results, xOffset, yOffset, currentDepth + 1);
                    }
                    if (minY > 100.0f) {
                        doDecodeMultiple(image.crop(0, 0, width, (int) minY), hints, results, xOffset, yOffset, currentDepth + 1);
                    }
                    if (maxX < ((float) (width - 100))) {
                        doDecodeMultiple(image.crop((int) maxX, 0, width - ((int) maxX), height), hints, results, xOffset + ((int) maxX), yOffset, currentDepth + 1);
                    }
                    if (maxY < ((float) (height - 100))) {
                        doDecodeMultiple(image.crop(0, (int) maxY, width, height - ((int) maxY)), hints, results, xOffset, yOffset + ((int) maxY), currentDepth + 1);
                    }
                }
            } catch (ReaderException e) {
            }
        }
    }

    private static Result translateResultPoints(Result result, int xOffset, int yOffset) {
        ResultPoint[] oldResultPoints = result.getResultPoints();
        if (oldResultPoints == null) {
            return result;
        }
        ResultPoint[] newResultPoints = new ResultPoint[oldResultPoints.length];
        for (int i = 0; i < oldResultPoints.length; i++) {
            ResultPoint oldPoint = oldResultPoints[i];
            if (oldPoint != null) {
                newResultPoints[i] = new ResultPoint(oldPoint.getX() + ((float) xOffset), oldPoint.getY() + ((float) yOffset));
            }
        }
        Result newResult = new Result(result.getText(), result.getRawBytes(), newResultPoints, result.getBarcodeFormat());
        newResult.putAllMetadata(result.getResultMetadata());
        return newResult;
    }
}
