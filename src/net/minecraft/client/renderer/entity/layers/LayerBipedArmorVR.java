package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelArmorVR;
import net.minecraft.client.model.ModelPlayerVR;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

public class LayerBipedArmorVR extends LayerArmorBase<ModelArmorVR>
{
    public LayerBipedArmorVR(RenderLivingBase<?> rendererIn)
    {
        super(rendererIn);
    }

    protected void initArmor()
    {
        this.modelLeggings = new ModelArmorVR(0.5F);
        this.modelArmor = new ModelArmorVR(1.0F);
    }

    @SuppressWarnings("incomplete-switch")
    protected void setModelSlotVisible(ModelArmorVR p_188359_1_, EntityEquipmentSlot slotIn)
    {
        this.setModelVisible(p_188359_1_);

        switch (slotIn)
        {
            case HEAD:
                p_188359_1_.bipedHead.showModel = true;
                p_188359_1_.bipedHeadwear.showModel = true;
                break;

            case CHEST:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightArm.showModel = true;
                p_188359_1_.bipedLeftArm.showModel = true;
                break;

            case LEGS:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
                break;

            case FEET:
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
        }
    }

    protected void setModelVisible(ModelArmorVR model)
    {
        model.setInvisible(false);
    }
}
