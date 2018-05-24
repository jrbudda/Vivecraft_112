package com.mtbs3d.minecrift.gui.settings;

import java.util.ArrayList;
import java.util.SortedSet;

import com.mtbs3d.minecrift.gui.framework.BaseGuiSettings;
import com.mtbs3d.minecrift.gui.framework.GuiEnterText;
import com.mtbs3d.minecrift.gui.framework.GuiEventEx;
import com.mtbs3d.minecrift.settings.VRSettings;
import com.mtbs3d.minecrift.settings.profile.ProfileManager;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.Tessellator;

public class GuiSelectSettingsProfile extends BaseGuiSettings implements GuiYesNoCallback, GuiEventEx
{
    public static final int ID_CANCEL_BUTTON = 0;
    public static final int ID_SELECT_PROFILE_BUTTON = 1;
    public static final int ID_DELETE_PROFILE_BUTTON = 2;
    public static final int ID_CREATE_NEW_PROFILE_BUTTON = 3;
    public static final int ID_RENAME_PROFILE_BUTTON = 6;
    public static final int ID_DUPLICATE_PROFILE_BUTTON = 7;

    private int selectedProfile;
    private java.util.List profiles = new ArrayList();
    private GuiSelectSettingsProfile.List profileListCtrl;
    private boolean deleteInvoked;
    private String profileToDelete = null;
    private GuiButton deleteProfileButton;
    private GuiButton selectProfileButton;
    private GuiButton renameProfileButton;
    private GuiButton duplicateProfileButton;
    private GuiButton createProfileButton;
    private GuiButton cancelButton;
    private GuiEnterText guiEnterText;

    public GuiSelectSettingsProfile(GuiScreen p_i1054_1_, VRSettings vrSettings)
    {
        super( p_i1054_1_, vrSettings );
        super.screenTitle = "Select settings profile";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.initProfileList();

        this.profileListCtrl = new GuiSelectSettingsProfile.List();
        this.profileListCtrl.registerScrollButtons(4, 5);
        this.initGuiButtons();
        updateButtonEnablement(this.selectedProfile);
    }

    private void initProfileList()
    {
        this.profiles.clear();
        SortedSet<String> profileList = VRSettings.getProfileList();
        String selectedProfile = VRSettings.getCurrentProfile();
        int index = 0;
        this.selectedProfile = -1;
        for (String profile : profileList) {
            this.profiles.add(profile);
            if (profile.equals(selectedProfile)) {
                this.selectedProfile = index;
            }
            index++;
        }
    }

    protected String getProfileNameFromIndex(int selectedProfile)
    {
        String var2 = (String)this.profiles.get(selectedProfile);
        return var2;
    }

