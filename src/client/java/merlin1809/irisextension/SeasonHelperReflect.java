package merlin1809.irisextension;

import net.minecraft.client.multiplayer.ClientLevel;
import java.lang.reflect.Method;

public class SeasonHelperReflect {
    private static boolean initialized = false;
    private static Method GET_SEASON_STATE;
    private static Method GET_SEASON;
    private static Method GET_SUB_SEASON;
    private static Method GET_TROPICAL_SEASON;
    private static Method GET_SEASON_CYCLE_TICKS;
    private static Method GET_DAY_DURATION;
    private static Method GET_SUB_SEASON_DURATION;
    private static Method GET_DAY;

    private static void init() {
        if (initialized) return;
        try {
            Class<?> seasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
            GET_SEASON_STATE = seasonHelper.getMethod("getSeasonState", ClientLevel.class);

            Class<?> seasonStateClass = GET_SEASON_STATE.getReturnType();
            GET_SEASON = seasonStateClass.getMethod("getSeason");
            GET_SUB_SEASON = seasonStateClass.getMethod("getSubSeason");
            GET_TROPICAL_SEASON = seasonStateClass.getMethod("getTropicalSeason");
            GET_SEASON_CYCLE_TICKS = seasonStateClass.getMethod("getSeasonCycleTicks");
            GET_DAY_DURATION = seasonStateClass.getMethod("getDayDuration");
            GET_SUB_SEASON_DURATION = seasonStateClass.getMethod("getSubSeasonDuration");
            GET_DAY = seasonStateClass.getMethod("getDay");

            initialized = true;
        } catch (Exception e) {
            Variables.isSereneSeasonsLoaded = false;
        }
    }

    private static Object getSeasonState(ClientLevel level) {
        if (!Variables.isSereneSeasonsLoaded) return null;
        init();
        if (!initialized) return null;
        try {
            return GET_SEASON_STATE.invoke(null, level);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getSeasonOrdinal(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            Object season = GET_SEASON.invoke(state);
            return ((Enum<?>) season).ordinal();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getSubSeasonOrdinal(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            Object subSeason = GET_SUB_SEASON.invoke(state);
            return ((Enum<?>) subSeason).ordinal();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getTropicalSeasonOrdinal(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            Object tropical = GET_TROPICAL_SEASON.invoke(state);
            return ((Enum<?>) tropical).ordinal();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getSeasonCycleTicks(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            return (int) GET_SEASON_CYCLE_TICKS.invoke(state);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getDayDuration(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            return (int) GET_DAY_DURATION.invoke(state);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getSubSeasonDuration(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            return (int) GET_SUB_SEASON_DURATION.invoke(state);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getDay(ClientLevel level) {
        Object state = getSeasonState(level);
        if (state == null) return 0;
        try {
            return (int) GET_DAY.invoke(state);
        } catch (Exception e) {
            return 0;
        }
    }
}
