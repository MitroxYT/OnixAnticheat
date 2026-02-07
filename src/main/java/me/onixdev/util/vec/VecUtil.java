package me.onixdev.util.vec;



import com.github.retrooper.packetevents.util.Vector3d;
import me.onixdev.util.grimentity.boxes.SimpleCollisionBox;
import me.onixdev.util.math.MathUtil;
import org.bukkit.util.Vector;

public class VecUtil {
    public static Vector cutBoxToVector(Vector vectorToCutTo, Vector min, Vector max) {
        SimpleCollisionBox box = new SimpleCollisionBox(min, max).sort();
        return cutBoxToVector(vectorToCutTo, box);
    }

    public static Vector cutBoxToVector(Vector vectorCutTo, SimpleCollisionBox box) {
        return new Vector(MathUtil.clamp(vectorCutTo.getX(), box.minX, box.maxX),
                me.onixdev.util.math.MathUtil.clamp(vectorCutTo.getY(), box.minY, box.maxY),
                MathUtil.clamp(vectorCutTo.getZ(), box.minZ, box.maxZ));
    }

    public static Vector fromVec3d(Vector3d vector3d) {
        return new Vector(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    public static Vector3d clampVector(Vector3d toClamp) {
        double x = MathUtil.clamp(toClamp.getX(), -3.0E7D, 3.0E7D);
        double y = MathUtil.clamp(toClamp.getY(), -2.0E7D, 2.0E7D);
        double z = MathUtil.clamp(toClamp.getZ(), -3.0E7D, 3.0E7D);

        return new Vector3d(x, y, z);
    }
}
