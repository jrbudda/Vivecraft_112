package com.mtbs3d.minecrift.gui.framework;

import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;

/**
 * Created by StellaArtois on 2/27/2016.
 */
public class GuiSelectOption extends BaseGuiSettings implements GuiYesNoCallback, GuiEventEx {

    private String[] options;
    private String title = "No title";
    private String question = "No question";
    protected long errorTextDisplayStart = 0;
    protected String errorText = "";
    public static final int ID_OPTION_SELECTED = 9998;
    protected final int ERROR_TEXT_TIMEOUT_MS = 4000;

    public GuiSelectOption(GuiScreen par1GuiScreen, VRSettings par2vrSettings, String title, String question, String[] options) {
        super(par1GuiScreen, par2vrSettings);
        this.options = options;
        this.title = title;
        this.question = question;
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.initGuiButtons();
    }

    protected void initGuiButtons()
    {
        this.buttonList.clear();
        for (int i = 0; i < options.length; i++) {
            this.buttonList.add(new GuiButton(i, this.width / 2 - 100, this.height / 4 + (i * 20) + 12, options[i]));
        }
        this.buttonList.add(new GuiButton(ID_GENERIC_DONE, this.width / 2 - 100, this.height / 4 + 130 + 12, "Cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == ID_GENERIC_DONE)
            {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else
            {
                boolean exit = true;
                if (this.parentGuiScreen instanceof GuiEventEx)
                {
                    ((GuiEventEx) this.parentGuiScreen).event(this.ID_OPTION_SELECTED, button.displayString);
                }
            }
        }
    }

    @Override
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
        this.drawString(this.fontRenderer, this.errorText, this.width / 2 - 100, 57, 16711680);
        super.drawScreen(mouseX, mouseY, partialTicks, false);
    }

    public void setErrorText(String errorText)
    {
        this.errorText = errorText;
        this.errorTextDisplayStart = System.currentTimeMillis();
    }

    @Override
    public void confirmClicked(boolean result, int id) {

    }

    @Override
    public boolean event(int id, VRSettings.VrOptions enumm) {
        return true;
    }

    @Override
    public boolean event(int id, String s) {
        return true;
    }
}
