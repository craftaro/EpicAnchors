package com.songoda.epicanchors.api;


/**
 * The access point of the EpicAnchorsAPI, a class acting as a bridge between API
 * and plugin implementation. It is from here where developers should access the
 * important and core methods in the API. All static methods in this class will
 * call directly upon the implementation at hand (in most cases this will be the
 * EpicAnchors plugin itself), therefore a call to {@link #getImplementation()} is
 * not required and redundant in most situations. Method calls from this class are
 * preferred the majority of time, though an instance of {@link EpicAnchors} may
 * be passed if absolutely necessary.
 *
 * @see EpicAnchors
 * @since 3.0.0
 */
public class EpicAnchorsAPI {

    private static EpicAnchors implementation;

    /**
     * Set the EpicAnchors implementation. Once called for the first time, this
     * method will throw an exception on any subsequent invocations. The implementation
     * may only be set a single time, presumably by the EpicAnchors plugin
     *
     * @param implementation the implementation to set
     */
    public static void setImplementation(EpicAnchors implementation) {
        if (EpicAnchorsAPI.implementation != null) {
            throw new IllegalArgumentException("Cannot set API implementation twice");
        }

        EpicAnchorsAPI.implementation = implementation;
    }

    /**
     * Get the EpicAnchors implementation. This method may be redundant in most
     * situations as all methods present in {@link EpicAnchors} will be mirrored
     * with static modifiers in the {@link EpicAnchorsAPI} class
     *
     * @return the EpicAnchors implementation
     */
    public static EpicAnchors getImplementation() {
        return implementation;
    }
}
