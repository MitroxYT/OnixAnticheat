package dev.onixac.api.user.data;

public interface IPlayerClickData {

    /**
     * @return Количество кликов в tick
     */

    int getCPS();

    /**
     * @return true Если игрок отправил анимацию между tick пакетами
     */
    boolean isClicking();
}
