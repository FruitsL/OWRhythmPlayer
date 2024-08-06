package com.fruitcoding.owrhythmplayer.map.base;

/**
 * 노트 입력 정보
 *
 * @param nanoTime 노트 누르기/떼기 시간
 * @param isPress 노트 누르기/떼기 여부 (True: 누르기, False: 떼기)
 * @param keyCode 누를 버튼의 키 코드
 */
public record NoteInfo(long nanoTime, boolean isPress, int keyCode) {}