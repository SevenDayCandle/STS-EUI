package extendedui.interfaces.markers;

import basemod.IUIElement;
import extendedui.utilities.ItemGroup;

import java.util.Collection;

public interface CustomFilterModule<T> extends IUIElement {
    default void processGroup(ItemGroup<T> group) {
    }

    boolean isEmpty();

    boolean isHovered();

    boolean isItemValid(T c);

    void initializeSelection(Collection<? extends T> cards);

    void reset();
}
