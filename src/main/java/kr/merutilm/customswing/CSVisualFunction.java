package kr.merutilm.customswing;

import java.util.List;

public class CSVisualFunction<E> {
    private static final int REFRESH_MILLIS = 2000;
    private final List<E> list;
    private List<E> copiedImmutableList;
    private long nextRefreshMillis;

    public CSVisualFunction(List<E> list) {
        this.list = list;
        this.nextRefreshMillis = System.currentTimeMillis() + REFRESH_MILLIS;
        copiedImmutableList = List.copyOf(list);
    }

    public void refreshImmediately(){
        nextRefreshMillis = 0;
    }

    public void refresh(Refresher<E> elementRefresher) {
        if (nextRefreshMillis <= System.currentTimeMillis()) {
            copiedImmutableList = List.copyOf(list);
            this.nextRefreshMillis = System.currentTimeMillis() + REFRESH_MILLIS;
        }

        for (E e : copiedImmutableList) {
            elementRefresher.refresh(e);
        }
    }

    @FunctionalInterface
    public interface Refresher<E> {
        void refresh(E element);
    }
}
