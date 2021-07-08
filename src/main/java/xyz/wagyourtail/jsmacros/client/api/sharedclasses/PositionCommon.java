package xyz.wagyourtail.jsmacros.client.api.sharedclasses;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * @author Wagyourtail
 * @since 1.2.6 [citation needed]
 */
@SuppressWarnings("unused")
public class PositionCommon {

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Pos2D {
        public static final Pos2D ZERO = new Pos2D(0, 0);
        public double x;
        public double y;

        public Pos2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Pos2D add(Pos2D pos) {
            return new Pos2D(x+pos.x, y+pos.y);
        }

        public Pos2D multiply(Pos2D pos) {
            return new Pos2D(x*pos.x, y*pos.y);
        }

        public String toString() {
            return String.format("%f, %f", x, y);
        }

        public Pos3D to3D() {
            return new Pos3D(x, y, 0);
        }

        public Vec2D toVector() {
            return new Vec2D(ZERO, this);
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Pos3D extends Pos2D {
        public static final Pos3D ZERO = new Pos3D(0, 0, 0);
        public double z;

        public Pos3D(Pos3D vec) {
            this(vec.x, vec.y, vec.z);
        }

        public Pos3D(Vec3 vec) {
            this(vec.xCoord, vec.yCoord, vec.zCoord);
        }

        public Pos3D(double x, double y, double z) {
            super(x, y);
            this.z = z;
        }

        public double getZ() {
            return z;
        }

        public Pos3D add(Pos3D pos) {
            return new Pos3D(x+pos.x, y+pos.y, z*pos.z);
        }

        public Pos3D multiply(Pos3D pos) {
            return new Pos3D(x*pos.x, y*pos.y, z*pos.z);
        }

        public String toString() {
            return String.format("%f, %f, %f", x, y, z);
        }

        @Override
        public Vec3D toVector() {
            return new Vec3D(ZERO, this);
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Vec2D {
        public double x1;
        public double y1;
        public double x2;
        public double y2;


        public Vec2D(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public Vec2D(Pos2D start, Pos2D end) {
            this.x1 = start.x;
            this.y1 = start.y;
            this.x2 = end.x;
            this.y2 = end.y;
        }


        public double getX1() {
            return x1;
        }

        public double getY1() {
            return y1;
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

        public double getDeltaX() {
            return x2 - x1;
        }

        public double getDeltaY() {
            return y2 - y1;
        }

        public Pos2D getStart() {
            return new Pos2D(x1, y1);
        }

        public Pos2D getEnd() {
            return new Pos2D(x2, y2);
        }

        public double getMagnitude() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            return Math.sqrt(dx*dx + dy*dy);
        }

        public Vec2D add(Vec2D vec) {
            return new Vec2D(getStart().add(vec.getStart()), getEnd().add(vec.getEnd()));
        }

        public Vec2D multiply(Vec2D vec) {
            return new Vec2D(getStart().multiply(vec.getStart()), getEnd().multiply(vec.getEnd()));
        }

        public double dotProduct(Vec2D vec) {
            double dx1 = x2 - x1;
            double dx2 = vec.x2 - vec.x1;
            double dy1 = y2 - y1;
            double dy2 = vec.y2 - vec.y1;
            return dx1 * dx2 + dy1 * dy2;
        }

        public Vec2D reverse() {
            return new Vec2D(getEnd(), getStart());
        }

        public String toString() {
            return String.format("%f, %f -> %f, %f", x1, y1, x2, y2);
        }

        public Vec3D to3D() {
            return new Vec3D(getStart().to3D(), getEnd().to3D());
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Vec3D extends Vec2D {
        public double z1;
        public double z2;

        public Vec3D(double x1, double y1, double z1, double x2, double y2, double z2) {
            super(x1, y1, x2, y2);
            this.z1 = z1;
            this.z2 = z2;
        }

        public Vec3D(Pos3D start, Pos3D end) {
            super(start, end);
            this.z1 = start.z;
            this.z2 = end.z;
        }

        public double getZ1() {
            return z1;
        }

        public double getZ2() {
            return z2;
        }

        public double getDeltaZ() {
            return z2 - z1;
        }

        @Override
        public Pos3D getStart() {
            return new Pos3D(x1, y1, z1);
        }

        @Override
        public Pos3D getEnd() {
            return new Pos3D(x2, y2, z2);
        }

        @Override
        public double getMagnitude() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }

        public Vec3D add(Vec3D vec) {
            return new Vec3D(getStart().add(vec.getStart()), getEnd().add(vec.getEnd()));
        }

        public Vec3D multiply(Vec3D vec) {
            return new Vec3D(getStart().multiply(vec.getStart()), getEnd().multiply(vec.getEnd()));
        }

        public float getPitch() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            double xz = Math.sqrt(dx*dx + dz*dz);
            return  90F - (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(xz, -dy)));
        }

        public float getYaw() {
            double dx = x2 - x1;
            double dz = z2 - z1;
            return (float) -MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(dx, dz)));
        }

        public double dotProduct(Vec3D vec) {
            double dz1 = z1 - z2;
            double dz2 = vec.z2 - vec.z1;
            return super.dotProduct(vec) + dz1 * dz2;
        }

        public Vec3D crossProduct(Vec3D vec) {
            double dx1 = x2 - x1;
            double dx2 = vec.x2 - vec.x1;
            double dy1 = y2 - y1;
            double dy2 = vec.y2 - vec.y1;
            double dz1 = z1 - z2;
            double dz2 = vec.z2 - vec.z1;
            return new Vec3D(0, 0, 0, dy1*dz2 - dz1*dy2, dz1*dx2 - dx1*dz2, dx1*dy2 - dy1*dx2);
        }

        @Override
        public Vec3D reverse() {
            return new Vec3D(getEnd(), getStart());
        }

        @Override
        public String toString() {
            return String.format("%f, %f, %f -> %f, %f, %f", x1, y1, z1, x2, y2, z2);
        }
    }

}
