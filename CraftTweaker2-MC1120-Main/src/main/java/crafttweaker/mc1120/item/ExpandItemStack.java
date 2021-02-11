package crafttweaker.mc1120.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.entity.attribute.IEntityAttributeModifier;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.item.IItemStack")
@ZenRegister
public class ExpandItemStack {
    private static ItemStack getInternal(IItemStack expanded) {
        return CraftTweakerMC.getItemStack(expanded);
    }

    @ZenMethod
    @ZenGetter("isBlock")
    public static boolean isBlock(IItemStack value) {
        return getInternal(value).getItem() instanceof ItemBlock;
    }

    @ZenMethod
    public static void addAttributeModifier(IItemStack stack, String attributeName, IEntityAttributeModifier modifier, IEntityEquipmentSlot equipmentSlot) {
        getInternal(stack).addAttributeModifier(attributeName, CraftTweakerMC.getAttributeModifier(modifier), CraftTweakerMC.getEntityEquipmentSlot(equipmentSlot));
    }

    @ZenGetter("maxItemUseDuration")
    @ZenMethod
    public static int getMaxItemUseDuration(IItemStack stack) {
        return getInternal(stack).getMaxItemUseDuration();
    }

    @ZenGetter("capNBT")
    @ZenMethod
    public static IData getCapNBT(IItemStack stack) {
        return CraftTweakerMC.getIData(CraftTweakerMC.getItemStack(stack).serializeNBT().getCompoundTag("ForgeCaps"));
    }

    @ZenMethod
    public static IItemStack withCapNBT(IItemStack stack, IData capNBT) {
        return CraftTweakerMC.getIItemStack(new ItemStack(
                CraftTweakerMC.getItem(stack.getDefinition()),
                stack.getAmount(),
                stack.getMetadata(),
                (NBTTagCompound) NBTConverter.from(capNBT))
        ).withTag(stack.getTag(), stack.getMatchTagExact());
    }
}