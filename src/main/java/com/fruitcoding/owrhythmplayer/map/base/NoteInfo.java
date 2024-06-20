package com.fruitcoding.owrhythmplayer.map.base;

/**
 * 노트 입력 정보
 *
 * @param nanoTime 노트 누르기/떼기 시간
 * @param isPress 노트 누르기/떼기 여부 (True: 누르기, False: 떼기)
 * @param button 누를 버튼
 */
public record NoteInfo(long nanoTime, boolean isPress, char button) {}