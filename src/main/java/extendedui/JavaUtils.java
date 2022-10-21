package extendedui;

import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import eatyourbeets.interfaces.delegates.FuncT1;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class JavaUtils
{
    private static final Gson GsonReader = new Gson();

    private static final StringBuilder sb1 = new StringBuilder();
    private static final StringBuilder sb2 = new StringBuilder();

    public static <T> boolean All(Iterable<T> list, Predicate<T> predicate)
    {
        for (T t : list)
        {
            if (!predicate.test(t))
            {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean Any(Iterable<T> list, Predicate<T> predicate)
    {
        for (T t : list)
        {
            if (predicate.test(t))
            {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public static <T> T[] Array(T... items)
    {
        return items;
    }

    public static String Capitalize(String text)
    {
        return text.length() <= 1 ? text.toUpperCase() : TipHelper.capitalize(text);
    }

    public static <T> ArrayList<T> Filter(T[] array, Predicate<T> predicate)
    {
        final ArrayList<T> res = new ArrayList<>();
        for (T t : array)
        {
            if (t != null && predicate.test(t))
            {
                res.add(t);
            }
        }

        return res;
    }

    public static <T> ArrayList<T> Filter(Iterable<T> list, Predicate<T> predicate)
    {
        final ArrayList<T> res = new ArrayList<>();
        for (T t : list)
        {
            if (predicate.test(t))
            {
                res.add(t);
            }
        }

        return res;
    }

    @SafeVarargs
    public static <T> ArrayList<T> Flatten(Collection<T>... lists)
    {
        return Stream.of(lists)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T> ArrayList<T> FlattenList(Collection<? extends Collection<T>> lists)
    {
        ArrayList<T> t = new ArrayList<>();
        for (Collection<T> list : lists)
        {
            t.addAll(list);
        }
        return t;
    }

    // Simple string Formatting in which integers inside curly braces are replaced by args[B].
    public static String Format(String format, Object... args)
    {
        if (StringUtils.isEmpty(format))
        {
            return "";
        }
        if (args == null || args.length == 0)
        {
            return format;
        }

        sb1.setLength(0);
        sb2.setLength(0);
        int braces = 0;
        for (int i = 0; i < format.length(); i++)
        {
            Character c = format.charAt(i);
            if (c == '{')
            {
                sb2.setLength(0);
                int j = i + 1;
                while (j < format.length())
                {
                    final Character next = format.charAt(j);
                    if (Character.isDigit(next))
                    {
                        sb2.append(next);
                        j += 1;
                        continue;
                    }
                    else if (next == '}' && sb2.length() > 0)
                    {
                        int index;
                        if (sb2.length() == 1)
                        {
                            index = Character.getNumericValue(sb2.toString().charAt(0));
                        }
                        else
                        {
                            index = ParseInt(sb2.toString(), -1);
                        }

                        if (index >= 0 && index < args.length)
                        {
                            sb1.append(args[index]);
                        }
                        else
                        {
                            LogError(JavaUtils.class, "Invalid format: " + format + "\n" + JoinStrings(", " , args));
                        }

                        i = j;
                    }

                    break;
                }

                if (sb2.length() > 0)
                {
                    continue;
                }
            }

            sb1.append(c);
        }

        return sb1.toString();
    }

    public static <T> T Deserialize(String s, Class<T> tokenClass) {
        return GsonReader.fromJson(s, tokenClass);
    }

    public static <T> T Deserialize(String s, Type token) {
        return GsonReader.fromJson(s, token);
    }

    public static String Serialize(Object o) {
        return GsonReader.toJson(o);
    }

    public static String Serialize(Object o, Type token) {
        return GsonReader.toJson(o, token);
    }

    public static Logger GetLogger(Object source)
    {
        if (source == null)
        {
            return LogManager.getLogger();
        }

        return LogManager.getLogger((source instanceof Class) ? ((Class)source).getName() : source.getClass().getName());
    }


    public static <T> String JoinStrings(String delimiter, Iterable<T> values)
    {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values)
        {
            sj.add(String.valueOf(value));
        }

        return sj.toString();
    }

    @SafeVarargs
    public static <T> String JoinStrings(String delimiter, T... values)
    {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values)
        {
            sj.add(String.valueOf(value));
        }

        return sj.toString();
    }

    public static <T> String JoinTrueStrings(String delimiter, Iterable<T> values)
    {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values)
        {
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
    public static <T> String JoinTrueStrings(String delimiter, T... values)
    {
        final StringJoiner sj = new StringJoiner(delimiter);
        for (T value : values)
        {
            if (value != null) {
                String valString = String.valueOf(value);
                if (!valString.isEmpty()) {
                    sj.add(String.valueOf(value));
                }
            }
        }

        return sj.toString();
    }

    public static String[] SplitString(String separator, String text)
    {
        return SplitString(separator, text, true);
    }

    public static String[] SplitString(String separator, String text, boolean removeEmptyEntries)
    {
        if (StringUtils.isEmpty(text))
        {
            return new String[0];
        }

        sb1.setLength(0);
        sb2.setLength(0);

        int s_index = 0;
        final ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (separator.charAt(s_index) != c)
            {
                if (s_index > 0)
                {
                    sb1.append(sb2);
                    sb2.setLength(0);
                    s_index = 0;
                }
                sb1.append(c);
            }
            else if ((separator.length() - 1) > s_index)
            {
                s_index += 1;
                sb2.append(c);
            }
            else
            {
                if (!removeEmptyEntries)
                {
                    result.add(sb1.toString());
                    if (i == text.length() - 1)
                    {
                        result.add("");
                    }
                }
                else if (sb1.length() > 0)
                {
                    result.add(sb1.toString());
                }

                s_index = 0;
                sb1.setLength(0);
                sb2.setLength(0);
            }
        }

        if (sb1.length() > 0)
        {
            result.add(sb1.toString());
        }

        final String[] arr = new String[result.size()];
        return result.toArray(arr);
    }

    public static void LogError(Object source, Object message)
    {
        GetLogger(source).error(message);
    }

    public static void LogInfo(Object source, Object message)
    {
        GetLogger(source).info(message);
    }

    public static void LogInfo(Object source, String format, Object... values)
    {
        GetLogger(source).info(Format(format, values));
    }

    public static void LogInfoIfDebug(Object source, Object message)
    {
        if (Settings.isDebug) {
            LogInfo(source, message);
        }
    }

    public static void LogInfoIfDebug(Object source, String format, Object... values)
    {
        if (Settings.isDebug) {
            LogInfo(source, format, values);
        }
    }

    public static void LogWarning(Object source, Object message)
    {
        GetLogger(source).warn(message);
    }

    public static <T, N extends Comparable<N>> N Max(T[] list, FuncT1<N, T> getProperty)
    {
        N best = null;
        for (T t : list)
        {
            if (t != null) {
                N prop = getProperty.Invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) > 0))
                {
                    best = prop;
                }
            }
        }

        return best;
    }


    public static <T, N extends Comparable<N>> N Max(Iterable<T> list, FuncT1<N, T> getProperty)
    {
        N best = null;
        for (T t : list)
        {
            if (t != null) {
                N prop = getProperty.Invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) > 0))
                {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static <T, N extends Comparable<N>> T FindMin(Iterable<T> list, FuncT1<N, T> getProperty)
    {
        N best = null;
        T result = null;
        for (T t : list)
        {
            if (t != null) {
                N prop = getProperty.Invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0))
                {
                    best = prop;
                    result = t;
                }
            }
        }

        return result;
    }

    public static <T, N extends Comparable<N>> T FindMin(T[] list, FuncT1<N, T> getProperty)
    {
        N best = null;
        T result = null;
        for (T t : list)
        {
            if (t != null) {
                N prop = getProperty.Invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0))
                {
                    best = prop;
                    result = t;
                }
            }
        }

        return result;
    }

    public static <T, N extends Comparable<N>> N Min(T[] list, FuncT1<N, T> getProperty)
    {
        N best = null;
        for (T t : list)
        {
            if (t != null) {
                N prop = getProperty.Invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0))
                {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static <T, N extends Comparable<N>> N Min(Iterable<T> list, FuncT1<N, T> getProperty)
    {
        N best = null;
        for (T t : list)
        {
            if (t != null) {
                N prop = getProperty.Invoke(t);
                if (prop != null && (best == null || prop.compareTo(best) < 0))
                {
                    best = prop;
                }
            }
        }

        return best;
    }

    public static <T, N> ArrayList<N> Map(T[] list, FuncT1<N, T> predicate)
    {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null) {
            for (T t : list)
            {
                res.add(predicate.Invoke(t));
            }
        }

        return res;
    }

    public static <T, N> ArrayList<N> Map(Iterable<T> list, FuncT1<N, T> predicate)
    {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null) {
            for (T t : list)
            {
                res.add(predicate.Invoke(t));
            }
        }

        return res;
    }

    public static <T, N> ArrayList<N> MapAsNonnull(T[] list, FuncT1<N, T> predicate)
    {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null)
        {
            for (T t : list)
            {
                N n = predicate.Invoke(t);
                if (n != null)
                {
                    res.add(predicate.Invoke(t));
                }
            }
        }

        return res;
    }

    public static <T, N> ArrayList<N> MapAsNonnull(Iterable<T> list, FuncT1<N, T> predicate)
    {
        final ArrayList<N> res = new ArrayList<>();
        if (list != null)
        {
            for (T t : list)
            {
                N n = predicate.Invoke(t);
                if (n != null)
                {
                    res.add(predicate.Invoke(t));
                }
            }
        }

        return res;
    }

    public static int ParseInt(String value, int defaultValue)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex)
        {
            return defaultValue;
        }
    }

    public static String PopBuilder(StringBuilder stringBuilder) {
        String result = stringBuilder.toString();
        stringBuilder.setLength(0);
        return result;
    }

    public static <T> float Sum(Iterable<T> list, FuncT1<Float, T> predicate)
    {
        float sum = 0;
        if (list == null) {
            return sum;
        }
        for (T t : list)
        {
            sum += predicate.Invoke(t);
        }
        return sum;
    }

    public static <T> int SumInt(Iterable<T> list, FuncT1<Integer, T> predicate)
    {
        int sum = 0;
        if (list == null) {
            return sum;
        }
        for (T t : list)
        {
            sum += predicate.Invoke(t);
        }
        return sum;
    }

    public static <T> T SafeCast(Object o, Class<T> type)
    {
        return type.isInstance(o) ? (T)o : null;
    }

}