package com.technosysint.pricechecker.Helper;

import android.view.animation.Interpolator;

/**
 * Created by Yasir.Raza on 10/23/2018.
 */

public class BounceInterpolator implements Interpolator {
    private double mAmplitude = 1;
    private double mFrequency = 10;

    public BounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    @Override
    public float getInterpolation(float v) {
        return (float) (-1 * Math.pow(Math.E, -v/ mAmplitude) * Math.cos(mFrequency * v) + 1);
    }
}
