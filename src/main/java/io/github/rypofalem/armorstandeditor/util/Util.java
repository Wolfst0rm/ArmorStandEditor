package io.github.rypofalem.armorstandeditor.util;

public abstract class Util {

    public static final double FULL_CIRCLE = Math.PI * 2;

    public static <T extends Enum<?>> String getEnumList(Class<T> enumType) {
        return getEnumList(enumType, " | ");
    }

    public static <T extends Enum<?>> String getEnumList(Class<T> enumType, String delimiter) {
        StringBuilder list = new StringBuilder();
        boolean put = false;
        for (Enum<?> e : enumType.getEnumConstants()) {
            list.append(e.toString()).append(delimiter);
            put = true;
        }
        if (put) list = new StringBuilder(list.substring(0, list.length() - delimiter.length()));
        return list.toString();
    }

    public static double addAngle(double current, double angleChange) {
        current += angleChange;
        current = fixAngle(current, angleChange);
        return current;
    }

    public static double subAngle(double current, double angleChange) {
        current -= angleChange;
        current = fixAngle(current, angleChange);
        return current;
    }

    //clamps angle to 0 if it exceeds 2PI rad (360 degrees), is closer to 0 than angleChange value, or is closer to 2PI rad than 2PI rad - angleChange value.
    private static double fixAngle(double angle, double angleChange) {
        if (angle > FULL_CIRCLE) {
            return 0;
        }

        if (angle > 0 && angle < angleChange && angle < angleChange / 2) {
            return 0;
        }

        if (angle > FULL_CIRCLE - angle && angle > FULL_CIRCLE - (angleChange / 2)) {
            return 0;
        }

        return angle;
    }
}