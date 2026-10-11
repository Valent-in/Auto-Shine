package com.mine.autoshine;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Filter for input fields to limit input of number values.
 */
public class InputValuesFilter implements InputFilter {

    private final int min;
    private final int max;

    /**
     * Filter for number inputs to limit the allowed input values.
     *
     * @param min smallest allowed value
     * @param max biggest allowed value
     */
    public InputValuesFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Filter for number inputs to limit the allowed input values.
     *
     * @param min smallest allowed value
     * @param max biggest allowed value
     */
    @SuppressWarnings("unused")
    public InputValuesFilter(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException ignored) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}