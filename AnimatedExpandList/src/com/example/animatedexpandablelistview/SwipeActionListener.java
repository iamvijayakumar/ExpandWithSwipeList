package com.example.animatedexpandablelistview;

/**
 * Interface that listeners of swipe events should implement
 */
public interface SwipeActionListener{
    public boolean hasActions(int position);
    public boolean shouldDismiss(int position, int direction);
    public void onSwipe(int[] position, int[] direction);
}  


