package eu.lynxit.sampleapp;

public enum IntervalFrequency {
    /*
     * Frequency REALTIME, means will refresh as soon as it changes.
     */
    REALTIME,
    /*
     * Frequency HIGH, means will do refresh every 10 frames (if it changes)
     */
    HIGH,
    /*
     * Frequency MEDIUM, means will do refresh every 20 frames (if it changes)
     */
    MEDIUM,
    /*
     * Frequency LOW, means will do refresh every 30 frames (if it changes)
     */
    LOW,
    /*
     * No periodic refresh, even if it changes.
     */
    NONE
}