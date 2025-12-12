package me.onixdev.manager;

import com.github.retrooper.packetevents.protocol.player.User;
import dev.onixac.api.manager.IPlayerDataManager;
import dev.onixac.api.user.IOnixUser;
import me.onixdev.user.OnixUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDatamanager implements IPlayerDataManager {
    private ConcurrentHashMap<UUID, OnixUser> data = new ConcurrentHashMap<>();
    public void add(User user) {
        data.put(user.getUUID(),new OnixUser(user));
    }
    public OnixUser get(UUID uuid) {
        try {
            return data.get(uuid);
        } catch (Exception e) {
         //   throw new RuntimeException(e);
        }
        return null;
    }
    public OnixUser get(User user) {
        try {
            return data.get(user.getUUID());
        } catch (Exception e) {
           // throw new RuntimeException(e);
        }
        return null;
    }
    public void remove(UUID uuid) {
        data.remove(uuid);
    }
    public Collection<OnixUser> getAllData() {
        return data.values();
    }

    @Override
    public IOnixUser getUser(UUID uuid) {
        return data.get(uuid);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@link
     *     equals(Object) equals} method, then calling the {@code
     *     hashCode} method on each of the two objects must produce the
     *     same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link equals(Object) equals} method, then
     *     calling the {@code hashCode} method on each of the two objects
     *     must produce distinct integer results.  However, the programmer
     *     should be aware that producing distinct integer results for
     *     unequal objects may improve the performance of hash tables.
     * </ul>
     *
     * @return a hash code value for this object.
     * @implSpec As far as is reasonably practical, the {@code hashCode} method defined
     * by class {@code Object} returns distinct integers for distinct objects.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return 0;
    }
}
