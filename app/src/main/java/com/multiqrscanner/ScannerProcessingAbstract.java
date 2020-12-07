package com.multiqrscanner;

import boofcv.alg.color.ColorFormat;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageInterleaved;
import boofcv.struct.image.ImageType;

public abstract class ScannerProcessingAbstract<T extends ImageBase<T>> implements ScannerProcessing<T> {

    protected ImageType<T> imageType;
    protected final Object lockGui = new Object();

    public ScannerProcessingAbstract(ImageType<T> type ) {
        imageType = type;
    }

    public ScannerProcessingAbstract(Class type ) {
        imageType = ImageType.single(type);
    }

    public ScannerProcessingAbstract(Class type , int numBands ) {
        if( ImageInterleaved.class.isAssignableFrom(type) ) {
            imageType = ImageType.il(numBands, type);
        } else {
            imageType = ImageType.pl(numBands, type);
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }

    @Override
    public ImageType<T> getImageType() {
        return imageType;
    }

    @Override
    public ColorFormat getColorFormat() {
        return ColorFormat.RGB;
    }
}