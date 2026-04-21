package net.skds.wpo.fluiddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.skds.wpo.WPO;

import java.util.Map;
import java.util.Optional;

public final class FluidStateSerializers {

    private static final String NAME_TAG = "Name";
    private static final String PROPERTIES_TAG = "Properties";

    private FluidStateSerializers() {
    }

    public static FluidState readFluidState(CompoundTag tag) {
        if (!tag.contains(NAME_TAG, CompoundTag.TAG_STRING)) {
            return Fluids.EMPTY.defaultFluidState();
        }

        ResourceLocation id = ResourceLocation.parse(tag.getString(NAME_TAG));
        Fluid fluid = BuiltInRegistries.FLUID.containsKey(id) ? BuiltInRegistries.FLUID.get(id) : Fluids.EMPTY;

        FluidState fluidState = fluid.defaultFluidState();
        if (!tag.contains(PROPERTIES_TAG, CompoundTag.TAG_COMPOUND)) {
            return fluidState;
        }

        CompoundTag propertyTag = tag.getCompound(PROPERTIES_TAG);
        StateDefinition<Fluid, FluidState> definition = fluid.getStateDefinition();
        for (String name : propertyTag.getAllKeys()) {
            Property<?> property = definition.getProperty(name);
            if (property != null) {
                fluidState = setValue(fluidState, property, name, propertyTag, tag);
            }
        }
        return fluidState;
    }

    public static CompoundTag writeFluidState(FluidState fluidState) {
        CompoundTag tag = new CompoundTag();
        ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluidState.getType());
        if (key == null) {
            key = BuiltInRegistries.FLUID.getKey(Fluids.EMPTY);
        }
        tag.putString(NAME_TAG, key.toString());

        Map<Property<?>, Comparable<?>> values = fluidState.getValues();
        if (!values.isEmpty()) {
            CompoundTag propertyTag = new CompoundTag();
            for (Map.Entry<Property<?>, Comparable<?>> entry : values.entrySet()) {
                propertyTag.putString(entry.getKey().getName(), getName(entry.getKey(), entry.getValue()));
            }
            tag.put(PROPERTIES_TAG, propertyTag);
        }

        return tag;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getName(Property<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValue(
            S stateHolder,
            Property<T> property,
            String propertyName,
            CompoundTag propertiesTag,
            CompoundTag fullTag
    ) {
        Optional<T> value = property.getValue(propertiesTag.getString(propertyName));
        if (value.isPresent()) {
            return stateHolder.setValue(property, value.get());
        }

        WPO.LOGGER.warn(
                "Unable to read property {}={} for fluid state {}",
                propertyName,
                propertiesTag.getString(propertyName),
                fullTag
        );
        return stateHolder;
    }
}
