package com.mtbs3d.minecrift.gui.framework;

import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

public class GuiEnterText extends BaseGuiSettings
{
    protected String title;
    protected String question;
    protected String confirmButtonText;
    protected String cancelButtonText;
    protected String initialText;
    protected int eventId;
    protected long errorTextDisplayStart = 0;
    protected String errorText = "";
    protected GuiTextField guiTextField;
    protected final int ERROR_TEXT_TIMEOUT_MS = 4000;

    public GuiEnterText(GuiScreen parent,
                        VRSettings vrSettings,
                        int eventId,
                        String title,
                        String question,
                        String initialText,
                        String ok,
                        String cancel)
    {
        super(parent, vrSettings);
        this.title = title;
        this.question = question;
        this.initialText = initialText;
        this.confirmButtonText = ok;
        this.cancelButtonText = cancel;
        this.eventId = eventId;
    }

    public void updateScreen()
    {
        this.guiTextField.updateCursorCounter();
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, this.confirmButtonText));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, this.cancelButtonText));
        this.guiTextField = new GuiTextField(1,this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.guiTextField.setFocused(true);
        this.guiTextField.setText(this.initialText);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (button.id == 0)
            {
                boolean exit = true;
                if (this.parentGuiScreen instanceof GuiEventEx)
                {
                    exit = ((GuiEventEx) this.parentGuiScreen).event(this.eventId, this.guiTextField.getText());
                }

                if (exit)
                {
                    this.mc.displayGuiScreen(this.parentGuiScreen);
                }
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) {
        this.guiTextField.textboxKeyTyped(typedChar, keyCode);
        ((GuiButton) this.buttonList.get(0)).enabled = this.guiTextField.getText().trim().length() > 0;

        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed((GuiButton) this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.errorTextDisplayStart != 0)
        {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.errorTextDisplayStart > ERROR_TEXT_TIMEOUT_MS)
            {
                this.errorText = "";
                this.errorTextDisplayStart = 0;
            }
        }
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, this.question, this.width / 2 - 100, 47, 10526880);
        this.drawString(this.fontRenderer, this.errorText, this.width / 2 - 100, 87, 16711680);
        this.guiTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks, false);
    }

    public void setErrorText(String errorText)
    {
        this.errorText = errorText;
        this.errorTextDisplayStart = System.currentTimeMillis();
    }
}
