package merlin1809.irisextension.mixin.client;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.ShaderPackOptionList;
import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import merlin1809.irisextension.Variables;

@Mixin(value = ShaderPackScreen.class, remap = false)
public class ShaderScreenMixin {

    @Shadow
    private ShaderPackSelectionList shaderPackList;

    @Shadow
    @Nullable
    private ShaderPackOptionList shaderOptionList;

    @Shadow
    private boolean guiHidden;

    @Shadow
    private boolean optionMenuOpen;

    private static final int PANEL_WIDTH = 160;
    private static final int PANEL_PADDING = 4;

    private boolean hasKeybinds() {
        if (Iris.getCurrentPack().isEmpty() || !optionMenuOpen || shaderOptionList == null) return false;
        for (int i = 0; i < 10; i++) {
            if (Variables.customKeyMethods[i] != 0) {
                return true;
            }
        }
        return false;
    }

    private void setOptionsWidth() {
        if(shaderOptionList != null) {
            ShaderPackScreen self = (ShaderPackScreen)(Object)this;
            int widthNew = hasKeybinds() ? self.width - PANEL_WIDTH - 4 : self.width;

            shaderOptionList.setWidth(widthNew);
            shaderOptionList.rebuild();
        }
    }

    @Inject(method = "refreshForChangedPack", at = @At("TAIL"))
    private void changeOnPackChange(CallbackInfo ci) {
        setOptionsWidth();
    }

    @Inject(method = "init", at = @At("TAIL"))
    private ShaderPackOptionList fixOptionWidth(CallbackInfo ci) {
        setOptionsWidth();

        return shaderOptionList;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderKeybindPanel(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!hasKeybinds() || guiHidden) return;
        ShaderPackScreen self = (ShaderPackScreen)(Object)this;
        Font font = Minecraft.getInstance().font;

        int panelX = self.width - PANEL_WIDTH - 2;
        int panelY = 36;
        int panelHeight = self.height - 58 - panelY;

        graphics.fill(panelX, panelY, self.width, panelY + panelHeight, 0x55000000);

        graphics.drawString(font, Component.literal("Shader Keybinds").withStyle(ChatFormatting.WHITE), panelX + PANEL_PADDING, panelY + PANEL_PADDING, 0xFFFFFFFF);

        graphics.fill(panelX + 2, panelY + 15, panelX + PANEL_WIDTH - 2, panelY + 16, 0xFF888888);

        for (int i = 0; i < 10; i++) {
            String baseKey = "iris_extension.keybind.key" + i;
            String defaultName = Language.getInstance().getOrDefault(baseKey + ".short", baseKey + ".short");
            int rowY = panelY + 20 + (i * 12);

            if (rowY + 10 > panelY + panelHeight) break;

            KeyMapping mapping = Variables.customKeybinds[i];
            if (mapping != null) {
                Component boundKey = mapping.getTranslatedKeyMessage();
                int keyWidth = font.width(boundKey);
                graphics.drawString(font, boundKey, panelX + PANEL_WIDTH - keyWidth - PANEL_PADDING, rowY, 0xFFAAAAAA);
            }

            if (Variables.customKeyMethods[i] != 0) {                
                String shaderName = defaultName + ": " + Language.getInstance().getOrDefault(baseKey + ".name", "");
                String truncated = font.plainSubstrByWidth(shaderName, PANEL_WIDTH - 8);
                graphics.drawString(font, truncated, panelX + PANEL_PADDING, rowY, 0xFFFFFF55);
            } else {
                String truncated = font.plainSubstrByWidth(defaultName, PANEL_WIDTH - 8);
                graphics.drawString(font, truncated, panelX + PANEL_PADDING, rowY, 0xFF888888);
            }
        }
    }
}
