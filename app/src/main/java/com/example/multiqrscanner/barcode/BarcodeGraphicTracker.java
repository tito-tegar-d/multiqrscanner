/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.multiqrscanner.barcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.multiqrscanner.barcode.camera.GraphicOverlay;
import com.example.multiqrscanner.misc.MiscUtil;
import com.example.multiqrscanner.navdrawer.NavigationViewActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<BarcodeGraphic> mOverlay;
    private BarcodeGraphic mGraphic;
    private volatile List<String> mBarcodes;
    private Context mContext;

    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic, Context context) {
        mOverlay = overlay;
        mGraphic = graphic;
        mContext = context;
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, Barcode item) {
        mGraphic.setId(id);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
        saveBarcodeToSharedPreferences(item);
    }

    public void saveBarcodeToSharedPreferences(Barcode item) {
        if (mBarcodes == null || mBarcodes.size() == 0) {
            mBarcodes = new ArrayList<>();
        }
        Gson gson = new Gson();
        String listBarcodeShared = MiscUtil.getStringSharedPreferenceByKey(mContext, MiscUtil.ListBarcodeKey);
        if (!listBarcodeShared.trim().equalsIgnoreCase("")) {
            mBarcodes = gson.fromJson(listBarcodeShared, new TypeToken<List<String>>() {
            }.getType());
        }
        if (mBarcodes.size() > 0) {
            if (!mBarcodes.contains(item.displayValue.trim())) {
                mBarcodes.add(item.displayValue);
            }
        } else {
            mBarcodes.add(item.displayValue);
        }
        MiscUtil.saveStringSharedPreferenceAsString(mContext, MiscUtil.ListBarcodeKey, gson.toJson(mBarcodes));
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }
}
