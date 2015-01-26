package assignment;
/**
 * Abstraction for any type of parameter used in the ImageEffect class.
 */
public abstract class ImageEffectParam {

    // Name of the parameter.
    public abstract String getName();

    // A short description of this parameter which will be shown in the
    // UI.
    public abstract String getDescription();

    // Default value of this parameter as a string.
    public abstract String getDefaultValue();
}
