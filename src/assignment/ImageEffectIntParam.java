package assignment;
/**
 * This class provides an abstraction for integer parameter which is
 * used in the ImageEffect class.
 */
public class ImageEffectIntParam extends ImageEffectParam {
    private String name;
    private String description;
    private int defaultValue;
    private int value;
    private int maxValue;
    private int minValue;

    public ImageEffectIntParam(String name, String description,
                               int defaultValue, int minValue,
                               int maxValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return Integer.toString(defaultValue);
    }

    public int getValue() {
        return value;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }
}
