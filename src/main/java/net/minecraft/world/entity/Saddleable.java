package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public interface Saddleable {
    boolean isSaddleable();

    void equipSaddle(ItemStack p_344009_, @Nullable SoundSource p_21748_);

    default SoundEvent getSaddleSoundEvent() {
        return SoundEvents.HORSE_SADDLE;
    }

    boolean isSaddled();
}