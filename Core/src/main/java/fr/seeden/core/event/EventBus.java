package fr.seeden.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class EventBus {

    private static final List<EventListener> LISTENERS = new ArrayList<>();

    public static void addEventListener(EventListener listener){
        LISTENERS.add(listener);
    }

    public static void dispatchEvent(AppEvent event){
        try {
            for (EventListener listener : LISTENERS) {
                for (Method method : listener.getClass().getDeclaredMethods()) {
                    if(method.isAnnotationPresent(EventHandler.class) && method.getParameterCount()==1
                            && method.getParameterTypes()[0].isInstance(event)){

                        method.invoke(listener, event);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private EventBus(){}
}