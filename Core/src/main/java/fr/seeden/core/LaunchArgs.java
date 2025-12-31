package fr.seeden.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LaunchArgs {

    private static final String PREFIX = "-";

    /**
     * Compute launch {@code args} and create a computed args map from it depending on the {@code availableArgs} map.
     * - Missing arguments will have their {@code missingDefaultValue}s as their launch value.
     * - Arguments with optional value will have their value set to either the actual value they have been set to in the
     * launch arguments or, if not set, the {@code notSetDefaultValue}.
     * Quite useful for easy toggle-use boolean args, removing useless value. Ex: to enable something, use -enableThing
     * instead of -enableThing=true, and if -enableThing is missing, it means it is disabled.
     * - Arguments with mandatory values are considered missing if their respective values are not set.
     * @param args The actual launch arguments
     * @param availableArgs The available launch arguments for this program
     * @return A computed arguments map to easily retrieve argument value with its key
     */
    public static ComputedArgs compute(String[] args, HashMap<String, Arg<?>> availableArgs){
        ComputedArgs computedArgs = new ComputedArgs(availableArgs);
        for (String arg : args) {
            if(!arg.startsWith(PREFIX)) continue;
            arg = arg.substring(PREFIX.length());

            if(!availableArgs.containsKey(arg) && !arg.contains("=")) continue;

            String argKey = arg;
            Arg<?> argModel = availableArgs.get(argKey);
            Object argValue = argModel!=null ? argModel.getNotSetDefaultValue() : null;

            if(argModel==null){
                String[] argNode = arg.split("=", 2);
                argKey = argNode[0];
                argModel = availableArgs.get(argKey);
                if(argModel==null) continue;
                String rawValue = argNode[1];
                argValue = argModel.parseValue(rawValue);
            }
            if(argValue==null) continue;

            computedArgs.set(argKey, argValue);
        }
        return computedArgs;
    }
    public static ComputedArgs compute(String[] args, Arg<?>... availableArgs){
        return compute(args, availableArgsMapFrom(availableArgs));
    }

    private static HashMap<String, Arg<?>> availableArgsMapFrom(Arg<?>... args){
        HashMap<String, Arg<?>> availableArgs = new HashMap<>();
        for (Arg<?> arg : args) {
            availableArgs.put(arg.getKey(), arg);
        }
        return availableArgs;
    }

    public static class ComputedArgs {

        private final HashMap<String, Object> computedArgs = new HashMap<>();

        private ComputedArgs(HashMap<String, Arg<?>> availableArgs){
            for (Map.Entry<String, Arg<?>> entry : availableArgs.entrySet()) {
                computedArgs.put(entry.getKey(), entry.getValue().getMissingDefaultValue());
            }
        }

        private void set(String key, Object value){
            computedArgs.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Arg<T> arg){
            if(!computedArgs.containsKey(arg.getKey())) return null;
            return (T) computedArgs.get(arg.getKey());
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder("Args:");
            for (Map.Entry<String, Object> entry : computedArgs.entrySet()) {
                s.append("\n").append(entry.getKey()).append("=").append(entry.getValue()).append("/").append(entry.getValue().getClass());
            }
            return s.toString();
        }
    }

    /**
     * A launch argument ignored if its value is not set, such as its defaultValue is set (same as when argument is missing)
     * @param <T> The argument value type (see {@link Arg#parseValue(String)} below for supported types)
     */
    public static class ArgMandatoryValue<T> extends Arg<T> {
        public ArgMandatoryValue(String key, T defaultValue) {
            super(key, defaultValue, defaultValue);
        }
    }

    /**
     * A launch argument with two default values. One if the argument is missing, one if its value is not set
     * @param <T> The argument value type (see {@link Arg#parseValue(String)} below for supported types)
     */
    public static class Arg<T> {
        private final String key;
        private final T missingDefaultValue; // The defaultValue if the arg is missing in the launch arguments
        private final T notSetDefaultValue; // The defaultValue if the arg is present but its value is not set
        private final Class<?> type;

        public Arg(String key, T missingDefaultValue, T notSetDefaultValue) {
            this.key = key;
            this.missingDefaultValue = missingDefaultValue;
            this.notSetDefaultValue = notSetDefaultValue;
            this.type = missingDefaultValue.getClass();
        }

        public String getKey() {
            return key;
        }

        public T getMissingDefaultValue() {
            return missingDefaultValue;
        }

        public T getNotSetDefaultValue() {
            return notSetDefaultValue;
        }

        @SuppressWarnings("unchecked")
        public T parseValue(String rawValue){
            if(type==Boolean.class) return (T) Boolean.valueOf(rawValue);
            if(type==Byte.class) return (T) Byte.valueOf(rawValue);
            if(type==Short.class) return (T) Short.valueOf(rawValue);
            if(type==Integer.class) return (T) Integer.valueOf(rawValue);
            if(type==Long.class) return (T) Long.valueOf(rawValue);
            if(type==Float.class) return (T) Float.valueOf(rawValue);
            if(type==Double.class) return (T) Double.valueOf(rawValue);
            if(type==String.class) return (T) rawValue;
            if(List.class.isAssignableFrom(type)) return (T) List.of(rawValue.split(","));
            if(Enum.class.isAssignableFrom(type)) {
                try {
                    return (T) Enum.valueOf((Class<? extends Enum>) type, rawValue.toUpperCase());
                }
                catch(IllegalArgumentException e) {
                    return null;
                }
            };
            return null;
        }
    }
}