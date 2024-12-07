package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum Direction implements StringRepresentable {
    DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

    public static final StringRepresentable.EnumCodec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values);
    public static final Codec<Direction> VERTICAL_CODEC = CODEC.validate(Direction::verifyVertical);
    public static final IntFunction<Direction> BY_ID = ByIdMap.continuous(Direction::get3DDataValue, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, Direction> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Direction::get3DDataValue);
    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Direction.Axis axis;
    private final Direction.AxisDirection axisDirection;
    private final Vec3i normal;
    private static final Direction[] VALUES = values();
    private static final Direction[] BY_3D_DATA = Arrays.stream(VALUES)
        .sorted(Comparator.comparingInt(p_235687_ -> p_235687_.data3d))
        .toArray(Direction[]::new);
    private static final Direction[] BY_2D_DATA = Arrays.stream(VALUES)
        .filter(p_235685_ -> p_235685_.getAxis().isHorizontal())
        .sorted(Comparator.comparingInt(p_235683_ -> p_235683_.data2d))
        .toArray(Direction[]::new);

    private Direction(
        final int p_122356_,
        final int p_122357_,
        final int p_122358_,
        final String p_122359_,
        final Direction.AxisDirection p_122360_,
        final Direction.Axis p_122361_,
        final Vec3i p_122362_
    ) {
        this.data3d = p_122356_;
        this.data2d = p_122358_;
        this.oppositeIndex = p_122357_;
        this.name = p_122359_;
        this.axis = p_122361_;
        this.axisDirection = p_122360_;
        this.normal = p_122362_;
    }

    public static Direction[] orderedByNearest(Entity p_122383_) {
        float f = p_122383_.getViewXRot(1.0F) * (float) (Math.PI / 180.0);
        float f1 = -p_122383_.getViewYRot(1.0F) * (float) (Math.PI / 180.0);
        float f2 = Mth.sin(f);
        float f3 = Mth.cos(f);
        float f4 = Mth.sin(f1);
        float f5 = Mth.cos(f1);
        boolean flag = f4 > 0.0F;
        boolean flag1 = f2 < 0.0F;
        boolean flag2 = f5 > 0.0F;
        float f6 = flag ? f4 : -f4;
        float f7 = flag1 ? -f2 : f2;
        float f8 = flag2 ? f5 : -f5;
        float f9 = f6 * f3;
        float f10 = f8 * f3;
        Direction direction = flag ? EAST : WEST;
        Direction direction1 = flag1 ? UP : DOWN;
        Direction direction2 = flag2 ? SOUTH : NORTH;
        if (f6 > f8) {
            if (f7 > f9) {
                return makeDirectionArray(direction1, direction, direction2);
            } else {
                return f10 > f7 ? makeDirectionArray(direction, direction2, direction1) : makeDirectionArray(direction, direction1, direction2);
            }
        } else if (f7 > f10) {
            return makeDirectionArray(direction1, direction2, direction);
        } else {
            return f9 > f7 ? makeDirectionArray(direction2, direction, direction1) : makeDirectionArray(direction2, direction1, direction);
        }
    }

    private static Direction[] makeDirectionArray(Direction p_122399_, Direction p_122400_, Direction p_122401_) {
        return new Direction[]{p_122399_, p_122400_, p_122401_, p_122401_.getOpposite(), p_122400_.getOpposite(), p_122399_.getOpposite()};
    }

    public static Direction rotate(Matrix4f p_254393_, Direction p_254252_) {
        Vec3i vec3i = p_254252_.getNormal();
        Vector4f vector4f = p_254393_.transform(new Vector4f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), 0.0F));
        return getNearest(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public static Collection<Direction> allShuffled(RandomSource p_235668_) {
        return Util.shuffledCopy(values(), p_235668_);
    }

    public static Stream<Direction> stream() {
        return Stream.of(VALUES);
    }

    public Quaternionf getRotation() {
        return switch (this) {
            case DOWN -> new Quaternionf().rotationX((float) Math.PI);
            case UP -> new Quaternionf();
            case NORTH -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) Math.PI);
            case SOUTH -> new Quaternionf().rotationX((float) (Math.PI / 2));
            case WEST -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2));
            case EAST -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) (-Math.PI / 2));
        };
    }

    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public Direction.AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public static Direction getFacingAxis(Entity p_175358_, Direction.Axis p_175359_) {
        return switch (p_175359_) {
            case X -> EAST.isFacingAngle(p_175358_.getViewYRot(1.0F)) ? EAST : WEST;
            case Y -> p_175358_.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
            case Z -> SOUTH.isFacingAngle(p_175358_.getViewYRot(1.0F)) ? SOUTH : NORTH;
        };
    }

    public Direction getOpposite() {
        return from3DDataValue(this.oppositeIndex);
    }

    public Direction getClockWise(Direction.Axis p_175363_) {
        return switch (p_175363_) {
            case X -> this != WEST && this != EAST ? this.getClockWiseX() : this;
            case Y -> this != UP && this != DOWN ? this.getClockWise() : this;
            case Z -> this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
        };
    }

    public Direction getCounterClockWise(Direction.Axis p_175365_) {
        return switch (p_175365_) {
            case X -> this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
            case Y -> this != UP && this != DOWN ? this.getCounterClockWise() : this;
            case Z -> this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
        };
    }

    public Direction getClockWise() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    private Direction getClockWiseX() {
        return switch (this) {
            case DOWN -> SOUTH;
            case UP -> NORTH;
            case NORTH -> DOWN;
            case SOUTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction getCounterClockWiseX() {
        return switch (this) {
            case DOWN -> NORTH;
            case UP -> SOUTH;
            case NORTH -> UP;
            case SOUTH -> DOWN;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction getClockWiseZ() {
        return switch (this) {
            case DOWN -> WEST;
            case UP -> EAST;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST -> UP;
            case EAST -> DOWN;
        };
    }

    private Direction getCounterClockWiseZ() {
        return switch (this) {
            case DOWN -> EAST;
            case UP -> WEST;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST -> DOWN;
            case EAST -> UP;
        };
    }

    public Direction getCounterClockWise() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public Vector3f step() {
        return new Vector3f((float)this.getStepX(), (float)this.getStepY(), (float)this.getStepZ());
    }

    public String getName() {
        return this.name;
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }

    @Nullable
    public static Direction byName(@Nullable String p_122403_) {
        return CODEC.byName(p_122403_);
    }

    public static Direction from3DDataValue(int p_122377_) {
        return BY_3D_DATA[Mth.abs(p_122377_ % BY_3D_DATA.length)];
    }

    public static Direction from2DDataValue(int p_122408_) {
        return BY_2D_DATA[Mth.abs(p_122408_ % BY_2D_DATA.length)];
    }

    @Nullable
    public static Direction fromDelta(int p_278323_, int p_278296_, int p_278347_) {
        if (p_278323_ == 0) {
            if (p_278296_ == 0) {
                if (p_278347_ > 0) {
                    return SOUTH;
                }

                if (p_278347_ < 0) {
                    return NORTH;
                }
            } else if (p_278347_ == 0) {
                if (p_278296_ > 0) {
                    return UP;
                }

                return DOWN;
            }
        } else if (p_278296_ == 0 && p_278347_ == 0) {
            if (p_278323_ > 0) {
                return EAST;
            }

            return WEST;
        }

        return null;
    }

    public static Direction fromYRot(double p_122365_) {
        return from2DDataValue(Mth.floor(p_122365_ / 90.0 + 0.5) & 3);
    }

    public static Direction fromAxisAndDirection(Direction.Axis p_122388_, Direction.AxisDirection p_122389_) {
        return switch (p_122388_) {
            case X -> p_122389_ == Direction.AxisDirection.POSITIVE ? EAST : WEST;
            case Y -> p_122389_ == Direction.AxisDirection.POSITIVE ? UP : DOWN;
            case Z -> p_122389_ == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
        };
    }

    public float toYRot() {
        return (float)((this.data2d & 3) * 90);
    }

    public static Direction getRandom(RandomSource p_235673_) {
        return Util.getRandom(VALUES, p_235673_);
    }

    public static Direction getNearest(double p_122367_, double p_122368_, double p_122369_) {
        return getNearest((float)p_122367_, (float)p_122368_, (float)p_122369_);
    }

    public static Direction getNearest(float p_122373_, float p_122374_, float p_122375_) {
        Direction direction = NORTH;
        float f = Float.MIN_VALUE;

        for (Direction direction1 : VALUES) {
            float f1 = p_122373_ * (float)direction1.normal.getX()
                + p_122374_ * (float)direction1.normal.getY()
                + p_122375_ * (float)direction1.normal.getZ();
            if (f1 > f) {
                f = f1;
                direction = direction1;
            }
        }

        return direction;
    }

    public static Direction getNearest(Vec3 p_328055_) {
        return getNearest(p_328055_.x, p_328055_.y, p_328055_.z);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static DataResult<Direction> verifyVertical(Direction p_194529_) {
        return p_194529_.getAxis().isVertical() ? DataResult.success(p_194529_) : DataResult.error(() -> "Expected a vertical direction");
    }

    public static Direction get(Direction.AxisDirection p_122391_, Direction.Axis p_122392_) {
        for (Direction direction : VALUES) {
            if (direction.getAxisDirection() == p_122391_ && direction.getAxis() == p_122392_) {
                return direction;
            }
        }

        throw new IllegalArgumentException("No such direction: " + p_122391_ + " " + p_122392_);
    }

    public Vec3i getNormal() {
        return this.normal;
    }

    public boolean isFacingAngle(float p_122371_) {
        float f = p_122371_ * (float) (Math.PI / 180.0);
        float f1 = -Mth.sin(f);
        float f2 = Mth.cos(f);
        return (float)this.normal.getX() * f1 + (float)this.normal.getZ() * f2 > 0.0F;
    }

    public static enum Axis implements StringRepresentable, Predicate<Direction> {
        X("x") {
            @Override
            public int choose(int p_122496_, int p_122497_, int p_122498_) {
                return p_122496_;
            }

            @Override
            public double choose(double p_122492_, double p_122493_, double p_122494_) {
                return p_122492_;
            }
        },
        Y("y") {
            @Override
            public int choose(int p_122510_, int p_122511_, int p_122512_) {
                return p_122511_;
            }

            @Override
            public double choose(double p_122506_, double p_122507_, double p_122508_) {
                return p_122507_;
            }
        },
        Z("z") {
            @Override
            public int choose(int p_122524_, int p_122525_, int p_122526_) {
                return p_122526_;
            }

            @Override
            public double choose(double p_122520_, double p_122521_, double p_122522_) {
                return p_122522_;
            }
        };

        public static final Direction.Axis[] VALUES = values();
        public static final StringRepresentable.EnumCodec<Direction.Axis> CODEC = StringRepresentable.fromEnum(Direction.Axis::values);
        private final String name;

        Axis(final String p_122456_) {
            this.name = p_122456_;
        }

        @Nullable
        public static Direction.Axis byName(String p_122474_) {
            return CODEC.byName(p_122474_);
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Direction.Axis getRandom(RandomSource p_235689_) {
            return Util.getRandom(VALUES, p_235689_);
        }

        public boolean test(@Nullable Direction p_122472_) {
            return p_122472_ != null && p_122472_.getAxis() == this;
        }

        public Direction.Plane getPlane() {
            return switch (this) {
                case X, Z -> Direction.Plane.HORIZONTAL;
                case Y -> Direction.Plane.VERTICAL;
            };
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public abstract int choose(int p_122466_, int p_122467_, int p_122468_);

        public abstract double choose(double p_122463_, double p_122464_, double p_122465_);
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        private AxisDirection(final int p_122538_, final String p_122539_) {
            this.step = p_122538_;
            this.name = p_122539_;
        }

        public int getStep() {
            return this.step;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public Direction.AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
        HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
        VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

        private final Direction[] faces;
        private final Direction.Axis[] axis;

        private Plane(final Direction[] p_122555_, final Direction.Axis[] p_122556_) {
            this.faces = p_122555_;
            this.axis = p_122556_;
        }

        public Direction getRandomDirection(RandomSource p_235691_) {
            return Util.getRandom(this.faces, p_235691_);
        }

        public Direction.Axis getRandomAxis(RandomSource p_235693_) {
            return Util.getRandom(this.axis, p_235693_);
        }

        public boolean test(@Nullable Direction p_122559_) {
            return p_122559_ != null && p_122559_.getAxis().getPlane() == this;
        }

        @Override
        public Iterator<Direction> iterator() {
            return Iterators.forArray(this.faces);
        }

        public Stream<Direction> stream() {
            return Arrays.stream(this.faces);
        }

        public List<Direction> shuffledCopy(RandomSource p_235695_) {
            return Util.shuffledCopy(this.faces, p_235695_);
        }

        public int length() {
            return this.faces.length;
        }
    }
}