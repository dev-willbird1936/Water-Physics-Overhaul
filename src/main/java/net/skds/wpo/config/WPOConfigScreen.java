package net.skds.wpo.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.skds.core.config.PerformancePreset;
import net.skds.wpo.WPOConfig;

public class WPOConfigScreen extends Screen {
    private static final int LABEL_X_OFFSET = 150;
    private static final int CONTROL_WIDTH = 150;

    private final Screen parent;
    private CycleButton<PerformancePreset> presetButton;
    private EditBox maxEqDistBox;
    private EditBox maxSlideDistBox;
    private EditBox maxBucketDistBox;
    private Component statusMessage = CommonComponents.EMPTY;

    public WPOConfigScreen(Screen parent) {
        super(Component.literal("Water Physics Overhaul Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = Math.max(28, this.height / 5 - 6);

        presetButton = this.addRenderableWidget(CycleButton.builder(PerformancePreset::getDisplayName)
                .withValues(PerformancePreset.values())
                .withInitialValue(WPOConfig.getPerformancePreset())
                .create(centerX, top, CONTROL_WIDTH, 20,
                        Component.translatable("wpo.config.performancePreset"),
                        (button, value) -> updateManualFieldState()));

        maxEqDistBox = createNumberBox(centerX, top + 28,
                Component.translatable("wpo.config.setMaxEqualizeDistance"),
                String.valueOf(WPOConfig.getManualMaxEqDist()));
        maxSlideDistBox = createNumberBox(centerX, top + 56,
                Component.translatable("wpo.config.setMaxSlidingDistance"),
                String.valueOf(WPOConfig.getManualMaxSlideDist()));
        maxBucketDistBox = createNumberBox(centerX, top + 98,
                Component.translatable("wpo.config.setMaxBucketDistance"),
                String.valueOf(WPOConfig.getMaxBucketDist()));

        this.addRenderableWidget(Button.builder(Component.literal("Defaults"), button -> loadDefaults())
                .bounds(centerX - 154, this.height - 28, 100, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(centerX - 50, this.height - 28, 100, 20)
                .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(centerX + 54, this.height - 28, 100, 20)
                .build());

        updateManualFieldState();
    }

    private EditBox createNumberBox(int x, int y, Component message, String value) {
        EditBox box = new EditBox(this.font, x, y, CONTROL_WIDTH, 20, message);
        box.setValue(value);
        box.setHint(message);
        this.addRenderableWidget(box);
        return box;
    }

    private void updateManualFieldState() {
        boolean custom = presetButton.getValue() == PerformancePreset.CUSTOM;
        maxEqDistBox.active = custom;
        maxSlideDistBox.active = custom;
    }

    private void loadDefaults() {
        presetButton.setValue(WPOConfig.COMMON.performancePreset.getDefault());
        maxEqDistBox.setValue(String.valueOf(WPOConfig.COMMON.maxEqDist.getDefault()));
        maxSlideDistBox.setValue(String.valueOf(WPOConfig.COMMON.maxSlideDist.getDefault()));
        maxBucketDistBox.setValue(String.valueOf(WPOConfig.COMMON.maxBucketDist.getDefault()));
        updateManualFieldState();
        statusMessage = Component.literal("Defaults loaded. Press Save to apply.");
    }

    private void saveAndClose() {
        try {
            WPOConfig.setPerformancePreset(presetButton.getValue());
            WPOConfig.setManualMaxEqDist(parseInt(maxEqDistBox.getValue(), 0, 256));
            WPOConfig.setManualMaxSlideDist(parseInt(maxSlideDistBox.getValue(), 0, 256));
            WPOConfig.setMaxBucketDist(parseInt(maxBucketDistBox.getValue(), 0, WPOConfig.MAX_FLUID_LEVEL));
            WPOConfig.save();
            this.minecraft.setScreen(parent);
        } catch (NumberFormatException ex) {
            statusMessage = Component.literal("Enter whole numbers for every field.");
        }
    }

    private static int parseInt(String text, int min, int max) {
        return Mth.clamp(Integer.parseInt(text.trim()), min, max);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int top = Math.max(28, this.height / 5 - 6);
        int labelX = centerX - LABEL_X_OFFSET;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, 12, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("wpo.config.performancePreset"), labelX, top + 6, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, Component.translatable("wpo.config.setMaxEqualizeDistance"), labelX, top + 34, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, Component.translatable("wpo.config.setMaxSlidingDistance"), labelX, top + 62, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, Component.literal("Interaction"), labelX, top + 82, 0xE0E0E0, false);
        guiGraphics.drawString(this.font, Component.translatable("wpo.config.setMaxBucketDistance"), labelX, top + 104, 0xFFFFFF, false);

        if (presetButton.getValue() != PerformancePreset.CUSTOM) {
            guiGraphics.drawCenteredString(this.font,
                    Component.literal("Manual equalize/sliding values are ignored unless preset is CUSTOM."),
                    centerX, top + 138, 0xC0C0C0);
        }

        guiGraphics.drawCenteredString(this.font,
                Component.literal("SKDS update budget stays under the SKDS Core config button."),
                centerX, this.height - 52, 0xC0C0C0);

        if (!statusMessage.getString().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, statusMessage, centerX, this.height - 40, 0xFF8080);
        }
    }
}
