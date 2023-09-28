package extendedui.interfaces.markers;

import extendedui.EUIInputManager;

public interface TextInputProvider {

    default boolean allowCopy() {
        return true;
    }

    default boolean allowPaste() {
        return true;
    }

    default void appendText(CharSequence text) {
        getBuffer().append(text);
    }

    default void cancel() {
        EUIInputManager.releaseType(this);
    }

    default void clearBuffer() {
        getBuffer().setLength(0);
        EUIInputManager.resetPos();
    }

    default void complete() {
        EUIInputManager.releaseType(this);
    }

    default int getMaxPosition() {
        return -1;
    }

    default boolean isEditing() {
        return EUIInputManager.isInputTyping(this);
    }

    default boolean onKeyDown(int keycode) {
        return false;
    }

    default boolean onKeyUp(int keycode) {
        return false;
    }

    default int onPushArrowDown(int pos) {
        return getBuffer().length();
    }

    default int onPushArrowLeft(int pos) {
        return pos - 1;
    }

    default int onPushArrowRight(int pos) {
        return pos + 1;
    }

    default int onPushArrowUp(int pos) {
        return 0;
    }

    default boolean onPushBackspace() {
        return false;
    }

    default boolean onPushDelete() {
        return false;
    }

    default boolean onPushEnter() {
        complete();
        return true;
    }

    default boolean onPushEscape() {
        cancel();
        return true;
    }

    default boolean onPushTab() {
        complete();
        return true;
    }

    default void onUpdate(int pos, char keycode) {
    }

    default void setBufferText(CharSequence text) {
        getBuffer().setLength(0);
        getBuffer().append(text);
        EUIInputManager.resetPos();
    }

    default boolean start() {
        return EUIInputManager.tryStartType(this);
    }

    boolean acceptCharacter(char c);

    StringBuilder getBuffer();
}
