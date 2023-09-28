package extendedui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import extendedui.configuration.STSStringConfigItem;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public abstract class EUIUtils {
    private static final Gson GsonReader = new Gson();
    private static final StringBuilder sb1 = new StringBuilder();
    private static final StringBuilder sb2 = new StringBuilder();
    private static final String NEWLINE = "NL";
    public static final Random RNG = new Random();
    public static final String EMPTY_STRING = "";
    public static final String DOUBLE_SPLIT_LINE = " || ";
    public static final String LEGACY_DOUBLE_SPLIT_LINE = " NL  NL ";
    public static final String SPLIT_LINE = " | ";

    public static <T> List<T> addIf(List<T> res, T[] list, FuncT1<Boolean, T> predicate) {
        if (list != null) {
            for (T t : list) {
                if (predicate.invoke(t)) {
                    res.add(t);
                }
            }
        }

        return res;
    }

    public static <T> List<T> addIf(List<T> res, Iterable<? extends T> list, FuncT1<Boolean, T> predicate) {
        if (list != null) {
            for (T t : list) {
                if (predicate.invoke(t)) {
                    res.add(t);
                }
            }
        }

        return res;
    }

    public static <T, N> List<N> addMap(List<N> res, T[] list, FuncT1<N, T> predicate) {
        if (list != null) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }

        return res;
    }

    public static <T, N> List<N> addMap(List<N> res, Iterable<? extends T> list, FuncT1<N, T> predicate) {
        if (list != null) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }

        return res;
    }

    public static <T, N> List<N> addMapNonnull(List<N> res, T[] list, FuncT1<N, T> predicate) {
        if (list != null) {
            for (T t : list) {
                N item = predicate.invoke(t);
                if (item != null) {
                    res.add(item);
                }
            }
        }

        return res;
    }

    public static <T, N> List<N> addMapNonnull(List<N> res, Iterable<? extends T> list, FuncT1<N, T> predicate) {
        if (list != null) {
            for (T t : list) {
                N item = predicate.invoke(t);
                if (item != null) {
                    res.add(item);
                }
            }
        }

        return res;
    }

    public static boolean all(CharSequence sequence, FuncT1<Boolean, Character> func) {
        for (int i = 0; i < sequence.length(); i++) {
            if (!func.invoke(sequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean all(T[] list, FuncT1<Boolean, T> predicate) {
        for (T t : list) {
            if (!predicate.invoke(t)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean all(Iterable<? extends T> list, FuncT1<Boolean, T> predicate) {
        for (T t : list) {
            if (!predicate.invoke(t)) {
                return false;
            }
        }

        return true;
    }

    public static boolean any(CharSequence sequence, FuncT1<Boolean, Character> func) {
        for (int i = 0; i < sequence.length(); i++) {
            if (func.invoke(sequence.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean any(T[] list, FuncT1<Boolean, T> predicate) {
        for (T t : list) {
            if (t != null && predicate.invoke(t)) {
                return true;
            }
        }

        return false;
    }

    public static <T> boolean any(Iterable<? extends T> list, FuncT1<Boolean, T> predicate) {
        for (T t : list) {
            if (predicate.invoke(t)) {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public static <T> T[] array(T... items) {
        return items;
    }

    public static <T> T[] arrayAppend(T[] base, T item) {
        final T[] res = (T[]) Array.newInstance(base.getClass().getComponentType(), base.length + 1);
        System.arraycopy(base, 0, res, 0, base.length);
        res[base.length] = item;
        return res;
    }

    public static <T> T[] arrayConcat(T[] base, T[] secondary) {
        final T[] res = (T[]) Array.newInstance(base.getClass().getComponentType(), base.length + secondary.length);
        System.arraycopy(base, 0, res, 0, base.length);
        System.arraycopy(secondary, 0, res, base.length, base.length + secondary.length);
        return res;
    }

    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    @SuppressWarnings("unchecked")
    public static <T, N> N[] arrayMap(T[] list, Class<N> listClass, FuncT1<N, T> predicate) {
        if (list != null) {
            final N[] res = (N[]) Array.newInstance(listClass, list.length);
            for (int i = 0; i < list.length; i++) {
                res[i] = predicate.invoke(list[i]);
            }
            return res;
        }

        return (N[]) Array.newInstance(listClass, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T, N> N[] arrayMap(List<? extends T> list, Class<N> listClass, FuncT1<N, T> predicate) {
        if (list != null) {
            final N[] res = (N[]) Array.newInstance(listClass, list.size());
            for (int i = 0; i < list.size(); i++) {
                res[i] = predicate.invoke(list.get(i));
            }
            return res;
        }

        return (N[]) Array.newInstance(listClass, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T, N> N[] arrayMapAsNonnull(T[] list, Class<N> listClass, FuncT1<N, T> predicate) {
        if (list != null) {
            final N[] res = (N[]) Array.newInstance(listClass, list.length);
            for (int i = 0; i < list.length; i++) {
                N item = predicate.invoke(list[i]);
                if (item != null) {
                    res[i] = (item);
                }
            }
            return res;
        }

        return (N[]) Array.newInstance(listClass, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T, N> N[] arrayMapAsNonnull(List<? extends T> list, Class<N> listClass, FuncT1<N, T> predicate) {
        if (list != null) {
            final N[] res = (N[]) Array.newInstance(listClass, list.size());
            for (int i = 0; i < list.size(); i++) {
                N item = predicate.invoke(list.get(i));
                if (item != null) {
                    res[i] = (item);
                }
            }
            return res;
        }

        return (N[]) Array.newInstance(listClass, 0);
    }

    public static String capitalize(String text) {
        return text.length() <= 1 ? text.toUpperCase() : TipHelper.capitalize(text);
    }

    public static <T> void changeIndex(T item, List<T> list, int index) {
        if (list.remove(item)) {
            list.add(Math.max(0, Math.min(index, list.size())), item);
        }
    }

    public static File chooseFile() {
        return chooseFile(null, null, null);
    }

    public static File chooseFile(String... exts) {
        return chooseFile(getFileFilter(exts), null, null);
    }

    public static File chooseFile(FileNameExtensionFilter extensionFilter) {
        return chooseFile(extensionFilter, null, null);
    }

    public static File chooseFile(FileNameExtensionFilter extensionFilter, File currentFile) {
        return chooseFile(extensionFilter, currentFile, null);
    }

    public static File chooseFile(FileNameExtensionFilter extensionFilter, STSStringConfigItem pathConfig) {
        return chooseFile(extensionFilter, new File(pathConfig.get()), pathConfig);
    }

    public static File chooseFile(FileNameExtensionFilter extensionFilter, File currentFile, STSStringConfigItem pathConfig) {
        try {
            JFileChooser fc = createFileChooser(extensionFilter, currentFile);
            JFrame f = createFrame(fc);

            int result = fc.showOpenDialog(f);
            f.setVisible(false);
            f.dispose();

            if (pathConfig != null) {
                File cd = fc.getCurrentDirectory();
                if (cd != null && cd.isDirectory()) {
                    pathConfig.set(cd.getAbsolutePath());
                }
            }

            if (result == JFileChooser.APPROVE_OPTION) {
                return fc.getSelectedFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(EUIUtils.class, "Failed to select file");
        }
        return null;
    }

    public static <T> int count(Iterable<? extends T> list, FuncT1<Boolean, T> predicate) {

        int count = 0;
        for (T t : list) {
            if (predicate.invoke(t)) {
                count += 1;
            }
        }

        return count;
    }

    private static JFileChooser createFileChooser(FileNameExtensionFilter extensionFilter, File currentFile) {
        JFileChooser fc = new JFileChooser() {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            EUIUtils.format(EUIRM.strings.misc_overwriteDesc, f.getName()),
                            EUIRM.strings.misc_overwrite,
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        if (extensionFilter != null) {
            fc.setFileFilter(extensionFilter);
        }
        fc.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = evt.getTransferable();
                    if (t != null && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        for (File file : droppedFiles) {
                            fc.setSelectedFiles(droppedFiles.toArray(new File[]{}));
                        }
                        evt.dropComplete(true);
                    }
                    else {
                        evt.dropComplete(false);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    evt.dropComplete(false);
                }
            }
        });

        if (currentFile != null && currentFile.exists()) {
            if (currentFile.isDirectory()) {
                fc.setCurrentDirectory(currentFile);
            }
            else {
                File cd = currentFile.getParentFile();
                if (cd != null && cd.isDirectory()) {
                    fc.setCurrentDirectory(cd);
                }
            }
        }

        return fc;
    }

    private static JFrame createFrame(JFileChooser fc) {
        JFrame f = new JFrame();
        f.toFront();
        f.setAlwaysOnTop(true);
        f.setLocationRelativeTo(null);
        f.setPreferredSize(new Dimension(Settings.WIDTH / 2, Settings.HEIGHT / 2));
        fc.setPreferredSize(f.getPreferredSize());
        f.setVisible(true);
        return f;
    }

    public static <T> T deserialize(String s, Class<T> tokenClass) {
        return GsonReader.fromJson(s, tokenClass);
    }

    public static <T> T deserialize(String s, Type token) {
        return GsonReader.fromJson(s, token);
    }

    /* Creates a NEW arraylist with the filtered values. If you want to do the filtering in place and the iterable is already a list, use filterInPlace instead */
    public static <T> ArrayList<T> filter(T[] array, FuncT1<Boolean, T> predicate) {
        final ArrayList<T> res = new ArrayList<>();
        for (T t : array) {
            if (t != null && predicate.invoke(t)) {
                res.add(t);
            }
        }

        return res;
    }

    /* Creates a NEW arraylist with the filtered values. If you want to do the filtering in place and the iterable is already a list, use filterInPlace instead */
    public static <T> ArrayList<T> filter(Iterable<? extends T> list, FuncT1<Boolean, T> predicate) {
        final ArrayList<T> res = new ArrayList<>();
        for (T t : list) {
            if (t != null && predicate.invoke(t)) {
                res.add(t);
            }
        }

        return res;
    }

    /* Retains only elements in the list that pass the filter. MODIFIES the list being called in */
    public static <T, U extends Collection<T>> U filterInPlace(U list, FuncT1<Boolean, T> predicate) {
        list.removeIf(t -> !predicate.invoke(t));
        return list;
    }

    public static <T, N> ArrayList<N> filterMap(T[] list, FuncT1<Boolean, T> predicate, FuncT1<N, T> mapFunc) {
        if (list != null) {
            final ArrayList<N> res = new ArrayList<>();
            for (T t : list) {
                if (predicate.invoke(t)) {
                    res.add(mapFunc.invoke(t));
                }
            }
            return res;
        }

        return new ArrayList<>();
    }

    public static <T, N> ArrayList<N> filterMap(Iterable<? extends T> list, FuncT1<Boolean, T> predicate, FuncT1<N, T> mapFunc) {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null) {
            for (T t : list) {
                if (predicate.invoke(t)) {
                    res.add(mapFunc.invoke(t));
                }
            }
        }

        return res;
    }

    public static <T, N> ArrayList<N> filterMap(List<? extends T> list, FuncT1<Boolean, T> predicate, FuncT1<N, T> mapFunc) {
        if (list != null) {
            final ArrayList<N> res = new ArrayList<>(list.size());
            for (T t : list) {
                if (predicate.invoke(t)) {
                    res.add(mapFunc.invoke(t));
                }
            }
            return res;
        }

        return new ArrayList<>();
    }

    public static <T> T find(T[] array, FuncT1<Boolean, T> predicate) {
        for (T t : array) {
            if (t != null && predicate.invoke(t)) {
                return t;
            }
        }

        return null;
    }

    public static <T> T find(Iterable<? extends T> list, FuncT1<Boolean, T> predicate) {
        for (T t : list) {
            if (predicate.invoke(t)) {
                return t;
            }
        }

        return null;
    }

    public static <T, N extends Comparable<N>> T findMax(Iterable<? extends T> list, FuncT1<N, T> getProperty) {
        N best = null;
        T result = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) > 0)) {
                    best = prop;
                    result = t;
                }
            }
        }

        return result;
    }

    public static <T, N extends Comparable<N>> T findMax(T[] list, FuncT1<N, T> getProperty) {
        N best = null;
        T result = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) > 0)) {
                    best = prop;
                    result = t;
                }
            }
        }

        return result;
    }

    public static <T, N extends Comparable<N>> T findMin(Iterable<? extends T> list, FuncT1<N, T> getProperty) {
        N best = null;
        T result = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0)) {
                    best = prop;
                    result = t;
                }
            }
        }

        return result;
    }

    public static <T, N extends Comparable<N>> T findMin(T[] list, FuncT1<N, T> getProperty) {
        N best = null;
        T result = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0)) {
                    best = prop;
                    result = t;
                }
            }
        }

        return result;
    }

    @SafeVarargs
    public static <T> ArrayList<T> flatten(Collection<? extends T>... lists) {
        return Stream.of(lists)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T> ArrayList<T> flattenList(Collection<? extends Collection<? extends T>> lists) {
        ArrayList<T> t = new ArrayList<>();
        for (Collection<? extends T> list : lists) {
            t.addAll(list);
        }
        return t;
    }

    // Simple string Formatting in which integers inside curly braces are replaced by args[B].
    public static String format(String format, Object... args) {
        if (StringUtils.isEmpty(format)) {
            return "";
        }
        if (args == null || args.length == 0) {
            return format;
        }

        sb1.setLength(0);
        sb2.setLength(0);
        int braces = 0;
        for (int i = 0; i < format.length(); i++) {
            Character c = format.charAt(i);
            if (c == '{') {
                sb2.setLength(0);
                int j = i + 1;
                while (j < format.length()) {
                    final Character next = format.charAt(j);
                    if (Character.isDigit(next)) {
                        sb2.append(next);
                        j += 1;
                        continue;
                    }
                    else if (next == '}' && sb2.length() > 0) {
                        int index;
                        if (sb2.length() == 1) {
                            index = Character.getNumericValue(sb2.toString().charAt(0));
                        }
                        else {
                            index = EUIUtils.parseInt(sb2.toString(), -1);
                        }

                        if (index >= 0 && index < args.length) {
                            sb1.append(args[index]);
                        }
                        else {
                            EUIUtils.logError(EUIUtils.class, "Invalid format: " + format + "\n" + joinStrings(", ", args));
                        }

                        i = j;
                    }

                    break;
                }

                if (sb2.length() > 0) {
                    continue;
                }
            }

            sb1.append(c);
        }

        return sb1.toString();
    }

    public static FileNameExtensionFilter getFileFilter(String... filters) {
        return new FileNameExtensionFilter(EUIUtils.joinStringsMap(", ", f -> "*." + f, filters), filters);
    }

    @SafeVarargs
    public static <T> Comparator<? super T> getMultiComparator(Comparator<? super T>... comparators) {
        Comparator<? super T> finalComparator = comparators[0];
        for (int i = 1; i < comparators.length; i++) {
            Comparator<? super T> comp = comparators[i];
            Comparator<? super T> last = finalComparator;
            finalComparator = (a, b) -> {
                int res = comp.compare(a, b);
                return res != 0 ? res : last.compare(a, b);
            };
        }
        return finalComparator;
    }

    public static Logger getLogger(Object source) {
        if (source == null) {
            return LogManager.getLogger();
        }

        return LogManager.getLogger((source instanceof Class) ? ((Class<?>) source).getName() : source.getClass().getName());
    }

    public static <K, V> HashMap<K, List<V>> group(Iterable<V> list, FuncT1<K, V> getKey) {
        final HashMap<K, List<V>> map = new HashMap<>();
        for (V v : list) {
            K k = getKey.invoke(v);
            map.computeIfAbsent(k, key -> new ArrayList<>()).add(v);
        }

        return map;
    }

    public static <K, V, C> HashMap<K, C> group(Iterable<V> list, FuncT1<K, V> getKey, ActionT3<K, V, C> add) {
        final HashMap<K, C> map = new HashMap<>();
        for (V v : list) {
            K k = getKey.invoke(v);
            add.invoke(k, v, map.get(k));
        }

        return map;
    }

    public static <K, V> HashMap<K, V> hashMap(Iterable<K> list, FuncT1<V, K> getVal) {
        final HashMap<K, V> map = new HashMap<>();
        for (K k : list) {
            map.putIfAbsent(k, getVal.invoke(k));
        }

        return map;
    }

    public static <T> String invokeBuilder(StringBuilder stringBuilder) {
        String result = stringBuilder.toString();
        stringBuilder.setLength(0);
        return result;
    }

    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(T[] list) {
        return list == null || list.length == 0;
    }

    public static boolean isNullOrZero(Number number) {
        return number == null || number.intValue() == 0;
    }

    public static <T> String joinStrings(String delimiter, Iterable<T> values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            sj.add(String.valueOf(value));
        }

        return sj.toString();
    }

    @SafeVarargs
    public static <T> String joinStrings(String delimiter, T... values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            sj.add(String.valueOf(value));
        }

        return sj.toString();
    }

    public static <T> String joinStringsMap(String delimiter, FuncT1<String, T> stringFunc, Iterable<T> values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            sj.add(stringFunc.invoke(value));
        }

        return sj.toString();
    }

    @SafeVarargs
    public static <T> String joinStringsMap(String delimiter, FuncT1<String, T> stringFunc, T... values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            sj.add(stringFunc.invoke(value));
        }

        return sj.toString();
    }

    @SafeVarargs
    public static <T> String joinStringsMapLists(String delimiter, FuncT1<String, T> stringFunc, T[]... values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T[] array : values) {
            for (T value : array) {
                sj.add(stringFunc.invoke(value));
            }
        }

        return sj.toString();
    }

    public static <T> String joinStringsMapNonnull(String delimiter, FuncT1<String, T> stringFunc, Iterable<T> values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            String s = stringFunc.invoke(value);
            if (s != null) {
                sj.add(s);
            }
        }

        return sj.toString();
    }

    @SafeVarargs
    public static <T> String joinStringsMapNonnull(String delimiter, FuncT1<String, T> stringFunc, T... values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            String s = stringFunc.invoke(value);
            if (s != null) {
                sj.add(s);
            }
        }

        return sj.toString();
    }

    public static <T> String joinTrueStrings(String delimiter, Iterable<T> values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            if (value != null) {
                String valString = String.valueOf(value);
                if (!valString.isEmpty()) {
                    sj.add(String.valueOf(value));
                }
            }
        }

        return sj.toString();
    }

    @SafeVarargs
    public static <T> String joinTrueStrings(String delimiter, T... values) {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values) {
            if (value != null) {
                String valString = String.valueOf(value);
                if (!valString.isEmpty()) {
                    sj.add(String.valueOf(value));
                }
            }
        }

        return sj.toString();
    }

    public static void logError(Object source, String format, Object... values) {
        getLogger(source).error(format(format, values));
    }

    public static void logError(Object source, Object message) {
        getLogger(source).error(message);
    }

    public static void logInfo(Object source, Object message) {
        getLogger(source).info(message);
    }

    public static void logInfo(Object source, String format, Object... values) {
        getLogger(source).info(format(format, values));
    }

    public static void logInfoIfDebug(Object source, Object message) {
        if (Settings.isDebug) {
            logInfo(source, message);
        }
    }

    public static void logInfoIfDebug(Object source, String format, Object... values) {
        if (Settings.isDebug) {
            logInfo(source, format, values);
        }
    }

    public static void logWarning(Object source, Object message) {
        getLogger(source).warn(message);
    }

    public static void logWarning(Object source, String format, Object... values) {
        getLogger(source).warn(format(format, values));
    }

    public static <T, N> ArrayList<N> map(T[] list, FuncT1<N, T> predicate) {
        if (list != null) {
            final ArrayList<N> res = new ArrayList<>(list.length);
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
            return res;
        }

        return new ArrayList<>();
    }

    public static <T, N> ArrayList<N> map(Iterable<? extends T> list, FuncT1<N, T> predicate) {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }

        return res;
    }

    public static <T, N> ArrayList<N> map(List<? extends T> list, FuncT1<N, T> predicate) {
        if (list != null) {
            final ArrayList<N> res = new ArrayList<>(list.size());
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
            return res;
        }

        return new ArrayList<>();
    }

    @SafeVarargs
    public static <T, N> ArrayList<N> mapAll(FuncT1<N, T> predicate, T[]... lists) {
        final ArrayList<N> res = new ArrayList<>(EUIUtils.sumInt(lists, Array::getLength));
        for (T[] list : lists) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }
        return res;
    }

    @SafeVarargs
    public static <T, N> ArrayList<N> mapAll(FuncT1<N, T> predicate, Iterable<? extends T>... lists) {
        final ArrayList<N> res = new ArrayList<>();
        for (Iterable<? extends T> list : lists) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }
        return res;
    }

    @SafeVarargs
    public static <T, N> ArrayList<N> mapAll(FuncT1<N, T> predicate, List<? extends T>... lists) {
        final ArrayList<N> res = new ArrayList<>(EUIUtils.sumInt(lists, List::size));
        for (List<? extends T> list : lists) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }
        return res;
    }

    public static <T, N> ArrayList<N> mapAsNonnull(T[] list, FuncT1<N, T> predicate) {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null) {
            for (T t : list) {
                N n = predicate.invoke(t);
                if (n != null) {
                    res.add(n);
                }
            }
        }

        return res;
    }

    public static <T, N> ArrayList<N> mapAsNonnull(Iterable<? extends T> list, FuncT1<N, T> predicate) {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null) {
            for (T t : list) {
                N n = predicate.invoke(t);
                if (n != null) {
                    res.add(n);
                }
            }
        }

        return res;
    }

    public static <T, N> HashSet<N> mapAsSet(T[] list, FuncT1<N, T> predicate) {
        if (list != null) {
            final HashSet<N> res = new HashSet<>(list.length);
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
            return res;
        }

        return new HashSet<>();
    }

    public static <T, N> HashSet<N> mapAsSet(Iterable<? extends T> list, FuncT1<N, T> predicate) {
        final HashSet<N> res = new HashSet<>();
        if (list != null) {
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
        }

        return res;
    }

    public static <T, N> HashSet<N> mapAsSet(List<? extends T> list, FuncT1<N, T> predicate) {
        if (list != null) {
            final HashSet<N> res = new HashSet<>(list.size());
            for (T t : list) {
                res.add(predicate.invoke(t));
            }
            return res;
        }

        return new HashSet<>();
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T max(T... list) {
        return max(list, l -> l);
    }

    public static <T, N extends Comparable<N>> N max(T[] list, FuncT1<N, T> getProperty) {
        N best = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) > 0)) {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static <T, N extends Comparable<N>> N max(Iterable<? extends T> list, FuncT1<N, T> getProperty) {
        N best = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) > 0)) {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static <T> float mean(List<? extends T> list, FuncT1<Float, T> predicate) {
        if (list.size() <= 0) {
            return 0;
        }
        return sum(list, predicate) / list.size();
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T min(T... list) {
        return min(list, l -> l);
    }

    public static <T, N extends Comparable<N>> N min(T[] list, FuncT1<N, T> getProperty) {
        N best = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0)) {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static <T, N extends Comparable<N>> N min(Iterable<? extends T> list, FuncT1<N, T> getProperty) {
        N best = null;
        for (T t : list) {
            if (t != null) {
                N prop = getProperty.invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0)) {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static String modifyString(String text, FuncT1<String, String> modifyWord) {
        return EUIUtils.modifyString(text, " ", " ", modifyWord);
    }

    public static String modifyString(String text, String separator, String delimiter, FuncT1<String, String> modifyWord) {
        final String[] words = splitString(separator, text);
        if (modifyWord != null) {
            for (int i = 0; i < words.length; i++) {
                words[i] = modifyWord.invoke(words[i]);
            }
        }

        return joinStrings(delimiter, words);
    }

    public static float parseFloat(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static String popBuilder(StringBuilder stringBuilder) {
        String result = stringBuilder.toString();
        stringBuilder.setLength(0);
        return result;
    }

    public static <T> T random(T[] items) {
        return items != null && items.length > 0 ? items[RNG.nextInt(items.length)] : null;
    }

    public static <T> T random(List<? extends T> items) {
        int size = items != null ? items.size() : 0;
        if (size == 0) {
            return null;
        }
        return items.get(RNG.nextInt(size));
    }

    public static <T> T random(Collection<? extends T> items) {
        int size = items != null ? items.size() : 0;
        if (size == 0) {
            return null;
        }

        int i = 0;
        int targetIndex = RNG.nextInt(size);
        for (T item : items) {
            if (i++ == targetIndex) {
                return item;
            }
        }

        throw new RuntimeException("items.size() was smaller than " + targetIndex + ".");
    }

    public static <T> T random(ArrayList<T> items) {
        int size = items != null ? items.size() : 0;
        if (size == 0) {
            return null;
        }
        return items.get(RNG.nextInt(size));
    }

    public static Integer[] range(int lowest, int highest) {
        return range(lowest, highest, 1);
    }

    public static Integer[] range(int lowest, int highest, int step) {
        if (highest < lowest) {
            return new Integer[]{};
        }
        Integer[] values = new Integer[(highest - lowest) / step + 1];
        Arrays.setAll(values, i -> i * step + lowest);
        return values;
    }

    public static <T> Collection<T> replaceContents(Collection<T> subject, Collection<? extends T> source) {
        subject.clear();
        subject.addAll(source);
        return subject;
    }

    public static <T> T safeCast(Object o, Class<T> type) {
        return type.isInstance(o) ? (T) o : null;
    }

    public static <T> T safeIndex(T[] items, int index) {
        if (items.length == 0) {
            return null;
        }
        return items[Math.min(items.length - 1, index)];
    }

    public static File saveFile() {
        return saveFile(null, null, null);
    }

    public static File saveFile(String... exts) {
        return saveFile(getFileFilter(exts), null, null);
    }

    public static File saveFile(FileNameExtensionFilter extensionFilter) {
        return saveFile(extensionFilter, null, null);
    }

    public static File saveFile(FileNameExtensionFilter extensionFilter, File currentFile) {
        return saveFile(extensionFilter, currentFile, null);
    }

    public static File saveFile(FileNameExtensionFilter extensionFilter, STSStringConfigItem pathConfig) {
        return saveFile(extensionFilter, new File(pathConfig.get()), pathConfig);
    }

    public static File saveFile(FileNameExtensionFilter extensionFilter, File currentFile, STSStringConfigItem pathConfig) {
        try {
            JFileChooser fc = createFileChooser(extensionFilter, currentFile);
            JFrame f = createFrame(fc);

            int result = fc.showSaveDialog(f);
            f.setVisible(false);
            f.dispose();

            if (pathConfig != null) {
                File cd = fc.getCurrentDirectory();
                if (cd != null && cd.isDirectory()) {
                    pathConfig.set(cd.getAbsolutePath());
                }
            }

            if (result == JFileChooser.APPROVE_OPTION) {
                return fc.getSelectedFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(EUIUtils.class, "Failed to select file");
        }
        return null;
    }

    public static String serialize(Object o) {
        return GsonReader.toJson(o);
    }

    public static String serialize(Object o, Type token) {
        return GsonReader.toJson(o, token);
    }

    @SafeVarargs
    public static <T> HashSet<T> set(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    public static String[] splitString(String separator, String text) {
        return splitString(separator, text, true);
    }

    public static String[] splitString(String separator, String text, boolean removeEmptyEntries) {
        if (StringUtils.isEmpty(text)) {
            return new String[0];
        }

        sb1.setLength(0);
        sb2.setLength(0);

        int s_index = 0;
        final ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (separator.charAt(s_index) != c) {
                if (s_index > 0) {
                    sb1.append(sb2);
                    sb2.setLength(0);
                    s_index = 0;
                }
                sb1.append(c);
            }
            else if ((separator.length() - 1) > s_index) {
                s_index += 1;
                sb2.append(c);
            }
            else {
                if (!removeEmptyEntries) {
                    result.add(sb1.toString());
                    if (i == text.length() - 1) {
                        result.add("");
                    }
                }
                else if (sb1.length() > 0) {
                    result.add(sb1.toString());
                }

                s_index = 0;
                sb1.setLength(0);
                sb2.setLength(0);
            }
        }

        if (sb1.length() > 0) {
            result.add(sb1.toString());
        }

        final String[] arr = new String[result.size()];
        return result.toArray(arr);
    }

    public static <T> float sum(Iterable<? extends T> list, FuncT1<Float, T> predicate) {
        float sum = 0;
        if (list == null) {
            return sum;
        }
        for (T t : list) {
            sum += predicate.invoke(t);
        }
        return sum;
    }

    public static <T> float sum(T[] list, FuncT1<Float, T> predicate) {
        float sum = 0;
        if (list == null) {
            return sum;
        }
        for (T t : list) {
            sum += predicate.invoke(t);
        }
        return sum;
    }

    public static <T> int sumInt(Iterable<? extends T> list, FuncT1<Integer, T> predicate) {
        int sum = 0;
        if (list == null) {
            return sum;
        }
        for (T t : list) {
            sum += predicate.invoke(t);
        }
        return sum;
    }

    public static <T> int sumInt(T[] list, FuncT1<Integer, T> predicate) {
        int sum = 0;
        if (list == null) {
            return sum;
        }
        for (T t : list) {
            sum += predicate.invoke(t);
        }
        return sum;
    }

    public static String titleCase(String text) {
        return EUIUtils.modifyString(text, w -> Character.toUpperCase(w.charAt(0)) + (w.length() > 1 ? w.substring(1) : ""));
    }

    public static <T> Constructor<T> tryGetConstructor(Class<T> type, Class<?>... paramTypes) {
        try {
            return paramTypes.length > 0 ? type.getDeclaredConstructor(paramTypes) : type.getConstructor();
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
}