    public void initGuiButtons()
    {
        this.buttonList.add(this.selectProfileButton = new GuiButton(ID_SELECT_PROFILE_BUTTON, this.width / 2 - 154, this.height - 52, 150, 20, "Select Profile"));
        this.buttonList.add(this.createProfileButton = new GuiButton(ID_CREATE_NEW_PROFILE_BUTTON, this.width / 2 + 4, this.height - 52, 150, 20, "Create New Profile..."));
        this.buttonList.add(this.renameProfileButton = new GuiButton(ID_RENAME_PROFILE_BUTTON, this.width / 2 - 154, this.height - 28, 72, 20, "Rename..."));
        this.buttonList.add(this.deleteProfileButton = new GuiButton(ID_DELETE_PROFILE_BUTTON, this.width / 2 - 76, this.height - 28, 72, 20, "Delete..."));
        this.buttonList.add(this.duplicateProfileButton = new GuiButton(ID_DUPLICATE_PROFILE_BUTTON, this.width / 2 + 4, this.height - 28, 72, 20, "Duplicate..."));
        this.buttonList.add(this.cancelButton = new GuiButton(ID_CANCEL_BUTTON, this.width / 2 + 82, this.height - 28, 72, 20, "Done"));
        this.selectProfileButton.enabled = false;
        this.deleteProfileButton.enabled = false;
        this.renameProfileButton.enabled = false;
        this.duplicateProfileButton.enabled = false;
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            String profile = getProfileNameFromIndex(this.selectedProfile);

            if (button.id == ID_DELETE_PROFILE_BUTTON)
            {
                if (profile != null)
                {
                    this.deleteInvoked = true;
                    this.profileToDelete = profile;
                    GuiYesNo var3 = createDeleteConfirmationGui(this, profile, this.selectedProfile);
                    this.mc.displayGuiScreen(var3);
                }
            }
            else if (button.id == ID_SELECT_PROFILE_BUTTON)
            {
                VRSettings.setCurrentProfile(profile);
                this.mc.reinitFramebuffers = true;
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (button.id == ID_CREATE_NEW_PROFILE_BUTTON)
            {
                this.guiEnterText = new GuiEnterText(
                        this,
                        this.mc.vrSettings,
                        button.id,
                        "Create New Profile",
                        "Create new profile (using defaults) called:",
                        "New Profile",
                        "Create",
                        "Cancel"
                );

                this.mc.displayGuiScreen(this.guiEnterText);
            }
            else if (button.id == ID_RENAME_PROFILE_BUTTON)
            {
                this.guiEnterText = new GuiEnterText(
                        this,
                        this.mc.vrSettings,
                        button.id,
                        "Rename Profile",
                        "Rename the profile '" + profile + "' to:",
                        profile,
                        "Rename",
                        "Cancel"
                );

                this.mc.displayGuiScreen(this.guiEnterText);
            }
            else if (button.id == ID_CANCEL_BUTTON)
            {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (button.id == ID_DUPLICATE_PROFILE_BUTTON)
            {
                this.guiEnterText = new GuiEnterText(
                        this,
                        this.mc.vrSettings,
                        button.id,
                        "Duplicate Profile",
                        "Duplicating profile '" + profile + "' as:",
                        "Copy of " + profile,
                        "Duplicate",
                        "Cancel"
                );

                this.mc.displayGuiScreen(this.guiEnterText);
            }
            else
            {
                this.profileListCtrl.actionPerformed(button);
            }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (this.deleteInvoked)
        {
            this.deleteInvoked = false;

            if (result)
            {
                VRSettings.deleteProfile(this.profileToDelete);

                this.reinit = true;
                this.profileToDelete = null;
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {      
        this.profileListCtrl.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks, false);
    }

    public static GuiYesNo createDeleteConfirmationGui(GuiYesNoCallback p_152129_0_, String p_152129_1_, int p_152129_2_)
    {
        String var3 = "Delete Profile";
        String var4 = "Are you sure you want to delete the profile '" + p_152129_1_ + "'?";
        String var5 = "Delete";
        String var6 = "Cancel";
        GuiYesNo var7 = new GuiYesNo(p_152129_0_, var3, var4, var5, var6, p_152129_2_);
        return var7;
    }

    public static final int SLOT_HEIGHT = 12;

    @Override
    public boolean event(int id, VRSettings.VrOptions enumm) {
        return true;
    }

    @Override
    public boolean event(int id, String s)
    {
        boolean result = true;
        StringBuilder error = new StringBuilder();
        String currentProfile = getProfileNameFromIndex(this.selectedProfile);

        if (id == this.ID_CREATE_NEW_PROFILE_BUTTON)
        {
            // Use default settings when creating a profile
            result = VRSettings.createProfile(s, true, error);
        }
        else if (id == this.ID_RENAME_PROFILE_BUTTON)
        {
            result = VRSettings.renameProfile(currentProfile, s, error);
        }
        else if (id == this.ID_DUPLICATE_PROFILE_BUTTON)
        {
            result = VRSettings.duplicateProfile(currentProfile, s, error);
        }

        if (!result)
        {
            this.guiEnterText.setErrorText(error.toString());
            return false;
        }

        this.reinit = true;
        return true;
    }

    protected void updateButtonEnablement(int selectedId)
    {
        GuiSelectSettingsProfile.this.selectedProfile = selectedId;
        String currentProfile = getProfileNameFromIndex(this.selectedProfile);

        if (!currentProfile.equals(ProfileManager.DEFAULT_PROFILE)) {
            GuiSelectSettingsProfile.this.createProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.selectProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.deleteProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.renameProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.duplicateProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.cancelButton.enabled = true;
        }
        else {
            GuiSelectSettingsProfile.this.createProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.selectProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.deleteProfileButton.enabled = false;
            GuiSelectSettingsProfile.this.renameProfileButton.enabled = false;
            GuiSelectSettingsProfile.this.duplicateProfileButton.enabled = true;
            GuiSelectSettingsProfile.this.cancelButton.enabled = true;
        }
    }

    class List extends GuiSlot
    {
        public List()
        {
            super(GuiSelectSettingsProfile.this.mc, GuiSelectSettingsProfile.this.width, GuiSelectSettingsProfile.this.height, 32, GuiSelectSettingsProfile.this.height - 64, SLOT_HEIGHT);
        }

        protected int getSize()
        {
            return GuiSelectSettingsProfile.this.profiles.size();
        }

        protected void elementClicked(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
            updateButtonEnablement(p_148144_1_);
        }

        protected boolean isSelected(int p_148131_1_)
        {
            return p_148131_1_ == GuiSelectSettingsProfile.this.selectedProfile;
        }

        protected int getContentHeight()
        {
            return GuiSelectSettingsProfile.this.profiles.size() * SLOT_HEIGHT;
        }

        protected void drawBackground()
        {
            GuiSelectSettingsProfile.this.drawDefaultBackground();
        }

		@Override
		protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn,
				int mouseYIn, float partial) {
            int colour = 16777215; // White
            String var9 = GuiSelectSettingsProfile.this.getProfileNameFromIndex(entryID);
            if (var9.equals(VRSettings.getCurrentProfile())) {
                colour = 16777120; // Yellow if current profile
            }

            GuiSelectSettingsProfile.this.drawString(GuiSelectSettingsProfile.this.fontRenderer, var9, insideLeft + 2, yPos + 1, colour);

		}
    }
}
