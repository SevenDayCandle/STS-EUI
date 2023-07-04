package extendedui.interfaces.markers;

public interface TourProvider {
    default boolean isComplete() {
        return true;
    }

    default void onComplete() {

    }
}
