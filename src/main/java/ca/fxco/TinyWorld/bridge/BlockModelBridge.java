package ca.fxco.TinyWorld.bridge;

public interface BlockModelBridge {

    /**
     * If this model should use a custom collision shape
     */
    void tiny$setCustomCollisionShape(boolean useCustomCollisionShape);

    /**
     * If this model uses a custom collision shape
     */
    boolean tiny$useCustomCollisionShape();
}
